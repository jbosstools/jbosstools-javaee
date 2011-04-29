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
package org.jboss.tools.jsf.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2Util {

	/**
	 * Returns true if either project has in class path jsf-api.jar with implementation version 2
	 * or it is faceted project with jst.jsf facet of version 2.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static boolean isJSF2(IProject project) throws CoreException {
		int v = getJSFImplementationVersion(project);
		return (v > 1) || isJSF2FacetedProject(project);
	}

	/**
	 * Returns implementation version of 'jsf-api.jar' if it is included into class path;
	 * returns -1 if 'jsf-api.jar' is not found in the class path.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static int getJSFImplementationVersion(IProject project) throws CoreException {
		try {
			String content = readManifest(project, "jsf-api.jar");
			if(content != null) {
				String attr = "Implementation-Version";
				int i = content.indexOf(attr);
				if(i < 0) return -1;
				i += attr.length();
				for (int j = i; j < content.length(); j++) {
					char ch = content.charAt(j);
					if(Character.isDigit(ch)) {
						return (int)ch - (int)'0';
					}
				}
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, JSFModelPlugin.PLUGIN_ID, "Failed to read manifest in jsf-api.jar in project " + project.getName(), e));
		}
		return -1;
	}

	/**
	 * Returns text content of META-INF/MANIFEST.MF of selected jar file;
	 * returns null if jar file is not found in the class path.
	 * 
	 * @param project
	 * @param jarName
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public static String readManifest(IProject project, String jarName) throws CoreException, IOException {
		IPackageFragmentRoot library = findLibrary(project, jarName);
		if(library instanceof JarPackageFragmentRoot) {
			ZipFile zip = ((JarPackageFragmentRoot)library).getJar();
			try {
				ZipEntry entry = zip.getEntry("META-INF/MANIFEST.MF");
				if(entry != null) {
					InputStream is = zip.getInputStream(entry);
					if(is != null) {
						return FileUtil.readStream(is);
					}
				}
			} finally {
				zip.close();
			}
		}
		return null;
	}

	/**
	 * Returns root object of Java Model for selected jar file;
	 * returns null if jar file is not found in the class path.	 * 
	 * 
	 * @param project
	 * @param jarName
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot findLibrary(IProject project, String jarName) throws JavaModelException {
		if(project == null || !project.isAccessible()) {
			return null;
		}
		IJavaProject javaProject = JavaCore.create(project);
		if(javaProject == null || !javaProject.exists()) {
			return null;
		}
		for (IPackageFragmentRoot fragmentRoot : javaProject.getAllPackageFragmentRoots()) {
			IPath resource = fragmentRoot.getPath();
			if(resource != null && resource.lastSegment().equals(jarName)) {
				return fragmentRoot;
			}
		}
		return null;
	}

	/**
	 * Returns true if selected project is a faceted project with jst.jsf fact of version 2.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static boolean isJSF2FacetedProject(IProject project) throws CoreException {
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet("jst.jsf");
		IFacetedProject fp = ProjectFacetsManager.create(project);
		if(fp != null) {
			IProjectFacetVersion v = fp.getProjectFacetVersion(facet);
			if(v != null) {
				String vs = v.getVersionString();
				return vs != null && vs.startsWith("2.");
			}
		}
		return false;
	}

}
