package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CreateForms extends AbstractSeamTestBase {
	
	private static String TEST_TYPE = "Form";
	
	  public CreateForms() {
		}
	
	@Test
	@Category(WARTests.class)
	public void testCreateFormForWar(){
		createSeamUnit(TEST_TYPE, TestControl.TYPE_WAR);		
	}
	
	@Test
	@Category(WARTests.class)
	public void checkCreateFormForWar(){
		checkSeamUnit(TEST_TYPE, TestControl.TYPE_WAR);		
	}
	
	@Test
	@Category(EARTests.class)
	public void testCreateFormForEar(){
		createSeamUnit(TEST_TYPE, TestControl.TYPE_EAR);		
	}

	@Test
	@Category(EARTests.class)
	public void checkCreateFormForEar(){
		checkSeamUnit(TEST_TYPE, TestControl.TYPE_EAR);		
	}
	
}
	