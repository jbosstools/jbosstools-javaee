/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;

/**
 * @author Alexey Kazakov
 */
public class SeamUtil {

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime which is set for the project.
	 * @param project
	 * @return
	 */
	public static String getSeamVersionFromManifest(IProject project) {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		if(seamProject == null) {
			SeamCorePlugin.getPluginLog().logWarning("Can't find Seam Project for " + project.getName());
			return null;
		}
		return getSeamVersionFromManifest(seamProject);
	}

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime which is set for the project.
	 * @param project
	 * @return
	 */
	public static String getSeamVersionFromManifest(ISeamProject project) {
		SeamRuntime runtime = project.getRuntime();
		if(runtime==null) {
			SeamCorePlugin.getPluginLog().logWarning("Seam Runtime for " + project.getProject().getName() + " is null.");
			return null;
		}
		return getSeamVersionFromManifest(runtime);
	}

	private final static String seamJarName = "jboss-seam.jar";
	private final static String seamVersionAttributeName = "Seam-Version";

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime.
	 * @param runtime
	 * @return
	 */
	public static String getSeamVersionFromManifest(SeamRuntime runtime) {
		File jarFile = new File(runtime.getLibDir(), seamJarName);
		if(!jarFile.isFile()) {
			jarFile = new File(runtime.getHomeDir(), seamJarName);
			if(!jarFile.isFile()) {
				SeamCorePlugin.getPluginLog().logWarning(jarFile.getAbsolutePath() + " as well as " + new File(runtime.getLibDir(), seamJarName).getAbsolutePath() + " don't exist for Seam Runtime " + runtime.getName());
				return null;
			}
		}
		try {
			JarFile jar = new JarFile(jarFile);
			Attributes attributes = jar.getManifest().getMainAttributes();
			String version = attributes.getValue(seamVersionAttributeName);
			if(version==null) {
				SeamCorePlugin.getPluginLog().logWarning("Can't get Seam-Version from " + jar.getName() + " MANIFEST.");
			}
			return version;
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return null;
		}
	}
	
	/**
	 * Converts seam project name to string which suitable for package names
	 * @param projectNamePackage
	 * @return
	 */
	public static String getSeamPackageName(String projectName){
		if(projectName == null)
			return null;
		
		String packageName = projectName.toLowerCase();
		
		if(packageName.indexOf(" ") >= 0)
			packageName = packageName.replaceAll(" ", "");
		
		if(packageName.indexOf("-") >= 0)
			packageName = packageName.replaceAll("-", "");
			
		if(packageName.indexOf("+") >= 0)
			packageName = packageName.replaceAll("+", "");
			
		if(packageName.indexOf("_") >= 0)
			packageName = packageName.replaceAll("_", "");
			
		while(packageName.indexOf("..") >= 0){
			packageName = packageName.replace("..", ".");
		}
		return packageName;
	}

	/**
	 * Finds referencing Seam war project
	 * @param project
	 * @return
	 */
	public static ISeamProject findReferencingSeamWarProjectForProject(IProject project) {
		return findReferencingSeamWarProjectForProject(project, true);
	}

	/**
	 * Finds referencing Seam war project
	 * @param project
	 * @param searchInEARs if "true" then try to search web projects in parent EAR project.
	 * @return
	 */
	public static ISeamProject findReferencingSeamWarProjectForProject(IProject project, boolean searchInEARs) {
		IProject[] referencing = J2EEProjectUtilities.getReferencingWebProjects(project);
		for (int i = 0; i < referencing.length; i++) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(referencing[i], false);
			if(seamProject!=null) {
				return seamProject;
			}
		}
		if(searchInEARs) {
			referencing = J2EEProjectUtilities.getReferencingEARProjects(project);
			for (int i = 0; i < referencing.length; i++) {
				ISeamProject seamProject = findReferencingSeamWarProjectForProject(referencing[i], false);
				if(seamProject!=null) {
					return seamProject;
				}
			}
			IVirtualComponent comp = ComponentCore.createComponent(project);
			IVirtualReference[] refComponents = null;
			if(comp!=null) {
				refComponents = comp.getReferences();
				for (IVirtualReference virtualReference : refComponents) {
					IVirtualComponent component = virtualReference.getReferencedComponent();
					if(component!=null && !component.isBinary() && JavaEEProjectUtilities.isDynamicWebComponent(component)) {
						ISeamProject seamProject = SeamCorePlugin.getSeamProject(component.getProject(), false);
						if(seamProject!=null) {
							return seamProject;
						}
					}
				}
			}
		}
		return null;
	}
}