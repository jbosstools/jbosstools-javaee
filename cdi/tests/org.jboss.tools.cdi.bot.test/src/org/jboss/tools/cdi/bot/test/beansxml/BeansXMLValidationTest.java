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

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.base.BeansXMLQuickFixTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test operates on beans validation in beans.xml 
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class BeansXMLValidationTest extends BeansXMLQuickFixTestBase {

	private static final String someBean = "Bean1";
	private static final String nonExistingPackage = "somePackage";
	
	@BeforeClass
	public static void setup() {
		problems.show();		
	}
	
	@Test
	public void testEmptyBeansXMLValidation() {
		
		beansHelper.createEmptyBeansXML(getProjectName());		
			
		assertTrue(isBeanXMLValidationErrorEmpty());
		
	}
	
	@Test
	public void testInterceptorsValidation() {
		
		String className = "I1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				(CDIConstants.JAVA_RESOURCES_SRC_FOLDER + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.INTERCEPTOR, className, getPackageName(), null);

		beansHelper.createBeansXMLWithInterceptor(getProjectName(), getPackageName(), className);
		assertTrue(isBeanXMLValidationErrorEmpty());
		
		beansHelper.createBeansXMLWithInterceptor(getProjectName(), nonExistingPackage, className);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_CLASS, 
				getProjectName(), getValidationProvider()));
		
		beansHelper.createBeansXMLWithInterceptor(getProjectName(), getPackageName(), someBean);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_INTERCEPTOR, 
				getProjectName(), getValidationProvider()));
		
	}
	
	@Test
	public void testDecoratorsValidation() {
		
		String className = "D1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				(CDIConstants.JAVA_RESOURCES_SRC_FOLDER + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.DECORATOR, className, getPackageName(), "java.util.Set");

		beansHelper.createBeansXMLWithDecorator(getProjectName(), getPackageName(), className);
		assertTrue(isBeanXMLValidationErrorEmpty());
		
		beansHelper.createBeansXMLWithDecorator(getProjectName(), nonExistingPackage, className);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_CLASS, 
				getProjectName(), getValidationProvider()));
		
		beansHelper.createBeansXMLWithDecorator(getProjectName(), getPackageName(), someBean);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_DECORATOR, 
				getProjectName(), getValidationProvider()));
	}
	
	@Test
	public void testAlternativesValidation() {
		
		String className = "A1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				(CDIConstants.JAVA_RESOURCES_SRC_FOLDER + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), "alternative");

		beansHelper.createBeansXMLWithAlternative(getProjectName(), getPackageName(), className);
		assertTrue(isBeanXMLValidationErrorEmpty());
		
		beansHelper.createBeansXMLWithAlternative(getProjectName(), nonExistingPackage, className);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_CLASS, 
				getProjectName(), getValidationProvider()));
		
		beansHelper.createBeansXMLWithAlternative(getProjectName(), getPackageName(), someBean);
		assertFalse(isBeanXMLValidationErrorEmpty());
		assertNotNull(quickFixHelper.getProblem(ValidationType.NO_ALTERNATIVE, 
				getProjectName(), getValidationProvider()));
		
	}
	
}