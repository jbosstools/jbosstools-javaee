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
package org.jboss.tools.jsf.web.helpers.context;

import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectContext;

public class NewProjectWizardContext extends NewWebProjectContext {

	public NewProjectWizardContext () {}

	protected void initRegistry() {
		registry.setNatureIndex(JSFNature.NATURE_NICK);
		registry.setPreferences(JSFPreference.REGISTER_NEW_JSF_PROJECT_IN_TOMCAT);
		registry.init();
	}

	protected IWebProjectTemplate createTemplate() {
		return new JSFTemplate();
	}

	public String getJSFVersion() {
		return getVersion();
	}

	public void setJSFVersion(String value)	{
		setVersion(value);
	}

}
