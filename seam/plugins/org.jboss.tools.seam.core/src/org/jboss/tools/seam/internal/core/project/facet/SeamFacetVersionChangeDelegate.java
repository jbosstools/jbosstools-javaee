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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * This delegate changes Seam facet version
 * 
 * @author snjeza
 */
public class SeamFacetVersionChangeDelegate implements IDelegate {

	/**
	 * @see 
	 *      IDelegate.execute(IProject,IProjectFacetVersion,Object,IProgressMonitor
	 *      )
	 */
	public void execute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				IResource resource = project;
				PropertyDialog dialog = PropertyDialog.createDialogOn(shell, "org.jboss.tools.seam.ui.propertyPages.SeamSettingsPreferencePage", resource);
				
				if (dialog != null) {
					dialog.open();
				}
			}
			
		});
		
	}

}
