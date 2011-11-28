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

	public JSFWebProjectWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("New JSF Project").equals(getText());		
	}

	public JSFWebProjectWizard setName(String name) {				
		setText("Project Name*", name);
		return this;		
	}

	public JSFWebProjectWizard setEnvironment(JSFEnvironment env) {
		setTextInCombobox("JSF Environment*", env.getName());
		return this;
	}
	
	public JSFWebProjectWizard setJSFTemplate(JSFTemplate template) {
		if (canCheckInCombobox("Template*", template.getName())) {
			setTextInCombobox("Template*", template.getName());
		}
		return this;
	}
	
}
