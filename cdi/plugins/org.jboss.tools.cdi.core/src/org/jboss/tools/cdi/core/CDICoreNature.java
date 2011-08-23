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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.extension.CDIExtensionManager;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.lib.ClassPathMonitor;
import org.jboss.tools.common.java.ParametedTypeFactory;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XJob.XRunnable;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.common.validation.internal.ProjectValidationContext;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.w3c.dom.Element;

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
	
	public CDICoreNature() {
		extensions.setProject(this);
		definitions.setProject(this);
	}

	public void configure() throws CoreException {
		addToBuildSpec(CDICoreBuilder.BUILDER_ID);
	}

	public void deconfigure() throws CoreException {
		removeFromBuildSpec(CDICoreBuilder.BUILDER_ID);
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
	}

	public void setCDIProject(ICDIProject cdiProject) {
		this.cdiProjectDelegate = cdiProject;
		cdiProject.setNature(this);
	}

	public Set<CDICoreNature> getCDIProjects() {
		return dependsOn;
	}

	public CDIExtensionManager getExtensionManager() {
		return extensions;
	}

	public Set<CDICoreNature> getCDIProjects(boolean hierarchy) {
		if(hierarchy) {
			if(dependsOn.isEmpty()) return dependsOn;
			Set<CDICoreNature> result = new HashSet<CDICoreNature>();
			getAllCDIProjects(result);
			return result;
		} else {
			return dependsOn;
		}
	}

	void getAllCDIProjects(Set<CDICoreNature> result) {
		for (CDICoreNature n:dependsOn) {
			if(result.contains(n)) continue;
			result.add(n);
			n.getAllCDIProjects(result);
		}
	}

	public List<TypeDefinition> getAllTypeDefinitions() {
		Set<CDICoreNature> ps = getCDIProjects(true);
		if(ps == null || ps.isEmpty()) {
			return getDefinitions().getTypeDefinitions();
		}
		List<TypeDefinition> ds = getDefinitions().getTypeDefinitions();
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		result.addAll(ds);
		Set<String> keys = new HashSet<String>();
		for (TypeDefinition d: ds) {
			keys.add(d.getKey());
		}
		for (CDICoreNature p: ps) {
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

	public List<AnnotationDefinition> getAllAnnotations() {
		Set<CDICoreNature> ps = getCDIProjects(false);
		if(ps == null || ps.isEmpty()) {
			return getDefinitions().getAllAnnotations();
		}
		List<AnnotationDefinition> result = new ArrayList<AnnotationDefinition>();
		Set<IType> types = new HashSet<IType>();
		for (CDICoreNature p: ps) {
			List<AnnotationDefinition> ds2 = p.getAllAnnotations();
			for (AnnotationDefinition d: ds2) {
				IType t = d.getType();
				if(t != null && !types.contains(t)) {
					types.add(t);
					result.add(d);
				}
			}
		}

		List<AnnotationDefinition> ds = getDefinitions().getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			IType t = d.getType();
			if(t != null && !types.contains(t)) {
				types.add(t);
				result.add(d);
			}
		}

		return result;
	}

	public Set<BeansXMLDefinition> getAllBeanXMLDefinitions() {
		Set<CDICoreNature> ps = getCDIProjects(true);
		if(ps == null || ps.isEmpty()) {
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
		for (CDICoreNature p: ps) {
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

	public Set<CDICoreNature> getDependentProjects() {
		return usedBy;
	}

	public void addCDIProject(final CDICoreNature p) {
		if(dependsOn.contains(p)) return;
		addUsedCDIProject(p);
		p.addDependentCDIProject(this);
		//TODO
		if(!p.isStorageResolved()) {
			XJob.addRunnableWithPriority(new XRunnable() {
				public void run() {
					p.resolve();
					if(p.getDelegate() != null) {
						p.getDelegate().update();
					}
				}
				
				public String getId() {
					return "Build CDI Project " + p.getProject().getName();
				}
			});
		}
	}

	public void removeCDIProject(CDICoreNature p) {
		if(!dependsOn.contains(p)) return;
		p.usedBy.remove(this);
		synchronized (dependsOn) {
			dependsOn.remove(p);
		}
		//TODO
	}

	void addUsedCDIProject(CDICoreNature p) {
		synchronized (dependsOn) {
			dependsOn.add(p);
		}
	}

	public void addDependentCDIProject(CDICoreNature p) {
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
			isStorageResolved = true;
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
		isStorageResolved = true;
		try {
			getProject().build(IncrementalProjectBuilder.FULL_BUILD, CDICoreBuilder.BUILDER_ID, new HashMap(), new NullProgressMonitor());
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
			cdiProjectDelegate.update();
		}
//		IPath[] ps = sourcePaths2.keySet().toArray(new IPath[0]);
//		for (int i = 0; i < ps.length; i++) {
//			pathRemoved(ps[i]);
//		}
		fireChanges();
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
		dependsOn.clear();
		usedBy.clear();
		loadProjectDependenciesFromKBProject();
	}

	private void loadProjectDependenciesFromKBProject() {
		Element root = null;
		File file = getKBStorageFile();
		if(file != null && file.isFile()) {
			root = XMLUtilities.getElement(file, null);
			if(root != null) {
				loadProjectDependencies(root);
			}
		}		
	}
	
	private File getKBStorageFile() {
		IPath path = WebKbPlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects/" + project.getName() + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
		return file;
	}
	
	private void loadProjectDependencies(Element root) {
		Element dependsOnElement = XMLUtilities.getUniqueChild(root, "depends-on-projects"); //$NON-NLS-1$
		if(dependsOnElement != null) {
			Element[] paths = XMLUtilities.getChildren(dependsOnElement, "project"); //$NON-NLS-1$
			for (int i = 0; i < paths.length; i++) {
				String p = paths[i].getAttribute("name"); //$NON-NLS-1$
				if(p == null || p.trim().length() == 0) continue;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p);
				if(project == null || !project.isAccessible()) continue;
				CDICoreNature sp = CDICorePlugin.getCDI(project, false);
				if(sp != null) {
					addUsedCDIProject(sp);
					sp.addDependentCDIProject(this);
				}
			}
		}

		Element usedElement = XMLUtilities.getUniqueChild(root, "used-by-projects"); //$NON-NLS-1$
		if(usedElement != null) {
			Element[] paths = XMLUtilities.getChildren(usedElement, "project"); //$NON-NLS-1$
			for (int i = 0; i < paths.length; i++) {
				String p = paths[i].getAttribute("name"); //$NON-NLS-1$
				if(p == null || p.trim().length() == 0) continue;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p);
				if(project == null || !project.isAccessible()) continue;
				CDICoreNature sp = CDICorePlugin.getCDI(project, false);
				if(sp != null) {
					addDependentCDIProject(sp);
				}
			}
		}
	
	}

}