package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateForms extends TestControl{
	
	private static String TEST_TYPE = "Form";
	
	public void testCreateFormFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
	public void testCreateFormFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
	}

	public void testCreateFormFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
	}
	
	public void testCreateFormFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
	}
	
	public void testCreateFormFor22war(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_WAR);		
	}

	public void testCreateFormFor22ear(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_EAR);		
	}
}
	