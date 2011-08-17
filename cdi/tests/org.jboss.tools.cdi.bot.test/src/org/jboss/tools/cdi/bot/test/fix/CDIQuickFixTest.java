package org.jboss.tools.cdi.bot.test.fix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewFileWizardAction;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.entity.JavaClassEntity;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on quick fixes of CDI components
 * 
 * @author Jaroslav Jankovic
 */

@SWTBotTestRequires(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIQuickFixTest extends SWTTestExt {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";

	@Before
	public void setUp() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		createAndCheckCDIProject();
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
		assertTrue("Warnings node should contain only one record instead of "
				+ warningTrees.length + " records.", warningTrees.length == 1);
		
		CDIUtil.resolveQuickFix(bot.tree(), warningTrees[0], bot, util);
		SWTBotEclipseEditor eclEditor = ed.toTextEditor();
		assertTrue("Quick fix does not resolve issue properly.", eclEditor
				.getText().contains("import java.io.Serializable;"));
		warningTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Managed bean B1 which", "/" + PROJECT_NAME, "B1.java",
				"CDI Problem");
		assertTrue("Warnings should not contain resolved problem.",
				warningTrees.length == 0);	
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

		JavaClassEntity brokenFarm = new JavaClassEntity();
		brokenFarm.setClassName("BrokenFarm");
		brokenFarm.setPackageName(PACKAGE_NAME);
		eclipse.createJavaClass(brokenFarm);
		ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"),
				false);
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
		
		CDIUtil.resolveQuickFix(bot.tree(), warningTrees[0], bot, util);
		assertFalse("No qualifier has been chosen, Add button should not be active", 
				bot.button("Add >").isEnabled());
		assertFalse("No qualifier has been chosen, Finish button should not be active", 
				bot.button("Finish").isEnabled());
		SWTBotTable table = bot.table(0);		
		table.click(table.indexOf("Q1 - " + PACKAGE_NAME), 0);
		assertTrue("Qualifier has been chosen, Add button should be active", 
				bot.button("Add >").isEnabled());
		assertFalse("No qualifier has been chosen, Finish button should not be active", 
				bot.button("Finish").isEnabled());
		bot.clickButton("Add >");
		assertTrue("Qualifier has been chosen, Finish button should be active", 
				bot.button("Finish").isEnabled());
		bot.clickButton("Finish");
		
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
		code = ed.toTextEditor().getText();
		assertTrue(code.contains("@Inject @Q1 private Animal animal;"));
		code = bot.editorByTitle("Dog.java").toTextEditor().getText();
		assertTrue(code.contains("@Q1"));
		warningTrees = ProblemsView
				.getFilteredWarningsTreeItems(bot, "Multiple beans are eligible", "/"
						+ PROJECT_NAME, "BrokenFarm.java", "CDI Problem");
		assertTrue("Warnings node should not contain resolved problem.", warningTrees.length == 0);
	}

	private void createAndCheckCDIProject() {
		createCDIProject();
		SWTBotTree tree = bot.tree();
		assertTrue("Project " + PROJECT_NAME + " was not created properly.",
				SWTEclipseExt.treeContainsItemWithLabel(tree, PROJECT_NAME));
		SWTBotTreeItem item = tree.getTreeItem(PROJECT_NAME);
		item.expand();
		CDIUtil.addCDISupport(tree, item, bot, util);
	}

	private void createCDIProject() {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(PROJECT_NAME).finish();
		util.waitForNonIgnoredJobs();
	}
}
