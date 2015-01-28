/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.internal.core.scanner.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.scanner.BatchArchiveDetector;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.util.UniquePaths;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class Libs implements IElementChangedListener {
	public static interface LibsListener {
		public void pathsChanged(List<String> paths);
		public void libraryChanged(String path);
	}
	protected IProject object;
	protected List<String> paths = null;
	Map<IPath, String> paths2 = new HashMap<IPath, String>();
	Set<String> projects = new HashSet<String>();

	int excudedState = 0;

	List<LibsListener> listeners = new ArrayList<LibsListener>();

	boolean isActive = false;

	public Libs(IProject object) {
		this.object = object;
	}

	public void init() {
		if(!isActive) {
			isActive = true;
			JavaCore.addElementChangedListener(this);
		}
	}

	public void destroy() {
		if(isActive) {
			JavaCore.removeElementChangedListener(this);
			isActive = false;
		}
	}

	private IProject getProjectResource() {
		return object;
	}

	/**
	 * Path should use the separator provided by the current OS.
	 * For example IPath.toOSString() or java.io.File.getCanonicalPath().
	 * 
	 * @param path
	 * @return
	 */
	public IPackageFragmentRoot getLibrary(String path) {
		return BatchArchiveDetector.findPackageFragmentRoot(path, object);
	}

	public IPackageFragmentRoot getLibrary(File f) {
		IPackageFragmentRoot result = null;
		if(f.exists()) {
			String path = "";
			try {
				path = f.getCanonicalPath();
			} catch (IOException e) {
				path = f.getAbsolutePath().replace('\\', '/');
			}
			result = getLibrary(path);
		}
		return result;
	}

	public boolean update() {
		boolean result = false;
		int cpv = classpathVersion;
		if(hasToUpdatePaths()) {
			result = updatePaths(getNewPaths(), cpv);
			if(isExcludedStateChanged()) {
				result = true;
			}
			if(paths == null && result) {
				fire();
				return true;
			}
		}	
	
		if(result) {
			fire();
		}
		return result;
	}

	public void requestForUpdate() {
		classpathVersion++;
	}

	synchronized boolean hasToUpdatePaths() {
		return (classpathVersion > pathsVersion);
	}

	private List<String> getNewPaths() {
		List<String> result = null;
		try {
			result = getAllVisibleLibraries(getProjectResource());
			List<String> jre = getJREClassPath(getProjectResource());
			if(jre != null) result.removeAll(jre);
			if(result != null) {
				Iterator<String> it = result.iterator();
				while(it.hasNext()) {
					String path = it.next();
					String fileName = new File(path).getName();
					if(isJar(path) && SYSTEM_JAR_SET.contains(fileName)) {
						it.remove();
					}
				}
			}
			updateProjects();
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		return result;
	}

	private void updateProjects() throws JavaModelException {
		Set<String> result = new HashSet<String>();
		IJavaProject javaProject = EclipseUtil.getJavaProject(getProjectResource());
		if(javaProject != null) {
			result.add(getProjectResource().getName());
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
					if(p == null || !p.isAccessible()) continue;
					result.add(p.getName());
				}
			}
		}
		projects = result;
	}

	private boolean isExcludedStateChanged() {
		try {
			int es = computeExcludedState();
			if(es != excudedState) {
				excudedState = es;
				return true;
			}
		} catch (JavaModelException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		return false;
	}

	private int computeExcludedState() throws JavaModelException {
		int result = 0;
		IJavaProject javaProject = EclipseUtil.getJavaProject(getProjectResource());
		if(javaProject != null) {
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				IPath p = es[i].getPath();
				IPath[] ps = es[i].getExclusionPatterns();
				if(ps != null && ps.length > 0) {
					for (int j = 0; j < ps.length; j++) {
						String key = p.toString() + "/" + ps[j].toString(); //$NON-NLS-1$
						result += key.hashCode();
					}
				}
			}
		}
		return result;
	}

	private synchronized boolean updatePaths(List<String> newPaths, int cpv) {
		if(cpv <= pathsVersion) {
			return false;
		}
		pathsVersion = cpv;
		if(paths == null && newPaths == null) return false;
		if((newPaths != null && paths != null) && (paths.size() == newPaths.size())) {
			boolean b = false;
			for (int i = 0; i < paths.size() && !b; i++) {
				if(!paths.get(i).equals(newPaths.get(i))) b = true;
			}
			if(!b) return false;
		}
		paths = newPaths;
		createMap();
		return true;
	}
	
	public List<String> getPaths() {
		return paths;
	}

	public Map<IPath, String> getPathsAsMap() {
		return paths2;
	}
	
	private void createMap() {
		paths2.clear();
		if(paths != null) {
			for (String p : paths) {
				paths2.put(UniquePaths.getInstance().intern(new Path(p)), p);
			}
		}
	}

	public synchronized void addListener(LibsListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeListener(LibsListener listener) {
		listeners.remove(listener);
	}

	void fire() {
		for (LibsListener listener: getListeners()) {
			listener.pathsChanged(paths);
		}
	}

	private synchronized LibsListener[] getListeners() {
		return listeners.toArray(new LibsListener[0]);
	}

	int classpathVersion = 0;
	int pathsVersion = -1;

	public void elementChanged(ElementChangedEvent event) {
		IProject project = getProjectResource();
		if(project == null || !project.exists()) {
			destroy();
			return;
		}

		for (IJavaElementDelta dc: event.getDelta().getAffectedChildren()) {
			if(dc.getElement() instanceof IJavaProject && (isReleventProject(((IJavaProject)dc.getElement()).getProject()))) {
				int f = dc.getFlags();
				if((f & (IJavaElementDelta.F_CLASSPATH_CHANGED 
					| IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED)) != 0) {
					requestForUpdate();
					return;
				} else {
					for (IJavaElementDelta d1: dc.getAffectedChildren()) {
						if(d1.getKind() == IJavaElementDelta.ADDED || d1.getKind() == IJavaElementDelta.REMOVED) {
							requestForUpdate();
							return;
						}
					}
				}
			}
		}
	}

	private boolean isReleventProject(IProject p) {
		return projects.contains(p.getName());
	}

	public void libraryChanged(String jar) {
		for (LibsListener listener: getListeners()) {
			listener.libraryChanged(jar);
		}
	}





	private static class LibraryCollector {
		IProject project;
		List<String> ordered = new ArrayList<String>();
		Set<String> paths = new HashSet<String>();
		Set<IProject> processed = new HashSet<IProject>();

		LibraryCollector(IProject project) {
			this.project = project;
			process(project);
		}
		
		void process(IProject project) {
			if(processed.contains(project)) {
				return;
			}
			processed.add(project);
			IJavaProject javaProject = EclipseUtil.getJavaProject(project);
			if(javaProject == null) {
				return;
			}
			IClasspathEntry[] es = null;
			try {
				es = javaProject.getResolvedClasspath(true);
			} catch (CoreException e) {
				BatchCorePlugin.pluginLog().logError(e);
				return;
			}
			for (int i = 0; i < es.length; i++) {
				if(project == this.project || es[i].isExported()) {
					if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
						IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
						if(p != null && p.isAccessible()) {
							process(p);
						}
					} else if(es[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
						String s = expandPath(es[i].getPath(), project);
						if(s != null && !paths.contains(s)) {
							paths.add(s);
							ordered.add(s);
						}	
					}
				}
			}
		}
	}

	public static List<String> getAllVisibleLibraries(IProject project) {
		return new LibraryCollector(project).ordered;
	}

	static String expandPath(IPath ipath, IProject project) {
		String s = null;
		String path = ipath.toString();
		//First let's check if path is defined within Eclipse work space.
		if(path.startsWith("/") && path.indexOf("/", 1) > 1) {
			IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(ipath);
			if(findMember != null) {
				s = findMember.getLocation().toString();
			}
		}
		//If search in Eclipse work space has failed, this is a useless attempt, but
		//let keep it just in case (this is good old code that worked for a long while).
		if(s == null && path.startsWith("/" + project.getName() + "/")) {
			IResource findMember = project.findMember(ipath.removeFirstSegments(1));
			if(findMember != null) {
				s = findMember.getLocation().toString();
			}
		}
	
		//If we failed to find resource in Eclipse work space, 
		//lets try the path as absolute on disk
		if(s == null && new java.io.File(path).exists()) {
			s = path;
		}
		try {
			if(s != null) {
				return new java.io.File(s).getCanonicalPath();
			}				
		} catch (IOException e) {
			//ignore - we do not care about malformed URLs in classpath here.
		}
		return null;
	}
	
	public static List<String> getJREClassPath(IProject project) throws CoreException {
		if(project == null || !project.isAccessible() || !project.hasNature(JavaCore.NATURE_ID)) return null;
		ArrayList<String> l = new ArrayList<String>();
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] es0 = javaProject.getRawClasspath();
		IClasspathEntry[] es = null;
		for (int i = 0; i < es0.length && es == null; i++) {
			if(es0[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER && 
					es0[i].getPath().toString().startsWith("org.eclipse.jdt.launching.JRE_CONTAINER")) { //$NON-NLS-1$
				IClasspathContainer container = JavaCore.getClasspathContainer(es0[i].getPath(), javaProject);
				if(container == null) continue;
				es = container.getClasspathEntries();
			}
		}
		if(es == null) return l;
		for (int i = 0; i < es.length; i++) {
			try {
				String s = null;
				String path = es[i].getPath().toString();
				if(path.startsWith("/" + project.getName() + "/")) {
					s = project.findMember(es[i].getPath().removeFirstSegments(1)).getLocation().toString();
				} else if(new java.io.File(path).isFile()) {
					s = path;
				}
				if(s != null) {
					l.add(new java.io.File(s).getCanonicalPath());
				}
			} catch (IOException e) {
				//ignore - we do not care about malformed URLs in class path here.
			}
		}
		return l;
	}
	
	public static boolean isJar(String path) {
		path = path.toLowerCase();
		return path.endsWith(".jar") || path.endsWith(".zip"); //$NON-NLS-1$ //$NON-NLS-2$
	}

    static String[] SYSTEM_JARS = {"rt.jar", "jsse.jar", "jce.jar", "charsets.jar"};
    public static Set<String> SYSTEM_JAR_SET = new HashSet<String>();
	static {
		for (int i = 0; i < SYSTEM_JARS.length; i++) SYSTEM_JAR_SET.add(SYSTEM_JARS[i]);
	}


}
