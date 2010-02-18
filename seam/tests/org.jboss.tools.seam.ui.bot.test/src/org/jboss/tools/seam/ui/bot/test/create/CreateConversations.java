package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateConversations extends TestControl{
	
	private static String TEST_TYPE = "Conversation";
	
	public void testCreateConversationFor12war(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_WAR);		
	}
	
	public void testCreateConversationFor12ear(){
		createSeamUnit(TEST_TYPE, seam12Settings, TYPE_EAR);		
	}

	public void testCreateConversationFor2fpwar(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_WAR);		
	}
	
	public void testCreateConversationFor2fpear(){
		createSeamUnit(TEST_TYPE, seam2fpSettings, TYPE_EAR);		
	}
	
	public void testCreateConversationFor22war(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_WAR);		
	}

	public void testCreateConversationFor22ear(){
		createSeamUnit(TEST_TYPE, seam22Settings, TYPE_EAR);		
	}
}
	