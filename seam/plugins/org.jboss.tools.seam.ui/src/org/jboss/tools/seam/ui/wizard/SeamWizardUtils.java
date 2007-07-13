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

package org.jboss.tools.seam.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * @author eskimo
 *
 */
public class SeamWizardUtils {

	/**
	 * @return
	 */
	public static String getSelectedProjectName() {
		ISelection sel  = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if(sel != null && sel instanceof IStructuredSelection) {
			IStructuredSelection structSel = (IStructuredSelection)sel;
			Object selElem = structSel.getFirstElement();
			if(selElem instanceof IAdaptable) {
				IProject project = (IProject)((IAdaptable)selElem).getAdapter(IProject.class);
				if(project!=null)return project.getName();
			}
		}
		return "";
	}

}
