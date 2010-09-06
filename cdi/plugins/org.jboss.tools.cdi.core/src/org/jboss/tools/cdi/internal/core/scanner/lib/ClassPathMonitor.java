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
package org.jboss.tools.cdi.internal.core.scanner.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.AbstractClassPathMonitor;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class ClassPathMonitor extends AbstractClassPathMonitor<CDICoreNature>{
	IPath[] srcs = new IPath[0];

	public ClassPathMonitor(CDICoreNature project) {
		this.project = project;
	}

	public void init() {
		model = EclipseResourceUtil.createObjectForResource(getProjectResource()).getModel();
	}

	public Map<String, XModelObject> process() {
		Map<String, XModelObject> newJars = new HashMap<String, XModelObject>();
		Iterator<String> it = processedPaths.iterator();
		while(it.hasNext()) {
			String p = it.next();
			if(paths.contains(p)) continue;
			project.pathRemoved(new Path(p));
			it.remove();
		}
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			if(processedPaths.contains(p)) continue;
			processedPaths.add(p);

			String fileName = new File(p).getName();
			if(EclipseResourceUtil.SYSTEM_JAR_SET.contains(fileName)) continue;
			String jsname = "lib-" + fileName; //$NON-NLS-1$
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname); //$NON-NLS-1$
			if(o == null) continue;
			XModelObject b = o.getChildByPath("META-INF/beans.xml");
			if(b == null) continue;
			newJars.put(p, b);
		}
		
		validateProjectDependencies();
		return newJars;
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
				project.pathRemoved(s);
			}
		}
		srcs = newSrcs;
	}

	public void validateProjectDependencies() {
		List<CDICoreNature> ps = null;
		
		try {
			ps = getProjects(project.getProject());
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		if(ps != null) {
			Set<CDICoreNature> set = project.getCDIProjects();
			Set<CDICoreNature> removable = new HashSet<CDICoreNature>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (CDICoreNature p : ps) {
				project.addCDIProject(p);
			}
			for (CDICoreNature p : removable) {
				project.removeCDIProject(p);
			}
		}
	}

	public boolean hasToUpdateProjectDependencies() {
		List<CDICoreNature> ps = null;
		
		try {
			ps = getProjects(project.getProject());
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		if(ps != null) {
			Set<CDICoreNature> set = project.getCDIProjects();
			Set<CDICoreNature> removable = new HashSet<CDICoreNature>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (CDICoreNature p : ps) {
				return true;
			}
			for (CDICoreNature p : removable) {
				return true;
			}
		}
		return false;
	}

	public static List<CDICoreNature> getProjects(IProject project) throws CoreException {
		List<CDICoreNature> list = new ArrayList<CDICoreNature>();
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < es.length; i++) {
			if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
				if(p == null || !p.isAccessible()) continue;
				CDICoreNature sp = CDICorePlugin.getCDI(p, false);
				if(sp != null) list.add(sp);
			}
		}
		return list;
	}
}