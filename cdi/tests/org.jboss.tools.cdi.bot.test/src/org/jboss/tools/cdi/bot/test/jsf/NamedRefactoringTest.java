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

package org.jboss.tools.cdi.bot.test.jsf;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on EL named refactoring  
 * 
 * @author Jaroslav Jankovic
 * 
 */

@Require(clearProjects = true, perspective = "Web Development", 
		 server = @Server(state = ServerState.NotRunning, 
		 version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class NamedRefactoringTest extends JSFTestBase {
	
	@Override
	public String getProjectName() {
		return "CDIRefactoring";
	}
				
	@Test
	public void testNamedAnnotation() {
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "ManagedBean", getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(NamedRefactoringTest.class.
				getResourceAsStream("/resources/jsf/ManagedBean.java.cdi"), false);
		
		createXHTMLPage("index.xhtml");		
		editResourceUtil.replaceClassContentByResource(NamedRefactoringTest.class.
				getResourceAsStream("/resources/jsf/index.xhtml.cdi"), false);

		
		
	}
	
}
