package org.jboss.tools.seam.ui.bot.test.misc;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.test.SWTJBTBot;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class GotoComponent extends AbstractSeamTestBase {

    @Test
	@Category(WARTests.class)
	public void testGotoComponentWar() {
		testGotoComponent(TestControl.TYPE_WAR);		
	}

    @Test
	@Category(EARTests.class)
	public void testGotoComponentEar() {
		testGotoComponent(TestControl.TYPE_EAR);		
	}
    
	private void testGotoComponent(String type) {
		SWTJBTBot bot = new SWTJBTBot();
		bot.menu("Navigate").menu("Open Seam Component").click();
		bot.text().setText("authenticator");
		SWTBotTableItem tabItem = bot.table().getTableItem("authenticator - " + AbstractSeamTestBase.testProjectName + type + 
				((type == TestControl.TYPE_EAR) ? "-ejb" : ""));
		tabItem.select();
		bot.button("OK").click();
		
		SWTBotEditor editor = bot.activeEditor();
		assertTrue("Authenticator component not activated.", editor.getTitle().equals("Authenticator.java"));
		editor.close();
	}
}
