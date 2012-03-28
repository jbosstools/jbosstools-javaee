/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class AssignableBeansDialog {

	private SWTBotShell shell = null; 
	
	public AssignableBeansDialog(SWTBotShell shell) {
		this.shell = shell;
		showAmbiguousBeans().showUnavailableBeans();
	}
	
	public SWTBotTable getAllBeans() {
		return shell.bot().table();
	}
	
	public AssignableBeansDialog hideUnavailableBeans() {
		getTreeItem("Unavailable Beans").uncheck();
		return this;
	}
	
	public AssignableBeansDialog showUnavailableBeans() {
		getTreeItem("Unavailable Beans").check();
		return this;
	}
	
	public AssignableBeansDialog hideAmbiguousBeans() {
		getTreeItem("Eliminated ambiguous").uncheck();
		return this;
	}
	
	public AssignableBeansDialog showAmbiguousBeans() {
		getTreeItem("Eliminated ambiguous").check();
		return this;
	}
	
	private SWTBotTree getAllOptions() {
		return shell.bot().tree();
	}
	
	protected SWTBotTreeItem getTreeItem(String treeItemText) {
		for (SWTBotTreeItem ti : getAllOptions().getAllItems()) {
			if (ti.getText().contains(treeItemText)) return ti;
		}
		return null;
	}
	
}
