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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author eskimo,max
 * 
 */
public class SeamWizardUtils {

	/**
	 * @return current root seam project name based on the current selection;
	 *         empty string if there is no seam project to be found
	 */
	public static String getCurrentSelectedRootSeamProjectName() {
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().getSelection();
		return getRootSeamProjectName(sel);
	}

	public static String getRootSeamProjectName(ISelection sel) {
		IProject project = getInitialProject(sel);
		if (project != null) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project,
					false);
			if (seamProject == null) {
				return "";
			}
			
			
			String parentProjectName = seamProject.getParentProjectName();
						
			IProject targetProject = null;
			if (parentProjectName == null) {
				targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());				
			} else {
				targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(parentProjectName);
			}
			
			if(targetProject.exists()) {
				if("".equals(SeamCorePlugin.getSeamPreferences(targetProject).get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ""))) {
					return "";
				} else {
					return targetProject.getName();
				}
			}
		}
		return "";
	}

	static private IProject getInitialProject(ISelection simpleSelection) {

		IProject project = null;
		if (simpleSelection != null && !simpleSelection.isEmpty()
				&& simpleSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) simpleSelection;
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				IResource resource = (IResource) adaptable
						.getAdapter(IResource.class);
				return resource.getProject();				
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
