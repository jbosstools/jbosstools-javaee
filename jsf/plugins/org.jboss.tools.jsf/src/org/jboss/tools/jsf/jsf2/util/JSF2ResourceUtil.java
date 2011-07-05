/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.util;

import java.io.File;
import java.util.zip.ZipEntry;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarEntryDirectory;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarEntryResource;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalHelper;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2TemplateManager;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ResourceUtil {

	public static final String JSF2_URI_PREFIX = "http://java.sun.com/jsf/composite"; //$NON-NLS-1$

	public static final String COMPONENT_RESOURCE_PATH_KEY = "component_resource_path_key"; //$NON-NLS-1$

	public static final String JSF2_COMPONENT_NAME = "jsf2_resource_name"; //$NON-NLS-1$
	
	public static final int JAR_FILE_RESOURCE_TYPE = 1;

	public static final int JAR_DIRECTORY_RESOURCE_TYPE = JAR_FILE_RESOURCE_TYPE << 1;

	public static Object findCompositeComponentContainer(IProject project,
			IDOMElement jsf2Element) {
		ElementImpl elementImpl = (ElementImpl) jsf2Element;
		String nameSpaceURI = elementImpl.getNamespaceURI();
		if (nameSpaceURI == null || nameSpaceURI.indexOf(JSF2_URI_PREFIX) == -1) {
			return null;
		}
		String nodeName = jsf2Element.getLocalName();
		String relativeLocation = "/resources" + nameSpaceURI.replaceFirst( //$NON-NLS-1$
				JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer[] folders = webRootFolder.getUnderlyingFolders();
			for (IContainer folder: folders) {
				IPath path = folder.getFullPath().append(relativeLocation).append(
					"/" + nodeName + ".xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file.isAccessible()) {
					return file;
				}
			}
		}
		String classPathResource = "META-INF" + relativeLocation //$NON-NLS-1$
					+ "/" + nodeName + ".xhtml"; //$NON-NLS-1$ //$NON-NLS-2$
		JarEntryResource jer = searchInClassPath(project, classPathResource, JAR_FILE_RESOURCE_TYPE);
		if(jer != null) {
			return jer;
		}
		IResource r = searchInClassPath2(project, classPathResource, JAR_FILE_RESOURCE_TYPE);
		if(r != null) {
			return r;
		}
		return null;
	}

	private static JarEntryResource searchInClassPath(IProject project,
			String classPathResource, int jarResourceType) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for (IPackageFragmentRoot fragmentRoot : javaProject
					.getAllPackageFragmentRoots()) {
				if (fragmentRoot instanceof JarPackageFragmentRoot) {
					JarPackageFragmentRoot jarPackageFragmentRoot = (JarPackageFragmentRoot) fragmentRoot;
					ZipEntry zipEntry = jarPackageFragmentRoot.getJar()
							.getEntry(classPathResource);
					if (zipEntry != null) {
						if (jarResourceType == JAR_FILE_RESOURCE_TYPE) {
							JarEntryFile fileInJar = new JarEntryFile(
									classPathResource);
							fileInJar.setParent(jarPackageFragmentRoot);
							return fileInJar;
						}
						if (jarResourceType == JAR_DIRECTORY_RESOURCE_TYPE) {
							JarEntryDirectory directoryInJar = new JarEntryDirectory(
									classPathResource);
							directoryInJar.setParent(jarPackageFragmentRoot);
							return directoryInJar;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return null;
	}

	private static IResource searchInClassPath2(IProject project,
			String classPathResource, int jarResourceType) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for (IPackageFragmentRoot fragmentRoot : javaProject
					.getAllPackageFragmentRoots()) {
				IResource r = fragmentRoot.getResource();
				if(fragmentRoot instanceof ExternalPackageFragmentRoot) {
					r = ((ExternalPackageFragmentRoot) fragmentRoot).resource();
				}
				if(r instanceof IFolder && r.exists()) {
					IFolder f = (IFolder)r;
					IFile f1 = f.getFile(classPathResource);
					if(f1.exists()) {
						return f1;
					}
					IFolder f2 = f.getFolder(classPathResource);
					if(f2.exists()) {
						return f2;
					}
				}				
			}
		} catch (JavaModelException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return null;
	}

	public static Object findResourcesFolderContainerByNameSpace(
			IProject project, String nameSpaceURI) {
		if (nameSpaceURI == null || nameSpaceURI.indexOf(JSF2_URI_PREFIX) == -1) {
			return null;
		}
		String relativeLocation = "/resources" + nameSpaceURI.replaceFirst( //$NON-NLS-1$
				JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer[] folders = webRootFolder.getUnderlyingFolders();
			for (IContainer folder: folders) {
				IPath path = folder.getFullPath().append(relativeLocation);
				IFolder resFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(path);
				if (resFolder.isAccessible()) {
					return resFolder;
				}
			}
		}
		String classPathResource = "META-INF" + relativeLocation; //$NON-NLS-1$
		JarEntryResource jer = searchInClassPath(project, classPathResource, JAR_DIRECTORY_RESOURCE_TYPE);
		if(jer != null) {
			return jer;
		}
		IResource r = searchInClassPath2(project, classPathResource, JAR_DIRECTORY_RESOURCE_TYPE);
		if(r != null) {
			return r;
		}
		return null;
	}

	public static boolean isResourcesFolderExists(IProject project,
			String nameSpaceURI) {
		return findResourcesFolderContainerByNameSpace(project, nameSpaceURI) == null ? false
				: true;
	}

	public static IFolder createResourcesFolderByNameSpace(IProject project,
			String nameSpaceURI) throws CoreException {
		IFolder compositeCompResFolder = null;
		String relativeLocation = nameSpaceURI.replaceFirst(
				JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
		if (!project.exists()) {
			return null;
		}
		if (!project.isAccessible()) {
			try {
				project.open(new NullProgressMonitor());
			} catch (CoreException e) {
				JSFModelPlugin.getPluginLog().logError(e);
				return compositeCompResFolder;
			}
		}
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer folder = webRootFolder.getUnderlyingFolder();
			IFolder webFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folder.getFullPath());
			IFolder resourcesFolder = webFolder.getFolder("resources"); //$NON-NLS-1$
			NullProgressMonitor monitor = new NullProgressMonitor();
			if (!resourcesFolder.exists()) {
				resourcesFolder.create(true, true, monitor);
			}
			String[] segments = new Path(relativeLocation).segments();
			compositeCompResFolder = resourcesFolder;
			for (int i = 0; i < segments.length; i++) {
				compositeCompResFolder = compositeCompResFolder
						.getFolder(segments[i]);
				if (!compositeCompResFolder.exists()) {
					compositeCompResFolder.create(true, true, monitor);
				}
			}

		}
		return compositeCompResFolder;
	}

	public static IFile createCompositeComponentFile(IProject project,
			IPath resourceRelativePath) throws CoreException {
		IFile compositeCompResFile = null;
		if (!project.exists()) {
			return null;
		}
		if (!project.isAccessible()) {
			try {
				project.open(new NullProgressMonitor());
			} catch (CoreException e) {
				JSFModelPlugin.getPluginLog().logError(e);
				return compositeCompResFile;
			}
		}
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer folder = webRootFolder.getUnderlyingFolder();
			IFolder webFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folder.getFullPath());
			IFolder resourcesFolder = webFolder.getFolder("resources"); //$NON-NLS-1$
			NullProgressMonitor monitor = new NullProgressMonitor();
			if (!resourcesFolder.exists()) {
				resourcesFolder.create(true, true, monitor);
			}
			String[] segments = resourceRelativePath.segments();
			IFolder componentPathFolder = resourcesFolder;
			for (int i = 0; i < segments.length - 1; i++) {
				componentPathFolder = componentPathFolder
						.getFolder(segments[i]);
				if (!componentPathFolder.exists()) {
					componentPathFolder.create(true, true, monitor);
				}
			}
			compositeCompResFile = componentPathFolder
					.getFile(segments[segments.length - 1]);
			if (!compositeCompResFile.exists()) {
				compositeCompResFile.create(JSF2TemplateManager.getManager()
						.createStreamFromTemplate("composite.xhtml"), true, //$NON-NLS-1$
						monitor);
			} else {
				compositeCompResFile = JSF2ComponentModelManager.getManager()
						.revalidateCompositeComponentFile(compositeCompResFile);
			}
		}
		return compositeCompResFile;
	}

	public static IFile createCompositeComponentFile(IProject project,
			IPath resourceRelativePath, String[] attrNames)
			throws CoreException {
		IFile jsf2ResFile = createCompositeComponentFile(project,
				resourceRelativePath);
		if (jsf2ResFile == null) {
			return null;
		}
		if (attrNames == null || attrNames.length == 0) {
			return jsf2ResFile;
		}
		return JSF2ComponentModelManager.getManager()
				.updateJSF2CompositeComponentFile(jsf2ResFile, attrNames);
	}
	/**
	 * Calculates workspace relative jsf2 resources string
	 * @return workspace relative resource string
	 * @author mareshkau
	 */
	
	public static String calculateProjectRelativeJSF2ResourceProposal( IProject project){
		IVirtualComponent component = ComponentCore.createComponent(project);
		String projectResourceRelativePath = "";
		if (component != null) {
			IVirtualFolder webRootFolder = component.getRootFolder().getFolder(
					new Path("/")); //$NON-NLS-1$
			IContainer folder = webRootFolder.getUnderlyingFolder();
			IFolder webFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folder.getFullPath());
			IFolder resourcesFolder = webFolder.getFolder("resources");
			resourcesFolder.getProjectRelativePath().toString();
			projectResourceRelativePath=project.getName()+File.separator+resourcesFolder.getProjectRelativePath().toString();
		}
		return projectResourceRelativePath;
	}
	/**
	 * Get validating resource
	 * @param helper
	 * @return IResource on which validator works
	 */
	public static IResource getValidatingResource(IValidationContext helper){
		IResource resource=null;
		if (helper instanceof IncrementalHelper) {
			IncrementalHelper incrementalHelper = (IncrementalHelper) helper;
			IProject project = incrementalHelper.getProject();
			if (project == null) {
				return resource;
			}
			String[] uris = helper.getURIs();
			if (uris == null || uris.length < 1) {
				return resource;
			}
			String filePath = uris[0];
			if (filePath == null) {
				return resource;
			}
			filePath = filePath.substring(filePath.indexOf('/') + 1);
			resource = project.findMember(filePath
					.substring(filePath.indexOf('/') + 1));
		}
		return resource;
	}
}
