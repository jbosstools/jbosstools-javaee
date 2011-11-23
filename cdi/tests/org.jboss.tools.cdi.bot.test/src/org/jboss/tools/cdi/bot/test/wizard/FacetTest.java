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

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
* Test checks if beans.xml is created when selecting CDI Facet
* 
* @author Jaroslav Jankovic
*/

@Require(clearProjects = true, perspective = "Java EE", 
		server = @Server(state = ServerState.NotRunning, 
		version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class , CDISmokeBotTests.class })
public class FacetTest extends CDITestBase {

	private static final Logger LOGGER = Logger.getLogger(FacetTest.class.getName());
	
	
	@Override	
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createDynamicWebProjectWithCDIFacets(getProjectName());
		}
	}
	
	@Override
	public String getProjectName() {
		return "CDIFacetsProject";
	}
	
	@Test
	public void testCDIFacet() {
		LOGGER.info("Dynamic Web Project with CDI Facet created");		
		assertTrue("Error: beans.xml should be created when selecting CDI Facet", 
				projectExplorer.isFilePresent(getProjectName(), "WebContent/WEB-INF/beans.xml".split("/")));
	}
		
}
