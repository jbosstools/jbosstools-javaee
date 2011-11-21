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

import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
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
public class BeansXMLValidationTest extends CDITestBase {

	private static final Logger LOGGER = Logger
			.getLogger(BeansXMLValidationTest.class.getName());	

	@Override
	public String getProjectName() {
		return "CDIBeansValidation";
	}
	
	@Test
	public void testClearBeansXMLValidation() {
		
		wizard.createClearBeansXML(getProjectName());
		LOGGER.info("Clear beans.xml was created");
		
		SWTBotTreeItem[] validationErrors = getBeansXMLValidationErrors();
		
		assertTrue(validationErrors.length == 0);
		
	}
	
	@Test
	public void testInterceptorsValidation() {
		
		String firstPackage = getPackageName() + ".test1";
		String secondPackage = getPackageName() + ".test2";
		String nonExistingPackage = getPackageName() + ".test3";
		
		prepareTwoInterceptors(firstPackage, secondPackage);

		wizard.createClearBeansXML(getProjectName());
		LOGGER.info("Clear beans.xml was created");
		
		String interceptorTag = "<interceptors>" + LINE_SEPARATOR + 											
								"</interceptors>" + LINE_SEPARATOR;
		String firstInterceptor = "<class>" + firstPackage + ".I1</class>" + LINE_SEPARATOR;								
								 
		editResourceUtil.insertInEditor(3, 0, interceptorTag);
		editResourceUtil.insertInEditor(4, 0, firstInterceptor);
		assertTrue(getBeansXMLValidationErrors().length == 0);
		
		String secondDecorator = "<class>" + secondPackage + ".I2</class>" + LINE_SEPARATOR;
		editResourceUtil.insertInEditor(5, 0, secondDecorator);
		assertTrue(getBeansXMLValidationErrors().length == 0);
		
		
		editResourceUtil.replaceInEditor(firstPackage, nonExistingPackage);
		
		SWTBotTreeItem[] validationErrors = getBeansXMLValidationErrors();
		assertTrue(validationErrors.length == 1);
		assertTrue(validationErrors[0].getText().contains("There is no class with the specified name"));
		
		wizard.createComponent(CDIWizardType.BEAN, "Bean1", firstPackage, null);
		bot.editorByTitle("beans.xml").show();
		bot.editorByTitle("beans.xml").setFocus();
		setEd(bot.activeEditor().toTextEditor());
		editResourceUtil.replaceInEditor(nonExistingPackage + ".I1", firstPackage + ".Bean1");
		
		validationErrors = getBeansXMLValidationErrors();
		assertTrue(validationErrors.length == 1);
		assertTrue(validationErrors[0].getText().contains("must specify the name of an interceptor class"));
	}
	
	@Test
	public void testDecoratorsValidation() {

	}
	
	@Test
	public void testAlternativesValidation() {

	}
	
	@Test
	public void testNotSupportedCDIComponentsValidation() {
		
	}
	
	private SWTBotTreeItem[] getBeansXMLValidationErrors() {
		return ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
				+ getProjectName() + "/WebContent/WEB-INF", "beans.xml", "CDI Problem");
	}
	
	private void prepareTwoInterceptors(String firstPackage, String secondPackage) {
		
		wizard.createComponent(CDIWizardType.INTERCEPTOR, "I1", firstPackage, null);
		
		wizard.createComponent(CDIWizardType.INTERCEPTOR, "I2", secondPackage, null);
		
	}
}