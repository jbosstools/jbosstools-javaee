package org.jboss.tools.jsf.ui.bot.test.jsf2.refactor;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public class JSF2AttributeRenameTest extends JSF2AbstractRefactorTest {

	public void testJSF2AttributeRename() throws Exception {
		createCompositeComponent();
		createTestPage();
		renameCompositeAttribute();
		checkContent();
	}

	private void renameCompositeAttribute() {
		SWTBotEclipseEditor editor = bot
				.editorByTitle("echo.xhtml").toTextEditor(); //$NON-NLS-1$
		editor.selectRange(9, 29, 1);
		bot.menu("Refactor").menu("Rename").click(); //$NON-NLS-1$ //$NON-NLS-2$
		bot.shell("Rename Composite Attribute").activate(); //$NON-NLS-1$
		bot.textWithLabel("New name:").setText("echo1"); //$NON-NLS-1$ //$NON-NLS-2$
		bot.button("OK").click(); //$NON-NLS-1$
	}

	private void checkContent() throws IOException {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		tree
				.expandNode(projectProperties.getProperty("JSFProjectName")).expandNode("WebContent").expandNode(JSF2_Test_Page_Name + ".xhtml").doubleClick(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		delay();
		SWTBotEclipseEditor editor = bot.editorByTitle(
				JSF2_Test_Page_Name + ".xhtml").toTextEditor(); //$NON-NLS-1$
		assertEquals(
				loadFileContent("refactor/jsf2RenameAttrTestPageRefactor.html"), editor.getText()); //$NON-NLS-1$
		delay();
		editor.close();
	}

	@Override
	protected void createCompositeComponent() throws Exception {
		super.createCompositeComponent();
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		tree
				.expandNode(projectProperties.getProperty("JSFProjectName")).expandNode("WebContent").expandNode("resources").expandNode("mycomp").expandNode("echo.xhtml").doubleClick(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		SWTBotEclipseEditor editor = bot
				.editorByTitle("echo.xhtml").toTextEditor(); //$NON-NLS-1$
		bot.menu("Edit").menu("Select All").click(); //$NON-NLS-1$ //$NON-NLS-2$
		bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$//$NON-NLS-2$
		bot.sleep(2000);
		editor.setText(loadFileContent("refactor/compositeComponent.html")); //$NON-NLS-1$
		editor.save();
		bot.sleep(2000);
	}

	@Override
	protected void tearDown() throws Exception {
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
				.bot();
		SWTBotTree tree = innerBot.tree();
		tree
				.expandNode(projectProperties.getProperty("JSFProjectName")).expandNode("WebContent").expandNode(JSF2_Test_Page_Name + ".xhtml").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$
		bot.button("OK").click(); //$NON-NLS-1$
		delay();
		tree
				.expandNode(projectProperties.getProperty("JSFProjectName")).expandNode("WebContent").expandNode("resources").expandNode("mycomp").expandNode("echo.xhtml").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$
		bot.button("OK").click(); //$NON-NLS-1$
		delay();
		super.tearDown();
	}

}
