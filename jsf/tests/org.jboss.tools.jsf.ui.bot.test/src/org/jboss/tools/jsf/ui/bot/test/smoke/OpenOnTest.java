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
package org.jboss.tools.jsf.ui.bot.test.smoke;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.OpenOnHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/**
 * Test open on functionality of JSF components within jsp page
 * @author Vladimir Pakan
 *
 */
public class OpenOnTest extends JSFAutoTestCase{
  /**
   * Test open on functionality of JSF components within jsp page
   */
	public void testOpenOn(){
	 
	  eclipse.closeAllEditors();
	  openPage();
	  checkOpenOn();
		
	}
	/**
	 * Test open on functionality of faces-config.xml fioe
	 * @throws Throwable
	 */
	public void testFacesConfigOpenOn() throws Throwable{
	   
	  eclipse.closeAllEditors();
	  SWTBotEditor facesConfigEditor = eclipse.openFile(VPEAutoTestCase.JBT_TEST_PROJECT_NAME, "WebContent","WEB-INF","faces-config.xml");
	  new SWTBotEditorExt(facesConfigEditor.toTextEditor().getReference(),bot)
	    .selectPage(IDELabel.FacesConfigEditor.SOURCE_TAB_LABEL);
	  checkFacesConfigOpenOn();
	    
	}
	/**
	 * Check Open On functionality for jsp page
	 */
  private void checkOpenOn() {
    // Check open on for #{Message.header} EL
    String expectedOpenedFileName = "Messages.properties";
    SWTBotEditor openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, TEST_PAGE, "value=\"#{Message.header}\"", 10,
        0, 0, expectedOpenedFileName);
    SWTBotTable propTable = openedEditor.bot().table();
    String selectedTableRowLabel = propTable.selection().get(0, 0);
    String firstTableRowLabel = propTable.cell(0,0);
    assertTrue("First table row in properties table has to be selected but is not.",
        selectedTableRowLabel.equals(firstTableRowLabel));
    openedEditor.close();
    // Check open on for #{Message.prompt_message} EL
    String expectedTableRowLabel = "prompt_message";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, TEST_PAGE, "value=\"#{Message." + expectedTableRowLabel + "}\"", 18,
        0, 0, expectedOpenedFileName);
    selectedTableRowLabel = openedEditor.bot().table().selection().get(0, 0);
    assertTrue("Selected table row has to have value " + expectedTableRowLabel + " but has " + selectedTableRowLabel,
        selectedTableRowLabel.equalsIgnoreCase(expectedTableRowLabel));
    openedEditor.close();
    // Check open on for "#{user.name} EL when text 'user' is selected
    expectedOpenedFileName = "User.java";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, TEST_PAGE, "value=\"#{user.name}\"", 10,
        0, 0, expectedOpenedFileName);
    String selectedTextInEditor = openedEditor.toTextEditor().getSelection();
    String expectedSelectedTextInEditor = "User";
    assertTrue("Selected text in editor has to be " + expectedSelectedTextInEditor +
          " but is " + selectedTextInEditor,
          selectedTextInEditor.equalsIgnoreCase(expectedSelectedTextInEditor));
    openedEditor.close();
    // Check open on for "#{user.name} EL when text 'name' is selected
    expectedOpenedFileName = "User.java";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, TEST_PAGE, "value=\"#{user.name}\"", 15,
        0, 0, expectedOpenedFileName);
    selectedTextInEditor = openedEditor.toTextEditor().getSelection();
    expectedSelectedTextInEditor = "getName";
    assertTrue("Selected text in editor has to be " + expectedSelectedTextInEditor +
          " but is " + selectedTextInEditor,
          selectedTextInEditor.equalsIgnoreCase(expectedSelectedTextInEditor));
    openedEditor.close();
  }
  /**
   * Check Open On functionality for faces-config.xml file
   */
  private void checkFacesConfigOpenOn() {
    // Check open on for demo.User managed bean
    final String facesConfigTitle = "faces-config.xml";
    String expectedOpenedFileName = "User.java";
    SWTBotEditor openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, facesConfigTitle, "<managed-bean-class>demo.User</managed-bean-class>", 22,
        0, 0, expectedOpenedFileName);
    String selectedTextInSourceEditor = openedEditor.toTextEditor().getSelection();
    String expectedSelectedTextInEditor = "User";
    assertTrue("Selected text in editor has to be " + expectedSelectedTextInEditor +
          " but is " + selectedTextInSourceEditor,
          selectedTextInSourceEditor.equalsIgnoreCase(expectedSelectedTextInEditor));
    openedEditor.close();
    // Check open on for name property of demo.User managed bean
    expectedOpenedFileName = "User.java";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, facesConfigTitle, "<property-name>name</property-name>", 17,
        0, 0, expectedOpenedFileName);
    selectedTextInSourceEditor = openedEditor.toTextEditor().getSelection();
    expectedSelectedTextInEditor = "getName";
    assertTrue("Selected text in editor has to be " + expectedSelectedTextInEditor +
          " but is " + selectedTextInSourceEditor,
          selectedTextInSourceEditor.equalsIgnoreCase(expectedSelectedTextInEditor));
    openedEditor.close();
    // Check open on for java.lang.String class
    expectedOpenedFileName = "String.class";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, facesConfigTitle, "<property-class>java.lang.String</property-class>", 18,
        0, 0, expectedOpenedFileName);
    selectedTextInSourceEditor = openedEditor.toTextEditor().getSelection();
    expectedSelectedTextInEditor = "String";
    assertTrue("Selected text in editor has to be " + expectedSelectedTextInEditor +
          " but is " + selectedTextInSourceEditor,
          selectedTextInSourceEditor.equalsIgnoreCase(expectedSelectedTextInEditor));
    openedEditor.close();
    // Check open on for URI /pages/inputUserName.jsp within <from-view-id> tag
    expectedOpenedFileName = "inputUserName.jsp";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, facesConfigTitle, "<from-view-id>/pages/inputUserName.jsp</from-view-id>", 16,
        0, 0, expectedOpenedFileName);
    openedEditor.close();
    // Check open on for URI /pages/hello.jsp within <to-view-id> tag
    expectedOpenedFileName = "hello.jsp";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, facesConfigTitle, "<to-view-id>/pages/hello.jsp</to-view-id>", 14,
        0, 0, expectedOpenedFileName);
    openedEditor.close();

  }
  
  /**
   * Test Open On functionality for Composite Component
   */
  public void testOpenOnForCompositeComponent() {
    eclipse.closeAllEditors();
    openPage(JSF2_TEST_PAGE,JSF2_TEST_PROJECT_NAME);
    // Check open on for <ez:input
    String expectedOpenedFileName = "input.xhtml";
    SWTBotEditor compositeComponentEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, JSF2_TEST_PAGE, "<ez:input ", 5,
        0, 0, expectedOpenedFileName);
    // Check open on for cc.attrs.submitlabel
    compositeComponentEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, expectedOpenedFileName, "value=\"#{cc.attrs.submitlabel}\"", 20,
        0, 0, expectedOpenedFileName);
    String selectedText = compositeComponentEditor.toTextEditor().getSelection();
    String expectedSelectedText = "<composite:attribute name=\"submitlabel\"/>";
    assertTrue("Selected text in editor has to be " + expectedSelectedText
        + " but it is " + selectedText,
        selectedText.equalsIgnoreCase(expectedSelectedText));
    compositeComponentEditor.close();
  }

}