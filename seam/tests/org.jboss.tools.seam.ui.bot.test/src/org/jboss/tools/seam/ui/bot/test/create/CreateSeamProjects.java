package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;

public class CreateSeamProjects extends TestControl{
  
  private SWTJBTExt swtJbtExt = new SWTJBTExt(bot);
	
	public void testCreateSeamProject12war(){
		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam12Settings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam12Settings.getProperty("testProjectName"));
	}
	
	public void testCreateSeamProject12ear(){
		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam12Settings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam12Settings.getProperty("testProjectName"));
	}
	
	public void testCreateSeamProject2fpwar(){
		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam2fpSettings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam2fpSettings.getProperty("testProjectName"));
	}
	
	public void testCreateSeamProject2fpear(){
		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam2fpSettings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam2fpSettings.getProperty("testProjectName"));
	}
	
	public void testCreateSeamProject22war(){
		createSeamProject(seam22Settings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam22Settings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam22Settings.getProperty("testProjectName"));
	}
	
	public void testCreateSeamProject22ear(){
		createSeamProject(seam22Settings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(240000, BUILDING_WS, VALIDATION + 
				" " + seam22Settings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
		swtJbtExt.removeSeamProjectFromServers(seam22Settings.getProperty("testProjectName"));
		new SWTUtilExt(bot).waitForAll(60 * 1000L);
	}
	
}