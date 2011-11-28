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

import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.JSFEnvironment;
import org.jboss.tools.cdi.bot.test.annotations.JSFTemplate;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewJSFProjectWizard;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewXHTMLFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.XHTMLDialogWizard;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.Before;

public class JSFTestBase extends CDITestBase {
	
	private static final Logger LOGGER = Logger.getLogger(JSFTestBase.class.getName());
	
	private JSFEnvironment env = JSFEnvironment.JSF_20;
	
	private JSFTemplate template = JSFTemplate.BLANK_LIBS;
	
	public JSFEnvironment getEnv() {
		return env; 
	}
	
	public JSFTemplate getTemplate() {
		return template;
	}
	
	@Before
	public void checkAndCreateProject() {
		
		if (!projectHelper.projectExists(getProjectName())) {
			createJSFProjectWithCDISupport(getProjectName(), getEnv(), getTemplate());
		}
		
	}
	
	protected void createXHTMLPage(String pageName) {
		XHTMLDialogWizard xhtmlWizard = new NewXHTMLFileWizard().run();
		xhtmlWizard.setName(pageName).finish();
		bot.sleep(Timing.time3S());
		util.waitForNonIgnoredJobs();
		setEd(bot.activeEditor().toTextEditor());
	}
	
	protected void contextMenuForTextInEditor(final String text, 
			final String... menu) {
		assert menu.length > 0;		
		SWTJBTExt.selectTextInSourcePane(bot, getEd().getTitle(), 
				text, 0, text.length());	
		bot.sleep(Timing.time2S());
		
		getEd().toTextEditor().contextMenu(menu[0]);		
		bot.sleep(Timing.time3S());		
		util.waitForNonIgnoredJobs();
		
	}

	private void createJSFProjectWithCDISupport(String projectName, JSFEnvironment env, 
			JSFTemplate template) {
		
		createJSFProject(projectName, env, template);
		projectHelper.addCDISupport(projectName);
		
	}

	private void createJSFProject(String projectName, JSFEnvironment env, 
			JSFTemplate template) {				
		new NewJSFProjectWizard().run().
			setName(getProjectName()).
			setEnvironment(env).
			setJSFTemplate(template).		
			finish();		
		/*
		 * workaround for non Web Perspective, click No button
		 * to not change perspective to Web Perspectives
		 * 
		 */
		try {
			bot.button("No").click();
		} catch (WidgetNotFoundException exc) {
			log.info("There is no dialog to change perspective.");
		}
		util.waitForNonIgnoredJobs();						
	}
				
}