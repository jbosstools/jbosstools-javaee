/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewFileWizardAction;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.condition.ProgressInformationShellIsActiveCondition;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class CDIProjectHelper extends CDIBase{
	
	/**
	 * Method creates new CDI Project with CDI Web Project wizard
	 * @param projectName
	 */
	public void createCDIProjectWithCDIWizard(String projectName) {
		
		new NewFileWizardAction().run()
			.selectTemplate(CDIConstants.CDI_GROUP, CDIConstants.CDI_WEB_PROJECT).next();
		new DynamicWebProjectWizard().setProjectName(projectName).finishWithWait();		
	}
	
	/**
	 * Method creates new CDI Project with Dynamic Web Project, after that it 
	 * adds CDI Support
	 * @param projectName
	 */
	public void createCDIProjectWithDynamicWizard(String projectName) {
		createDynamicWebProject(projectName);
		addCDISupport(projectName);
	}
	
	/**
	 * Method creates new Dynamic Web Project with CDI Preset checked
	 * @param projectName
	 */
	public void createDynamicWebProjectWithCDIPreset(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate(CDIConstants.WEB_GROUP, CDIConstants.DYNAMIC_WEB_PROJECT).next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIPreset().finishWithWait();
	}
	
	/**
	 * Method creates new Dynamic Web Project with CDI Facets checked
	 * @param projectName
	 */
	public void createDynamicWebProjectWithCDIFacets(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate(CDIConstants.WEB_GROUP, CDIConstants.DYNAMIC_WEB_PROJECT).next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIFacet().finishWithWait();
	}
	
	/**
	 * Methods checks if project with entered name exists in actual workspace
	 * @param projectName
	 * @return 
	 */
	public boolean projectExists(String projectName) {
		SWTBotTree tree = projectExplorer.bot().tree();
		boolean projectExists = false;
		try {
			tree.getTreeItem(projectName);
			projectExists = true;
		}catch (WidgetNotFoundException exc) {
		}
		return projectExists;
	}

	/**
	 * Method creates new Dynamic Web Project
	 * @param projectName
	 */
	private void createDynamicWebProject(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate(CDIConstants.WEB_GROUP, CDIConstants.DYNAMIC_WEB_PROJECT).next();
		new DynamicWebProjectWizard().setProjectName(projectName).finishWithWait();
	}
	
	/**
	 * Method adds CDI support to project with entered name
	 * @param projectName
	 */
	public void addCDISupport(String projectName) {
		projectExplorer.selectProject(projectName);
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = tree.getTreeItem(projectName);
		item.expand();
		NodeContextUtil.nodeContextMenu(tree, item, CDIConstants.CONFIGURE, 
				CDIConstants.ADD_CDI_SUPPORT).click();
		bot.activeShell().bot().button(IDELabel.Button.OK).click();
		bot.waitWhile(new ProgressInformationShellIsActiveCondition(), TaskDuration.LONG.getTimeout());
	}
	
	/**
	 * Method checks if entered project has CDI support set	
	 * @param projectName
	 * @return
	 */
	public boolean checkCDISupport(String projectName) {
		projectExplorer.selectProject(projectName);
		
		SWTBotTree tree = projectExplorer.bot().tree();
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,CDIConstants.PROPERTIES,false)).click();
	    
	    bot.tree().expandNode(CDIConstants.CDI_PROPERTIES_SETTINGS).select();	    	    
		boolean isCDISupported = bot.checkBox().isChecked();
		bot.button(IDELabel.Button.CANCEL).click();
		return isCDISupported;
	}
	
}
