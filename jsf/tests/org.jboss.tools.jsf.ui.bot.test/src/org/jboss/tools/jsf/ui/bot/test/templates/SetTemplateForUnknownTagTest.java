/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.templates;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.jsf.ui.bot.test.CSSStyleDialogVariables;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.jsf.ui.bot.test.UnknownTagDialogVariables;
import org.jboss.tools.ui.bot.ext.CompareUtils;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.WidgetVariables;
import org.jboss.tools.vpe.ui.bot.test.tools.SWTBotWebBrowser;

public class SetTemplateForUnknownTagTest extends JSFAutoTestCase {
	
	private static final String TAG_NAME = "h:unknowntag";//$NON-NLS-1$
	private static final String TAG_URI = "http://java.sun.com/jsf/html";//$NON-NLS-1$
	private static final String DISPALY_TAG = "b";//$NON-NLS-1$
	private static final String CHILDREN_ALLOWS = "yes";//$NON-NLS-1$
	private static final String TEMPLATE_VALUE = "myValue";
	
	public void testSetTemplateForUnknownTag() throws Throwable{
		openTestPage();
		setEditor(bot.editorByTitle(TEST_PAGE).toTextEditor());
		setEditorText(getEditor().getText());
		getEditor().navigateTo(13, 0);
		getEditor().insertText("<" + SetTemplateForUnknownTagTest.TAG_NAME + "></" + SetTemplateForUnknownTagTest.TAG_NAME + ">");//$NON-NLS-1$
		getEditor().save();
		getEditor().navigateTo(13,5);
		setUpTemplate();
		editTemplate();
		removeTemplate();
	}
	
	private void setUpTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.button(IDELabel.Button.ADD).click();
		bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
		bot.textWithLabel(UnknownTagDialogVariables.TAG_NAME_FIELD).setText(TAG_NAME);
		bot.textWithLabel(UnknownTagDialogVariables.TAG_URI_FIELD).setText(TAG_URI);
		bot.textWithLabel(UnknownTagDialogVariables.DISPLAY_TAG).setText(DISPALY_TAG);
		bot.checkBoxWithLabel(UnknownTagDialogVariables.ALLOW_CHILDREN_CHECKBOX).click();
		bot.textWithLabel(UnknownTagDialogVariables.VALUE_FIELD).setText(SetTemplateForUnknownTagTest.TEMPLATE_VALUE);//$NON-NLS-1$
		bot.textWithLabel(UnknownTagDialogVariables.TAG_STYLE_FIELD).setText("color:red");//$NON-NLS-1$
		bot.buttonWithTooltip(UnknownTagDialogVariables.EDIT_TAG_STYLE_TIP).click();
		String returnValue = setStyles();
    bot.button(WidgetVariables.OK_BUTTON).click();
    bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
    bot.button(WidgetVariables.OK_BUTTON).click();
		assertTrue("Atttributes are not as expexted:\n" 
		  + "Expected: " + "font-family:Arial;color:black;"
		  + "Value: " + returnValue
		  ,CompareUtils.compareStyleAttributes("font-family:Arial;color:black;", returnValue));//$NON-NLS-1$
		bot.toolbarButtonWithTooltip(SWTJBTExt.isRunningOnMacOs() ? 
        IDELabel.ToolbarButton.REFRESH_MAC_OS: IDELabel.ToolbarButton.REFRESH).click();
		assertVisualEditorContainsNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
		    SetTemplateForUnknownTagTest.TEMPLATE_VALUE, 
		    TEST_PAGE);
	}
	
	private void editTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.table().select(0);
    try {
      checkTable(bot.table());
      bot.button(IDELabel.Button.EDIT).click();
      bot.shell(UnknownTagDialogVariables.DIALOG_TITLE).activate();
      bot.textWithLabel(UnknownTagDialogVariables.TAG_URI_FIELD).setText(""); //$NON-NLS-1$
      bot.checkBoxWithLabel(UnknownTagDialogVariables.ALLOW_CHILDREN_CHECKBOX)
          .click();
      bot.textWithLabel(UnknownTagDialogVariables.VALUE_FIELD).setText(""); //$NON-NLS-1$
      bot.textWithLabel(UnknownTagDialogVariables.TAG_STYLE_FIELD).setText(""); //$NON-NLS-1$
      bot.button(WidgetVariables.OK_BUTTON).click();
    } catch (Throwable t) {
      throw t;
    } finally {
      bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
      bot.button(WidgetVariables.OK_BUTTON).click();
    }
    bot.toolbarButtonWithTooltip(SWTJBTExt.isRunningOnMacOs() ? 
        IDELabel.ToolbarButton.REFRESH_MAC_OS: IDELabel.ToolbarButton.REFRESH).click();
    assertVisualEditorNotContainNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
        SetTemplateForUnknownTagTest.TEMPLATE_VALUE, 
        TEST_PAGE);
    assertVisualEditorNotContainNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
        SetTemplateForUnknownTagTest.TAG_NAME, 
        TEST_PAGE);
	}
	
	private void removeTemplate() throws Throwable{
		bot.toolbarButtonWithTooltip(WidgetVariables.PREFERENCES).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.tabItem(WidgetVariables.VPE_TEMPLATES_TAB).activate();
		bot.table().select(0);
		bot.button(WidgetVariables.REMOVE_BUTTON).click();
		bot.shell(WidgetVariables.PREF_FILTER_SHELL_TITLE).activate();
		bot.button(WidgetVariables.OK_BUTTON).click();
		bot.toolbarButtonWithTooltip(SWTJBTExt.isRunningOnMacOs() ? 
        IDELabel.ToolbarButton.REFRESH_MAC_OS: IDELabel.ToolbarButton.REFRESH).click();
		assertVisualEditorNotContainNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
        SetTemplateForUnknownTagTest.TEMPLATE_VALUE, 
        TEST_PAGE);
    assertVisualEditorContainsNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
        SetTemplateForUnknownTagTest.TAG_NAME, 
        TEST_PAGE);
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
