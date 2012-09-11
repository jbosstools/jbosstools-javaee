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
import org.jboss.tools.cdi.bot.test.quickfix.validators.BeanValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.junit.Test;

/**
 * Test operates on quick fixes used for validation errors of CDI bean component
 * 
 * @author Jaroslav Jankovic
 */

public class BeanValidationQuickFixTest extends CDITestBase {
	
	private static IValidationProvider validationProvider = new BeanValidationProvider();
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	// https://issues.jboss.org/browse/JBIDE-8550
	@Test
	public void testSerializableManagedBean() {
		
		String className = "ManagedBean";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/SerializableBean.java.cdi");
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		quickFixHelper.checkQuickFix(ValidationType.SERIALIZABLE, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7664
	@Test
	public void testConstructor() {
		
		String className = "Bean1";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/ConstructorWithParam.java.cdi");		
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		quickFixHelper.checkQuickFix(ValidationType.DISPOSES, getProjectName(), validationProvider());
		
		editResourceUtil.replaceClassContentByResource(BeanValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/bean/ConstructorWithParam.java.cdi"), false);
		
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		quickFixHelper.checkQuickFix(ValidationType.OBSERVES, getProjectName(), validationProvider());
	}
	
	// https://issues.jboss.org/browse/JBIDE-7665
	@Test
	public void testProducer() {
		
		String className = "Bean2";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/ProducerWithParam.java.cdi");
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.DISPOSES, getProjectName(), validationProvider());
		
		editResourceUtil.replaceClassContentByResource(BeanValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/bean/ProducerWithParam.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		
		quickFixHelper.checkQuickFix(ValidationType.OBSERVES, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectDisposer() {
			
		String className = "Bean3";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/BeanInjectDisposes.java.cdi");
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.DISPOSES, getProjectName(), validationProvider());
				
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectObserver() {
		
		String className = "Bean4";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/BeanInjectDisposes.java.cdi");
		
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		quickFixHelper.checkQuickFix(ValidationType.OBSERVES, getProjectName(), validationProvider());
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectProducer() {
		
		String className = "Bean5";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/BeanInjectProducer.java.cdi");
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
			
		quickFixHelper.checkQuickFix(ValidationType.PRODUCES, getProjectName(), validationProvider());
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7668
	@Test
	public void testObserverWithDisposer() {
			
		String className = "Bean6";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/quickfix/bean/ObserverWithDisposer.java.cdi");
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
			
		quickFixHelper.checkQuickFix(ValidationType.OBSERVES, getProjectName(), validationProvider());
			
	}
	
}
