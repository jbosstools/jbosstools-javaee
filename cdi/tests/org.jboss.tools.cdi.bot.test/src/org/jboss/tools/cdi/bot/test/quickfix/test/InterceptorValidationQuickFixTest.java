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
import org.jboss.tools.cdi.bot.test.quickfix.validators.InterceptorValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI Interceptor component
 * 
 * @author Jaroslav Jankovic
 */

public class InterceptorValidationQuickFixTest extends CDITestBase {
	
	private static IValidationProvider validationProvider = new InterceptorValidationProvider();
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-7680
	@Test
	public void testSessionAnnotation() {
			
		String className = "Interceptor1";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithStateless.java.cdi");

		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.STATELESS, getProjectName(), validationProvider());
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7636
	@Test
	public void testNamedAnnotation() {
		
		String className = "Interceptor2";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithNamed.java.cdi");

		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.NAMED, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7683
	@Test
	public void testProducer() {
		
		String className = "Interceptor3";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithProducer.java.cdi");

		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.PRODUCES, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7684
	@Test
	public void testDisposesAnnotation() {
		
		String className = "Interceptor4";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithDisposes.java.cdi");

		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.DISPOSES, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7685
	@Test
	public void testObservesAnnotation() {
		
		String className = "Interceptor5";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithDisposes.java.cdi");
		
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.OBSERVES, getProjectName(), validationProvider());
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7686
	@Test
	public void testSpecializesAnnotation() {
		
		String className = "Interceptor6";
		
		wizard.createCDIComponentWithContent(CDIWizardType.INTERCEPTOR, className, 
				getPackageName(), null, "/resources/quickfix/interceptor/" +
						"InterceptorWithSpecializes.java.cdi");

		editResourceUtil.replaceInEditor("InterceptorComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.SPECIALIZES, getProjectName(), validationProvider());
			
	}
	
}
