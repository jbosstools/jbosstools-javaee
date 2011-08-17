package org.jboss.tools.jsf.ui.bot.test.jsf2.refactor;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public abstract class JSF2AbstractRefactorTest extends JSFAutoTestCase {

	protected static final String JSF2_Test_Page_Name = "jsf2TestPage"; //$NON-NLS-1$

	protected void createCompositeComponent() throws Exception {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		try {
			tree.expandNode(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (WidgetNotFoundException e) {
			tree.getTreeItem(JBT_TEST_PROJECT_NAME).expandNode("WebContent").select(); //$NON-NLS-1$ //$NON-NLS-2$
			bot.menu("File").menu("New").menu("Folder").click(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.textWithLabel("Folder name:").setText("resources"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Finish").click(); //$NON-NLS-1$
		}
		try {
			tree.expandNode(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").expandNode("mycomp").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} catch (WidgetNotFoundException e) {
			tree.getTreeItem(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.menu("File").menu("New").menu("Folder").click(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.textWithLabel("Folder name:").setText("mycomp"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Finish").click(); //$NON-NLS-1$
		}
		try {
			tree.expandNode(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").expandNode("mycomp").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} catch (WidgetNotFoundException e) {
			tree.getTreeItem(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.menu("File").menu("New").menu("Folder").click(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.textWithLabel("Folder name:").setText("mycomp"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Finish").click(); //$NON-NLS-1$
		}
		try {
			tree.expandNode(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").expandNode("mycomp").expandNode("echo.xhtml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		} catch (WidgetNotFoundException e) {
			tree.getTreeItem(JBT_TEST_PROJECT_NAME).expandNode("WebContent").expandNode("resources").expandNode("mycomp").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			bot.menu("File").menu("New").menu("Other...").click(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.shell("New").activate(); //$NON-NLS-1$
			tree = bot.tree();
			tree.expandNode("JBoss Tools Web").select("XHTML Page"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Next >").click(); //$NON-NLS-1$
			bot.textWithLabel("File name:").setText("echo"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Finish").click(); //$NON-NLS-1$
			bot.sleep(2000);
			SWTBotEclipseEditor editor = bot
					.editorByTitle("echo.xhtml").toTextEditor(); //$NON-NLS-1$
			bot.menu("Edit").menu("Select All").click(); //$NON-NLS-1$ //$NON-NLS-2$
			bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$//$NON-NLS-2$
			bot.sleep(2000);
			editor.setText(loadFileContent("refactor/compositeComponent.html")); //$NON-NLS-1$
			editor.save();
			bot.sleep(2000);
		}
	}

	protected void createTestPage() throws Exception {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		try {
			tree.expandNode(JBT_TEST_PROJECT_NAME).expandNode("WebContent"). //$NON-NLS-1$ //$NON-NLS-2$
					getNode(JSF2_Test_Page_Name + ".xhtml").doubleClick(); //$NON-NLS-1$
		} catch (WidgetNotFoundException e) {
			tree.getTreeItem(JBT_TEST_PROJECT_NAME).select(); //$NON-NLS-1$
			bot.menu("File").menu("New").menu("Other...").click(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.shell("New").activate(); //$NON-NLS-1$
			tree = bot.tree();
			tree.expandNode("JBoss Tools Web").select("XHTML Page"); //$NON-NLS-1$ //$NON-NLS-2$
			bot.button("Next >").click(); //$NON-NLS-1$
			bot.textWithLabel("File name:").setText(JSF2_Test_Page_Name); //$NON-NLS-1$
			bot.button("Finish").click(); //$NON-NLS-1$
		}
		SWTBotEclipseEditor editor = bot.editorByTitle(
				JSF2_Test_Page_Name + ".xhtml").toTextEditor(); //$NON-NLS-1$
		editor.setFocus();
		bot.sleep(2000);
		bot.menu("Edit").menu("Select All").click(); //$NON-NLS-1$ //$NON-NLS-2$
		bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$//$NON-NLS-2$
		bot.sleep(2000);
		editor.setText(loadFileContent("refactor/jsf2TestPage.html")); //$NON-NLS-1$
		bot.sleep(2000);
		editor.saveAndClose();
	}

}
