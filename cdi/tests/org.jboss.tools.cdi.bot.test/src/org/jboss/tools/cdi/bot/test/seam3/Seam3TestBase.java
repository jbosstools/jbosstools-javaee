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

package org.jboss.tools.cdi.bot.test.seam3;

import java.io.IOException;
import java.util.logging.Level;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISeam3AllBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@Require(clearProjects = true, perspective = "Java EE", 
server = @Server(state = ServerState.NotRunning, 
version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class, CDISeam3AllBotTests.class })
public class Seam3TestBase extends CDITestBase {

	private String projectName = "CDISeam3Project";
	private String packageName = "cdi.seam";
	
	protected String getProjectName() {
		return projectName;
	}
	
	protected String getPackageName() {
		return packageName;
	}
	
	@Override
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProject(getProjectName());
			addSeamSolderLibrary();
		}
	}
	
	protected void addSeamSolderLibrary() {
		addLibrary("seam-solder.jar");
		checkLibrary("seam-solder.jar");
	}
	
	private void addLibrary(String libraryName) {
		try {
			libraryUtil.addLibraryIntoProject(getProjectName(), libraryName);			
			LOGGER.info("Library: \"" + libraryName + "\" copied");
			util.waitForNonIgnoredJobs();
			libraryUtil.addLibraryToProjectsClassPath(getProjectName(), libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" on class path of project\"" + getProjectName() + "\"");
		} catch (IOException exc) {
			LOGGER.log(Level.SEVERE, "Error while adding seam solder library into project");
		}		
	}
	
	private void checkLibrary(String libraryName) {
		assertTrue(libraryUtil.isLibraryInProjectClassPath(getProjectName(), libraryName));		
	}
	
}
