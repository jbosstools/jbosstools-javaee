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
package org.jboss.tools.seam.internal.core.project.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * 
 * @author eskimo
 *
 */
public abstract class SeamFacetAbstractInstallDelegate implements ILogListener, 
										IDelegate,ISeamFacetDataModelProperties {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		try {
			// TODO find a better way to handle exceptions for creating seam projects
			// now here is the simple way that allows keep most of seam classes
			// untouched, this abstract class just listen to eclipse log and show an
			// error dialog if there were records logged from seam.core plugin
			startListening();
			doExecute(project,fv,config,monitor);		
		} finally {
			stopListening();
		}
		if(errorOccurs) {
			Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						ErrorDialog.openError(Display.getCurrent().getActiveShell(), 
								SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_ERROR,
								SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_CHECK_ERROR_LOG_VIEW,
								new Status(IStatus.ERROR,SeamCorePlugin.PLUGIN_ID,
										SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_ERRORS_OCCURED));
					}
				});
		}
	}

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param config
	 * @param monitor
	 * @throws CoreException
	 */
	protected abstract void doExecute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * 
	 */
	private void stopListening() {
		SeamCorePlugin.getDefault().getLog().removeLogListener(this);
	}

	/**
	 * 
	 */
	private void startListening() {
		SeamCorePlugin.getDefault().getLog().addLogListener(this);
	}

	private boolean errorOccurs = false;
	
	private boolean hasErrors() {
		return errorOccurs;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void logging(IStatus status, String plugin) {
		if(status.getPlugin().equals(SeamCorePlugin.PLUGIN_ID)) {
			errorOccurs = true; 
		}
	}
}
