/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.ChainedJob;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.jboss.tools.jst.web.server.RegistrationHelper;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 */
public class Seam23FacetInstallDelegate extends Seam2FacetInstallDelegate {

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate#copyFilesToWarProject(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, org.eclipse.wst.common.frameworks.datamodel.IDataModel, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void copyFilesToWarProject(IProject project, IProjectFacetVersion fv, IDataModel model, IProgressMonitor monitor) throws CoreException {
		super.copyFilesToWarProject(project, fv, model, monitor);
		if(!shouldCopyLibrariesAndTemplates(model)) {
			return;
		}
		if(isWarConfiguration(model)) {
			File destFolder = new File(project.getLocation().toFile(), "resources"); //$NON-NLS-1$
			copyDBDriverToProject(project, model, destFolder);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate#getProjectCreator(org.eclipse.wst.common.frameworks.datamodel.IDataModel, org.eclipse.core.resources.IProject)
	 */
	@Override
	protected SeamProjectCreator getProjectCreator(IDataModel model, IProject project) {
		return new Seam23ProjectCreator(model, project);
	}

	/**
	 * Copies and deploys the driver jar from connection profile to the server. 
	 * @param project
	 * @param model
	 * @param destFolder
	 */
	public static void copyDBDriverToProject(IProject project, IDataModel model, File destFolder) {
		Object drvrs = model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH);
		if(drvrs!=null) {
			String[] drvrsStrings = (String[])drvrs;
			if(drvrsStrings.length>0) {
				File driver = new File(drvrsStrings[0]);
				if(driver.exists()) {
					File dest = new File(destFolder, driver.getName());
					AntCopyUtils.copyFileToFile(driver, dest, null, false);
					IFile resource = null;
					IFile[] files = project.getWorkspace().getRoot().findFilesForLocationURI(dest.toURI());
					for (IFile file : files) {
						if(project.equals(file.getProject())) {
							resource = file;
							break;
						}
					}
					if(resource != null) {
						try {
							resource.refreshLocal(IResource.DEPTH_ZERO, null);
						} catch (CoreException e) {
							SeamCorePlugin.getDefault().logError(e);
						}
						JBossServer server = getJBossServer(model);
						if (server != null) {
							ChainedJob dsJob = new ResourceDeployer(project, server.getServer(), resource.getFullPath().removeFirstSegments(1));
							dsJob.setNextJob(RegistrationHelper.getRegisterInServerJob(project, new IServer[]{server.getServer()}, null));
							dsJob.schedule();
						}
					}
				}
			}
		}
	}
}