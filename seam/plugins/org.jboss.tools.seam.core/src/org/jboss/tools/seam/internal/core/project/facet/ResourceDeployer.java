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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.ChainedJob;
import org.jboss.ide.eclipse.as.core.modules.SingleDeployableFactory;
import org.jboss.ide.eclipse.as.core.server.internal.DeployableServerBehavior;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * This class is provided to deploy data source descriptor or any other resource to JBoss AS for Seam
 * Web Project in WAR and EAR deployment configurations.
 * 
 * @author eskimo
 * 
 */
public class ResourceDeployer extends ChainedJob {
	IProject project = null;
	IServer s = null;
	IPath deploy = null;

	public ResourceDeployer(IProject project, IServer s, IPath deploy) {
		super(SeamCoreMessages.DATA_SOURCE_XML_DEPLOYER_DEPLOYING_DATASOURCE_TO_SERVER, s);
		this.project = project;
		// is must be user since ds.xml (or other resource which is being deployed) has the same behaviour for EAR
		// deployment. It should run after ear project created and imported into 
		// workspace
		setUser(true);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
		this.s = s; 
		this.deploy = deploy;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (s == null) {
			return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID,
					SeamCoreMessages.DATA_SOURCE_XML_DEPLOYER_NO_SERVER_SELECTED_TO_DEPLOY_DATASOURCE_TO);
		}
		// convert it to a DeployableServer instance
		DeployableServerBehavior deployer = (DeployableServerBehavior) s
				.loadAdapter(DeployableServerBehavior.class, null);

		// if its not null, the adaptation worked.
		if (deployer == null) {
			return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID,
					SeamCoreMessages.DATA_SOURCE_XML_DEPLOYER_SERVER_DID_NOT_SUPPORT_DEPLOY_OF_DATASOURCE);
		}

		IPath projectPath = new Path("/" //$NON-NLS-1$
				+ project.getName());
		IPath append = projectPath.append(deploy); //$NON-NLS-1$

		if (SingleDeployableFactory.makeDeployable(append)) {
			try {
				IModule module = SingleDeployableFactory.findModule(append);
				IServerWorkingCopy copy = s.createWorkingCopy();
				copy.modifyModules(new IModule[]{module}, new IModule[0], new NullProgressMonitor());
				IServer saved = copy.save(false, new NullProgressMonitor());
				saved.publish(IServer.PUBLISH_INCREMENTAL, new NullProgressMonitor());
			} catch( CoreException ce ) {
				return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID, 
						SeamCoreMessages.DATA_SOURCE_XML_DEPLOYER_COULD_NOT_DEPLOY_DATASOURCE + append, ce);
			}
			return Status.OK_STATUS;
		} else {
			return new Status(Status.WARNING, SeamCorePlugin.PLUGIN_ID,
					SeamCoreMessages.DATA_SOURCE_XML_DEPLOYER_COULD_NOT_DEPLOY_DATASOURCE + append); //$NON-NLS-1$
		}
	}
}
