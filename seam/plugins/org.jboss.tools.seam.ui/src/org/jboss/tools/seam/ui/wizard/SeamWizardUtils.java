/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author eskimo,max
 * 
 */
public class SeamWizardUtils {

	/**
	 * @return current root seam project name based on the current selection;
	 *         name of currently selected project if it is not a seam project - 
	 *         in this case it is up to wizard to show what is the problem with the selection;
	 *         empty string if no project is selected
	 */
	public static String getCurrentSelectedRootSeamProjectName() {
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().getSelection();
		return getRootSeamProjectName(sel);
	}

	/**
	 * 
	 * @return
	 */
	public static IProject getCurrentSelectedRootSeamProject() {
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().getSelection();
		return getRootSeamProject(sel);
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	public static IProject getRootSeamProject(IProject project) {
		if (project != null) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
			if (seamProject == null && EclipseResourceUtil.getJavaProject(project)==null) {
				// Maybe it is EAR? Then it doesn't have Seam nature and we should try to find child Seam WAR project.
				ISeamProject warProject = SeamUtil.findReferencingSeamWarProjectForProject(project);
				return warProject!=null?warProject.getProject():null;
			}
			if(seamProject == null) {
				return null;
			}

			String parentProjectName = seamProject.getParentProjectName();

			IProject targetProject = null;
			if (parentProjectName == null || parentProjectName.trim().length()==0) {
				targetProject = project;
			} else {
				targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(parentProjectName);
			}

			if(targetProject.exists()) {
				if("".equals(SeamCorePlugin.getSeamPreferences(targetProject).get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ""))) {
					return null;
				} else {
					return targetProject;
				}
			}
		}
		return null;
	}

	/**
	 * @param sel
	 * @return root seam project name based on the passed selection;
	 *         name of project if selection contains a project which is not a seam project - 
	 *         in this case it is up to wizard to show what is the problem with the selection;
	 *         empty string if selection contains no project
	 */
	public static String getRootSeamProjectName(ISelection sel) {
		IProject project = getRootSeamProject(sel);
		return project == null ? "" : project.getName();
	}

	/**
	 * @param sel
	 * @return project if selection contains a project which is not a seam project - 
	 *         in this case it is up to wizard to show what is the problem with the selection;
	 *         null if selection contains no project
	 */
	public static IProject getRootSeamProject(ISelection sel) {
		IProject initial = getInitialProject(sel);
		IProject project = getRootSeamProject(initial);
		if(project == null) {
			project = initial;
		}
		return project == null ? null : project;
	}

	/**
	 * 
	 * @param simpleSelection
	 * @return
	 */
	public static IProject getInitialProject(ISelection simpleSelection) {
		IProject project = null;
		if (simpleSelection != null && !simpleSelection.isEmpty()
				&& simpleSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) simpleSelection;
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				IResource resource = (IResource) adaptable
						.getAdapter(IResource.class);
				if(resource!=null) {
					project = resource.getProject();
				}
			}
		}

		if(project==null) {
			IEditorPart activeEditor = getActivePage().getActiveEditor();
			if(activeEditor!=null) {
				IEditorInput input = activeEditor.getEditorInput();
				if(input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
		         return fileInput.getFile().getProject();		         
				}
			}
		}
		return project;
	}

	private static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window= getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	private static IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }
}