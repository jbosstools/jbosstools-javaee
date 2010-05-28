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
package org.jboss.tools.seam.internal.core.scanner.lib;

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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsLoader;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.helpers.InnerModelHelper;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;

/**
 * Monitors class path of project and loads seam components of it.
 *  
 * @author Viacheslav Kabanovich
 */
public class ClassPath {
	SeamProject project;
	XModel model = null;
	
	List<String> paths = null;
	Map<IPath, String> paths2 = new HashMap<IPath, String>();
	
	Set<String> processedPaths = new HashSet<String>();
	
	/**
	 * Creates instance of class path for seam project
	 * @param project
	 */
	public ClassPath(SeamProject project) {
		this.project = project;
	}
	
	/**
	 * Returns seam project
	 * @return
	 */
	public SeamProject getProject() {
		return project;
	}
	
	/**
	 * Initialization of inner model.
	 */
	public void init() {
		model = InnerModelHelper.createXModel(project.getProject());
	}
	
	static String[] SYSTEM_JARS = {"rt.jar", "jsse.jar", "jce.jar", "charsets.jar"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	static Set<String> SYSTEM_JAR_SET = new HashSet<String>();
	
	static {
		for (int i = 0; i < SYSTEM_JARS.length; i++) SYSTEM_JAR_SET.add(SYSTEM_JARS[i]);
	}
	
	/**
	 * Returns true if class path was up-to-date.
	 * Otherwise, updates inner model and disables class loader.
	 * @return
	 */
	public boolean update() {
		List<String> newPaths = null;
		try {
			newPaths = EclipseResourceUtil.getClassPath(project.getProject());
			List<String> jre = EclipseResourceUtil.getJREClassPath(project.getProject());
			if(jre != null) newPaths.removeAll(jre);
		} catch (CoreException e) {
			//TODO
			SeamCorePlugin.getDefault().logError(e);
		} catch(IOException e) {
			SeamCorePlugin.getDefault().logError(e);			
		}
		if(paths == null && newPaths == null) return false;
		if((newPaths == null || paths == null) || (paths.size() != newPaths.size())) {
			paths = newPaths;
		} else { 
			boolean b = false;
			for (int i = 0; i < paths.size() && !b; i++) {
				if(!paths.get(i).equals(newPaths.get(i))) b = true;
			}
			if(!b) return false;
			paths = newPaths;
		}
		createMap();
		XModelObject object = model.getByPath("FileSystems"); //$NON-NLS-1$
		XModelObject[] fs = object.getChildren("FileSystemJar"); //$NON-NLS-1$
		Set<XModelObject> fss = new HashSet<XModelObject>();
		for (int i = 0; i < fs.length; i++) fss.add(fs[i]);
		
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			if(!EclipseResourceUtil.isJar(path)) continue;
			String fileName = new File(path).getName();
			if(SYSTEM_JAR_SET.contains(fileName)) continue;
			String jsname = "lib-" + fileName; //$NON-NLS-1$
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname); //$NON-NLS-1$
			if(o != null) {
				fss.remove(o);
			} else {
				o = object.getModel().createModelObject("FileSystemJar", null); //$NON-NLS-1$
				o.setAttributeValue("name", jsname); //$NON-NLS-1$
				o.setAttributeValue("location", path); //$NON-NLS-1$
				o.set(FileSystemsLoader.IS_ADDED_TO_CLASSPATH, "true"); //$NON-NLS-1$
				object.addChild(o);
//				object.setModified(true);
			}			
		}
		
		for (XModelObject o: fss) {
			String path = XModelObjectUtil.expand(o.getAttributeValue("location"), o.getModel(), null); //$NON-NLS-1$
			if("true".equals(o.get(FileSystemsLoader.IS_ADDED_TO_CLASSPATH))) { //$NON-NLS-1$
				o.removeFromParent(); 
			} else if(!new File(path).exists()) {
				o.removeFromParent();
			}			
		}
		
		return true;
	}
	
	private void createMap() {
		paths2.clear();
		if(paths != null) {
			for (String p : paths) {
				paths2.put(new Path(p), p);
			}
		}
	}
	
	/**
	 * Loads seam components from items recently added to class path. 
	 */
	public void process() {
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

			LibraryScanner scanner = new LibraryScanner();
			scanner.setClassPath(this);

			String fileName = new File(p).getName();
			if(SYSTEM_JAR_SET.contains(fileName)) continue;
			String jsname = "lib-" + fileName; //$NON-NLS-1$
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname); //$NON-NLS-1$
			if(o == null) continue;
			
			LoadedDeclarations c = null;
			try {
				if(scanner.isLikelyComponentSource(o)) {
					c = scanner.parse(o, new Path(p), project);
				}
			} catch (ScannerException e) {
				SeamCorePlugin.getDefault().logError(e);
			}
			if(c == null) {
				c = new LoadedDeclarations();
			}
			if(c != null) {
				componentsLoaded(c, new Path(p));
			}
		}
		
		validateProjectDependencies();
	}
	
	public void validateProjectDependencies() {
		List<SeamProject> ps = null;
		
		try {
			ps = getSeamProjects(project.getProject());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		if(ps != null) {
			Set<SeamProject> set = project.getSeamProjects();
			Set<SeamProject> removable = new HashSet<SeamProject>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (SeamProject p : ps) {
				project.addSeamProject(p);
			}
			for (SeamProject p : removable) {
				project.removeSeamProject(p);
			}
		}
	}

	public boolean hasToUpdateProjectDependencies() {
		List<SeamProject> ps = null;
		
		try {
			ps = getSeamProjects(project.getProject());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		if(ps != null) {
			Set<SeamProject> set = project.getSeamProjects();
			Set<SeamProject> removable = new HashSet<SeamProject>();
			removable.addAll(set);
			removable.removeAll(ps);
			ps.removeAll(set);
			for (SeamProject p : ps) {
				return true;
			}
			for (SeamProject p : removable) {
				return true;
			}
		}
		return false;
	}

	void componentsLoaded(LoadedDeclarations c, IPath path) {
		if(c == null) return;
		project.registerComponents(c, path);
	}

	List<SeamProject> getSeamProjects(IProject project) throws CoreException {
		List<SeamProject> list = new ArrayList<SeamProject>();
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < es.length; i++) {
			if(es[i].getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(es[i].getPath().lastSegment());
				if(p == null || !p.isAccessible()) continue;
				ISeamProject sp = SeamCorePlugin.getSeamProject(p, false);
				if(sp != null) list.add((SeamProject)sp);
			}
		}
		return list;
	}
	
	public void pathLoaded(IPath path) {
		String p = paths2.get(path);
		if(p != null) {
			processedPaths.add(p);
		}
	}
	
	public boolean hasPath(IPath path) {
		return paths2.get(path) != null;
	}

	public void clean() {
		paths = null;
		if(paths2 != null) paths2.clear();
		processedPaths.clear();
	}

	public void build() {
		if(update()) {
			process();
		} else if(hasToUpdateProjectDependencies()) {
			validateProjectDependencies();
		}
	}
}
