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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

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
		return null;
	}

	public void logging(IStatus status, String plugin) {
		if(status.getPlugin().equals(SeamCorePlugin.PLUGIN_ID)) {
			errorOccurs = true; 
		}
	}

	/**
	 * @param project
	 * @param model
	 */
	protected void createSeamProjectPreferenes(final IProject project,
			final IDataModel model) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);

		prefs.put(JBOSS_AS_DEPLOY_AS, model.getProperty(JBOSS_AS_DEPLOY_AS).toString());
		prefs.put(SEAM_SETTINGS_VERSION, SEAM_SETTINGS_VERSION_1_1);
		prefs.put(SEAM_RUNTIME_NAME, model.getProperty(SEAM_RUNTIME_NAME).toString());
		prefs.put(SEAM_CONNECTION_PROFILE, model.getProperty(SEAM_CONNECTION_PROFILE).toString());
		prefs.put(SESSION_BEAN_PACKAGE_NAME, model.getProperty(SESSION_BEAN_PACKAGE_NAME).toString());
		prefs.put(ENTITY_BEAN_PACKAGE_NAME, model.getProperty(ENTITY_BEAN_PACKAGE_NAME).toString());
		prefs.put(TEST_CASES_PACKAGE_NAME, model.getProperty(TEST_CASES_PACKAGE_NAME).toString());
		prefs.put(TEST_CREATING, "true");

		String testSrcPath = project.getFullPath().removeLastSegments(1).append(project.getName() + "-test").append("test-src").toString();
		prefs.put(TEST_SOURCE_FOLDER, testSrcPath);

		prefs.put(SEAM_TEST_PROJECT, 
				model.getProperty(SEAM_TEST_PROJECT)==null?
						"":model.getProperty(SEAM_TEST_PROJECT).toString()); //$NON-NLS-1$

		if(DEPLOY_AS_EAR.equals(model.getProperty(JBOSS_AS_DEPLOY_AS))) {
			prefs.put(SEAM_EJB_PROJECT, 
					model.getProperty(SEAM_EJB_PROJECT)==null? 
						"":model.getProperty(SEAM_EJB_PROJECT).toString()); //$NON-NLS-1$

			prefs.put(SEAM_EAR_PROJECT, 
					model.getProperty(SEAM_EAR_PROJECT)==null? 
						"":model.getProperty(SEAM_EAR_PROJECT).toString()); //$NON-NLS-1$

			String srcPath = project.getFullPath().removeLastSegments(1).append(project.getName() + "-ejb").append("ejbModule").toString();
			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcPath);
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcPath);
		} else {
			IVirtualComponent component = ComponentCore.createComponent(project);
			IPath srcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/classes")).getUnderlyingFolder().getParent().getFullPath(); //$NON-NLS-1$

			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcRootFolder.append("model").toString());
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcRootFolder.append("action").toString());
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}
}