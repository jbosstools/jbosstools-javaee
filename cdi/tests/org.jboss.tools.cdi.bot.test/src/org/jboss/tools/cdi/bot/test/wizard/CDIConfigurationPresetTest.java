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
package org.jboss.tools.cdi.bot.test.wizard;


import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
* Test checks if CDI configuration preset sets CDI support correctly
* 
* @author Jaroslav Jankovic
*/

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class , CDISmokeBotTests.class })
public class CDIConfigurationPresetTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIConfigurationPresetTest.class.getName());
	private static final String PROJECT_NAME = "CDIPresetProject";	
		
	@After
	public void waitForJobs() {		
		jbt.deleteProject(PROJECT_NAME);
	}
	
	@Test
	public void testCDIPreset() {
		createCDIProjectWithCDIPreset(util, PROJECT_NAME);
		LOGGER.info("Dynamic Web Project with CDI Configuration Preset created");
		assertTrue(checkCDISupport(PROJECT_NAME));
	}
	
	private boolean checkCDISupport(String projectName) {
		projectExplorer.selectProject(projectName);
		
		SWTBotTree tree = projectExplorer.bot().tree();
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Properties",false)).click();
	    
	    bot.tree().expandNode("CDI (Context and Dependency Injection) Settings").select();	    	    
		boolean isCDISupported = bot.checkBox().isChecked();
		bot.button("Cancel").click();
		return isCDISupported;
	}
		
	
}
