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

package org.jboss.tools.seam.internal.core.project.facet;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathContainer;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author eskimo
 *
 */
public class WtpUtils {
	public static IProject createEclipseProject(String projectName, IProgressMonitor monitor) {

        IProject newProjectHandle = ResourcesPlugin.getWorkspace()
                .getRoot().getProject(projectName);

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace
                .newProjectDescription(projectName);

        String eclWsPath = ResourcesPlugin.getWorkspace().getRoot()
                .getRawLocation().toString();
        try {
			newProjectHandle.create(description,new NullProgressMonitor());
			newProjectHandle.open(monitor);

		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return newProjectHandle;
	}
	
	public static IProject createEarProject(String projectName, IProgressMonitor monitor) {
		IProject earProject = createEclipseProject(projectName,monitor);
		return null;
	}
	
	public static IProject createDefaultEjbProject(String projectName,IProgressMonitor monitor) {
		IProject ejbProject = createEclipseProject(projectName,monitor);
		//J2EEComponentClasspathContainer.CONTAINER_ID;
		addJavaNature(ejbProject, new Path("build/classes"), new Path("ejbModule"), monitor);
		return null;
	}
	
	public static void addJavaNature(IProject project, IPath outputLocation, IPath srcLocation, IProgressMonitor monitor) {
		try {
			IProjectDescription newDescr = project.getDescription();
			newDescr.setNatureIds(new String[] {JavaCore.NATURE_ID});
			ICommand builderCmd = project.getDescription().newCommand();
			builderCmd.setBuilderName(JavaCore.BUILDER_ID);
			newDescr.setBuildSpec(new ICommand[] {builderCmd});
			project.setDescription(newDescr, monitor);
			IJavaProject newJavaPr = JavaCore.create(project);
			project.getFolder(outputLocation).create(IFolder.FORCE, true, monitor);
			project.getFolder(srcLocation).create(IFolder.FORCE, true, monitor);
			
			newJavaPr.setRawClasspath(
					new IClasspathEntry[]{
							JavaCore.newSourceEntry(Path.ROOT.append(srcLocation)),
							JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER))}, 
					monitor);
			newJavaPr.setOutputLocation(outputLocation, monitor);
			newJavaPr.save(monitor, true);
		} catch (JavaModelException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	public static String getServerRuntimename(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);

		if(javaProject!=null) {
			try {
				IFacetedProject facetedProject = ProjectFacetsManager.create(project);
				return facetedProject.getPrimaryRuntime().getName();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}
