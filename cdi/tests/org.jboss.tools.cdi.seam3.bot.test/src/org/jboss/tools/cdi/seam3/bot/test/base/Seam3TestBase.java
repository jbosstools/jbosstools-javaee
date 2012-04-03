/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.base;

import java.io.IOException;
import java.util.logging.Level;

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.seam3.bot.test.Activator;
import org.jboss.tools.cdi.seam3.bot.test.CDISeam3AllBotTests;
import org.jboss.tools.cdi.seam3.bot.test.util.LibraryHelper;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;
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
@SuiteClasses({ CDISeam3AllBotTests.class })
public class Seam3TestBase extends CDITestBase {

	private String projectName = "CDISeam3Project";
	private String packageName = "cdi.seam";
	
	protected final LibraryHelper libraryHelper = new LibraryHelper();
	
	protected String getProjectName() {
		return projectName;
	}
	
	protected String getPackageName() {
		return packageName;
	}
	
	@Override
	public void prepareWorkspace() {
		if (!projectHelper.projectExists(getProjectName())) {
			importSeam3TestProject("/resources/projects/" + 
					getProjectName(), getProjectName());
		}
		
	}
	
	protected void importSeam3TestProject(String projectLocation, String dir) {
		projectImportHelper.importTestProject(projectLocation, dir, Activator.PLUGIN_ID);
	}
	
	protected void addAndCheckLibraryInProject(String projectName, SeamLibraries library) {
		addLibraryIntoProject(projectName, library.getName());
		checkLibraryInProject(projectName, library.getName());
	}
	
	protected void importSeam3ProjectWithLibrary(String projectName, SeamLibraries library) {
		importSeam3TestProject("/resources/projects/" + projectName, projectName);
		addAndCheckLibraryInProject(projectName, library);
		eclipse.cleanAllProjects();
	}
	
	private void addLibraryIntoProject(String projectName, String libraryName) {
		try {
			libraryHelper.addLibraryIntoProject(projectName, libraryName);			
			LOGGER.info("Library: \"" + libraryName + "\" copied");
			util.waitForNonIgnoredJobs();
			libraryHelper.addLibraryToProjectsClassPath(projectName, libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" on class path of project\"" + projectName + "\"");
		} catch (IOException exc) {
			LOGGER.log(Level.SEVERE, "Error while adding " + libraryName + " library into project");
			LOGGER.log(Level.SEVERE, exc.getMessage());
		}		
	}
	
	private void checkLibraryInProject(String projectName, String libraryName) {
		assertTrue(libraryHelper.isLibraryInProjectClassPath(projectName, libraryName));		
	}
	
}
