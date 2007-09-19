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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.jboss.ide.eclipse.as.core.modules.SingleDeployableFactory;
import org.jboss.ide.eclipse.as.core.server.internal.DeployableServerBehavior;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author eskimo
 *
 */
public class DataSourceXmlDeployer extends Job {
	IProject project = null;
	public DataSourceXmlDeployer(IProject project) {
		super("Deploying datasource to server");
		this.project = project;
	}
		@Override
		protected IStatus run(IProgressMonitor monitor) {

			IFacetedProject facetedProject;
			try {
				facetedProject = ProjectFacetsManager.create(project);
			} catch (CoreException e) {
				return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID, "No server selected to deploy datasource to");
			}
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime primaryRuntime = facetedProject.getPrimaryRuntime();
			IServer s = null;
			IServer[] servers = ServerCore.getServers();
			for (IServer server : servers) {
				String primaryName = primaryRuntime.getName();
				IRuntime runtime = server.getRuntime();
				if(runtime!=null) {
					String serverName = runtime.getName();
					if(primaryName.equals(serverName)) {
						s = server;
					}
				}
			}

			if(s==null) {
				return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID, "No server selected to deploy datasource to");							
			} 
			
	        // convert it to a DeployableServer instance
            DeployableServerBehavior deployer =  (DeployableServerBehavior) s.loadAdapter(DeployableServerBehavior.class, null);

            // if its not null, the adaptation worked.
            if( deployer == null ) {
            	return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID, "Server did not support deploy of datasource.");
            }

            IVirtualComponent com = ComponentCore.createComponent(project);
    		final IVirtualFolder srcRootFolder = com.getRootFolder().getFolder(new Path("/WEB-INF/classes"));
	        IContainer underlyingFolder = srcRootFolder.getUnderlyingFolder();
			
			IPath projectPath = new Path("/"+underlyingFolder.getProject().getName());
			IPath projectRelativePath = underlyingFolder.getProjectRelativePath();
			
			IPath append = projectPath.append(projectRelativePath).append(project.getName()+"-ds.xml");
			
			if(SingleDeployableFactory.makeDeployable(append)) {
			
				IModule module = SingleDeployableFactory.findModule(append);
				
		         // custom API to deploy / publish only one module.
				IStatus t = deployer.publishOneModule(IServer.PUBLISH_FULL, new IModule[] { module}, ServerBehaviourDelegate.ADDED, monitor);
				SingleDeployableFactory.unmakeDeployable(append);
		        return 	t;				
			} else {
				return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID, "Could not deploy datasource " + append);
			}
		}
}
