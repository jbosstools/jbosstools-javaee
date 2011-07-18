package org.jboss.tools.seam.ui.bot.test.examples;

import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;

@SWTBotTestRequires(server=@Server(state=ServerState.Running),seam=@Seam(version="2.2"))
public class Booking22EAR extends SeamExample {

	@Override
	public String[] getProjectNames() {
		return new String[] {"booking22","booking22-ear","booking22-ejb","booking22-test"};
	}

	@Override
	public String getExampleName() {
		return "Seam 2.2 Booking Example - EAR (including a tutorial)";
	}
	@Override
	protected void executeExample() {
		runExample();
		checkDeployment("http://localhost:8080/booking22/home.seam","About this example application");
		execSeamTestNG(getProjectNames()[getProjectNames().length-1], "testngjdk6.launch", "testngjdk6");
	}
}
