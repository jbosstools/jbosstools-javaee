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

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.server.core.IServer;
import org.jboss.tools.jst.web.server.RegistrationHelper;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author eskimo
 *
 */
public class SeamFacetPostInstallDelegate implements
		IDelegate, ISeamFacetDataModelProperties{

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IEclipsePreferences prefs = SeamCorePlugin.getSeamPreferences(project);
		final IDataModel model = (IDataModel)config;
		
		prefs.put(JBOSS_AS_DEPLOY_AS, model.getProperty(JBOSS_AS_DEPLOY_AS).toString());
		
		prefs.put(SEAM_RUNTIME_NAME, model.getProperty(SEAM_RUNTIME_NAME).toString());
		
		prefs.put(SEAM_CONNECTION_PROFILE,model.getProperty(SEAM_CONNECTION_PROFILE).toString());
		
		prefs.put(SESION_BEAN_PACKAGE_NAME, model.getProperty(SESION_BEAN_PACKAGE_NAME).toString());
		
		prefs.put(ENTITY_BEAN_PACKAGE_NAME, model.getProperty(ENTITY_BEAN_PACKAGE_NAME).toString());
		
		prefs.put(TEST_CASES_PACKAGE_NAME, model.getProperty(TEST_CASES_PACKAGE_NAME).toString());
	
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
		}
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		
		IServer server = (IServer)model.getProperty(JBOSS_AS_TARGET_SERVER);
		if (server != null) {
			RegistrationHelper.runRegisterInServerJob(project, server);
		}
	}

	/**
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {return null; }
	
}
