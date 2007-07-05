/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsLoader;
import org.jboss.tools.common.model.filesystems.impl.JarSystemImpl;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;

/**
 * Monitors class path of project and loads seam components of it.
 *  
 * @author Viacheslav Kabanovich
 */
public class ClassPath {
	SeamProject project;
	XModel model = null;
	ClassLoader classLoader = null;
	
	List<String> paths = null;
	
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
		IModelNature n = EclipseResourceUtil.getModelNature(project.getProject());
		model = n == null ? EclipseResourceUtil.createObjectForResource(project.getProject()).getModel() : n.getModel();
	}
	
	static String[] SYSTEM_JARS = {"rt.jar", "jsse.jar", "jce.jar", "charsets.jar"};
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
			List<String> jre = getJREClassPath(project.getProject());
			if(jre != null) newPaths.removeAll(jre);
		} catch (Exception e) {
			//TODO
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
		XModelObject object = model.getByPath("FileSystems");
		XModelObject[] fs = object.getChildren("FileSystemJar");
		Set<XModelObject> fss = new HashSet<XModelObject>();
		for (int i = 0; i < fs.length; i++) fss.add(fs[i]);
		
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			if(!path.endsWith(".jar")) continue;
			String fileName = new File(path).getName();
			if(SYSTEM_JAR_SET.contains(fileName)) continue;
			String jsname = "lib-" + fileName;
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname);
			if(o != null) {
				fss.remove(o);
			} else {
				o = object.getModel().createModelObject("FileSystemJar", null);
				o.setAttributeValue("name", jsname);
				o.setAttributeValue("location", path);
				o.set(FileSystemsLoader.IS_ADDED_TO_CLASSPATH, "true");
				object.addChild(o);
//				object.setModified(true);
			}			
		}
		
		for (XModelObject o: fss) {
			String path = XModelObjectUtil.expand(o.getAttributeValue("location"), o.getModel(), null);
			if("true".equals(o.get(FileSystemsLoader.IS_ADDED_TO_CLASSPATH))) {
				o.removeFromParent(); 
			} else if(!new File(path).exists()) {
				o.removeFromParent();
			}			
		}
		
		classLoader = null;
		
		return true;
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
			String jsname = "lib-" + fileName;
			XModelObject o = model.getByPath("FileSystems").getChildByPath(jsname);
			if(!scanner.isLikelyComponentSource(o)) continue;
			
			LoadedDeclarations c = null;
			try {
				c = scanner.parse(o, new Path(p));
			} catch (Exception e) {
				SeamCorePlugin.getDefault().logError(e);
			}
			if(c != null) componentsLoaded(c, new Path(p));
		}
	}	

	void componentsLoaded(LoadedDeclarations c, IPath path) {
		if(c == null || c.getComponents().size() + c.getFactories().size() == 0) return;
		project.registerComponents(c, path);
	}
	
	public ClassLoader getClassLoader() {
		if(classLoader != null) return classLoader;

		XModelObject object = model.getByPath("FileSystems");
		XModelObject[] fs = object.getChildren("FileSystemJar");
		
		List<URL> urls = new ArrayList<URL>();
		for (int i = 0; i < fs.length; i++) {
			JarSystemImpl jar = (JarSystemImpl)fs[i];
			String tempLocation = jar.getTempLocation();
			File f = new File(tempLocation);
			if(f.isFile()) {
				try {
					URL u = new File(tempLocation).toURL();
					if(u != null) urls.add(u);
				} catch (MalformedURLException e) {
					//ignore
				}
			}
		}
		
		classLoader = new URLClassLoader(urls.toArray(new URL[0]), Class.class.getClassLoader());
		
		return classLoader;
	}

	public static List<String> getJREClassPath(IProject project) throws Exception {
		if(!project.hasNature(JavaCore.NATURE_ID)) return null;
		ArrayList<String> l = new ArrayList<String>();
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] es0 = javaProject.getRawClasspath();
		IClasspathEntry[] es = null;
		for (int i = 0; i < es0.length && es == null; i++) {
			if(es0[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER && 
					es0[i].getPath().toString().equals("org.eclipse.jdt.launching.JRE_CONTAINER")) {
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

}
