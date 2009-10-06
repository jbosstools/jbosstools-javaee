package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateForms extends TestControl{
	
	private static String TEST_TYPE = "Form";
	
	public void testCreateFormFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
/*	public void testCreateFormFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
	}

	public void testCreateFormFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
	}
	
	public void testCreateFormFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
	}
	
	public void testCreateFormFor21war(){
		createSeamUnit(TEST_TYPE, seam21Settings, TYPE_WAR);		
	}

	public void testCreateFormFor21ear(){
		createSeamUnit(TEST_TYPE, seam21Settings, TYPE_EAR);		
	}*/
}
	