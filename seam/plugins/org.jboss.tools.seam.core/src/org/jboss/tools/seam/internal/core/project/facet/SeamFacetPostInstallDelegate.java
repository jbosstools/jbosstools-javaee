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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.jboss.tools.jst.web.server.RegistrationHelper;

/**
 * <p>Facet Post install delegate that handles:
 *  <ul>
 *  	<li>JDBC driver copying to server libraries folder;
 *  	<li> registering faceted project on the selected server;
 *  	<li>deploying datasource .xml file to the selected server;
 *  </ul>
 *  </p>
 * @author eskimo
 *
 */
public class SeamFacetPostInstallDelegate implements IDelegate, ISeamFacetDataModelProperties {
	/**
	 * Description
	 * @param project 
	 * 	target project
	 * @param fv
	 *  Facet version information
	 * @param config
	 * 	configuration parameters
	 * @param monitor
	 *  progress monitor
	 * @throws CoreException
	 *  never throws 
	 */
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
	final IDataModel model = (IDataModel) config;
	
		IServer server = (IServer) model.getProperty(JBOSS_AS_TARGET_SERVER);
		if (server != null) {
			JBossServer jbs = (JBossServer) server.loadAdapter(JBossServer.class, new NullProgressMonitor());
			if (jbs != null) {
				String[] driverJars = (String[]) model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH);
				String configFolder = jbs.getConfigDirectory();
				AntCopyUtils.copyFiles(driverJars, new File(configFolder, "lib"), false);
			} 

			RegistrationHelper.runRegisterInServerJob(project, server);
			
			IPath filePath = new Path("resources").append(project.getName() + "-ds.xml");
			
			if (!isWarConfiguration(model)) {
				IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
				IProject earProjectToBeImported = wsRoot.getProject(project.getName() + "-ear");
				new DataSourceXmlDeployer(earProjectToBeImported, server, filePath).schedule();
			} else {
				new DataSourceXmlDeployer(project, server, filePath).schedule();
			}			
		}
	}

	/**
	 * Never used
	 * @throws CoreException
	 * 	never throws
	 * @return 
	 *  always return null
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {
		return null; 
	}
	/**
	 * Define if WAR deployment configuration is used
	 * @param model
	 * 	configuration parameters
	 * @return
	 * 	true  - is Seam Project uses EAR deployment
	 *  false - EAR
	 */
	public static boolean isWarConfiguration(IDataModel model) {
		return "war".equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)); //$NON-NLS-1$
	}
	
}
