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
 * Test operates on quick fixes used for validation errors of CDI Scope component
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class ScopeValidationQuickFixTest extends QuickFixTestBase {
	
	@Override
	public String getProjectName() {
		return "CDIQuickFixScopeTest";
	}
	
	// https://issues.jboss.org/browse/JBIDE-7633
	@Test
	public void testTargetAnnotation() {
		
		String className = "Scope1";
		
		wizard.createCDIComponent(CDIWizardType.SCOPE, className, getPackageName(), null);
		
		editResourceUtil.replaceInEditor("@Target({ TYPE, METHOD, FIELD })", 
				"@Target({ TYPE, FIELD })");
		
		checkQuickFix(CDIAnnotationsType.TARGET, CDIWizardType.SCOPE);
		
		editResourceUtil.replaceInEditor("@Target({TYPE, METHOD, FIELD})", "");
		
		checkQuickFix(CDIAnnotationsType.TARGET, CDIWizardType.SCOPE);
	}
	
	// https://issues.jboss.org/browse/JBIDE-7631
	@Test
	public void testRetentionAnnotation() {
		
		String className = "Scope2";

		wizard.createCDIComponent(CDIWizardType.SCOPE, className, getPackageName(), null);
				
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "@Retention(CLASS)");
		
		checkQuickFix(CDIAnnotationsType.RETENTION, CDIWizardType.SCOPE);
		
		editResourceUtil.replaceInEditor("@Retention(RUNTIME)", "");
		
		checkQuickFix(CDIAnnotationsType.RETENTION, CDIWizardType.SCOPE);
		
	}
	
}
