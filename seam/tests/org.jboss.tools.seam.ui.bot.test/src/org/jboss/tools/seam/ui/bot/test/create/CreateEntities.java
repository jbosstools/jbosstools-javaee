package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateEntities extends TestControl{
	
	private static String TEST_TYPE = "Entity";
	
	public void testCreateEntityFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
/*	public void testCreateEntityFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
		waitForJobs();
	}

	public void testCreateEntityFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
		waitForJobs();
	}
	
	public void testCreateEntityFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
		waitForJobs();
	}
	
	public void testCreateEntityFor21war(){
		createSeamUnit(TEST_TYPE, seam21Settings, TYPE_WAR);		
		waitForJobs();
	}

	public void testCreateEntityFor21ear(){
		createSeamUnit(TEST_TYPE, seam21Settings, TYPE_EAR);		
		waitForJobs();
	}*/
}
	