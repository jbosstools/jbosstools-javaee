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
package org.jboss.tools.seam.ui.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamCorePlugin;

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
	public void execute(final IProject project, final IProjectFacetVersion fv,
			final Object config, final IProgressMonitor monitor) throws CoreException {
		
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				try {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					//Shell shell = null;
					IFacetedProject fproj = ProjectFacetsManager
							.create(project);
					SeamFacetVersionChangeDialog dialog = new SeamFacetVersionChangeDialog(
							shell, fproj, fv);
					dialog.open();
				} catch (CoreException e) {
					log(e);
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}

		});
	}

	private static void log(Throwable e) {
		IStatus status = new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, e
				.getLocalizedMessage(), e);
		SeamCorePlugin.getDefault().getLog().log(status);
	}
}
