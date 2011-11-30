/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.annotations.JSFEnvironment;
import org.jboss.tools.cdi.bot.test.annotations.JSFTemplate;

public class JSFWebProjectWizard extends Wizard {

	private static final String JSF_NEW_PROJECT_LABEL = "New JSF Project";
	private static final String JSF_PROJECT_NAME_LABEL = "Project Name*";
	private static final String JSF_ENV_LABEL = "JSF Environment*";
	private static final String JSF_TEMPL_LABEL = "Template*";
	
	public JSFWebProjectWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert (JSF_NEW_PROJECT_LABEL).equals(getText());		
	}

	public JSFWebProjectWizard setName(String name) {				
		setText(JSF_PROJECT_NAME_LABEL, name);
		return this;		
	}

	public JSFWebProjectWizard setEnvironment(JSFEnvironment env) {
		setTextInCombobox(JSF_ENV_LABEL, env.getName());
		return this;
	}
	
	public JSFWebProjectWizard setJSFTemplate(JSFTemplate template) {
		if (canCheckInCombobox(JSF_TEMPL_LABEL, template.getName())) {
			setTextInCombobox(JSF_TEMPL_LABEL, template.getName());
		}
		return this;
	}
	
}
