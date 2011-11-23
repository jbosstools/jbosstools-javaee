/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.jsf;

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.JSFEnvironment;
import org.jboss.tools.cdi.bot.test.annotations.JSFTemplate;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewJSFProjectWizard;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewXHTMLFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.XHTMLDialogWizard;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.Before;

public class JSFTestBase extends CDITestBase {
	
	private JSFEnvironment env = JSFEnvironment.JSF_20;
	
	public JSFEnvironment getEnv() {
		return env; 
	}
	
	@Before
	public void checkAndCreateProject() {
		
		if (!projectHelper.projectExists(getProjectName())) {
			createJSFProjectWithCDISupport(getProjectName(), getEnv());
		}
		
	}
	
	protected void createXHTMLPage(String pageName) {
		XHTMLDialogWizard xhtmlWizard = new NewXHTMLFileWizard().run();
		xhtmlWizard.setName(pageName).finish();
		bot.sleep(Timing.time3S());
		util.waitForNonIgnoredJobs();
		setEd(bot.activeEditor().toTextEditor());
	}

	private void createJSFProjectWithCDISupport(String projectName, JSFEnvironment env) {
		
		createJSFProject(projectName, env);
		projectHelper.addCDISupport(projectName);
		
	}

	private void createJSFProject(String projectName, JSFEnvironment env) {				
		new NewJSFProjectWizard().run().
			setName(getProjectName()).
			setEnvironment(env).
			setJSFTemplate(JSFTemplate.BLANK_LIBS).finish();
		util.waitForNonIgnoredJobs();
	}
				
}