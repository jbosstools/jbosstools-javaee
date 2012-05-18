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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ChainedJob;
import org.jboss.ide.eclipse.as.core.modules.SingleDeployableFactory;
import org.jboss.tools.jst.web.server.RegistrationHelper;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;

/**
 * @author Alexey Kazakov
 */
public class Seam23FacetInstallDelegate extends Seam2FacetInstallDelegate {

	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
		.include("WEB-INF") //$NON-NLS-1$
		.include("WEB-INF/pages\\.xml") //$NON-NLS-1$
		.include("WEB-INF/componets\\.xml") //$NON-NLS-1$
		.include("WEB-INF/jboss-deployment-structure\\.xml"); //$NON-NLS-1$

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
	protected SeamProjectCreator getProjectCreator(IDataModel model, IProject project, SeamFacetAbstractInstallDelegate seamFacetInstallDelegate) {
		return new Seam23ProjectCreator(model, project, seamFacetInstallDelegate);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#getJBossWarWebinfSet()
	 */
	@Override
	protected AntCopyUtils.FileSet getJBossWarWebinfSet() {
		return JBOOS_WAR_WEBINF_SET;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate#getEarLibFileSet()
	 */
	@Override
	public AntCopyUtils.FileSet getEarLibFileSet() {
		return getEarLibFileSet(facetModel);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate#getWarLibFileSet()
	 */
	@Override
	public AntCopyUtils.FileSet getWarLibFileSet() {
		return getWarLibFileSet(facetModel);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate#getWarLibFileSetForEar()
	 */
	@Override
	public AntCopyUtils.FileSet getWarLibFileSetForEar() {
		return getWarLibFileSetForEar(facetModel);
	}

	public static AntCopyUtils.FileSet getEarLibFileSet(IDataModel model) {
		SeamRuntime runtime = getSeamRuntime(model);
		return getEarLibFileSet(runtime);
	}

	public static AntCopyUtils.FileSet getWarLibFileSet(IDataModel model) {
		SeamRuntime runtime = getSeamRuntime(model);
		return getWarLibFileSet(runtime);
	}

	public static AntCopyUtils.FileSet getWarLibFileSetForEar(IDataModel model) {
		SeamRuntime runtime = getSeamRuntime(model);
		return getWarLibFileSetForEar(runtime);
	}

	public static AntCopyUtils.FileSet getEarLibFileSet(SeamRuntime runtime) {
		String path = runtime.getDeployedJarsEarListFile();
		return getFileSetOfJars(path);
	}

	public static AntCopyUtils.FileSet getWarLibFileSet(SeamRuntime runtime) {
		String path = runtime.getDeployedJarsWarListFile();
		return getFileSetOfJars(path);
	}

	public static AntCopyUtils.FileSet getWarLibFileSetForEar(SeamRuntime runtime) {
		String path = runtime.getDeployedJarsEarWarListFile();
		return getFileSetOfJars(path);
	}

	private static AntCopyUtils.FileSet getFileSetOfJars(String path) {
		AntCopyUtils.FileSet fileSet = new AntCopyUtils.FileSet();
		File listFile = new File(path);
		if(listFile.exists()) {
			FileInputStream fis = null;
			try {
				Properties list = new Properties();
		        fis = new FileInputStream(listFile);
		        list.load(fis);
		        Set<String> jarList = list.stringPropertyNames();
		        for (String jar : jarList) {
					fileSet.include(jar);
				}
			} catch (FileNotFoundException e) {
				SeamCorePlugin.getDefault().logError(e);
			} catch (IOException e) {
				SeamCorePlugin.getDefault().logError(e);
			} finally {
				if(fis!=null) {
			        try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			SeamCorePlugin.getDefault().logError(path + " doesn't exist. Can't get the list of the JARs to copy to the project.");
		}
		return fileSet;
	}

	private static SeamRuntime getSeamRuntime(IDataModel model) {
		Object runtimeName = model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME);
		SeamRuntime runtime = SeamRuntimeManager.getInstance().findRuntimeByName(runtimeName.toString());
		return runtime;
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
						
						IServer server = getServer(model);
						if (serverSupportsSingleFileModule(server)) {
							ChainedJob dsJob = new ResourceDeployer(project, server, resource.getFullPath().removeFirstSegments(1));
							dsJob.setNextJob(RegistrationHelper.getRegisterInServerJob(project, new IServer[]{server}, null));
							dsJob.schedule();
						}
					}
				}
			}
		}
	}
	
	private static boolean serverSupportsSingleFileModule(IServer s) {
		IRuntimeType rt = s.getServerType().getRuntimeType();
		if (ServerUtil.isSupportedModule(rt.getModuleTypes(),
				SingleDeployableFactory.MODULE_TYPE, SingleDeployableFactory.VERSION)) {
			return true;
		}
		return false;
	}
}