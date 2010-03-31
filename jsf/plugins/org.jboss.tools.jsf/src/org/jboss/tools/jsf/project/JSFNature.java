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
package org.jboss.tools.jsf.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import org.jboss.tools.common.model.project.IAutoLoad;
import org.jboss.tools.common.model.project.ModelNature;
import org.jboss.tools.jsf.JSFModelPlugin;

public class JSFNature extends ModelNature {
	public static final String NATURE_ID = JSFModelPlugin.PLUGIN_ID + ".jsfnature";
	public static final String NATURE_NICK = "org.jboss.tools.struts.strutsnature";	
	static String BUILDER_ID = "org.jboss.tools.common.verification.verifybuilder";

	public String getID() {
		return NATURE_ID;
	}
	
	public static boolean hasJSFNature(IProject project) {
		if(project == null || !project.isAccessible()) return false;
		try {
			return project.hasNature(NATURE_ID);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
			return false;
		}
	}
	
	protected IAutoLoad createAutoLoad() {
		return new JSFAutoLoad();
	}

	public void configure() throws CoreException {
		super.configure();
//Verify builder is deprecated. WTP's validation framework is used for the functionality.
//		addToBuildSpec(BUILDER_ID);
	}
	
	public void deconfigure() throws CoreException {
		removeFromBuildSpec(BUILDER_ID);
		super.deconfigure();
	}
}
