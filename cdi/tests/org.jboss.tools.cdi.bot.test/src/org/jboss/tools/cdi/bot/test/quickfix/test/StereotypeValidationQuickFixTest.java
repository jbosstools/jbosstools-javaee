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

package org.jboss.tools.cdi.bot.test.quickfix.test;


import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.base.QuickFixTestBase;
import org.jboss.tools.cdi.bot.test.quickfix.validators.StereotypeValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI Stereotype component
 * 
 * @author Jaroslav Jankovic
 */

public class StereotypeValidationQuickFixTest extends QuickFixTestBase {
	
	private static IValidationProvider validationProvider = new StereotypeValidationProvider();
	
	@Override
	public String getProjectName() {
		return "CDIQuickFixStereotypeTest";
	}
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-7630
	@Test
	public void testTargetAnnotation() {
		
		String className = "Stereotype1";
		
		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Target({ TYPE, METHOD, FIELD })", 
				"@Target({ TYPE, FIELD })");
		
		checkQuickFix(ValidationType.TARGET);

		editResourceUtil.replaceInEditor("@Target({TYPE, METHOD, FIELD})", "");
		
		checkQuickFix(ValidationType.TARGET);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7631
	@Test
	public void testRetentionAnnotation() {
		
		String className = "Stereotype2";

		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "@Retention(CLASS)");
		
		checkQuickFix(ValidationType.RETENTION);
				
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "");
		
		checkQuickFix(ValidationType.RETENTION);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7634
	@Test
	public void testNamedAnnotation() {
		
		String className = "Stereotype3";
		
		wizard.createCDIComponentWithContent(CDIWizardType.STEREOTYPE, className, 
				getPackageName(), null, "/resources/quickfix/stereotype/StereotypeWithNamed.java.cdi");
	
		editResourceUtil.replaceInEditor("StereotypeComponent", className);
		
		checkQuickFix(ValidationType.NAMED);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7640
	@Test	
	public void testTypedAnnotation() {
		
		String className = "Stereotype4";
		
		wizard.createCDIComponentWithContent(CDIWizardType.STEREOTYPE, className, 
				getPackageName(), null, "/resources/quickfix/stereotype/StereotypeWithTyped.java.cdi");
		
		editResourceUtil.replaceInEditor("StereotypeComponent", className);
		
		checkQuickFix(ValidationType.TYPED);
		
	}	

}
