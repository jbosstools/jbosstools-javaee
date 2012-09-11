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

import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.base.EligibleInjectionQuickFixTestBase;
import org.junit.After;
import org.junit.Test;

/**
 * Test checks if Quick Fix provides useful operations when 
 * ambiguous injection points
 * @author jjankovi
 *
 */

public class ProblemEligibleInjectionTest extends EligibleInjectionQuickFixTestBase {
	
	private static final String ANIMAL = "Animal";
	private static final String DOG = "Dog";
	private static final String BROKEN_FARM = "BrokenFarm";
	private static final String QUALIFIER = "Q1";
	
	@After
	public void waitForJobs() {
		editResourceUtil.deletePackage(getProjectName(), getPackageName());		
		util.waitForNonIgnoredJobs();
	}
	
	@Test
	public void testMultipleBeansAddingExistingQualifier() {
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, QUALIFIER,
				getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, ANIMAL,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, DOG,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, BROKEN_FARM,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/BrokenFarm.java.cdi");

		resolveMultipleBeans(ValidationType.MULTIPLE_BEAN_ELIGIBLE, DOG, QUALIFIER, QualifierOperation.ADD);

		bot.editorByTitle(BROKEN_FARM + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		String code = getEd().getText();
		assertTrue(code.contains("@Inject @" + QUALIFIER));
		code = bot.editorByTitle(DOG + ".java").toTextEditor().getText();
		assertTrue(code.contains("@" + QUALIFIER));
	}
	
	@Test
	public void testMultipleBeansAddingNonExistingQualifier() {

		wizard.createCDIComponent(CDIWizardType.BEAN, ANIMAL,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, DOG,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, BROKEN_FARM,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/BrokenFarm.java.cdi");
		
		resolveMultipleBeans(ValidationType.MULTIPLE_BEAN_ELIGIBLE, DOG, QUALIFIER, QualifierOperation.ADD);

		bot.editorByTitle(BROKEN_FARM + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		String code = getEd().getText();
		assertTrue(code.contains("@Inject @" + QUALIFIER));
		code = bot.editorByTitle(DOG + ".java").toTextEditor().getText();
		assertTrue(code.contains("@" + QUALIFIER));
	}
	
	@Test
	public void testMultipleBeansRemovingExistingQualifier() {

		wizard.createCDIComponent(CDIWizardType.QUALIFIER, QUALIFIER,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, ANIMAL,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/AnimalWithQualifier.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, DOG,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/DogWithQualifier.java.cdi");

		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, BROKEN_FARM,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/BrokenFarmWithQualifier.java.cdi");

		resolveMultipleBeans(ValidationType.MULTIPLE_BEAN_ELIGIBLE, DOG, QUALIFIER, QualifierOperation.REMOVE);
		
		bot.editorByTitle(BROKEN_FARM + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		String code = getEd().getText();
		assertTrue(code.contains("@Inject private") || code.contains("@Inject  private"));
		code = bot.editorByTitle(DOG + ".java").toTextEditor().getText();
		assertTrue(!code.contains("@" + QUALIFIER));
	}
	
	@Test
	public void testNoBeanEligibleAddingExistingQualifier() {

		wizard.createCDIComponent(CDIWizardType.QUALIFIER, QUALIFIER,
				getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, ANIMAL,
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, DOG,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/Dog.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, BROKEN_FARM,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/BrokenFarmWithQualifier.java.cdi");

		resolveMultipleBeans(ValidationType.NO_BEAN_ELIGIBLE, DOG, QUALIFIER, QualifierOperation.ADD);

		bot.editorByTitle(BROKEN_FARM + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		String code = getEd().getText();
		assertTrue(code.contains("@Inject @" + QUALIFIER));
		code = bot.editorByTitle(DOG + ".java").toTextEditor().getText();
		assertTrue(code.contains("@" + QUALIFIER));
		
	}
	
	@Test
	public void testNoBeanEligibleRemovingExistingQualifier() {
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, QUALIFIER, 
				getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, ANIMAL,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/AnimalWithQualifier.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, DOG,
				getPackageName(), null, "/resources/quickfix/" +
						"injection/DogWithQualifier.java.cdi");

		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, BROKEN_FARM,
				getPackageName(), null,  "/resources/quickfix/" +
						"injection/BrokenFarm.java.cdi");

		resolveMultipleBeans(ValidationType.NO_BEAN_ELIGIBLE, DOG, QUALIFIER, QualifierOperation.REMOVE);

		bot.editorByTitle(BROKEN_FARM + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		String code = getEd().getText();
		assertTrue(code.contains("@Inject private") || code.contains("@Inject  private"));
		code = bot.editorByTitle(DOG + ".java").toTextEditor().getText();
		assertFalse(code.contains("@" + QUALIFIER));
		
	}

}
