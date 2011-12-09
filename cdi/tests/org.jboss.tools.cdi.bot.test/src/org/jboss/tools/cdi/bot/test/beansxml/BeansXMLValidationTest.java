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

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.BeansXMLValidationErrors;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test operates on beans validation in beans.xml 
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class BeansXMLValidationTest extends CDITestBase {

	private final String someBean = "Bean1";
	private final String nonExistingPackage = "somePackage";
	
	@Override
	public String getProjectName() {
		return "CDIBeansValidation";
	}
	
	@BeforeClass
	public static void setup() {
		problems.show();		
	}
	
	@Test
	public void testClearBeansXMLValidation() {
		
		beansHelper.createClearBeansXML(getProjectName());		
			
		assertTrue(beansHelper.getBeansXMLValidationErrors(
				getProjectName()).length == 0);
		
	}
	
	@Test
	public void testInterceptorsValidation() {
		
		String className = "I1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				("Java Resources/src/" + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.INTERCEPTOR, className, getPackageName(), null);

		assertTrue(beansHelper.checkInterceptorInBeansXML(getProjectName(), 
				getPackageName(), className));
		
		
		assertFalse(beansHelper.checkInterceptorInBeansXML(getProjectName(), 
				nonExistingPackage, className));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(), 
				BeansXMLValidationErrors.NO_SUCH_CLASS));
		
		
		assertFalse(beansHelper.checkInterceptorInBeansXML(getProjectName(), 
				getPackageName(), someBean));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(),
				BeansXMLValidationErrors.INTERCEPTOR));
	}
	
	@Test
	public void testDecoratorsValidation() {
		
		String className = "D1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				("Java Resources/src/" + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.Set");

		assertTrue(beansHelper.checkDecoratorInBeansXML(getProjectName(), 
				getPackageName(), className));
		
		
		assertFalse(beansHelper.checkDecoratorInBeansXML(getProjectName(), 
				nonExistingPackage, className));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(),
				BeansXMLValidationErrors.NO_SUCH_CLASS));

		
		assertFalse(beansHelper.checkDecoratorInBeansXML(getProjectName(), 
				getPackageName(), someBean));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(),
				BeansXMLValidationErrors.DECORATOR));
	}
	
	@Test
	public void testAlternativesValidation() {
		
		String className = "A1";
		
		if (!projectExplorer.isFilePresent(getProjectName(),  
				("Java Resources/src/" + getPackageName() + 
				"/" + someBean + ".java").split("/"))) {
			wizard.createCDIComponent(CDIWizardType.BEAN, someBean, getPackageName(), null);
		}
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, 
				getPackageName(), "alternative");

		assertTrue(beansHelper.checkAlternativeInBeansXML(getProjectName(), 
				getPackageName(), className));
		
		
		assertFalse(beansHelper.checkAlternativeInBeansXML(getProjectName(), 
				nonExistingPackage, className));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(),
				BeansXMLValidationErrors.NO_SUCH_CLASS));
		
		
		assertFalse(beansHelper.checkAlternativeInBeansXML(getProjectName(), 
				getPackageName(), someBean));		
		assertTrue(beansHelper.checkValidationErrorInBeansXML(getProjectName(),
				BeansXMLValidationErrors.ALTERNATIVE));
		
	}
	
}