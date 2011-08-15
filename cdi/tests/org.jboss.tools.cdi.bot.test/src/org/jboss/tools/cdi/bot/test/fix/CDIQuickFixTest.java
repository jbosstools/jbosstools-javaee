package org.jboss.tools.cdi.bot.test.fix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewCDIFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewFileWizardAction;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Before;
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
	private static final String BEAN_NAME = "B1";

	
	@Before
	public void setUp() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
	}
	
	@Test
	public void testSerializableQF() {	
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(PROJECT_NAME).finish();
		util.waitForNonIgnoredJobs();		
		SWTBotTree tree = bot.tree();
		tree.setFocus();
				
		assertTrue("Project " + PROJECT_NAME + " was not created properly.",
				SWTEclipseExt.treeContainsItemWithLabel(tree, PROJECT_NAME));
		SWTBotTreeItem item = tree.getTreeItem(PROJECT_NAME);
		item.expand();
				
		CDIUtil.addCDISupport(tree, item, bot, util);
				
		new NewCDIFileWizard(CDIWizardType.BEAN).run().setPackage(PACKAGE_NAME)
				.setName(BEAN_NAME).finish();
		util.waitForNonIgnoredJobs();

		SWTBotEditor ed = bot.editorByTitle(BEAN_NAME + ".java");
		assertNotNull("Bean: " + BEAN_NAME + " was not created properly.", ed);
		
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/" + BEAN_NAME + ".java.cdi"), false);				
		assertContains("@SessionScoped",ed.toTextEditor().getText());
		
		SWTEclipseExt.showView(bot, ViewType.PROBLEMS);
		bot.sleep(3 * TIME_1S);
		
		
		SWTBotTreeItem[] warningTrees = ProblemsView.getFilteredWarningsTreeItems(bot, "Managed bean B1 which", 
				"/" + PROJECT_NAME , BEAN_NAME + ".java", "CDI Problem");	
			
		assertNotNull("Warnings node should contain the expected problem.", warningTrees);
		assertTrue("Warnings node should contain only one record instead of " + warningTrees.length + " records.", 
				warningTrees.length == 1);
		
		CDIUtil.resolveQuickFix(bot.tree(), warningTrees[0], bot, util);
				
		SWTBotEclipseEditor eclEditor = ed.toTextEditor();
		assertTrue("Quick fix does not resolve issue properly.", 
					eclEditor.getText().contains("import java.io.Serializable;"));
		warningTrees = ProblemsView.getFilteredWarningsTreeItems(bot, "Managed bean B1 which", 
				"/" + PROJECT_NAME , BEAN_NAME + ".java", "CDI Problem");				
		assertTrue("Warnings should not contain resolved problem.", warningTrees.length == 0);
	}	
}
