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
import org.jboss.tools.cdi.bot.test.quickfix.validators.QualifierValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI Qualifier component
 * 
 * @author Jaroslav Jankovic
 */

public class QualifierValidationQuickFixTest extends QuickFixTestBase {
	
	private static IValidationProvider validationProvider = new QualifierValidationProvider();
	
	@Override
	public String getProjectName() {
		return "CDIQuickFixQualifierTest";
	}
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-7630
	@Test
	public void testTargetAnnotation() {
		
		String className = "Qualifier1";
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Target({ TYPE, METHOD, PARAMETER, FIELD })", 
				"@Target({ TYPE, FIELD })");
		
		checkQuickFix(ValidationType.TARGET);
		
		editResourceUtil.replaceInEditor("@Target({TYPE, METHOD, FIELD, PARAMETER})", "");
		
		checkQuickFix(ValidationType.TARGET);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7631
	@Test
	public void testRetentionAnnotation() {
		
		String className = "Qualifier2";

		wizard.createCDIComponent(CDIWizardType.QUALIFIER, className, getPackageName(), null);
				
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "@Retention(CLASS)");
		
		checkQuickFix(ValidationType.RETENTION);
		
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "");
		
		checkQuickFix(ValidationType.RETENTION);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7641
	@Test
	public void testNonbindingAnnotation() {
	
		String className = "Qualifier3";
		
		wizard.createAnnotation("AAnnotation", getPackageName());
		wizard.createCDIComponentWithContent(CDIWizardType.QUALIFIER, className, 
				getPackageName(), null, "/resources/quickfix/" +
						"qualifier/QualifierWithAnnotation.java.cdi");
	
		editResourceUtil.replaceInEditor("QualifierComponent", className);
	
		checkQuickFix(ValidationType.NONBINDING);
				
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/qualifier/QualifierWithStringArray.java.cdi"), false);
		editResourceUtil.replaceInEditor("QualifierComponent", className);
		
		checkQuickFix(ValidationType.NONBINDING);
	}
}
