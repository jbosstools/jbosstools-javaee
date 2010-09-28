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
package org.jboss.tools.jsf.web;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jst.web.project.helpers.*;
import org.jboss.tools.jst.web.project.version.*;

public class JSFTemplate extends AbstractWebProjectTemplate {
	
	static JSFTemplate instance;
	
	public static JSFTemplate getInstance() {
		if(instance == null) {
			instance = new JSFTemplate();
		}
		return instance;
	}

	public String getNatureDir() {
		return "jsf";
	}
	
	public ProjectVersions getProjectVersions() {
		return JSFVersions.getInstance(getProjectTemplatesLocation());
	}

	public String getDefaultVersion() {
		String preference = JSFPreference.DEFAULT_JSF_VERSION.getValue();
		return getDefaultVersion(preference);
	}

	public String getDefaultTemplate(String version) {
		String preference = JSFPreference.DEFAULT_JSF_PROJECT_TEMPLATE.getValue();
		return getDefaultTemplate(version, preference);	
	}

	public void setDefaultTemplate(String template) {
		try {
			JSFPreference.DEFAULT_JSF_PROJECT_TEMPLATE.setValue(template);
		} catch (XModelException e) {
			ModelPlugin.getPluginLog().logError(e);
		}
	}

	protected String getWizardEntitySuffix() {
		return "JSF";
	}

}
