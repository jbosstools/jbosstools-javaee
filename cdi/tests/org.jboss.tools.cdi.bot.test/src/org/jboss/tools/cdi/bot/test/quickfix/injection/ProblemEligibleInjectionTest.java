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

package org.jboss.tools.cdi.bot.test.quickfix.injection;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.quickfix.base.QuickFixTestBase;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@Require(clearProjects = true, perspective = "Java EE", 
		 server = @Server(state = ServerState.NotRunning, 
		 version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class ProblemEligibleInjectionTest extends QuickFixTestBase {
	
	@Override
	public String getProjectName() {
		return "CDIMultipleInjections";
	}
	
	@After
	public void waitForJobs() {
		editResourceUtil.deletePackage(getProjectName(), getPackageName());		
		util.waitForNonIgnoredJobs();
	}
	
	@Test
	public void testMultipleBeansAddingExistingQualifier() {
		String animalClassName = "Animal";
		String dogClassName = "Dog";
		String brokenFarmClassName = "BrokenFarm";
		String qualifierClassName = "Q1";

		wizard.createCDIComponent(CDIWizardType.QUALIFIER, qualifierClassName,
				getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, animalClassName,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, dogClassName,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/addQualifier/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, brokenFarmClassName,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/addQualifier/BrokenFarm.java.cdi");

		resolveMultipleBeans(dogClassName, qualifierClassName, QualifierOperation.ADD);

		String code = bot.editorByTitle(brokenFarmClassName + ".java").
				toTextEditor().getText();
		assertTrue(code.contains("@Inject @" + qualifierClassName));
		code = bot.editorByTitle(dogClassName + ".java").toTextEditor().getText();
		assertTrue(code.contains("@" + qualifierClassName));
	}
	
	@Test
	public void testMultipleBeansRemovingExistingQualifier() {
		String animalClassName = "Animal";
		String dogClassName = "Dog";
		String brokenFarmClassName = "BrokenFarm";
		String qualifierClassName = "Q1";

		wizard.createCDIComponent(CDIWizardType.QUALIFIER, qualifierClassName,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, animalClassName,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/removeQualifier/Animal.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, dogClassName,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/removeQualifier/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, brokenFarmClassName,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/removeQualifier/BrokenFarm.java.cdi");

		resolveMultipleBeans(dogClassName, qualifierClassName, QualifierOperation.REMOVE);
		
		String code = bot.editorByTitle(brokenFarmClassName + ".java").
				toTextEditor().getText();
		assertTrue(code.contains("@Inject private"));
		code = bot.editorByTitle(dogClassName + ".java").toTextEditor().getText();
		assertTrue(!code.contains("@" + qualifierClassName));
	}
	
	@Test
	public void testMultipleBeansAddingNonExistingQualifier() {
		String animalClassName = "Animal";
		String dogClassName = "Dog";
		String brokenFarmClassName = "BrokenFarm";
		String qualifierClassName = "Q1";

		wizard.createCDIComponent(CDIWizardType.BEAN, animalClassName,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, dogClassName,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/addQualifier/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, brokenFarmClassName,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/addQualifier/BrokenFarm.java.cdi");
		
		resolveMultipleBeans(dogClassName, qualifierClassName, QualifierOperation.ADD);

		String code = bot.editorByTitle(brokenFarmClassName + ".java").
				toTextEditor().getText();
		assertTrue(code.contains("@Inject @" + qualifierClassName));
		code = bot.editorByTitle(dogClassName + ".java").toTextEditor().getText();
		assertTrue(code.contains("@" + qualifierClassName));
	}
	
	@Test
	public void testNoBeanEligibleAddingExistingQualifier() {
		
		
		
	}
	
	@Test
	public void testNoBeanEligibleAddingNonExistingQualifier() {
		
		
		
	}

}
