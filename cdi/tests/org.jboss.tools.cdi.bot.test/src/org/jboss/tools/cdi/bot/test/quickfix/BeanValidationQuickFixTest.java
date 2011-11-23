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

package org.jboss.tools.cdi.bot.test.quickfix;


import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on quick fixes used for validation errors of CDI bean component
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", 
		server = @Server(state = ServerState.NotRunning, 
		version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class BeanValidationQuickFixTest extends QuickFixTestBase {
	

	@Override
	public String getProjectName() {
		return "CDIQuickFixBeanTest";
	}
	
	// https://issues.jboss.org/browse/JBIDE-8550
	@Test
	public void testSerializableManagedBean() {
		
		String className = "ManagedBean";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource( BeanValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/SerializableBean.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		checkQuickFix(CDIAnnotationsType.SERIALIZABLE, CDIWizardType.BEAN);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7635
	@Test	
	public void testMultipleBeansQF() {
		String animalClassName = "Animal";
		String dogClassName = "Dog";
		String brokenFarmClassName = "BrokenFarm";
		
		
		wizard.createCDIComponent(CDIWizardType.BEAN, brokenFarmClassName, getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Q1", getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, animalClassName, getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, dogClassName, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(BeanValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/Dog.java.cdi"), false);
		
		bot.editorByTitle(brokenFarmClassName + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		editResourceUtil.replaceClassContentByResource(BeanValidationQuickFixTest.class
				.getResourceAsStream("/resources/quickfix/BrokenFarm.java.cdi"),
				false);
		
		bot.sleep(TIME_1S);
		util.waitForNonIgnoredJobs();
		
		resolveMultipleBeans();
		
		checkMultipleBean();
	}

	
	// https://issues.jboss.org/browse/JBIDE-7664
	@Test
	public void testConstructor() {
		
		String className = "Bean1";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/ConstructorWithParam.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		checkQuickFix(CDIAnnotationsType.DISPOSES, CDIWizardType.BEAN);
		
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/ConstructorWithParam.java.cdi"), false);
		
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		checkQuickFix(CDIAnnotationsType.OBSERVES, CDIWizardType.BEAN);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7665
	@Test
	public void testProducer() {
		
		String className = "Bean2";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/ProducerWithParam.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		checkQuickFix(CDIAnnotationsType.DISPOSES, CDIWizardType.BEAN);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/ProducerWithParam.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		
		checkQuickFix(CDIAnnotationsType.OBSERVES, CDIWizardType.BEAN);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectDisposer() {
			
		String className = "Bean3";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/BeanInjectDisposes.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		checkQuickFix(CDIAnnotationsType.DISPOSES, CDIWizardType.BEAN);
				
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectObserver() {
		
		String className = "Bean4";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/BeanInjectDisposes.java.cdi"), false);
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		checkQuickFix(CDIAnnotationsType.OBSERVES, CDIWizardType.BEAN);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7667
	@Test
	public void testInjectProducer() {
		
		String className = "Bean5";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/BeanInjectProducer.java.cdi"), false);
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
			
		checkQuickFix(CDIAnnotationsType.PRODUCES, CDIWizardType.BEAN);
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7668
	@Test
	public void testObserverWithDisposer() {
			
		String className = "Bean6";
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/bean/ObserverWithDisposer.java.cdi"), 
				false);
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
			
		checkQuickFix(CDIAnnotationsType.OBSERVES, CDIWizardType.BEAN);
			
	}
	
}
