package org.jboss.tools.cdi.bot.test.wizard;

import java.util.logging.Logger;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
* Test checks if beans.xml is created when selectin CDI Facet
* 
* @author Jaroslav Jankovic
*/

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class , CDISmokeBotTests.class })
public class CDIFacetTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIFacetTest.class.getName());
	
	
	@Override	
	public void checkAndCreateProject() {
		if (!projectExists(getProjectName())) {
			createCDIProjectWithCDIFacets(util, getProjectName());
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
				projectExplorer.isFilePresent(getProjectName(), getBeansXmlLocation().split("/")));		
	}
	
	private String getBeansXmlLocation() {
		return "WebContent/WEB-INF/beans.xml";
	}
	
}
