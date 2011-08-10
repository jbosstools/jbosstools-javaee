package org.jboss.tools.cdi.bot.test.fix;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
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

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class
			.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";
	private static final String BEAN_NAME = "B1";

	
	@Test
	public void testSerializableQF() {		
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(PROJECT_NAME).finish();
		util.waitForNonIgnoredJobs();
		SWTBot v = eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTBotTree tree = v.tree();
		tree.setFocus();
		assertTrue("Project " + PROJECT_NAME + " was not created properly.",
				SWTEclipseExt.treeContainsItemWithLabel(tree, PROJECT_NAME));
		SWTBotTreeItem item = tree.getTreeItem(PROJECT_NAME);
		item.expand();
		new CDIUtil().nodeContextMenu(tree, item, "Configure",
				"Add CDI (Context and Dependency Injection) support...")
				.click();
		bot.activeShell().bot().button("OK").click();		
		util.waitForNonIgnoredJobs();
		new NewCDIFileWizard(CDIWizardType.BEAN).run().setPackage(PACKAGE_NAME)
				.setName(BEAN_NAME).finish();
		util.waitForNonIgnoredJobs();

		SWTBotEditor ed = bot.editorByTitle(BEAN_NAME + ".java");
		SWTBotEclipseEditor st = ed.toTextEditor();
		st.selectRange(0, 0, st.getText().length());
		InputStream javasrc = CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/" + BEAN_NAME + ".java.cdi");
		String code = readStream(javasrc);
		st.setText(code);
		ed.save();
		assertContains("@SessionScoped",ed.toTextEditor().getText());
	}

	private String readStream(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}
}
