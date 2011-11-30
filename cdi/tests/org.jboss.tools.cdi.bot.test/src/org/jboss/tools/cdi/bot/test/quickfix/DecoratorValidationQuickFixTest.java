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
 * Test operates on quick fixes used for validation errors of CDI Decorator component
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", 
		server = @Server(state = ServerState.NotRunning, 
		version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class DecoratorValidationQuickFixTest extends QuickFixTestBase {
	

	@Override
	public String getProjectName() {
		return "CDIQuickFixDecoratorTest";
	}
	
	// https://issues.jboss.org/browse/JBIDE-7680
	@Test
	public void testSessionAnnotation() {
			
		String className = "Decorator1";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
						"DecoratorWithStateless.java.cdi");
		
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.STATELESS, CDIWizardType.DECORATOR);
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7636
	@Test
	public void testNamedAnnotation() {
		
		String className = "Decorator2";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
						"DecoratorWithNamed.java.cdi");
	
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.NAMED, CDIWizardType.DECORATOR);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7683
	@Test
	public void testProducer() {
		
		String className = "Decorator3";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
						"DecoratorWithProducer.java.cdi");
		
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.PRODUCES, CDIWizardType.DECORATOR);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7684
	@Test
	public void testDisposesAnnotation() {
		
		String className = "Decorator4";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
						"DecoratorWithDisposes.java.cdi");
		
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.DISPOSES, CDIWizardType.DECORATOR);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7685
	@Test
	public void testObservesAnnotation() {
		
		String className = "Decorator5";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
				"DecoratorWithDisposes.java.cdi");
		
		editResourceUtil.replaceInEditor("import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		editResourceUtil.replaceInEditor("@Disposes", "@Observes");
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.OBSERVES, CDIWizardType.DECORATOR);
			
	}
	
	// https://issues.jboss.org/browse/JBIDE-7686
	@Test
	public void testSpecializesAnnotation() {
		
		String className = "Decorator6";
		
		wizard.createCDIComponentWithContent(CDIWizardType.DECORATOR, className, 
				getPackageName(), "java.util.set", "/resources/quickfix/decorator/" +
						"DecoratorWithSpecializes.java.cdi");
		
		editResourceUtil.replaceInEditor("DecoratorComponent", className);
		
		checkQuickFix(CDIAnnotationsType.SPECIALIZES, CDIWizardType.DECORATOR);
			
	}
	
}