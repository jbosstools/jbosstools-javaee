package org.jboss.tools.seam.ui.bot.test.create;


import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CreateActions extends AbstractSeamTestBase {
	
	private static String TEST_TYPE = "Action";
	
	  public CreateActions() {
		}	
	
	@Test
	@Category(WARTests.class)
	public void testCreateActionForWar(){
		createSeamUnit(TEST_TYPE, TestControl.TYPE_WAR);		
	}

	@Test
	@Category(WARTests.class)
	public void checkCreateActionForWar(){
		checkSeamUnit(TEST_TYPE, TestControl.TYPE_WAR);		
	}
	
	@Test
	@Category(EARTests.class)
	public void testCreateActionForEar(){
		createSeamUnit(TEST_TYPE, TestControl.TYPE_EAR);		
	}

	@Test
	@Category(EARTests.class)
	public void checkCreateActionForEar(){
		checkSeamUnit(TEST_TYPE, TestControl.TYPE_EAR);		
	}
	
}
	