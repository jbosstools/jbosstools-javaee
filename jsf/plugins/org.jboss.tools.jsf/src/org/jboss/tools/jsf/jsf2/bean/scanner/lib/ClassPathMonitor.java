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
package org.jboss.tools.jsf.jsf2.bean.scanner.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.project.ext.AbstractClassPathMonitor;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;

public class ClassPathMonitor extends AbstractClassPathMonitor<IJSF2Project>{
	IPath[] srcs = new IPath[0];
	
	Set<IPath> removedPaths = new HashSet<IPath>();

	public ClassPathMonitor(IJSF2Project project) {
		this.project = project;
	}

	public void init() {
		model = EclipseResourceUtil.createObjectForResource(getProjectResource()).getModel();
		super.init();
	}

	public Map<String, XModelObject> process() {
		Map<String, XModelObject> newJars = new HashMap<String, XModelObject>();
		for (String p: syncProcessedPaths()) {
			synchronized (removedPaths) {
				removedPaths.add(new Path(p));
			}
		}
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			if(!requestForLoad(p)) continue;

			String fileName = new File(p).getName();
			if(EclipseResourceUtil.SYSTEM_JAR_SET.contains(fileName)) continue;

			XModelObject o = FileSystemsHelper.getLibs(model).getLibrary(p);
			if(o == null) continue;

			XModelObject b = o.getChildByPath("META-INF/web-fragment.xml");
			if(b != null) {
				newJars.put(p, b);
			}
		}
		
		validateProjectDependencies();
		return newJars;
	}

	public void applyRemovedPaths() {
		synchronized (removedPaths) {
			for (IPath p: removedPaths) {
				project.pathRemoved(p);
			}
			removedPaths.clear();
		}
	}

	public IProject getProjectResource() {
		return project.getProject();
	}

	public void setSrcs(IPath[] newSrcs) {
		Set<IPath> ss = new HashSet<IPath>();
		for (IPath s: newSrcs) {
			ss.add(s);
		}
		for (IPath s: srcs) {
			if(!ss.contains(s)) {
				synchronized (removedPaths) {
					removedPaths.add(s);
				}
			}
		}
		srcs = newSrcs;
	}

	public void validateProjectDependencies() {
		List<IJSF2Project> ps = null;
		
		try {
			ps = getProjects(project.getProject());
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		if(ps != null) {
			Set<? extends IJSF2Project> set = project.getUsedProjects();
			Set<IJSF2Project> removable = new HashSet<IJSF2Project>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (IJSF2Project p : ps) {
				project.addUsedProject(p);
			}
			for (IJSF2Project p : removable) {
				project.removeUsedProject(p);
			}
		}
	}

	public boolean hasToUpdateProjectDependencies() {
		List<IJSF2Project> ps = null;
		
		try {
			ps = getProjects(project.getProject());
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		if(ps != null) {
			Set<? extends IJSF2Project> set = project.getUsedProjects();
			Set<IJSF2Project> removable = new HashSet<IJSF2Project>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (IJSF2Project p : ps) {
				return true;
			}
			for (IJSF2Project p : removable) {
				return true;
			}
		}
		return false;
	}

	public static List<IJSF2Project> getProjects(IProject project) throws CoreException {
		List<IJSF2Project> list = new ArrayList<IJSF2Project>();
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		if(javaProject != null) {
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
					if(p == null || !p.isAccessible()) continue;
					IJSF2Project sp = JSF2ProjectFactory.getJSF2Project(p, false);
					if(sp != null) list.add(sp);
				}
			}
		}
		return list;
	}

	public synchronized void libraryChanged(String path) {
		super.libraryChanged(path);
		removedPaths.add(new Path(path));
	}

}