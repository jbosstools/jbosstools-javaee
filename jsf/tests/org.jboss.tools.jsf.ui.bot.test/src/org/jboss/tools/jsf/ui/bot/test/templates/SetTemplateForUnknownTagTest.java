package org.jboss.tools.jsf.ui.bot.test.templates;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.jsf.ui.bot.test.CSSStyleDialogVariables;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.jsf.ui.bot.test.UnknownTagDialogVariables;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public class SetTemplateForUnknownTagTest extends JSFAutoTestCase {
	
	private static final String TAG_NAME = "h:unknowntag";//$NON-NLS-1$
	private static final String TAG_URI = "http://java.sun.com/jsf/html";//$NON-NLS-1$
	private static final String DISPALY_TAG = "b";//$NON-NLS-1$
	private static final String CHILDREN_ALLOWS = "yes";//$NON-NLS-1$
	
	public void testSetTemplateForUnknownTag() throws Throwable{
		openTestPage();
		setEditor(bot.editorByTitle(TEST_PAGE).toTextEditor());
		setEditorText(getEditor().getText());
		getEditor().navigateTo(13, 0);
		getEditor().insertText("<h:unknowntag></h:unknowntag>");//$NON-NLS-1$
		getEditor().navigateTo(13,5);
		setUpTemplate();
		editTemplate();
		removeTemplate();
	}
	
	private void setUpTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.button(WidgetVariables.ADD_BUTTON).click();
		bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
		bot.textWithLabel(UnknownTagDialogVariables.TAG_NAME_FIELD).setText(TAG_NAME);
		bot.textWithLabel(UnknownTagDialogVariables.TAG_URI_FIELD).setText(TAG_URI);
		bot.textWithLabel(UnknownTagDialogVariables.DISPLAY_TAG).setText(DISPALY_TAG);
		bot.checkBoxWithLabel(UnknownTagDialogVariables.ALLOW_CHILDREN_CHECKBOX).click();
		bot.textWithLabel(UnknownTagDialogVariables.VALUE_FIELD).setText("myValue");//$NON-NLS-1$
		bot.textWithLabel(UnknownTagDialogVariables.TAG_STYLE_FIELD).setText("color:red");//$NON-NLS-1$
		bot.buttonWithTooltip(UnknownTagDialogVariables.EDIT_TAG_STYLE_TIP).click();
		String returnValue = setStyles();
		assertEquals("font-family:Arial;color:black;", returnValue);//$NON-NLS-1$
		bot.button(WidgetVariables.OK_BUTTON).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.button(WidgetVariables.OK_BUTTON).click();
		checkVPE("templates/SetTemplateForUnknownTag.xml");//$NON-NLS-1$
	}
	
	private void editTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.table().select(0);
		checkTable(bot.table());
		bot.button(WidgetVariables.EDIT_BUTTON).click();
		bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
		bot.textWithLabel(UnknownTagDialogVariables.TAG_URI_FIELD).setText(""); //$NON-NLS-1$
		bot.checkBoxWithLabel(UnknownTagDialogVariables.ALLOW_CHILDREN_CHECKBOX).click();
		bot.textWithLabel(UnknownTagDialogVariables.VALUE_FIELD).setText(""); //$NON-NLS-1$
		bot.textWithLabel(UnknownTagDialogVariables.TAG_STYLE_FIELD).setText(""); //$NON-NLS-1$
		bot.button(WidgetVariables.OK_BUTTON).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.button(WidgetVariables.OK_BUTTON).click();
		checkVPE("templates/EditedTemplateForUnknownTag.xml"); //$NON-NLS-1$
	}
	
	private void removeTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.table().select(0);
		bot.button(WidgetVariables.REMOVE_BUTTON).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.button(WidgetVariables.OK_BUTTON).click();
		checkVPE("templates/UnknownTemplate.xml"); //$NON-NLS-1$
	}
	
	private String setStyles(){
		bot.shell(CSSStyleDialogVariables.CSS_STYLE_DIALOG_TITLE).activate();
		bot.tabItem(CSSStyleDialogVariables.TEXT_FONT_TAB).activate();
		bot.textWithLabel(CSSStyleDialogVariables.FONT_FAMILY_FIELD).setText("Arial"); //$NON-NLS-1$
		String colorText = bot.textWithLabel(CSSStyleDialogVariables.COLOR_FIELD).getText();
		assertEquals("red", colorText); //$NON-NLS-1$
		bot.textWithLabel(CSSStyleDialogVariables.COLOR_FIELD).setText("black"); //$NON-NLS-1$
		bot.textWithLabel(CSSStyleDialogVariables.TEXT_DECORATION_FIELD).setText("underline"); //$NON-NLS-1$
		bot.button(WidgetVariables.OK_BUTTON).click();
		bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
		String returnValue = bot.textWithLabel(UnknownTagDialogVariables.TAG_STYLE_FIELD).getText();
		return returnValue;
	}
	
	private void checkTable(SWTBotTable table){
		assertEquals(TAG_NAME, table.cell(0, 0));
		assertEquals(DISPALY_TAG, table.cell(0, 1));
		assertEquals(TAG_URI, table.cell(0, 2));
		assertEquals(CHILDREN_ALLOWS, table.cell(0, 3));
	}
	
	@Override
	protected boolean isUnuseDialogOpened() {
		boolean isOpened = false;
		try {
			bot.shell(CSSStyleDialogVariables.CSS_STYLE_DIALOG_TITLE).activate();
			isOpened = true;
		} catch (WidgetNotFoundException e) {
		}
		try {
			bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
			isOpened = true;
		} catch (WidgetNotFoundException e) {
		}
		try {
			bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
			isOpened = true;
		} catch (WidgetNotFoundException e) {
		}
		return isOpened;
	}
	
	@Override
	protected void closeUnuseDialogs() {
		try {
			bot.shell(CSSStyleDialogVariables.CSS_STYLE_DIALOG_TITLE).close();
		} catch (WidgetNotFoundException e) {
		}
		try {
			bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).close();
		} catch (WidgetNotFoundException e) {
		}
		try {
			bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).close();
		} catch (WidgetNotFoundException e) {
		}
	}
	
}
