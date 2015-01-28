/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core.scanner.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.scanner.BatchArchiveDetector;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.jst.web.kb.internal.IKbProjectExtension;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ClassPathMonitor extends AbstractClassPathMonitor {
	IBatchProject batchProject;

	IPath[] srcs = new IPath[0];
	Set<IPath> removedPaths = new HashSet<IPath>();

	public ClassPathMonitor(IBatchProject project) {
		batchProject = project;
	}

	public JarSet process() {
		JarSet newJars = new JarSet();
		for (String p: syncProcessedPaths()) {
			synchronized (removedPaths) {
				removedPaths.add(new Path(p));
			}
		}
		Set<String> processed = new HashSet<String>();
		synchronized(this) {
			processed.addAll(processedPaths);
		}
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			if(!requestForLoad(p)) continue;
			removedPaths.add(new Path(p));

			String fileName = new File(p).getName();
			if(Libs.SYSTEM_JAR_SET.contains(fileName)) continue;

			detectBatchModule(p, newJars);
		}

		validateProjectDependencies();
		return newJars;
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

	public void applyRemovedPaths() {
		synchronized (removedPaths) {
			for (IPath p: removedPaths) {
				((BatchProject)batchProject).pathRemoved(p);
			}
			removedPaths.clear();
		}
	}

	public void validateProjectDependencies() {
		List<IBatchProject> ps = null;
		
		try {
			ps = getProjects(batchProject.getProject());
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		if(ps != null) {
			Set<IKbProjectExtension> set = batchProject.getUsedProjects();
			Set<IKbProjectExtension> removable = new HashSet<IKbProjectExtension>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (IBatchProject p : ps) {
				batchProject.addUsedProject(p);
			}
			for (IKbProjectExtension p : removable) {
				batchProject.removeUsedProject(p);
			}
		}
	}

	public boolean hasToUpdateProjectDependencies() {
		List<IBatchProject> ps = null;
		
		try {
			ps = getProjects(batchProject.getProject());
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		if(ps != null) {
			Set<IKbProjectExtension> set = batchProject.getUsedProjects();
			Set<IKbProjectExtension> removable = new HashSet<IKbProjectExtension>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			return !ps.isEmpty() || !removable.isEmpty();
		}
		return false;
	}

	public static List<IBatchProject> getProjects(IProject project) throws CoreException {
		List<IBatchProject> list = new ArrayList<IBatchProject>();
		IJavaProject javaProject = EclipseUtil.getJavaProject(project);
		if(javaProject != null) {
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
					if(p == null || !p.isAccessible()) continue;
					IBatchProject sp = BatchProjectFactory.getBatchProject(p, false);
					if(sp != null) list.add(sp);
				}
			}
		}
		return list;
	}

	private void detectBatchModule(String path, JarSet newJars) {
		newJars.getFileSystems().add(path);
		int archiveType = BatchArchiveDetector.getInstance().getBatchArchive(path);
		if(archiveType == BatchArchiveDetector.UNRESOLVED) {
			try {
				archiveType = BatchArchiveDetector.getInstance().resolve(path, batchProject);
			} catch (JavaModelException e) {
				BatchCorePlugin.pluginLog().logError(e);
				return;
			}
		}
		if(archiveType != BatchArchiveDetector.NOT_ARCHIVE) {
			newJars.getBatchModules().add(path);
		}
	}

	@Override
	public IProject getProjectResource() {
		return batchProject.getProject();
	}

	public synchronized void libraryChanged(String path) {
		super.libraryChanged(path);
		removedPaths.add(new Path(path));
	}

}
