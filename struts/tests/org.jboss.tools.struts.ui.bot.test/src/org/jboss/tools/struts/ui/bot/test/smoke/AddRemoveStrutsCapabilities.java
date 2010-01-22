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

import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.Test;
/**
 * Test adding and removing Struts Capabilities from/to Struts Project
 * @author Vladimir Pakan
 *
 */
public class AddRemoveStrutsCapabilities extends SWTTestExt{
  private SWTJBTExt swtJbtExt = null;
  public AddRemoveStrutsCapabilities (){
    swtJbtExt = new SWTJBTExt(bot);
  }
  @Test
	public void testCreateNewStrutsProject() {
     boolean jbdsIsRunning = SWTJBTExt.isJBDSRun(bot);
     removeStrutsCapabilities(jbdsIsRunning);
     addStrutsCapabilities();
	}
  
  /**
   * Remove Struts Capabilities from Struts Project
   * @param jbdsIsRunning
   */
  private void removeStrutsCapabilities(boolean jbdsIsRunning) {

    swtJbtExt.removeProjectFromServers(StrutsAllBotTests.STRUTS_PROJECT_NAME);
    util.delay();
    eclipse.showView(ViewType.WEB_PROJECTS);
    
    SWTBotTree tree = bot.viewByTitle(IDELabel.View.WEB_PROJECTS).bot().tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(StrutsAllBotTests.STRUTS_PROJECT_NAME));

    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
      IDELabel.Menu.WEB_PROJECT_JBT_STRUTS, false)).menu(
        IDELabel.Menu.JBT_REMOVE_STRUTS_CAPABILITIES).click();

    bot.shell("Confirmation").activate();
    bot.button(IDELabel.Button.OK).click();

    util.waitForNonIgnoredJobs(5L*1000);
    
    assertTrue(
      "Project "
        + StrutsAllBotTests.STRUTS_PROJECT_NAME
        + " was not removed from Web Projects view after Struts Capabilities were removed.",
      !SWTEclipseExt.treeContainsItemWithLabel(bot.viewByTitle(IDELabel.View.WEB_PROJECTS).bot().tree(),
        StrutsAllBotTests.STRUTS_PROJECT_NAME));
    
  }

  /**
   * Add Struts Capabilities to JSF Project
   */
  private void addStrutsCapabilities() {

    swtJbtExt.removeProjectFromServers(StrutsAllBotTests.STRUTS_PROJECT_NAME);
    
    SWTBotTree tree = eclipse.showView(ViewType.PACKAGE_EXPLORER).tree();
    
    util.delay();

    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(StrutsAllBotTests.STRUTS_PROJECT_NAME));
    
    try{
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.PACKAGE_EXPLORER_JBT, false)).menu(
        IDELabel.Menu.ADD_STRUTS_CAPABILITIES).click();
    } catch (WidgetNotFoundException wnfe){
      // From 3.1.0.RC1 version this menu is moved to Configure submenu
      new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.PACKAGE_EXPLORER_CONFIGURE, false)).menu(
        IDELabel.Menu.ADD_STRUTS_CAPABILITIES).click();
    }

    bot.shell("Add Struts Capabilities").activate();
    bot.button(IDELabel.Button.NEXT).click();
    bot.button(IDELabel.Button.FINISH).click();

    eclipse.closeOpenAssociatedPerspectiveShellIfOpened(false);
    
    util.waitForNonIgnoredJobs(5L*1000);
    
    eclipse.showView(ViewType.WEB_PROJECTS);
    
    assertTrue("JSF Capabilities were not added to project "
        + StrutsAllBotTests.STRUTS_PROJECT_NAME,
        SWTEclipseExt.treeContainsItemWithLabel(bot.viewByTitle(IDELabel.View.WEB_PROJECTS).bot().tree(),
            StrutsAllBotTests.STRUTS_PROJECT_NAME));

  }
}
