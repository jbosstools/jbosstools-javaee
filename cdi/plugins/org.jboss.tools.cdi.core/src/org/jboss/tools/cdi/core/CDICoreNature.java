/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.extension.CDIExtensionManager;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.cdi.internal.core.scanner.lib.ClassPathMonitor;
import org.jboss.tools.common.java.ParametedTypeFactory;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XJob.XRunnable;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.common.validation.internal.ProjectValidationContext;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;

public class CDICoreNature implements IProjectNature {
	public static String NATURE_ID = "org.jboss.tools.cdi.core.cdinature";

	IProject project = null;
	ICDIProject cdiProjectDelegate;

	ParametedTypeFactory typeFactory = new ParametedTypeFactory();

	ClassPathMonitor classPath = new ClassPathMonitor(this);
	DefinitionContext definitions = new DefinitionContext();

	ProjectValidationContext validationContext = null;

	boolean isBuilt = false;

//	Map<IPath, Object> sourcePaths2 = new HashMap<IPath, Object>(); //TODO

	private boolean isStorageResolved = false;

	Set<CDICoreNature> dependsOn = new HashSet<CDICoreNature>();
	
	Set<CDICoreNature> usedBy = new HashSet<CDICoreNature>();

	private CDIExtensionManager extensions = new CDIExtensionManager();

	private int version = CDIConstants.CDI_VERSION_1_0;
	private int beanDiscoveryMode = BeanArchiveDetector.ALL;
	
	public CDICoreNature() {
		extensions.setProject(this);
		definitions.setProject(this);
	}

	public void configure() throws CoreException {
		addToBuildSpec(CDICoreBuilder.BUILDER_ID);
	}

	public void deconfigure() throws CoreException {
		removeFromBuildSpec(CDICoreBuilder.BUILDER_ID);
		dispose();
	}

	public IProject getProject() {
		return project;
	}

