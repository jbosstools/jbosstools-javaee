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

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withRegex;

import java.io.File;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.StringResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.WidgetVariables;

/**
 * Test adding and removing JSF Capabilities from/to JSF Project
 * 
 * @author Vladimir Pakan
 */

public class AddRemoveJSFCapabilitiesTest extends JSFAutoTestCase {

  private static final String WEB_PROJECT_JBT_JSF_POPUP_MENU = "JBoss Tools JSF";
  private static final String PACKAGE_EXPLORER_JBT_POPUP_MENU = "JBoss Tools";
  private static final String ADD_JSF_CAPABILITIES_POPUP_MENU = "Add JSF Capabilities...";
  private static final String CLOSE_PROJECT_POPUP_MENU = "Clo&se Project";
  private static final String OPEN_PROJECT_POPUP_MENU = "Op&en Project";
  private static final String DELETE_PROJECT_POPUP_MENU = "Delete";
  private static final String IMPORT_PROJECT_POPUP_MENU = "Import Existing JSF Project...";
  private static final String JBDS_REMOVE_JSF_CAPABILITIES_POPUP_MENU = "Remove Red Hat Capabilities";
  private static final String JBT_REMOVE_JSF_CAPABILITIES_POPUP_MENU = "Remove JSF Capabilities";
  
  private MenuItem miRunOnServer = null;
  
