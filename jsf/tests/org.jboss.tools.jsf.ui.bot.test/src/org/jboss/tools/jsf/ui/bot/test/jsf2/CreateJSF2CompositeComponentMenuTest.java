/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.jsf2;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.Assertions;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/** Tests Functionality of Create JSF2 Composite Component Menu Item of Context Menu
 * @author Vladimir Pakan
 *
 */
public class CreateJSF2CompositeComponentMenuTest extends JSFAutoTestCase{
  private static final String CC_NAME_SPACE = "ccNameSpace";
  private static final String CC_NAME = "ccName";
  private static final String CC_FILE_NAME = CC_NAME + ".xhtml";
  private SWTBotEditor jsf2editor = null;
  private String originalContent = null;
  /**
   * Test if menu is working correctly  
   */
  public void testMenuFunctionality(){
    eclipse.closeAllEditors();
    createJSF2Project(JSF2_TEST_PROJECT_NAME);
    openPage(JSF2_TEST_PAGE, JSF2_TEST_PROJECT_NAME);
    jsf2editor = SWTTestExt.bot.swtBotEditorExtByTitle(JSF2_TEST_PAGE);
    originalContent = jsf2editor.toTextEditor().getText();
    SWTJBTExt.selectTextInSourcePane(new SWTBotExt(),
        JSF2_TEST_PAGE,
        "<ui:define ",
        0,
        0,
        0);
    jsf2editor.toTextEditor().insertText("\n");
    ContextMenuHelper.clickContextMenu(jsf2editor, IDELabel.Menu.CREATE_JSF2_COMPOSITE);
    bot.shell(IDELabel.Shell.CREATING_COMPOSITE_COMPONENT).activate();
    bot.text().setText(CC_NAME_SPACE + ":" + CC_NAME);
    bot.button(IDELabel.Button.OK).click();
    bot.sleep(Timing.time2S());
    jsf2editor.save();
    bot.sleep(Timing.time2S());
    final String activeEditorTitle = bot.activeEditor().getTitle();
    assertTrue(activeEditorTitle.equals(CC_FILE_NAME));
    Assertions.assertFileExistsInWorkspace(CC_FILE_NAME, JSF2_TEST_PROJECT_NAME,"WebContent","resources",CC_NAME_SPACE);
    final String editorText = jsf2editor.toTextEditor().getText();
    Assertions.assertSourceEditorContains(editorText.replaceAll(" ", ""),
      "<" + CreateJSF2CompositeComponentMenuTest.CC_NAME_SPACE + ":" + CreateJSF2CompositeComponentMenuTest.CC_NAME + ">" +
        "</" + CreateJSF2CompositeComponentMenuTest.CC_NAME_SPACE + ":" + CreateJSF2CompositeComponentMenuTest.CC_NAME + ">",
      JSF2_TEST_PROJECT_NAME);
    Assertions.assertSourceEditorContains(editorText,
      "xmlns:" + CreateJSF2CompositeComponentMenuTest.CC_NAME_SPACE + 
        "=\"http://java.sun.com/jsf/composite/" + CreateJSF2CompositeComponentMenuTest.CC_NAME_SPACE + "\"",
      JSF2_TEST_PROJECT_NAME);
  }
  /**
   * Tests if Menu is Not present for JSF project version 1.2
   */
  public void testMenuNotPresentForJSF12Project(){
    // Test default JSF 1.2 Project 
    openPage(VPEAutoTestCase.TEST_PAGE,VPEAutoTestCase.JBT_TEST_PROJECT_NAME);
    try{
      ContextMenuHelper.clickContextMenu(SWTTestExt.bot.swtBotEditorExtByTitle(VPEAutoTestCase.TEST_PAGE),
          IDELabel.Menu.CREATE_JSF2_COMPOSITE);   
      assertTrue("Menu Item has to be disabled but is not" , false);
    } catch (RuntimeException re){
      if (!(re.getCause() instanceof NotEnabledException)){
        throw re;
      }
    }
    // Test JSF 1.2 Project with Facelets
    openPage(VPEAutoTestCase.FACELETS_TEST_PAGE,VPEAutoTestCase.FACELETS_TEST_PROJECT_NAME);
    try{
      ContextMenuHelper.clickContextMenu(SWTTestExt.bot.swtBotEditorExtByTitle(VPEAutoTestCase.FACELETS_TEST_PAGE),
          IDELabel.Menu.CREATE_JSF2_COMPOSITE); 
      assertTrue("Menu Item has to be disabled but is not" , false);
    } catch (RuntimeException re){
      if (!(re.getCause() instanceof NotEnabledException)){
        throw re;
      }
    }
  }
  @Override
  public void tearDown() throws Exception {
    if (jsf2editor != null) {
      jsf2editor.toTextEditor().setText(originalContent);
      jsf2editor.saveAndClose();
      bot.sleep(Timing.time1S());
    }
    if (FileHelper.isExistingFileWithinWorkspace(CC_NAME_SPACE, JSF2_TEST_PROJECT_NAME,"WebContent","resources")){
      eclipse.deleteFile(JSF2_TEST_PROJECT_NAME,"WebContent","resources",CC_NAME_SPACE);
    }
    super.tearDown();
  }
}
  
