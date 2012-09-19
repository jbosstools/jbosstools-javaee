/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XJob.XRunnable;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.build.JSF2ProjectBuilder;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;
import org.jboss.tools.jsf.jsf2.bean.scanner.lib.ClassPathMonitor;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.w3c.dom.Element;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2Project implements IJSF2Project {
	IProject project = null;

	ClassPathMonitor classPath = new ClassPathMonitor(this);
	DefinitionContext definitions = new DefinitionContext();
	
	boolean isBuilt = false;
	private boolean isStorageResolved = false;

	Set<JSF2Project> dependsOn = new HashSet<JSF2Project>();
	Set<JSF2Project> usedBy = new HashSet<JSF2Project>();

	private Set<IJSF2ManagedBean> allBeans = new HashSet<IJSF2ManagedBean>();
	private Map<IPath, Set<IJSF2ManagedBean>> beansByPath = new HashMap<IPath, Set<IJSF2ManagedBean>>();
	private Map<String, Set<IJSF2ManagedBean>> beansByName = new HashMap<String, Set<IJSF2ManagedBean>>();
	private Set<IJSF2ManagedBean> namedBeans = new HashSet<IJSF2ManagedBean>();

	private boolean isMetadataComplete = false;

	public JSF2Project() {
		definitions.setProject(this);
	}

	@Override
	public Set<IJSF2ManagedBean> getManagedBeans() {
		Set<IJSF2ManagedBean> result = new HashSet<IJSF2ManagedBean>();
		result.addAll(namedBeans);
		return result;
	}

	@Override
	public Set<IJSF2ManagedBean> getManagedBeans(IPath path) {
		Set<IJSF2ManagedBean> result = new HashSet<IJSF2ManagedBean>();
		Set<IJSF2ManagedBean> beans = beansByPath.get(path);
		if(beans != null && !beans.isEmpty()) result.addAll(beans);
		return result;
	}

	@Override
	public Set<IJSF2ManagedBean> getManagedBeans(String name) {
		Set<IJSF2ManagedBean> result = new HashSet<IJSF2ManagedBean>();
		synchronized(this) {
			Set<IJSF2ManagedBean> beans = beansByName.get(name);
			if(beans != null) {
				result.addAll(beans);
			}
		}
		return result;
	}

	public boolean isMetadataComplete() {
		return isMetadataComplete;
	}

	@Override
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
			JSFModelPlugin.getDefault().logError(e);
		}
		return null;
	}

	public void setProject(IProject project) {
		this.project = project;
		classPath.init();
	}

	@Override
	public Set<JSF2Project> getUsedProjects() {
		return dependsOn;
	}

	public Set<JSF2Project> getUsedProjects(boolean hierarchy) {
		if(hierarchy) {
			if(dependsOn.isEmpty()) return dependsOn;
			Set<JSF2Project> result = new HashSet<JSF2Project>();
			getAllUsedProjects(result);
			return result;
		} else {
			return dependsOn;
		}
	}

	void getAllUsedProjects(Set<JSF2Project> result) {
		for (JSF2Project n: dependsOn) {
			if(result.contains(n)) continue;
			result.add(n);
			n.getAllUsedProjects(result);
		}
	}

	public Set<JSF2Project> getDependentProjects() {
		return usedBy;
	}

	@Override
	public void addUsedProject(final IJSF2Project project) {
		if(dependsOn.contains(project)) return;
		addUsedProjectInternal(project);
		project.addDependentProject(this);
		if(!project.isStorageResolved()) {
			XJob.addRunnableWithPriority(new XRunnable() {
				public void run() {
					project.resolve();
					project.update(true);
				}				
				public String getId() {
					return "Build JSF2 Project " + project.getProject().getName();
				}
			});
		}
	}

	synchronized void addUsedProjectInternal(IJSF2Project project) {
		dependsOn.add((JSF2Project)project);
	}

	public void addDependentProject(IJSF2Project project) {
		usedBy.add((JSF2Project)project);
	}

	@Override
	public void removeUsedProject(IJSF2Project project) {
		JSF2Project p = (JSF2Project)project;
		if(!dependsOn.contains(p)) return;
		p.usedBy.remove(this);
		synchronized (this) {
			dependsOn.remove(p);
		}
	}

	public List<TypeDefinition> getAllTypeDefinitions() {
		Set<JSF2Project> ps = getUsedProjects(true);
		if(ps == null || ps.isEmpty()) {
			return getDefinitions().getTypeDefinitions();
		}
		List<TypeDefinition> ds = getDefinitions().getTypeDefinitions();
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		result.addAll(ds);
		Set<IType> types = new HashSet<IType>();
		for (TypeDefinition d: ds) {
			IType t = d.getType();
			if(t != null) types.add(t);
		}
		for (JSF2Project p: ps) {
			List<TypeDefinition> ds2 = p.getDefinitions().getTypeDefinitions();
			for (TypeDefinition d: ds2) {
				IType t = d.getType();
				if(t != null && !types.contains(t)) {
					types.add(t);
					result.add(d);
				}
			}
		}
		return result;
	}

	public DefinitionContext getDefinitions() {
		return definitions;
	}

	@Override
	public void pathRemoved(IPath path) {
		definitions.getWorkingCopy().clean(path);
	}

	public ClassPathMonitor getClassPath() {
		return classPath;
	}

	@Override
	public boolean isStorageResolved() {
		return isStorageResolved;
	}

	public void resolveStorage(boolean load) {
		if(isStorageResolved) return;
		if(load) {
			load();
		} else {
			isStorageResolved = true;
		}
	}

	public void resolve() {
		resolveStorage(true);
	}

	public void load() {
		if(isStorageResolved) return;
		isStorageResolved = true;
		try {
			new JSF2ProjectBuilder(this);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		
		postponeFiring();
		
		try {		
			//Use kb storage for dependent projects since cdi is not stored.
			loadProjectDependenciesFromKBProject();
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

		fireChanges();
	}
	
	public void update(boolean updateDependent) {
		FacesConfigDefinition fc = definitions.getFacesConfig();
		isMetadataComplete = fc != null && fc.isMetadataComplete(); 
		
		List<TypeDefinition> typeDefinitions = getAllTypeDefinitions();
		List<IJSF2ManagedBean> beans = new ArrayList<IJSF2ManagedBean>();
		for (TypeDefinition typeDefinition : typeDefinitions) {
			if(typeDefinition.isManagedBean()) { //improve for managed properties
				JSF2ManagedBean bean = new JSF2ManagedBean();
				bean.setDefinition(typeDefinition);
				beans.add(bean);
			}
			
		}
		synchronized (this) {
			beansByPath.clear();
			beansByName.clear();
			namedBeans.clear();
			allBeans.clear();
		}
		
		if(!isMetadataComplete) {
			//No JSF2 beans in model when metadata is complete.
			for (IJSF2ManagedBean bean: beans) {
				addBean(bean);
			}
		}

		if(updateDependent) {
			Collection<JSF2Project> dependent = new ArrayList<JSF2Project>(usedBy);
			for (JSF2Project p: dependent) {
				p.update(false);
			}
		}

	}

	public void addBean(IJSF2ManagedBean bean) {
		String name = bean.getName();
		if(name != null && name.length() > 0) {
			synchronized (this) {
				Set<IJSF2ManagedBean> bs = beansByName.get(name);
				if(bs == null) {
					bs = new HashSet<IJSF2ManagedBean>();
					beansByName.put(name, bs);				
				}
				bs.add(bean);
				namedBeans.add(bean);
			}
		}
		IPath path = bean.getSourcePath();
		synchronized (this) {
			Set<IJSF2ManagedBean> bs = beansByPath.get(path);
			if(bs == null) {
				bs = new HashSet<IJSF2ManagedBean>();
				beansByPath.put(path, bs);
			}
			bs.add(bean);
			allBeans.add(bean);
		}
	}

	public void store() throws IOException {
		isBuilt = true;
//		File file = getStorageFile();
	}

	private File getStorageFile() {
		IPath path = JSFModelPlugin.getDefault().getStateLocation();
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
				IJSF2Project sp = JSF2ProjectFactory.getJSF2Project(project, false);
				if(sp != null) {
					addUsedProjectInternal(sp);
					sp.addDependentProject(this);
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
				IJSF2Project sp = JSF2ProjectFactory.getJSF2Project(project, false);
				if(sp != null) {
					addDependentProject(sp);
				}
			}
		}
	
	}

}
