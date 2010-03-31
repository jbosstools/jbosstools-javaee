/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts;

import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Bundle;

import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.project.ModelNature;

public class StrutsProject extends ModelNature implements IProjectNature {
	public static final String NATURE_ID = StrutsModelPlugin.PLUGIN_ID + ".strutsnature";
	static String BUILDER_ID = "org.jboss.tools.common.verification.verifybuilder";
	
	public StrutsProject() {}

	public void configure() throws CoreException {
		super.configure();
//Verify builder is deprecated. WTP's validation framework is used for the functionality.
//		addToBuildSpec(BUILDER_ID);
	}

	public void deconfigure() throws CoreException {
		removeFromBuildSpec(BUILDER_ID);
		super.deconfigure();
	}
	
	public String getID() {
		return NATURE_ID;
	}

	protected void updateProjectVersion()	{
		boolean obsoleteVersion = false;
//		String modelVersionStr = XModelUtil.getModelVersion(model);
//		PluginVersionIdentifier modelVersion = new PluginVersionIdentifier(modelVersionStr);
//		if((new PluginVersionIdentifier("5.1.1")).isGreaterThan(modelVersion)) {
//			try {
//				addToBuildSpec(BUILDER_ID);
//				obsoleteVersion = true;
//			} catch (CoreException ex) {
//				ModelPlugin.log(ex);
//			}
//		}
		if (obsoleteVersion) {
			Bundle bundle = ModelPlugin.getDefault().getBundle();
			String version = (String) bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		try {
			model.changeObjectAttribute(
				FileSystemsHelper.getFileSystems(model),
				XModelConstants.MODEL_VERSION, version
			);
		} catch (XModelException e) {
			ModelPlugin.getPluginLog().logError(e);
		}
			model.save();
		}
	}

}