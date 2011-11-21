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

package org.jboss.tools.cdi.bot.test.beansxml;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on code completion in beans.xml
 * 
 * @author Jaroslav Jankovic
 * 
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class BeansXMLCompletionTest extends CDITestBase {

	private static final Logger LOGGER = Logger
			.getLogger(BeansXMLCompletionTest.class.getName());

	@Override
	public String getProjectName() {
		return "CDIBeansCodeCompletion";
	}
	
	@Test
	public void testPossibleCompletionInBeansXML() {
		wizard.createClearBeansXML(getPackageName());
		LOGGER.info("Clear beans.xml was created");
				
		List<String> autoCompletion = getAutoCompletion(3, 0, "<");
		
		assertTrue("Error: Size of auto completion proposals should be 3 " +
				"instead of " + autoCompletion.size(), autoCompletion.size() == 3);
	}
	@Ignore
	@Test
	public void testInterceptorsCompletion() {

		prepareInterceptors();

		wizard.createClearBeansXML(getPackageName());
		LOGGER.info("Clear beans.xml was created");
				
	}
	@Ignore
	@Test
	public void testDecoratorsCompletion() {
		
		prepareDecorators();
		
		wizard.createClearBeansXML(getPackageName());
		LOGGER.info("Clear beans.xml was created");

	}
	@Ignore
	@Test
	public void testAlternativesCompletion() {
		
		prepareAlternatives();
		
		wizard.createClearBeansXML(getPackageName());
		LOGGER.info("Clear beans.xml was created");

	}

	private void prepareInterceptors() {

		wizard.createComponent(CDIWizardType.INTERCEPTOR, "Interceptor1",
				getPackageName(), null);

		wizard.createComponent(CDIWizardType.INTERCEPTOR, "Interceptor2",
				getPackageName(), null);

		wizard.createComponent(CDIWizardType.INTERCEPTOR, "Interceptor3",
				getPackageName(), null);		

	}
	
	private void prepareDecorators() {

		wizard.createComponent(CDIWizardType.DECORATOR, "Decorator1", 
				getPackageName(), "java.util.Set");
		
		wizard.createComponent(CDIWizardType.DECORATOR, "Decorator2", 
				getPackageName(), "java.util.Set");
		
		wizard.createComponent(CDIWizardType.DECORATOR, "Decorator3", 
				getPackageName(), "java.util.Set");

	}
	
	private void prepareAlternatives() {
		
		wizard.createComponent(CDIWizardType.BEAN, "Alternative1", 
				getPackageName(), "alternative");
		
		wizard.createComponent(CDIWizardType.BEAN, "Alternative2", 
				getPackageName(), "alternative");
		
		wizard.createComponent(CDIWizardType.BEAN, "Alternative3", 
				getPackageName(), "alternative");
		
	}

		//not complete yet
	private List<String> getAutoCompletion(int row, int column, String text) {
		List<String> listOfCompletion = new ArrayList<String>();
		getEd().navigateTo(row, column);
		bot.sleep(Timing.time500MS());
		getEd().typeText(text);
		bot.sleep(Timing.time500MS());
		getEd().pressShortcut(Keystrokes.CTRL, Keystrokes.SPACE);
		bot.sleep(Timing.time1S());
		for (int i = 0; i < bot.table().rowCount(); i++) {
			listOfCompletion.add(bot.table().getTableItem(i).getText());
		}
		return listOfCompletion;
	}

}
