 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.struts.ui.bot.test.smoke;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.FileRenameHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;

/**
 * Test renaming of struts-config.xml file
 * @author Vladimir Pakan
 *
 */
public class RenameStrutsConfigXmlFile extends SWTTestExt{
  private static final String OLD_STRUTS_CONFIG_FILE_NAME = "struts-config.xml";
  private static final String NEW_STRUTS_CONFIG_FILE_NAME = "struts-config-renamed.xml";
  /**
   * Test renaming of struts-config.xml file
   */
  @Test
	public void testRenameStrutsConfigXmlFile() {
   
    String checkResult = FileRenameHelper.checkFileRenamingWithinWebProjects(bot,
        OLD_STRUTS_CONFIG_FILE_NAME, NEW_STRUTS_CONFIG_FILE_NAME,
        new String[]{StrutsAllBotTests.STRUTS_PROJECT_NAME, IDELabel.WebProjectsTree.CONFIGURATION ,IDELabel.WebProjectsTree.DEFAULT});
    assertNull(checkResult, checkResult);
    // web.xml file was properly modified
    SWTBotTree tree = eclipse.showView(ViewType.WEB_PROJECTS).tree();
    tree.setFocus();
    SWTBotTreeItem configFilesTreeItem = tree
      .getTreeItem(StrutsAllBotTests.STRUTS_PROJECT_NAME)
      .expand()
      .getNode(IDELabel.WebProjectsTree.CONFIGURATION)
      .expand()
      .getNode(IDELabel.WebProjectsTree.WEB_XML)
      .expand()
      .getNode(IDELabel.WebProjectsTree.SERVLETS)
      .expand()
      .getNode(IDELabel.WebProjectsTree.ACTION_STRUTS)
       .expand()
      .getNode(IDELabel.WebProjectsTree.CONFIG);
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,configFilesTreeItem);
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree, IDELabel.Menu.PROPERTIES, true)).click();
    bot.shell(IDELabel.Shell.PROPERTIES).activate();
    SWTBotTable propertiesTable = bot.table(); 
    String fullConfigFileName = propertiesTable.cell(propertiesTable.indexOf(IDELabel.PropertiesDialog.PARAM_VALUE, 0), 1);
    bot.button(IDELabel.Button.CLOSE).click();
    assertTrue(NEW_STRUTS_CONFIG_FILE_NAME + " Name of " 
        + OLD_STRUTS_CONFIG_FILE_NAME 
        + " file was not changed in web.xml file.",
      fullConfigFileName.endsWith(NEW_STRUTS_CONFIG_FILE_NAME));
  }
	
}
