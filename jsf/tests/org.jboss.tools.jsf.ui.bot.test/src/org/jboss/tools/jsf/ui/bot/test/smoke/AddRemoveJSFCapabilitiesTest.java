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

package org.jboss.tools.jsf.ui.bot.test.smoke;

import static org.jboss.tools.ui.bot.ext.SWTTestExt.eclipse;

import java.io.File;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.WidgetFinderHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.WidgetVariables;

/**
 * Test adding and removing JSF Capabilities from/to JSF Project
 * 
 * @author Vladimir Pakan
 */

public class AddRemoveJSFCapabilitiesTest extends JSFAutoTestCase {
  
  private SWTJBTExt swtJbtExt = null;
  private SWTUtilExt swtUtilExt = null;
  
  public AddRemoveJSFCapabilitiesTest(){
    swtJbtExt = new SWTJBTExt(bot);
    swtUtilExt = new SWTUtilExt(bot);
  }
  public void testAddRemoveJSFCapabilities() {
    boolean jbdsIsRunning = SWTJBTExt.isJBDSRun(bot);
    removeJSFCapabilities(jbdsIsRunning);
    addJSFCapabilities();
    // Test add/remove JSF capabilities after project is closed and reopened
    closeOpenJsfProject();
    removeJSFCapabilities(jbdsIsRunning);
    addJSFCapabilities();
    // Test import of deleted JSF project
    deleteJsfProject();
    importJsfProject();
  }
  /**
   * Import existing JSF Project to Workspace
   */
  private void importJsfProject() {

    String[] parts = System.getProperty("eclipse.commands").split("\n");

    int index = 0;
    
    for (index = 0;parts.length > index + 1 && !parts[index].equals("-data");index++){
      // do nothing just go through
    }

    if (parts.length > index + 1){
      String webXmlFileLocation = SWTUtilExt.getTestPluginLocation(JBT_TEST_PROJECT_NAME) + File.separator
        + "WebContent" + File.separator
        + "WEB-INF" + File.separator
        + "web.xml";
      
      bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.IMPORT).click();
      bot.shell(IDELabel.Shell.IMPORT).activate();
      SWTBotTree tree = bot.tree();
      delay();
      tree.expandNode("Other").select("JSF Project");
      bot.button("Next >").click();
      bot.shell(IDELabel.Shell.IMPORT_JSF_PROJECT).activate();
      
      bot.textWithLabel("web.xml Location*").setText(webXmlFileLocation);
      bot.button(WidgetVariables.NEXT_BUTTON).click();
      SWTJBTExt.addServerToServerViewOnWizardPage(bot, IDELabel.ServerGroup.JBOSS_EAP_4_3, IDELabel.ServerType.JBOSS_EAP_4_3);
      bot.sleep(1000L);
      bot.button(IDELabel.Button.FINISH).click();
      eclipse.closeWarningWindowIfOpened(true);
      eclipse.closeOpenAssociatedPerspectiveShellIfOpened(false);
      // Start Application Server
      swtJbtExt.startApplicationServer(0);
      swtJbtExt.runProjectOnServer(JBT_TEST_PROJECT_NAME);
      // Check Browser Content
      String browserText = WidgetFinderHelper.browserInEditorText(bot, "Input User Name Page",true);
      assertTrue("Displayed HTML page has wrong content", 
        browserText.indexOf("<TITLE>Input User Name Page</TITLE>") > - 1);
      // Stop Application Server and remove Application Server from Server View
      SWTJBTExt.stopApplicationServer(bot, 0);
      SWTJBTExt.deleteApplicationServer(bot, 0);
      setException(null);      
    }
    else{
      throw new RuntimeException("eclipse.commands property doesn't contain -data option");
    }

    
  }
  /**
   * Delete JSF Project from workspace
   */
  private void deleteJsfProject() {

    removeJSFTestProjectFromServers();
    
    openPackageExplorer();
    delay();
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();
    delay();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
      IDELabel.Menu.DELETE, false)).click();
    bot.shell("Delete Resources").activate();
    bot.button(WidgetVariables.OK_BUTTON).click();
    
    new SWTUtilExt(bot).waitForNonIgnoredJobs();
    
  }

  /**
   * Remove JSF Capabilities from JSF Project
   * @param jbdsIsRunning
   */
  private void removeJSFCapabilities(boolean jbdsIsRunning) {

    openWebProjects();
    delay();
    
    removeJSFTestProjectFromServers();
    
    SWTBot webProjects = bot.viewByTitle(WidgetVariables.WEB_PROJECTS).bot();
    SWTBotTree tree = webProjects.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));

    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.WEB_PROJECT_JBT_JSF, false)).menu(
          IDELabel.Menu.JBT_REMOVE_JSF_CAPABILITIES).click();

    bot.shell("Confirmation").activate();
    bot.button(WidgetVariables.OK_BUTTON).click();

    delay();
    
    assertTrue(
        "Project "
            + JBT_TEST_PROJECT_NAME
            + " was not removed from Web Projects view after JSF Capabilities were removed.",
        !isTreeItemWithinWebProjectsView(JBT_TEST_PROJECT_NAME));
  }

  @Override
  protected void closeUnuseDialogs() {
    // not used
  }

  @Override
  protected boolean isUnuseDialogOpened() {
    return false;
  }

  /**
   * Add JSF Capabilities to JSF Project
   */
  private void addJSFCapabilities() {

    openPackageExplorer();
    delay();
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    
    delay();
    
    try{
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.PACKAGE_EXPLORER_JBT, false)).menu(
        IDELabel.Menu.ADD_JSF_CAPABILITIES).click();
    } catch (WidgetNotFoundException wnfe){
      // From 3.1.0.RC1 version this menu is moved to Configure submenu
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.PACKAGE_EXPLORER_CONFIGURE, false)).menu(
        IDELabel.Menu.ADD_JSF_CAPABILITIES).click();
    }

    bot.shell("Add JSF Capabilities").activate();
    bot.button(WidgetVariables.NEXT_BUTTON).click();
    bot.button(WidgetVariables.FINISH_BUTTON).click();

    delay();

    assertTrue("JSF Capabilities were not added to project "
        + JBT_TEST_PROJECT_NAME,
        isTreeItemWithinWebProjectsView(JBT_TEST_PROJECT_NAME));

  }

  /**
   * Return true when Web Projects Tree contains item with label treeItemLabel
   * 
   * @param treeItemLabel
   * @return
   */
  private boolean isTreeItemWithinWebProjectsView(String treeItemLabel) {

    openWebProjects();
    SWTBot webProjects = bot.viewByTitle(WidgetVariables.WEB_PROJECTS).bot();
    SWTBotTree tree = webProjects.tree();

    boolean isTreeItemWithinWebProjectsView = false;

    try {
      tree.getTreeItem(JBT_TEST_PROJECT_NAME);
      isTreeItemWithinWebProjectsView = true;
    } catch (WidgetNotFoundException e) {
      isTreeItemWithinWebProjectsView = false;
    }

    return isTreeItemWithinWebProjectsView;

  }

  /**
   * Close and reopen JSF test project
   */
  private void closeOpenJsfProject() {

    openPackageExplorer();
    delay();
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree, 
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
      IDELabel.Menu.CLOSE_PROJECT, false)).click();
    
    swtUtilExt.waitForNonIgnoredJobs(5*1000L);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
      IDELabel.Menu.OPEN_PROJECT, false)).click();
    
    swtUtilExt.waitForNonIgnoredJobs(5*1000L);
    
  }
  /**
   * Remove JSF Test Project from all Servers
   */
  private void removeJSFTestProjectFromServers(){
    
    openServerView();
    delay();
    
    SWTBot servers = bot.viewByTitle(WidgetVariables.SERVERS)
      .bot();
    SWTBotTree serverTree = servers.tree();
    
    // Expand All
    for (SWTBotTreeItem serverTreeItem : serverTree.getAllItems()){
      serverTreeItem.expand();
      // if JSF Test Project is deployed to server remove it
      int itemIndex = 0;
      SWTBotTreeItem[] serverTreeItemChildren = serverTreeItem.getItems(); 
      while (itemIndex < serverTreeItemChildren.length 
        && !serverTreeItemChildren[itemIndex].getText().startsWith(JBT_TEST_PROJECT_NAME)){
        itemIndex++;
      }  
      // Server Tree Item has Child with Text equal to JSF TEst Project
      if (itemIndex < serverTreeItemChildren.length){
        ContextMenuHelper.prepareTreeItemForContextMenu(serverTree,serverTreeItemChildren[itemIndex]);
        new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree, IDELabel.Menu.REMOVE, false)).click();
        bot.shell("Server").activate();
        bot.button(WidgetVariables.OK_BUTTON).click();
      }  
    }
    delay();
  }
  
}
