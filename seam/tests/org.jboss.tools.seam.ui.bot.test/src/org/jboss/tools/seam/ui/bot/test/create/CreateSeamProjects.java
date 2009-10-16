package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateSeamProjects extends TestControl{
	
	public void testCreateSeamProject12war(){
		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam12Settings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
	public void testCreateSeamProject12ear(){
		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam12Settings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
	public void testCreateSeamProject2fpwar(){
		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam2fpSettings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
	public void testCreateSeamProject2fpear(){
		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam2fpSettings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
	public void testCreateSeamProject21war(){
		createSeamProject(seam21Settings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam21Settings.getProperty("testProjectName")
				+TYPE_WAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
	public void testCreateSeamProject21ear(){
		createSeamProject(seam21Settings, jbossEAPRuntime, TYPE_EAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS, VALIDATION + 
				" " + seam21Settings.getProperty("testProjectName")
				+TYPE_EAR, DEPLOY_SOURCE, REG_IN_SERVER);
	}
	
}