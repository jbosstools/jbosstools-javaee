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

import org.eclipse.swt.SWTException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.PerspectiveType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
* Test operates on CDI perspective
* 
* @author Jaroslav Jankovic
*/

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class, CDISmokeBotTests.class })
public class CDIPerspectiveTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIPerspectiveTest.class.getName());
	private enum CDIARTIFACTS {
		BEAN, QUALIFIER, STEREOTYPE, SCOPE, INTERBINDING, INTERCEPTOR, DECORATOR, ANNOTLITERAL, BEANSXML
	}
	
	@Override
	public void checkAndCreateProject() {
		if (!projectExists(getProjectName())) {
			createAndCheckCDIProject(bot, util, projectExplorer, getProjectName());
			eclipse.openPerspective(PerspectiveType.CDI);
			LOGGER.info("CDI perspective selected");
			bot.sleep(Timing.time2S());
		}	
	}
	
	@Override
	public String getProjectName() {
		return "CDIPerspectiveTest";
	}
		
	@Test
	public void testCDIArtifactBeanWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.BEAN));
	}
	
	@Test
	public void testCDIArtifactAnnotationLiteralWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.ANNOTLITERAL));
	}
	
	@Test
	public void testCDIArtifactBeanXMLWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.BEANSXML));
	}
	
	@Test
	public void testCDIArtifactDecoratorWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.DECORATOR));
	}
	
	@Test
	public void testCDIArtifactQualifierWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.QUALIFIER));
	}
	
	@Test
	public void testCDIArtifactScopeWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.SCOPE));
	}
	
	@Test
	public void testCDIArtifactStereoscopeWizard() {	
							
		assertTrue(openCDIArtifactsWizard(CDIARTIFACTS.STEREOTYPE));
	}
			
	private boolean openCDIArtifactsWizard(CDIARTIFACTS artifact) {
		
		SWTBotTree tree = packageExplorer.bot().tree();
		SWTBotTreeItem item = tree.getTreeItem(getProjectName());
		item.expand();
		boolean artifactWizardExists = true;
		try {
			CDIUtil.nodeContextMenu(tree, item, "New", getArtifactMenuItem(artifact)).click();
			bot.sleep(Timing.time500MS());
			bot.activeShell().bot().button("Cancel").click();
	
		} catch (SWTException exc) {			
			artifactWizardExists = false;
		}
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
		return artifactWizardExists;
	}
	
	private String getArtifactMenuItem(CDIARTIFACTS artifact) {
		switch (artifact) {
			case ANNOTLITERAL:
				return "Annotation Literal";
			case BEAN:
				return "Bean";
			case BEANSXML:
				return "File beans.xml";
			case DECORATOR:
				return "Decorator";
			case INTERBINDING:
				return "Interceptor Binding Annotation";
			case INTERCEPTOR:
				return "Interceptor";
			case QUALIFIER:
				return "Qualifier Annotation";
			case SCOPE:
				return "Scope Annotation";
			case STEREOTYPE:
				return "Stereotype Annotation";
			default:
				return null;
		}
	}
			
}
