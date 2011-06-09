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

import org.apache.log4j.Logger;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.OpenOnHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.JBTSWTBotTestCase;
/**
 * Test open on functionality of JSF components within jsp page
 * @author Vladimir Pakan
 *
 */
public class OpenOnTest extends JSFAutoTestCase{
  private static Logger log = Logger.getLogger(JBTSWTBotTestCase.class);
	public void testOpenOn() throws Throwable{
	  try{
	    bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.CLOSE_ALL).click();
	    log.info("All Editors closed");
	  } catch (WidgetNotFoundException wnfe){
	    log.info("No Editors to close");
	  } catch (TimeoutException te){
      log.info("No Editors to close");
    }
    		
	  openPage();
	  checkOpenOn();
		
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
        SWTTestExt.bot, TEST_PAGE, "value=\"#{Message." + expectedTableRowLabel + "}\"", 10,
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
    String selectedTextInSourceEditor = openedEditor.toTextEditor().getSelection();
    String expectedSelectedTextInSourceEditor = "User";
    assertTrue("Selected text in Source editor has to be " + expectedSelectedTextInSourceEditor +
          " but is " + selectedTextInSourceEditor,
          selectedTextInSourceEditor.equalsIgnoreCase(expectedSelectedTextInSourceEditor));
    openedEditor.close();
    // Check open on for "#{user.name} EL when text 'name' is selected
    expectedOpenedFileName = "User.java";
    openedEditor = OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, TEST_PAGE, "value=\"#{user.name}\"", 15,
        0, 0, expectedOpenedFileName);
    selectedTextInSourceEditor = openedEditor.toTextEditor().getSelection();
    expectedSelectedTextInSourceEditor = "getName";
    assertTrue("Selected text in Source editor has to be " + expectedSelectedTextInSourceEditor +
          " but is " + selectedTextInSourceEditor,
          selectedTextInSourceEditor.equalsIgnoreCase(expectedSelectedTextInSourceEditor));
    openedEditor.close();
  }
}