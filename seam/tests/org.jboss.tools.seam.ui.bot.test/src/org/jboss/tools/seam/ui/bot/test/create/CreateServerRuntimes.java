package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateServerRuntimes extends TestControl{
	
	public void testCreateEAPServerRuntime(){
		createServerRuntime(jbossEAPRuntime);
		}
}