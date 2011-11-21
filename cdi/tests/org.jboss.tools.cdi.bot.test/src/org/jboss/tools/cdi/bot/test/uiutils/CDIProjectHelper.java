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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewFileWizardAction;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.Timing;

public class CDIProjectHelper extends CDIBase{
	
	
	public void createCDIProject(String projectName) {
		createDynamicWebProject(projectName);
		addCDISupport(projectName);
	}
	
	public void createDynamicWebProjectWithCDIPreset(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIPreset().finish();
		util.waitForNonIgnoredJobs();		
	}
	
	public void createDynamicWebProjectWithCDIFacets(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIFacet().finish();
		bot.sleep(Timing.time5S());		
		util.waitForNonIgnoredJobs();		
	}
	
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

	private void createDynamicWebProject(String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).finish();
		util.waitForNonIgnoredJobs();		
	}
	
	private void addCDISupport(String projectName) {
		projectExplorer.selectProject(projectName);
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = tree.getTreeItem(projectName);
		item.expand();
		NodeContextUtil.nodeContextMenu(tree, item, "Configure",
				"Add CDI (Context and Dependency Injection) support...")
				.click();
		bot.activeShell().bot().button("OK").click();
		bot.sleep(Timing.time2S());		
		util.waitForNonIgnoredJobs();
	}
	
}
