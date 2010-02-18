package org.jboss.tools.seam.ui.bot.test.create;

import java.io.IOException;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateEntities extends TestControl{
	
	private static String TEST_TYPE = "Entity";
	
	public void testCreateEntityFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
	public void testCreateEntityFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
	}

	public void testCreateEntityFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
	}
	
	public void testCreateEntityFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
	}
	
	public void testCreateEntityFor22war(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_WAR);		
	}

	public void testCreateEntityFor22ear(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_EAR);
		// TODO: Remove it
	  try {
      System.in.read();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
	
}
	