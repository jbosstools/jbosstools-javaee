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
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.helper.WidgetFinderHelper;
import org.jboss.tools.ui.bot.ext.view.ServersView;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;

/**
 * Test running Struts Project on Server
 * 
 * @author Vladimir Pakan
 * @author Lukas Jungmann
 * 
 */
@Require(
		clearProjects=false,
		clearWorkspace=false, 
		server = @Server(state = ServerState.NotRunning), perspective = "Web Development")
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
		
		ServersView serversView = new ServersView();
		serversView.cleanServer(configuredState.getServer().name);
		serversView.openInWebBrowser(configuredState.getServer().name, 
				StrutsAllBotTests.STRUTS_PROJECT_NAME);
		
		// Check Browser Content
		String browserText = WidgetFinderHelper.browserInEditorText(bot,
				"KickStart: Input name", true);
		
		// stop server, remove application from it and remove server as well
		swtJbtExt.stopApplicationServer(0);
		swtJbtExt.removeProjectFromServers(StrutsAllBotTests.STRUTS_PROJECT_NAME);
		
		assertTrue("Displayed HTML page has wrong content.",
				(browserText != null)
					&& (browserText.indexOf("KickStart: Input name") > -1));
	}

}
