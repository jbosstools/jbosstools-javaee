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
import org.jboss.tools.cdi.bot.test.quickfix.QuickFixTestBase;
import org.jboss.tools.cdi.bot.test.quickfix.validators.ScopeValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI Scope component
 * 
 * @author Jaroslav Jankovic
 */

public class ScopeValidationQuickFixTest extends QuickFixTestBase {
	
	private static IValidationProvider validationProvider = new ScopeValidationProvider();
	
	@Override
	public String getProjectName() {
		return "CDIQuickFixScopeTest";
	}
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-7633
	@Test
	public void testTargetAnnotation() {
		
		String className = "Scope1";
		
		wizard.createCDIComponent(CDIWizardType.SCOPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Target({ TYPE, METHOD, FIELD })", 
				"@Target({ TYPE, FIELD })");
		
		checkQuickFix(ValidationType.TARGET);
		
		editResourceUtil.replaceInEditor("@Target({TYPE, METHOD, FIELD})", "");
		
		checkQuickFix(ValidationType.TARGET);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7631
	@Test
	public void testRetentionAnnotation() {
		
		String className = "Scope2";

		wizard.createCDIComponent(CDIWizardType.SCOPE, className, getPackageName(), null);
				
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "@Retention(CLASS)");
		
		checkQuickFix(ValidationType.RETENTION);
		
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "");
		
		checkQuickFix(ValidationType.RETENTION);
		
	}
	
}
