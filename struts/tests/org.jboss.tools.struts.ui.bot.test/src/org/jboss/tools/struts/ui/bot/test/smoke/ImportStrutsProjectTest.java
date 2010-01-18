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

import java.io.File;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.test.WidgetVariables;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;
/**
 * Test importing Struts Project
 * @author Vladimir Pakan
 *
 */
public class ImportStrutsProjectTest extends SWTTestExt{
  
  /**
   * Test importing Struts Project
   */
  private SWTJBTExt swtJbtExt = null;
  public ImportStrutsProjectTest (){
    swtJbtExt = new SWTJBTExt(bot);
  }
  @Test
	public void testImportStrutsProjectTest() {
    // Remove Struts Project
    swtJbtExt.deleteProject(StrutsAllBotTests.STRUTS_PROJECT_NAME);
    // Import Struts Project
    String webXmlFileLocation = SWTUtilExt
        .getTestPluginLocation(StrutsAllBotTests.STRUTS_PROJECT_NAME)
        + File.separator
        + "WebContent"
        + File.separator
        + "WEB-INF"
        + File.separator + "web.xml";

    bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.IMPORT).click();
    bot.shell(IDELabel.Shell.IMPORT).activate();
    SWTBotTree tree = bot.tree();
    swtJbtExt.delay();
    tree.expandNode("Other").select(IDELabel.Menu.STRUTS_PROJECT);
    bot.button("Next >").click();
    bot.shell(IDELabel.Shell.IMPORT_STRUTS_PROJECT).activate();

    bot.textWithLabel("web.xml Location*").setText(webXmlFileLocation);

    bot.button(WidgetVariables.NEXT_BUTTON).click();
    // Check if there is defined Application Server if not create one
    if (!SWTJBTExt.isServerDefinedInWebWizardPage(bot)) {
      // Specify Application Server for Deployment
      bot.button(WidgetVariables.NEW_BUTTON, 1).click();
      bot.shell("New Server").activate();
      bot.tree().expandNode("JBoss Enterprise Middleware").select(
          "JBoss Enterprise Application Platform 4.3");
      bot.button(WidgetVariables.FINISH_BUTTON).click();
      // Server Jobs has different labels now
    }
    swtJbtExt.delay();
    // Finish Import
    bot.button(WidgetVariables.FINISH_BUTTON).click();
    bot.shell("Warning").activate();
    bot.button(WidgetVariables.CONTINUE_BUTTON).click();
    
    eclipse.closeOpenAssociatedPerspectiveShellIfOpened(false);

    eclipse.showView(ViewType.WEB_PROJECTS);
    
    assertTrue("Project " + StrutsAllBotTests.STRUTS_PROJECT_NAME
        + " was not created properly.", SWTEclipseExt
        .treeContainsItemWithLabel(bot.viewByTitle(IDELabel.View.WEB_PROJECTS)
            .bot().tree(), StrutsAllBotTests.STRUTS_PROJECT_NAME));

  }
	
}
