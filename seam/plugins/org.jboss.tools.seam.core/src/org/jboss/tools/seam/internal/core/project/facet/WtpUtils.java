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

package org.jboss.tools.seam.internal.core.project.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
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
	
	public static IProject createEarProject(String projectName, 
													IProgressMonitor monitor) {
		IProject earProject = createEclipseProject(projectName,monitor);
		// TODO - implements this through WTP API
		return earProject;
	}
	
	public static IProject createDefaultEjbProject(String projectName,
													IProgressMonitor monitor) {
		IProject ejbProject = createEclipseProject(projectName,monitor);
		addJavaNature(ejbProject, new Path("build/classes"), 
				new Path("ejbModule"), monitor);
		// TODO - implements this through WTP API
		return ejbProject;
	}
	
	public static void addJavaNature(IProject project, 
			IPath outputLocation, IPath srcLocation, IProgressMonitor monitor) {
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

	public static String getServerRuntimeName(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			IRuntime rt = facetedProject.getPrimaryRuntime();
			if(facetedProject.getPrimaryRuntime()!=null) {
				return facetedProject.getPrimaryRuntime().getName();
			}
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return "";
	}
	
	public static IResource createSourceFolder (IProject project, IPath path) {
		IJavaProject javaProject;
		IClasspathEntry[] javaProjectEntries;
		IPath outputLocation;
		IWorkspaceRoot workspaceRoot;

		if (project == null || !project.exists()) {
			return null;
		}
		if (!project.isOpen()) {
			return null;	
		}
		try {
			if (!project.hasNature(JavaCore.NATURE_ID))
				return null;

			javaProject= JavaCore.create(project);
			javaProjectEntries= javaProject.getRawClasspath();
			outputLocation= javaProject.getOutputLocation();
			workspaceRoot= ResourcesPlugin.getWorkspace().getRoot();

			IPath projPath= javaProject.getProject().getFullPath();
			IPath newSourceFolderPath = projPath.append(path);

			IStatus validate= workspaceRoot.getWorkspace().validatePath(newSourceFolderPath.toString(), IResource.FOLDER);
			if (validate.matches(IStatus.ERROR))
				return null;

			IResource res= workspaceRoot.findMember(newSourceFolderPath);
			if (res != null) {
				if (res.getType() != IResource.FOLDER) {
					return null;
				}
			} else {
				URI projLocation= javaProject.getProject().getLocationURI();
				if (projLocation != null) {
					try {
						IFileStore store= EFS.getStore(projLocation).getChild(path.toString());
						if (store.fetchInfo().exists()) {
							return null;
						}
					} catch (CoreException e) {
						// Ignore if we cannot check that the file exists.
						// Assume that it doesn't 
					}
				}
			}
			ArrayList newEntries= new ArrayList(javaProjectEntries.length + 1);
			int projectEntryIndex= -1;
			
			for (int i= 0; i < javaProjectEntries.length; i++) {
				IClasspathEntry curr= javaProjectEntries[i];
				if (curr.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					if (newSourceFolderPath.equals(curr.getPath())) {
						return null;
					}
					if (projPath.equals(curr.getPath())) {
						projectEntryIndex= i;
					}	
				}
				newEntries.add(curr);
			}
			
			IClasspathEntry newEntry= JavaCore.newSourceEntry(newSourceFolderPath);
			if (projectEntryIndex != -1) {
				newEntries.set(projectEntryIndex, newEntry);
			} else {
				IClasspathEntry entry= JavaCore.newSourceEntry(newSourceFolderPath);
				insertClasspathEntry(entry, newEntries);
			}

			IClasspathEntry[] newClasspathEntries= (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
			IPath newOutputLocation= outputLocation;

			IJavaModelStatus result= JavaConventions.validateClasspath(javaProject, newClasspathEntries, newOutputLocation);
			if (!result.isOK()) {
				if (outputLocation.equals(projPath)) {
					newOutputLocation= projPath.append(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME));
					result= JavaConventions.validateClasspath(javaProject, newClasspathEntries, newOutputLocation);
					if (!result.isOK()) {
						return null;
					}
				} else {
					return null;
				}
			}
			
			IFolder newSourceFolder= javaProject.getProject().getFolder(newSourceFolderPath);
			if (!newSourceFolder.exists()) {
				CoreUtility.createFolder(newSourceFolder, true, true, null);			
			}
			
			javaProject.setRawClasspath(newClasspathEntries, newOutputLocation, new NullProgressMonitor());
	
			return newSourceFolder;
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return null;
	}
	
	static private void insertClasspathEntry(IClasspathEntry entry, List entries) {
		int length= entries.size();
		IClasspathEntry[] elements= (IClasspathEntry[])entries.toArray(new IClasspathEntry[length]);
		int i= 0;
		while (i < length && elements[i].getEntryKind() != entry.getEntryKind()) {
			i++;
		}
		if (i < length) {
			i++;
			while (i < length && elements[i].getEntryKind() == entry.getEntryKind()) {
				i++;
			}
			entries.add(i, entry);
			return;
		}
		
		switch (entry.getEntryKind()) {
		case IClasspathEntry.CPE_SOURCE:
			entries.add(0, entry);
			break;
		case IClasspathEntry.CPE_CONTAINER:
		case IClasspathEntry.CPE_LIBRARY:
		case IClasspathEntry.CPE_PROJECT:
		case IClasspathEntry.CPE_VARIABLE:
		default:
			entries.add(entry);
			break;
		}
	}

}
