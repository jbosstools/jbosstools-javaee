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

package org.jboss.tools.jsf.ui.bot.test.templates;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.WidgetVariables;
/**
 * Test creating new JSF project template from existing project
 * @author Vladimir Pakan
 *
 */
public class CreateNewTemplateFromJSFProject extends JSFAutoTestCase{
  
  private static final String TEST_PAGE_NAME = "CreateNewTemplateFromJSFProject.jsp";
  private static final String TEMPLATE_NAME = "JsfTestTemplate";
  private static final String TEMPLATE_IMPLEMENTATION = "JSF 1.2";
  private static final String TEMPLATE_TEST_PROJECT_NAME = "newTemplateProject";
	
	public void testCreateNewJSFProject() {
		createJspPage(CreateNewTemplateFromJSFProject.TEST_PAGE_NAME);
    openWebProjects();
    delay();
    SWTBot webProjects = bot.viewByTitle(WidgetVariables.WEB_PROJECTS).bot();
    SWTBotTree tree = webProjects.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(JBT_TEST_PROJECT_NAME));

    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
        IDELabel.Menu.WEB_PROJECT_JBT_JSF, false)).menu(
          IDELabel.Menu.SAVE_AS_TEMPLATE).click();
    // create template
    bot.shell(IDELabel.Shell.ADD_JSF_PROJECT_TEMPLATE).activate();
    bot.textWithLabel(IDELabel.AddJSFProjectTemplateDialog.NAME_TEXT_LABEL)
      .setText(CreateNewTemplateFromJSFProject.TEMPLATE_NAME);
    bot.comboBoxWithLabel(IDELabel.AddJSFProjectTemplateDialog.IMPLEMENTATION_COMBO_LABEL)
      .setText(CreateNewTemplateFromJSFProject.TEMPLATE_IMPLEMENTATION);
    bot.button(IDELabel.Button.FINISH).click();
    // create project using newly created template
    SWTBot wiz = open
      .newObject(ActionItem.NewObject.JBossToolsWebJSFJSFProject.LABEL);
    wiz.textWithLabel(IDELabel.NewJsfProjectDialog.PROJECT_NAME_LABEL)
      .setText(CreateNewTemplateFromJSFProject.TEMPLATE_TEST_PROJECT_NAME);
    wiz.comboBoxWithLabel(IDELabel.NewJsfProjectDialog.JSF_ENVIRONMENT_LABEL)
      .setSelection(CreateNewTemplateFromJSFProject.TEMPLATE_IMPLEMENTATION);
    wiz.comboBoxWithLabel(IDELabel.NewJsfProjectDialog.TEMPLATE_LABEL)
      .setSelection(CreateNewTemplateFromJSFProject.TEMPLATE_NAME);
    wiz.button(IDELabel.Button.NEXT).click();
    wiz.comboBoxWithLabel(IDELabel.NewJsfProjectDialog.RUNTIME_LABEL)
      .setSelection(SWTTestExt.configuredState.getServer().name); //$NON-NLS-1$ //$NON-NLS-2$
    open.finish(wiz);
    waitForBlockingJobsAcomplished(60 * 1000L, BUILDING_WS);
    packageExplorer.selectProject(CreateNewTemplateFromJSFProject.TEMPLATE_TEST_PROJECT_NAME);
    packageExplorer.bot().menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.REFRESH).click();
    bot.sleep(Timing.time3S());
    packageExplorer.selectTreeItem(CreateNewTemplateFromJSFProject.TEST_PAGE_NAME,
        new String[]{CreateNewTemplateFromJSFProject.TEMPLATE_TEST_PROJECT_NAME,
          "WebContent.pages"}).doubleClick();
    packageExplorer.deleteProject(CreateNewTemplateFromJSFProject.TEMPLATE_TEST_PROJECT_NAME, true);
        
	}

	@Override
	protected void closeUnuseDialogs() {
		// not used
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		return false;
	}
	
}
