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

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

public class QuickFixDialogWizard extends Wizard {

	public QuickFixDialogWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("Quick Fix").equals(getText());		
	}
	
	public QuickFixDialogWizard setFix(SWTBotTableItem fix) {
		fix.select();
		return this;
	}
	
	public SWTBotTableItem[] getFixes() {
		SWTBotTable fixTable = bot().table(0);
		SWTBotTableItem[] fixes = new SWTBotTableItem[fixTable.rowCount()];
		for (int i = 0; i < fixTable.rowCount(); i++) {
			fixes[i] = fixTable.getTableItem(i);
		}
		return fixes;
	}

	public QuickFixDialogWizard setResource(SWTBotTableItem resource) {
		resource.check();
		return this;
	}
	
	public SWTBotTableItem[] getResources() {
		SWTBotTable resourceTable = bot().table(1);
		SWTBotTableItem[] resources = new SWTBotTableItem[resourceTable.rowCount()];
		for (int i = 0; i < resourceTable.rowCount(); i++) {
			resources[i] = resourceTable.getTableItem(i);
		}
		return resources;
	}
	
}
