/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.struts.ui.bot.test.smoke;

import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.WidgetFinderHelper;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;

/**
 * Test running Struts Project on Server
 * 
 * @author Vladimir Pakan
 * @author Lukas Jungmann
 * 
 */
public class RunStrutsProjectOnServer extends SWTTestExt {
	private SWTJBTExt swtJbtExt = null;

	public RunStrutsProjectOnServer() {
		swtJbtExt = new SWTJBTExt(bot);
	}

	/**
	 * Test running Struts Project on Server
	 */
	@Test
	public void testRunStrutsProjectOnServer() {
		// Start Application Server
		if (!configuredState.getServer().isRunning) {
			servers.startServer(configuredState.getServer().name);
			configuredState.getServer().isRunning = true;
		}
		swtJbtExt.runProjectOnServer(StrutsAllBotTests.STRUTS_PROJECT_NAME);
		// Check Browser Content
		String browserText = WidgetFinderHelper.browserInEditorText(bot,
				"KickStart: Input name", true);
		swtJbtExt.stopApplicationServer(0);
		swtJbtExt.removeProjectFromServers(StrutsAllBotTests.STRUTS_PROJECT_NAME);
		SWTJBTExt.deleteApplicationServer(bot, 0);
		assertTrue("Displayed HTML page has wrong content. Application was not" +
				" deployed by struts tools - known issue JBIDE-11306",
				(browserText != null)
					&& (browserText.indexOf("<TITLE>KickStart: Input name</TITLE>") > -1));
	}

}
