package org.jboss.tools.seam.ui.bot.test.validate;

import java.util.Properties;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ComponentsValidator extends AbstractSeamTestBase {

	public static String NL = System.getProperty("line.separator");
	
	public ComponentsValidator() {
	}
	
	@Test
	@Category(WARTests.class)	
	public void testAddComponentPropertyWar() {
		testAddComponentProperty(TestControl.TYPE_WAR);
	
	}

	@Test
	@Category(EARTests.class)
	public void testAddComponentPropertyEar() {
		testAddComponentProperty(TestControl.TYPE_EAR);		
	}
	
	private void testAddComponentProperty(String type) {
		// open components.xml
		SWTBotEclipseEditor cEditor = projectExplorer.openFile(testProjectName + type,
				"Web Resources : WebContent", "WEB-INF", "components.xml").toTextEditor();
		
		
		// add non-existing property		
		int idx = 0;
		for(String line : cEditor.getLines()) {
			if (line.contains("</components")) {
				cEditor.insertText(idx, 0,     "<component name=\"authenticator\">" + NL);
				cEditor.insertText(idx + 1, 0, "<property name=\"foo\">TEST</property>" + NL);
				cEditor.insertText(idx + 2, 0, "</component>" + NL);
			}
			idx++;
		}		
		cEditor.save();
		SWTTestExt.util.waitForNonIgnoredJobs(60000);	
		
		// check that Seam Problem exists		
		SWTBotView pBotView = problems.show();
		SWTBotTreeItem[] sProblems = problems.getFilteredErrorsTreeItems(bot, "does not have a setter or a field", 
				"/" + testProjectName, "components.xml", "Seam Problem");
		assertTrue("No Seam problem found.", ( (sProblems != null) && (sProblems.length > 0) )); 
		assertTrue("More than one Seam problem found.", sProblems.length <= 1);
		
		// add property
		
		SWTBotEclipseEditor aEditor;
		if (type == TestControl.TYPE_EAR)		
			aEditor = projectExplorer.openFile(testProjectName + type + "-ejb", 
					"ejbModule", "org.domain.seamprjear.session", "Authenticator.java").toTextEditor();
		else
			aEditor = projectExplorer.openFile(testProjectName + type, 
					"src", "hot", "org", "domain", "seamprjwar", "session", "Authenticator.java").toTextEditor();
			
		idx = 0;
		for(String line : aEditor.getLines()) {
			if (line.contains("public boolean authenticate()")) {
				aEditor.insertText(idx, 0, "public String foo; \n");
				break;
			}
			idx++;
		}		
		aEditor.save();		
		SWTTestExt.util.waitForNonIgnoredJobs(60000);

		// check that Seam Problem disappeared		
		problems.show();
		sProblems = problems.getFilteredErrorsTreeItems(bot, "does not have a setter or a field", 
				"/" + testProjectName, "components.xml", "Seam Problem");
		assertTrue("Seam problem still exists.", sProblems == null);
		
		cEditor.close();
		aEditor.close();
		
	}
	
}
