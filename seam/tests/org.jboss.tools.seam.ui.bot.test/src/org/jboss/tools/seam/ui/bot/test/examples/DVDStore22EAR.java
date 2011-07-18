package org.jboss.tools.seam.ui.bot.test.examples;

import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;

@SWTBotTestRequires(server=@Server(state=ServerState.Running),seam=@Seam(version="2.2"))
public class DVDStore22EAR extends SeamExample {

	@Override
	public String[] getProjectNames() {
		return new String[] {"dvdstore22","dvdstore22-ear","dvdstore22-ejb","dvdstore22-test"};
	}
	@Override
	public String getExampleName() {
		return "Seam 2.2 DVD Store Example - EAR (including a test project)";
	}
	@Override
	protected void executeExample() {
		runExample();
		checkDeployment("http://localhost:8080/dvdstore22/home","Welcome to the DVD Store");
		execSeamTestNG(getProjectNames()[getProjectNames().length-1], "testngjdk6.launch", "testngjdk6");
	}
}
