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

package org.jboss.tools.cdi.seam3.bot.test.util;

import java.io.File;
import java.io.IOException;

import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.seam3.bot.test.Activator;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;

public class ProjectImportHelper extends CDIBase {

	public void importTestProject(String dir) {
		String rpath = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, dir);
		String wpath = ResourceHelper.getWorkspaceAbsolutePath() + dir;
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
	
}