  public void testAddRemoveJSFCapabilities() {
    boolean jbdsIsRunning = false;
    // Check out if JBoss Developer Studio Is Running
    try{
      bot.menu(IDELabel.Menu.HELP).menu(IDELabel.Menu.ABOUT_JBOSS_DEVELOPER_STUDIO);
      jbdsIsRunning = true;
    }catch (WidgetNotFoundException wnfe){
      // do nothing
    }
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
    
    openWebProjects();
    
    openWebProjects();
    SWTBot webProjects = bot.viewByTitle(WidgetVariables.WEB_PROJECTS).bot();
    SWTBotTree tree = webProjects.tree();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IMPORT_PROJECT_POPUP_MENU, false)).click();
    
    bot.shell("Import JSF Project").activate();
    
    String[] parts = System.getProperty("eclipse.commands").split("\n");
    
    int index = 0;
    
    for (index = 0;parts.length > index + 1 && !parts[index].equals("-data");index+=2){
      // do nothing just go through
    }
    
    if (parts.length > index + 1){
      String webXmlFileLocation = parts[index + 1] + File.separator
      + JBT_TEST_PROJECT_NAME + File.separator
      + "WebContent" + File.separator
      + "WEB-INF" + File.separator
      + "web.xml";
      bot.textWithLabel("web.xml Location*").setText(webXmlFileLocation);
    }
    else{
      throw new RuntimeException("eclipse.commands property doesn't contain -data option");
    }
    
    bot.button(WidgetVariables.NEXT_BUTTON).click();
    // Specify Application Server for Deployment
    bot.button(WidgetVariables.NEW_BUTTON, 1).click();
    bot.shell("New Server").activate();
    bot.tree().expandNode("JBoss Enterprise Middleware")
      .select("JBoss Enterprise Application Platform 4.3");
    bot.button(WidgetVariables.FINISH_BUTTON).click();
    delay();
    // Finish Import
    bot.button(WidgetVariables.FINISH_BUTTON).click();
    bot.shell("Warning").activate();
    bot.button(WidgetVariables.CONTINUE_BUTTON).click();
    
    waitForBlockingJobsAcomplished(BUILDING_WS);
    // Start Application Server
    openServerView();
    SWTBot servers = bot.viewByTitle(WidgetVariables.SERVERS)
      .bot();
    SWTBotTree serverTree = servers.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(serverTree);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree,
        "Start", false)).click();
    waitForBlockingJobsAcomplished(45*1000L,STARTING_JBOSS_EAP_43_RUNTIME);
    // Run it on server
    openPackageExplorer();
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree packageExplorerTree = packageExplorer.tree();

    packageExplorerTree.setFocus();

    SWTBotTreeItem packageExplorerTreeItem = packageExplorerTree
        .getTreeItem(JBT_TEST_PROJECT_NAME);
    packageExplorerTreeItem.select();
    packageExplorerTreeItem.click();
    // Search for Menu Item with Run on Server substring within label
    final SWTBotMenu menuRunAs = bot.menu("Run").menu("Run As");
    bot.getDisplay().syncExec(new Runnable() {
      public void run() {
        int menuItemIndex = 0;
        boolean isFound = false;
        final MenuItem[] menuItems = menuRunAs.widget.getMenu().getItems();
        while (!isFound && menuItemIndex < menuItems.length){
          if (menuItems[menuItemIndex].getText().indexOf("Run on Server") > - 1){
            isFound = true;
          }
          else{
            menuItemIndex++;
          }
        }
        if (isFound){
          setMiRunOnServer(menuItems[menuItemIndex]); 
        }
        else{
          setMiRunOnServer(null);
        }
      }
    });
    
    if (getMiRunOnServer() != null){
      new SWTBotMenu(getMiRunOnServer()).click();
    }
    else
    {
      throw new WidgetNotFoundException("Menu item with mnemonic Run on Server"); 
    }
  
    bot.shell("Run On Server").activate();
    bot.button(WidgetVariables.FINISH_BUTTON).click();

    waitForBlockingJobsAcomplished(10*1000L , BUILDING_WS);
    waitForBlockingJobsAcomplished(10*1000L , UPDATING_INDEXES);
    // Check Browser Content
    final SWTBotEditor editor = bot.editorByTitle("Input User Name Page");
    final Browser browser = ((Browser)editor.bot().widgets(withRegex("<HTML><HEAD>.*\n*.*")).get(0));
    final String browserText = UIThreadRunnable
      .syncExec(new StringResult() {
        public String run() {
          return browser.getText();
        }
      });
    assertTrue("Displayed HTML page has wrong content", 
      browserText.indexOf("<TITLE>Input User Name Page</TITLE>") > - 1);
    editor.close();
    // Stop Application Server and remove Application Server from Server View
    openServerView();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(serverTree);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree,
        "Stop", false)).click();
    
    waitForBlockingJobsAcomplished(10*1000L , STOPPING_JBOSS_EAP_43_RUNTIME);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree,
        "Delete", false)).click();
    bot.shell("Delete Server").activate();
    bot.button(WidgetVariables.OK_BUTTON).click();
    
    setException(null);
    
  }
  /**
   * Delete JSF Project from workspace
   */
  private void deleteJsfProject() {
    
    openPackageExplorer();
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        DELETE_PROJECT_POPUP_MENU, false)).click();
    
    bot.shell("Delete Resources").activate();
    bot.button(WidgetVariables.OK_BUTTON).click();
    
    delay();
    
  }

  /**
   * Remove JSF Capabilities from JSF Project
   * @param jbdsIsRunning
   */
  private void removeJSFCapabilities(boolean jbdsIsRunning) {

    openWebProjects();
    
    delay();
    
    SWTBot webProjects = bot.viewByTitle(WidgetVariables.WEB_PROJECTS).bot();
    SWTBotTree tree = webProjects.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));

    if (jbdsIsRunning){
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
          JBDS_REMOVE_JSF_CAPABILITIES_POPUP_MENU, true)).click();
    }
    else{
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
          WEB_PROJECT_JBT_JSF_POPUP_MENU, true)).menu(
            JBT_REMOVE_JSF_CAPABILITIES_POPUP_MENU).click();
      
    }

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
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        PACKAGE_EXPLORER_JBT_POPUP_MENU, false)).menu(
        ADD_JSF_CAPABILITIES_POPUP_MENU).click();

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
    SWTBot packageExplorer = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER)
        .bot();
    SWTBotTree tree = packageExplorer.tree();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree, 
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        CLOSE_PROJECT_POPUP_MENU, false)).click();
    
    delay();
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        OPEN_PROJECT_POPUP_MENU, false)).click();
    
  }
  
  private void setMiRunOnServer(MenuItem menuItem){
    miRunOnServer = menuItem;
  }
  
  private MenuItem getMiRunOnServer(){
    return miRunOnServer;
  }
  
}
