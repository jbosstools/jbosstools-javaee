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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.ui.bot.ext.Timing;

public class SearchNamedDialogWizard extends Wizard {

	private static final String NAMED_SEARCH_TITLE = "Open CDI Named Bean";
	private List<String> matchingItems = null;
	
	public SearchNamedDialogWizard() {
		super(new SWTBot().activeShell().widget);
		assert (NAMED_SEARCH_TITLE).equals(getText());	
		matchingItems = new ArrayList<String>();
	}
	
	public SearchNamedDialogWizard setNamedPrefix(String prefix) {
		bot().text().setText(prefix);
		bot().sleep(Timing.time2S());
		return this;
	}
	
	public void ok() {
		clickButton("OK");		
		bot().sleep(Timing.time1S());
	}
	
	public SearchNamedDialogWizard setMatchingItems(String... items) {
		bot().table(0).select(items);
		return this;
	}
	
	public List<String> matchingItems() {
		int tableItemsCount = bot().table(0).rowCount();
		for (int i = 0; i < tableItemsCount; i++) {
			String itemInTable = bot().table(0).getTableItem(i).getText();
			if (itemInTable.contains("Workspace matches")) continue;
			matchingItems.add(itemInTable);
		}
		return matchingItems;
	}
	
}
