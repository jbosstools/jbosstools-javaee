package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateActions extends TestControl{
	
	private static String TEST_TYPE = "Action";
	
	public void testCreateActionFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
	public void testCreateActionFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
	}

	public void testCreateActionFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
	}
	
	public void testCreateActionFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
	}
	
	public void testCreateActionFor22war(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_WAR);		
	}

	public void testCreateActionFor22ear(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_EAR);		
	}
}
	