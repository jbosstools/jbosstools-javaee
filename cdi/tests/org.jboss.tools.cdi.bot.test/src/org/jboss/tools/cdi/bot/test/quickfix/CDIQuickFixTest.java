package org.jboss.tools.cdi.bot.test.quickfix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on quick fixes of CDI components
 * 
 * @author Jaroslav Jankovic
 */

@Require(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIQuickFixTest extends SWTTestExt {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";

	@BeforeClass
	public static void setup() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		CDIUtil.createAndCheckCDIProject(bot, util, projectExplorer, PROJECT_NAME);
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}

	@Test
	public void testSerializableQF() {
		CDIUtil.bean(PACKAGE_NAME, "B1", true, false, false, false, null, null,
				null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		assertTrue(("B1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class B1 {"));

		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/B1.java.cdi"), false);
		assertContains("@SessionScoped", ed.toTextEditor().getText());
		SWTBotTreeItem[] warningTrees = ProblemsView.
				getFilteredWarningsTreeItems(bot, "Managed bean B1 which", "/"
						+ PROJECT_NAME, "B1.java", "CDI Problem");
		assertTrue(warningTrees.length == 1);
		
		CDIUtil.resolveQuickFix(warningTrees[0], bot, util);
		SWTBotEclipseEditor eclEditor = ed.toTextEditor();
		assertTrue(eclEditor.getText().contains("import java.io.Serializable;"));
		warningTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Managed bean B1 which", "/" + PROJECT_NAME, "B1.java",
				"CDI Problem");
		assertTrue(warningTrees.length == 0);	
	}

	
	@Test
	public void testMultipleBeansQF() {
		CDIUtil.bean(PACKAGE_NAME, "Animal", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		assertTrue(("Animal.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Animal {"));

		CDIUtil.bean(PACKAGE_NAME, "Dog", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/Dog.java.cdi"), false);
		assertTrue(("Dog.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Dog extends Animal {"));

		CDIUtil.qualifier(PACKAGE_NAME, "Q1", false, false).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		assertTrue(("Q1.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
	
		CDIUtil.bean(PACKAGE_NAME, "BrokenFarm", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"), false);
		assertTrue(("BrokenFarm.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class BrokenFarm {"));
		assertTrue(code.contains("@Inject private Animal animal;"));
	
		SWTBotTreeItem[] warningTrees = ProblemsView
				.getFilteredWarningsTreeItems(bot, "Multiple beans are eligible", "/"
						+ PROJECT_NAME, "BrokenFarm.java", "CDI Problem");
		assertTrue("Warnings node should contain only one record instead of "
				+ warningTrees.length + " records.", warningTrees.length == 1);
		
		CDIUtil.openQuickFix(warningTrees[0], bot, util);
		String qualifBean = null;
		if (bot.table(0).cell(0, 0).contains("Animal")) {
			qualifBean = "Animal";
		}else {
			qualifBean = "Dog";
		}
		bot.activeShell().bot().button("Finish").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
		assertFalse(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());	
		bot.table(0).click(bot.table(0).indexOf("Q1 - " + PACKAGE_NAME), 0);
		assertTrue(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());
		bot.clickButton("Add >");
		assertTrue(bot.button("Finish").isEnabled());
		bot.clickButton("Finish");
		
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
		code = ed.toTextEditor().getText();
		assertTrue(code.contains("@Inject @Q1 private Animal animal;"));
		code = bot.editorByTitle(qualifBean + ".java").toTextEditor().getText();
		assertTrue(code.contains("@Q1"));
		warningTrees = ProblemsView
				.getFilteredWarningsTreeItems(bot, "Multiple beans are eligible", "/"
						+ PROJECT_NAME, "BrokenFarm.java", "CDI Problem");
		assertTrue(warningTrees.length == 0);
	}
}
