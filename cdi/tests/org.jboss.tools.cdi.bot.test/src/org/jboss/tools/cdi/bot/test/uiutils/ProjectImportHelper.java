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

package org.jboss.tools.cdi.bot.test.uiutils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;

public class ProjectImportHelper {

	private SWTBotExt bot = new SWTBotExt();
	
	private SWTUtilExt util = new SWTUtilExt(bot);
	
	public void importTestProject(String projectLocation, String dir, String activatorPlugIn) {
		String rpath = ResourceHelper.getResourceAbsolutePath(activatorPlugIn, projectLocation);
		String wpath = ResourceHelper.getWorkspaceAbsolutePath() + "/" + dir;
		File rfile = new File(rpath);
		File wfile = new File(wpath);
		
		wfile.mkdirs();
		try {
			FileHelper.copyFilesBinaryRecursively(rfile, wfile, null);
		} catch (IOException e) {
			fail("Unable to copy test project");
		}
		ImportHelper.importAllProjects(wpath);
		util.waitForNonIgnoredJobs();	
	}
	
//	public void importTestProject(String projectLocation, String activatorPlugIn) {
//			importTestProject(projectLocation, projectLocation);
//	}
	
}
