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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XJob.XRunnable;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.build.JSF2ProjectBuilder;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;
import org.jboss.tools.jsf.jsf2.bean.scanner.lib.ClassPathMonitor;
import org.jboss.tools.jst.web.kb.internal.AbstractKbProjectExtension;
import org.jboss.tools.jst.web.kb.internal.IKbProjectExtension;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2Project extends AbstractKbProjectExtension implements IJSF2Project {
	ClassPathMonitor classPath = new ClassPathMonitor(this);
	DefinitionContext definitions = new DefinitionContext();
	
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
		synchronized(this) {
			result.addAll(namedBeans);
		}
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

	@Override
	public boolean isMetadataComplete() {
		return isMetadataComplete;
	}

	@Override
	public void setProject(IProject project) {
		super.setProject(project);
		classPath.init();
	}

	@Override
	protected void resolveUsedProjectInJob(final IKbProjectExtension project) {
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

	public List<TypeDefinition> getAllTypeDefinitions() {
		Set<IKbProjectExtension> ps = getUsedProjects(true);
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
		for (IKbProjectExtension p: ps) {
			List<TypeDefinition> ds2 = ((JSF2Project)p).getDefinitions().getTypeDefinitions();
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

	protected void build() {
		try {
			new JSF2ProjectBuilder(this);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
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
			Collection<IKbProjectExtension> dependent = new ArrayList<IKbProjectExtension>(usedBy);
			for (IKbProjectExtension p: dependent) {
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

	@Override
	protected IKbProjectExtension loadWithFactory(IProject project, boolean resolve) {
		return JSF2ProjectFactory.getJSF2Project(project, resolve);
	}

}
