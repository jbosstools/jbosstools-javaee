package org.jboss.tools.seam.ui.bot.test.validate;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ELExprValidator extends AbstractSeamTestBase {

	public static String NL = System.getProperty("line.separator");
	
	@Test
	@Category(WARTests.class)	
	public void testELExprWar() {
		testELExpr(TestControl.TYPE_WAR);	
	}

	@Test
	@Category(EARTests.class)	
	public void testELExprEar() {
		testELExpr(TestControl.TYPE_EAR);	
	}
	
	private void testELExpr(String type) {
		
		SWTBotEclipseEditor editor = packageExplorer.openFile(testProjectName + type,
				"WebContent", "home.xhtml").toTextEditor();
		
		// add correct expression		
		int idx = 0;
		for(String line : editor.getLines()) {
			if (line.contains("Welcome to Seam")) {
				editor.insertText(idx, 0, "#{identity.hasRole('admin')}" + NL);
				break;
			}
			idx++;
		}		
		editor.save();
		SWTTestExt.util.waitForNonIgnoredJobs(60000);	

		// check that there is no problem
		problems.show();
		SWTBotTreeItem[] sProblems = ProblemsView.getFilteredErrorsTreeItems(bot, "hasR", 
				"/" + testProjectName + type, "home.xhtml", "JSF EL Problem");
		assertTrue("JSF-EL problem found.", ( (sProblems == null) || (sProblems.length == 0) )); 
		
		// add incorrect expression		
		editor.insertText(idx, 0, "#{identity.hasR('admin')}" + NL);
		editor.save();
		SWTTestExt.util.waitForNonIgnoredJobs(60000);	
			
		// check that JSF EL Problem exists		
		problems.show();
		SWTBotTreeItem[] sWarnings = ProblemsView.getFilteredWarningsTreeItems(bot, "hasR", 
				"/" + testProjectName + type, "home.xhtml", "EL Knowledge Base Problem");
		assertTrue("No JSF-EL problem found.", ( (sWarnings != null) && (sWarnings.length > 0) )); 
		assertTrue("More than oneJSF-EL problem found.", sWarnings.length <= 1);
		
		
		editor.close();
	}
	
}
