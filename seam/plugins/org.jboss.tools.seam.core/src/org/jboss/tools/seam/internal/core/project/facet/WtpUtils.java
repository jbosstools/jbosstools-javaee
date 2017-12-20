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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.OpenableElementInfo;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
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
		addJavaNature(ejbProject, new Path("build/classes"),  //$NON-NLS-1$
				new Path("ejbModule"), monitor); //$NON-NLS-1$
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
			if(rt != null) {
				return rt.getName();
			}
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return ""; //$NON-NLS-1$
	}
	
	public static IResource createSourceFolder (IProject project, IPath path, IPath exclude, IPath outputFolder) {
		IJavaProject javaProject;
		IClasspathEntry[] javaProjectEntries;
		IPath outputLocation;

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

			IPath projPath= javaProject.getProject().getFullPath();
			IPath newSourceFolderPath = projPath.append(path);
			IPath excludeSourceFolderPath = projPath.append(exclude);

			ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(javaProjectEntries.length + 1);
			int projectEntryIndex= -1;
			
			for (int i= 0; i < javaProjectEntries.length; i++) {
				IClasspathEntry curr= javaProjectEntries[i];
				IClasspathEntry resolved = curr;
				if(resolved.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
					try {
						resolved = JavaCore.getResolvedClasspathEntry(resolved);
					} catch (AssertionFailedException e) {
						continue;
					}					
				}
				if (resolved.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					if (newSourceFolderPath.equals(resolved.getPath())) {
						return null;
					}
					if (projPath.equals(resolved.getPath())) {
						projectEntryIndex= i;
					}
					if (excludeSourceFolderPath.equals(resolved.getPath())) {
						continue;
					}
				}
				newEntries.add(curr);
			}
			if(outputFolder != null) {
				CoreUtility.createDerivedFolder(project.getFolder(outputFolder), true, true, new NullProgressMonitor());
			}
			IFolder newSourceFolder= javaProject.getProject().getFolder(path);
			if (!newSourceFolder.exists()) {
				CoreUtility.createFolder(newSourceFolder, true, true, new NullProgressMonitor()); 			
			}
			
			IClasspathEntry newEntry = JavaCore.newSourceEntry(newSourceFolderPath,new Path[]{},new Path[]{},outputFolder!=null?project.getFullPath().append(outputFolder):null);
			
			if (projectEntryIndex != -1) {
				newEntries.set(projectEntryIndex, newEntry);
			} else {
				insertClasspathEntry(newEntry, newEntries);
			}

			IClasspathEntry[] newClasspathEntries = newEntries.toArray(new IClasspathEntry[newEntries.size()]);
			IPath newOutputLocation = outputLocation;

			IJavaModelStatus result = JavaConventions.validateClasspath(javaProject, newClasspathEntries, newOutputLocation);
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
			
			javaProject.setRawClasspath(newClasspathEntries, newOutputLocation, new NullProgressMonitor());
			return newSourceFolder;
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return null;
	}
	
	static private void insertClasspathEntry(IClasspathEntry entry, List<IClasspathEntry> entries) {
		int length= entries.size();
		IClasspathEntry[] elements = entries.toArray(new IClasspathEntry[length]);
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
	
	/**
	 * @param project
	 * @param monitor
	 * @throws JavaModelException
	 */
	public static void setClasspathEntryAsExported(final IProject project,IPath path,
			IProgressMonitor monitor) throws JavaModelException {
		IJavaProject jProject = JavaCore.create(project);
		IClasspathEntry[] cps = jProject.getRawClasspath();
		for (int i=0;i<cps.length;i++) {
			if(cps[i].getEntryKind()==IClasspathEntry.CPE_CONTAINER 
					&& cps[i].getPath().equals(path)) {
				cps[i]=JavaCore.newContainerEntry(cps[i].getPath(),true);
			}
		}
		jProject.setRawClasspath(cps, monitor);
	}

	public static void reconfigure(IProject project, IProgressMonitor monitor) throws CoreException {
		if (project == null || !project.exists() || !project.isOpen() || !project.hasNature(JavaCore.NATURE_ID)) {
			return;
		}
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		IJavaProject javaProject = JavaCore.create(project);
		if (javaProject != null && javaProject.exists() && javaProject.isOpen() && javaProject instanceof JavaProject) {
			Object object = ((JavaProject) javaProject).getElementInfo();
			if (object instanceof OpenableElementInfo) {
				// copied from JavaProject.buildStructure(...)
				OpenableElementInfo info = (OpenableElementInfo) object;
				IClasspathEntry[] resolvedClasspath = ((JavaProject) javaProject).getResolvedClasspath();
				IPackageFragmentRoot[] children = ((JavaProject) javaProject).computePackageFragmentRoots(resolvedClasspath,false, false, null /* no reverse map */);
				info.setChildren(children);
				((JavaProject) javaProject).getPerProjectInfo().rememberExternalLibTimestamps();
			}
		}
	}

}
