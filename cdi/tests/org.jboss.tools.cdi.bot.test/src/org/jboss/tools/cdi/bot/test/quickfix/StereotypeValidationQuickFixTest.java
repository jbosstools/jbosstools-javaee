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
 * Test operates on quick fixes used for validation errors of CDI Stereotype component
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class StereotypeValidationQuickFixTest extends QuickFixTestBase {
	
	@Override
	public String getProjectName() {
		return "CDIQuickFixStereotypeTest";
	}
	
	// https://issues.jboss.org/browse/JBIDE-7630
	@Test
	public void testTargetAnnotation() {
		
		String className = "Stereotype1";
		
		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Target({ TYPE, METHOD, FIELD })", 
				"@Target({ TYPE, FIELD })");
		
		checkQuickFix(CDIAnnotationsType.TARGET, CDIWizardType.STEREOTYPE);

		editResourceUtil.replaceInEditor("@Target({TYPE, METHOD, FIELD})", "");
		
		checkQuickFix(CDIAnnotationsType.TARGET, CDIWizardType.STEREOTYPE);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7631
	@Test
	public void testRetentionAnnotation() {
		
		String className = "Stereotype2";

		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "@Retention(CLASS)");
		
		checkQuickFix(CDIAnnotationsType.RETENTION, CDIWizardType.STEREOTYPE);
				
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "");
		
		checkQuickFix(CDIAnnotationsType.RETENTION, CDIWizardType.STEREOTYPE);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7634
	@Test
	public void testNamedAnnotation() {
		
		String className = "Stereotype3";
		
		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/stereotype/StereotypeWithNamed.java.cdi"), false);
		editResourceUtil.replaceInEditor("StereotypeComponent", className);
		
		checkQuickFix(CDIAnnotationsType.NAMED, CDIWizardType.STEREOTYPE);
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7640
	@Test	
	public void testTypedAnnotation() {
		
		String className = "Stereotype4";
		
		wizard.createCDIComponent(CDIWizardType.STEREOTYPE, className, getPackageName(), null);
		
		editResourceUtil.replaceClassContentByResource(QuickFixTestBase.class
				.getResourceAsStream("/resources/quickfix/stereotype/StereotypeWithTyped.java.cdi"), false);
		editResourceUtil.replaceInEditor("StereotypeComponent", className);
		
		checkQuickFix(CDIAnnotationsType.TYPED, CDIWizardType.STEREOTYPE);
		
	}	

}
