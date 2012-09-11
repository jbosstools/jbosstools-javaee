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


import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.InterceptorBindingValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI Interceptor Binding component
 * 
 * @author Jaroslav Jankovic
 */

public class IBindingValidationQuickFixTest extends CDITestBase {
	
	private static IValidationProvider validationProvider = new InterceptorBindingValidationProvider();

	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-7641
	@Test
	public void testNonbindingAnnotation() {
		
		String className = "IBinding1";
			
		wizard.createAnnotation("AAnnotation", getPackageName());
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR_BINDING, 
				className, getPackageName(), null, "/resources/quickfix/interceptorBinding/" +
						"IBindingWithAnnotation.java.cdi");

		editResourceUtil.replaceInEditor("IBindingComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.NONBINDING, getProjectName(), validationProvider());
				
		editResourceUtil.replaceClassContentByResource(IBindingValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/interceptorBinding/IBindingWithStringArray.java.cdi"), 
				false);
		editResourceUtil.replaceInEditor("IBindingComponent", className);
			
		quickFixHelper.checkQuickFix(ValidationType.NONBINDING, getProjectName(), validationProvider());
	}
	
}
