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
package org.jboss.tools.struts.webprj.model.helpers.context;

import java.util.Properties;

import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsProjectUtil;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.context.RegisterTomcatContext;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectContext;

public class NewProjectWizardContext extends NewWebProjectContext {
	public static final String ATTR_TLDS = "TLDs";
	private String tlds;
	
	public NewProjectWizardContext () {}

	protected IWebProjectTemplate createTemplate() {
		return new StrutsUtils();
	}

	protected void initRegistry() {
		registry.setNatureIndex(StrutsProjectUtil.NATURE_NICK);
		registry.setPreferences(StrutsPreference.REGISTER_NEW_PROJECT_IN_TOMCAT);
		registry.init();
	}

	public String getTLDs()	{
		return tlds;
	}

	public void setTLDs(String value) {
		tlds = value;	
	}
		
	public Properties getActionProperties()	{
		Properties result = super.getActionProperties();		
		result.setProperty(ATTR_TLDS, tlds);		
		return result;
	}

	public void setVersion(String value) {
		if (value != null && !value.equals(version)) {
			super.setVersion(value);
			tlds = StrutsUtils.getInstance().getTldTemplateDefaultProperties(version).getProperty("tld_files");
		}
	}

	public RegisterTomcatContext getRegisterTomcatContext()	{
		return registry;
	}

}
