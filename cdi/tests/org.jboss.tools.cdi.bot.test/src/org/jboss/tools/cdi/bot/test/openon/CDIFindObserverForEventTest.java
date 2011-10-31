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
package org.jboss.tools.cdi.bot.test.openon;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.quickfix.CDIQuickFixTest;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
* Test operates on CDI perspective
* 
* @author Jaroslav Jankovic
*/

@Require(clearProjects = false, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIFindObserverForEventTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIFindObserverForEventTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "cdi";	
	
	
	@BeforeClass
	public static void checkAndCreateProject() {
		if (!projectExists(PROJECT_NAME)) {
			createAndCheckCDIProject(bot, util, projectExplorer,PROJECT_NAME);
		}		
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
		
	//not implemented yet
	@Test
	public void testSimpleObserverFinding() {	
		prepareSimpleObserverFinding();
				
	}
	//not implemented yet	
	@Test
	public void testComplexObserverFinding() {	
		prepareComplexObserverFinding();		
	}
	
	private void prepareSimpleObserverFinding() {
		
	}
	
	private void prepareComplexObserverFinding() {
		
	}
}