	/**
	 * Convenience method.
	 * 
	 * @param qualifiedName
	 * @return
	 */
	public IType getType(String qualifiedName) {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getProject());
		if(jp == null) return null;
		try {
			return EclipseJavaUtil.findType(jp, qualifiedName);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	public void setProject(IProject project) {
		this.project = project;
		classPath.init();
		updateVersion();
	}

	public int getVersion() {
		return version;
	}

	/**
	 * Helper method to check if version is 1.0.
	 * @return
	 */
	public boolean isFirstVersion() {
		return getVersion() == CDIConstants.CDI_VERSION_1_0;
	}

	/**
	 * Helper method to check if version is 1.1 or higher.
	 * @return
	 */
	public boolean isAdvancedVersion() {
		return getVersion() >= CDIConstants.CDI_VERSION_1_1;
	}

	public int getBeanDiscoveryMode() {
		return beanDiscoveryMode;
	}

	public void setBeanDiscoveryMode(int value) {
		beanDiscoveryMode = value;
	}

	/**
	 * Returns true if update detects change of version.
	 * Invoked by builder only. 
	 * @return
	 */
	boolean updateVersion() {
		int version = CDIUtil.getCDIVersion(getProject());
		boolean changed = version != this.version;
		this.version = version;
		return changed;
	}

	public void setCDIProject(ICDIProject cdiProject) {
		this.cdiProjectDelegate = cdiProject;
		cdiProject.setNature(this);
	}

	public Set<CDICoreNature> getCDIProjects() {
		Set<CDICoreNature> result = new HashSet<CDICoreNature>();
		synchronized(this) {
			result.addAll(dependsOn);
		}
		return result;
	}

	/**
	 * Returns the number of projects included explicitly 
	 * to classpath of current project that are contained in
	 * the passed set.
	 * 
	 * @param projects
	 * @return
	 */
	public synchronized int countDirectDependencies(Set<CDICoreNature> projects) {
		int result = 0;
		for (CDICoreNature r: dependsOn) {
			if(projects.contains(r)) result++;
		}
		return result;
	}

	public CDIExtensionManager getExtensionManager() {
		return extensions;
	}

	/**
	 * Returns all the project that are included into classpath of this project.
	 * Modification to the returned set does not affect stored references.
	 * 
	 * @param hierarchy If false then return the projects explicitly included into the project classpath.
	 * If true then all the project from the entire hierarchy will be returned.
	 * @return
	 */
	public Set<CDICoreNature> getCDIProjects(boolean hierarchy) {
		if(hierarchy && dependsOnOtherProjects()) {
			Set<CDICoreNature> result = new HashSet<CDICoreNature>();
			getAllCDIProjects(result);
			return result;
		} else {
			return getCDIProjects();
		}
	}

	synchronized void getAllCDIProjects(Set<CDICoreNature> result) {
		for (CDICoreNature n:dependsOn) {
			if(result.contains(n)) continue;
			result.add(n);
			n.getAllCDIProjects(result);
		}
	}

	public List<TypeDefinition> getAllTypeDefinitions() {
		if(!dependsOnOtherProjects()) {
			return getDefinitions().getTypeDefinitions();
		}
		List<TypeDefinition> ds = getDefinitions().getTypeDefinitions();
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		result.addAll(ds);
		Set<String> keys = new HashSet<String>();
		for (TypeDefinition d: ds) {
			keys.add(d.getKey());
		}
		for (CDICoreNature p: getCDIProjects(true)) {
			List<TypeDefinition> ds2 = p.getDefinitions().getTypeDefinitions();
			for (TypeDefinition d: ds2) {
				String key = d.getKey();
				if(!keys.contains(key)) {
					keys.add(key);
					result.add(d);
				}
			}
		}
		return result;
	}

	public synchronized boolean dependsOnOtherProjects() {
		return !dependsOn.isEmpty();
	}

	public List<AnnotationDefinition> getAllAnnotations() {
		return getDefinitions().getAllAnnotationsWithDependencies();
	}

	/**
	 * Returns set of types that were to be marked as vetoed by CDI extensions, but 
	 * for which it was impossible to set isVetoed=true on the type definition object,
	 * because type is declared in another project where it is not vetoed.
	 * 
	 * @return
	 */
	public Set<String> getAllVetoedTypes() {
		Set<String> result = new HashSet<String>();
		result.addAll(definitions.getVetoedTypes());
		for (CDICoreNature n: getCDIProjects(true)) {
			result.addAll(n.getDefinitions().getVetoedTypes());
		}		
		return result;
	}

	public Set<BeansXMLDefinition> getAllBeanXMLDefinitions() {
		if(!dependsOnOtherProjects()) {
			return getDefinitions().getBeansXMLDefinitions();
		}
		Set<BeansXMLDefinition> ds = getDefinitions().getBeansXMLDefinitions();
		Set<BeansXMLDefinition> result = new HashSet<BeansXMLDefinition>();
		result.addAll(ds);
		Set<IPath> paths = new HashSet<IPath>();
		for (BeansXMLDefinition d: ds) {
			IPath t = d.getPath();
			if(t != null) paths.add(t);
		}
		for (CDICoreNature p: getCDIProjects(true)) {
			Set<BeansXMLDefinition> ds2 = p.getDefinitions().getBeansXMLDefinitions();
			for (BeansXMLDefinition d: ds2) {
				IPath t = d.getPath();
				if(t != null && !paths.contains(t)) {
					paths.add(t);
					result.add(d);
				}
			}
		}
		return result;
	}

	/**
	 * Returns all the CDI projects that include this project into their class path.
	 * @return
	 */
	public Set<CDICoreNature> getDependentProjects() {
		return usedBy;
	}

	public CDICoreNature[] getAllDependentProjects(boolean resolve) {
		Map<CDICoreNature, Integer> set = new HashMap<CDICoreNature, Integer>();
		getAllDependentProjects(set, 0);
		if(resolve) {
			for (CDICoreNature n: set.keySet()) {
				n.resolve();
			}
		}
		CDICoreNature[] result = set.keySet().toArray(new CDICoreNature[set.size()]);
		Arrays.sort(result, new D(set));
		return result;
	}

	public CDICoreNature[] getAllDependentProjects() {
		return getAllDependentProjects(false);
	}

	private void getAllDependentProjects(Map<CDICoreNature, Integer> result, int level) {
		if(level > 10) return;
		for (CDICoreNature n:usedBy) {
			if(!result.containsKey(n) || result.get(n).intValue() < level) {
				result.put(n, level);
				n.getAllDependentProjects(result, level + 1);
			}
		}
	}
	private static class D implements Comparator<CDICoreNature> {
		Map<CDICoreNature, Integer> set;
		D(Map<CDICoreNature, Integer> set) {
			this.set = set;
		}
		@Override
		public int compare(CDICoreNature o1, CDICoreNature o2) {
			return set.get(o1).intValue() - set.get(o2).intValue();
		}
		
	}

	public void addCDIProject(final CDICoreNature p) {
		synchronized(this) {
			if(dependsOn.contains(p)) {
				return;
			}
		}
		addUsedCDIProject(p);
		p.addDependentCDIProject(this);
		//TODO
		if(!p.isStorageResolved() && p.getProject() != null) {
			XJob.addRunnableWithPriority(new XRunnable() {
				public void run() {
					p.resolve();
					if(p.getDelegate() != null) {
						p.getDelegate().update(true);
					}
				}
				
				public String getId() {
					return "Build CDI Project " + p.getProject().getName();
				}
			});
		}
	}

	public synchronized void removeCDIProject(CDICoreNature p) {
		if(!dependsOn.contains(p)) return;
		p.usedBy.remove(this);
		dependsOn.remove(p);
		definitions.clean(p.getProject());
		//TODO
	}

	synchronized void addUsedCDIProject(CDICoreNature p) {
		dependsOn.add(p);
	}

	public synchronized void addDependentCDIProject(CDICoreNature p) {
		usedBy.add(p);
	}

	public DefinitionContext getDefinitions() {
		return definitions;
	}

	public ICDIProject getDelegate() {
		return cdiProjectDelegate;
	}

	public ParametedTypeFactory getTypeFactory() {
		return typeFactory;
	}

	public ClassPathMonitor getClassPath() {
		return classPath;
	}

	public boolean isStorageResolved() {
		return isStorageResolved;
	}
	/**
	 * 
	 * @param load
	 */
	public void resolveStorage(boolean load) {
		if(isStorageResolved) return;
		if(load) {
			load();
		} else {
			loadProjectDependenciesFromKBProject();
			synchronized(this) {
				isStorageResolved = true;
			}
		}
	}

	/**
	 * 
	 */
	public void resolve() {
		resolveStorage(true);
	}

	/**
	 * Loads results of last build, which are considered 
	 * actual until next build.
	 */	
	public void load() {
		if(isStorageResolved) return;
		synchronized(this) {
			if(isStorageResolved) return;
			isStorageResolved = true;
		}
		try {
			new CDICoreBuilder(this);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		
		postponeFiring();
		
		try {		
//			boolean b = getClassPath().update();
//			if(b) {
//				getClassPath().validateProjectDependencies();
//			}
//			File file = getStorageFile();

			//Use kb storage for dependent projects since cdi is not stored.
			loadProjectDependenciesFromKBProject();
			//TODO

//			if(b) {
//				getClassPath().process();
//			}

		} finally {
			fireChanges();
		}
	}

	boolean isBuildOn = false;

	/**
	 * Returns true if either build is not currently performed by another thread
	 * or after waiting for less than 100 second for its completion. Otherwize, 
	 * returns false and logs warning.
	 *  
	 * Build can be invoked concurrently by the following clients:
	 * 1. Initial load invoked by 
	 * 		(a) content assist,
	 * 		(b) validation,
	 * 		(c) adding project to dependent project.
	 * 2. Eclipse's regular build.
	 * 
	 * Concurrent build should be prevented as definition context can have only
	 * one working copy. 
	 * 
	 * It is impossible to solve the problem by just declaring build method 
	 * synchronized as it calls for other synchronized methods which can result 
	 * in a deadlock.
	 * 
	 * This method is the single point that selects one thread to be waited for
	 * by all the other threads requesting for  build without locking with them.
	 * Thread that has just completed build, awakens the next thread to begin
	 * build. We cannot drop that request, as new changes might happen while
	 * the previous build was partly completed. 
	 * 
	 * The wait is restricted by 100 seconds as long enough time for building 
	 * one project. If the wait fails, warning is logged with the name of the 
	 * project. As long time as 100 seconds per one builder per one project
	 * most likely means already existing problems with performance. Anyway,
	 * clean/build  of the project called by user after the warning will 
	 * securely restore its correct state. 
	 * 
	 * @return whether the build can be safely performed after reasonable waiting for other threads
	 */
	synchronized boolean requestForBuild() {
		if(isBuildOn) {
			try {
				wait(100000);
			} catch (InterruptedException e) {
				CDICorePlugin.getDefault().logWarning("Interrupted waiting for build of " + project);
				notify();
				return false;
			}
		}
		if(!isBuildOn) {
			isBuildOn = true;
			return true;
		}
		CDICorePlugin.getDefault().logWarning("Could not wait for build of " + project);
		return false;
	}

	synchronized void releaseBuild() {
		isBuildOn = false;
		notify();
	}

	public void clean() {
		File file = getStorageFile();
		if(file != null && file.isFile()) {
			file.delete();
		}
		isBuilt = false;
		classPath.clean();
		postponeFiring();

		definitions.clean();
		if(cdiProjectDelegate != null) {
			cdiProjectDelegate.update(true);
		}
//		IPath[] ps = sourcePaths2.keySet().toArray(new IPath[0]);
//		for (int i = 0; i < ps.length; i++) {
//			pathRemoved(ps[i]);
//		}
		fireChanges();
	}

	public void cleanTypeFactory() {
		typeFactory.clean();
		CDICoreNature[] ps = getAllDependentProjects();
		for (CDICoreNature n: ps) {
			n.typeFactory.clean();
		}
	}
	
	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 * @throws IOException 
	 */
	public void store() throws IOException {
		isBuilt = true;
		File file = getStorageFile();
//TODO
//		file.getParentFile().mkdirs();
	}
	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
	protected void addToBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand command = null;
		ICommand commands[] = description.getBuildSpec();
		for (int i = 0; i < commands.length && command == null; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) 
				command = commands[i];
		}
		if (command == null) {
			command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] oldCommands = description.getBuildSpec();
			ICommand[] newCommands = new ICommand[oldCommands.length + 1];
			System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
			newCommands[oldCommands.length] = command;
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
		}
	}

	/**
	 * 
	 */
	static String EXTERNAL_TOOL_BUILDER = "org.eclipse.ui.externaltools.ExternalToolBuilder"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	static final String LAUNCH_CONFIG_HANDLE = "LaunchConfigHandle"; //$NON-NLS-1$

	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
	protected void removeFromBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			String builderName = commands[i].getBuilderName();
			if (!builderName.equals(builderID)) {
				if(!builderName.equals(EXTERNAL_TOOL_BUILDER)) continue;
				Object handle = commands[i].getArguments().get(LAUNCH_CONFIG_HANDLE);
				if(handle == null || handle.toString().indexOf(builderID) < 0) continue;
			}
			ICommand[] newCommands = new ICommand[commands.length - 1];
			System.arraycopy(commands, 0, newCommands, 0, i);
			System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
			return;
		}
	}

	/*
	 * 
	 */
	private File getStorageFile() {
		IPath path = CDICorePlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects/" + project.getName()); //$NON-NLS-1$
		return file;
	}
	
	public void clearStorage() {
		File f = getStorageFile();
		if(f == null || !f.exists()) return;
		FileUtil.clear(f);
		f.delete();
	}

	public boolean hasNoStorage() {
			if(isBuilt) return false;
		File f = getStorageFile();
		return f == null || !f.exists();
	}

	public void postponeFiring() {
		//TODO
	}

	public void fireChanges() {
		//TODO
	}

	public long fullBuildTime;
	public List<Long> statistics;

	public void pathRemoved(IPath source) {
//		sourcePaths2.remove(source);
		definitions.getWorkingCopy().clean(source);
		//TODO
	}

	public ProjectValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = new ProjectValidationContext();
		}
		return validationContext;
	}

	/**
	 * Test method.
	 */
	public void reloadProjectDependencies() {
		synchronized (this) {
			dependsOn.clear();
			usedBy.clear();
			projectDependenciesLoaded = false;
		}
		loadProjectDependenciesFromKBProject();
	}

	boolean projectDependenciesLoaded = false;

	public void loadProjectDependencies() {
		loadProjectDependenciesFromKBProject();
	}

	private void loadProjectDependenciesFromKBProject() {
		if(projectDependenciesLoaded) return;
		synchronized(this) {
			if(projectDependenciesLoaded) return;
			projectDependenciesLoaded = true;
		}
		_loadProjectDependencies();
	}
	
	private void _loadProjectDependencies() {
		KbProject kb = (KbProject)KbProjectFactory.getKbProject(project, true, true);

		if(kb == null) {
			return;
		}
		
		for (KbProject kb1: kb.getKbProjects()) {
			IProject project = kb1.getProject();
			if(project == null || !project.isAccessible()) continue;
			KbProjectFactory.getKbProject(project, true, true);
			CDICoreNature sp = CDICorePlugin.getCDI(project, false);
			if(sp != null) {
				addUsedCDIProject(sp);
				sp.addDependentCDIProject(this);
			}
		}
		
		for (KbProject kb2: kb.getDependentKbProjects()) {
			IProject project = kb2.getProject();
			if(project == null || !project.isAccessible()) continue;
			KbProjectFactory.getKbProject(project, true, true);
			CDICoreNature sp = CDICorePlugin.getCDI(project, false);
			if(sp != null) {
				addDependentCDIProject(sp);
			}
		}
	}

	public synchronized void dispose() {
		CDICoreNature[] ds = dependsOn.toArray(new CDICoreNature[dependsOn.size()]);
		for (CDICoreNature d: ds) {
			removeCDIProject(d);
		}
		CDICoreNature[] us = usedBy.toArray(new CDICoreNature[usedBy.size()]);
		for (CDICoreNature u: us) {
			u.removeCDIProject(this);
		}
	}

}