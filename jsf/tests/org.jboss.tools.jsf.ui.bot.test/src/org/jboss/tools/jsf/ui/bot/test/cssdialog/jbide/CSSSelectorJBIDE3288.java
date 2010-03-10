package org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public class CSSSelectorJBIDE3288 extends JSFAutoTestCase{

	private final static String CSS_FILE_NAME = "CSSSelectorJBIDE3288"; //$NON-NLS-1$
	private final static String CSS_CLASS1_NAME = ".cssclass1"; //$NON-NLS-1$
	private final static String CSS_CLASS2_NAME = ".cssclass2"; //$NON-NLS-1$
	private final static String css1Attrs = "\r\ncolor:red\r\n"; //$NON-NLS-1$
	private final static String css2Attrs = "\r\nbackground-color:green\r\n"; //$NON-NLS-1$
	
	public void testCSSSelector(){
		createCSSPage();
		createCSSClass();
		bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER).setFocus();
		openTestPage();
		linkCSSFile();
		selectTestElement();
		openCSSSelectorDialog();
		checkSelector();
		chooseStyleClass();
	}
	
	
	@Override
	protected void closeUnuseDialogs() {
		try {
			bot.shell(WidgetVariables.CSS_SELECTOR_DIALOG_TITLE).close();
		} catch (WidgetNotFoundException e) {
		}
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		boolean isOpened = false;
		try {
			bot.shell(WidgetVariables.CSS_SELECTOR_DIALOG_TITLE).activate();
			isOpened = true;
		} catch (WidgetNotFoundException e) {
		}
		return isOpened;
	}

	private void createCSSPage(){
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER).bot();
		SWTBotTree tree = innerBot.tree();
		try {
			tree.expandNode(projectProperties.getProperty("JSFProjectName")). //$NON-NLS-1$
			expandNode("WebContent").expandNode("pages").getNode(CSS_FILE_NAME+".css").doubleClick(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.editorByTitle(CSS_FILE_NAME+".css").setFocus(); //$NON-NLS-1$
			bot.menu("Edit").menu("Select All").click();  //$NON-NLS-1$//$NON-NLS-2$
			bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (WidgetNotFoundException e) {
			tree.expandNode(projectProperties.getProperty("JSFProjectName")).expandNode("WebContent").getNode("pages").select(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			bot.menu("File").menu("New").menu("CSS File").click();  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			bot.shell("New CSS File").activate(); //$NON-NLS-1$
			bot.textWithLabel("Name*").setText(CSS_FILE_NAME); //$NON-NLS-1$
			bot.button("Finish").click(); //$NON-NLS-1$
		}
	}
	
	private void createCSSClass(){
		SWTBotEclipseEditor eclipseEditor =	bot.editorByTitle(CSS_FILE_NAME+".css").toTextEditor(); //$NON-NLS-1$
		eclipseEditor.setFocus();
		eclipseEditor.insertText(CSS_CLASS1_NAME+"{"+css1Attrs+"}"+"\n"+CSS_CLASS2_NAME+"{"+css2Attrs+"}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		eclipseEditor.save();
	}
	
	private void linkCSSFile(){
		setEditor(bot.editorByTitle(TEST_PAGE).toTextEditor());
		setEditorText(getEditor().getText());
		getEditor().navigateTo(6, 10);
		getEditor().insertText("<link href=\"" + CSS_FILE_NAME + ".css\" type=\"text/css\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		getEditor().save();
	}
	
	private void selectTestElement(){
		getEditor().navigateTo(12, 21);
		getEditor().insertText(""); //$NON-NLS-1$
	}
	
	private void openCSSSelectorDialog(){
		openPropertiesView();
		getEditor().setFocus();
		delay();
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PROPERTIES).bot();
		SWTBotTree tree = innerBot.tree();
		tree.getAllItems()[0].getNode("styleClass").select(); //$NON-NLS-1$
		tree.getAllItems()[0].getNode("styleClass").click(); //$NON-NLS-1$
		bot.button("...").click(); //$NON-NLS-1$
	}
	
	private void checkSelector(){
		SWTBotShell cssSelectorShell = bot.shell(WidgetVariables.CSS_SELECTOR_DIALOG_TITLE).activate();
		SWTBotTree cssSelectorTree = cssSelectorShell.bot().tree();
		cssSelectorTree.expandNode(CSS_FILE_NAME+".css").getNode("cssclass1").select(); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(".cssclass1 {\n\tcolor: red\n}", bot.styledText().getText()); //$NON-NLS-1$
		cssSelectorShell.bot().buttonWithTooltip("Add CSS class").click(); //$NON-NLS-1$
		cssSelectorShell.bot().button(WidgetVariables.OK_BUTTON).click();
	}
	
	private void chooseStyleClass(){
		getEditor().setFocus();
		assertEquals("\t\t<h1><h:outputText value=\"#{Message.header}\" styleClass=\"cssclass1\"/></h1>", getEditor().getTextOnCurrentLine()); //$NON-NLS-1$
	}
	
}
