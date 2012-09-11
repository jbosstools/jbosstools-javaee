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
import org.jboss.tools.cdi.bot.test.CDIConstants;

public class QuickFixDialogWizard extends Wizard {

	private static final String QUICK_FIX_TITLE = CDIConstants.QUICK_FIX;
	private List<String> availableFixes = null;
	private List<String> resources = null;
	
	public QuickFixDialogWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert (QUICK_FIX_TITLE).equals(getText());	
		availableFixes = new ArrayList<String>();
		resources = new ArrayList<String>();
	}
	
	public QuickFixDialogWizard setFix(String fix) {
		bot().table(0).select(fix);
		return this;
	}
	
	public List<String> getAvailableFixes() {
		int tableItemsCount = bot().table(0).rowCount();
		for (int i = 0; i < tableItemsCount; i++) {
			availableFixes.add(bot().table(0).getTableItem(i).getText());
		}
		return availableFixes;
	}

	public QuickFixDialogWizard setResource(String resource) {
		bot().table(1).getTableItem(resource).check();
		return this;
	}
	
	public List<String> getResources() {
		int tableItemsCount = bot().table(1).rowCount();
		for (int i = 0; i < tableItemsCount; i++) {
			resources.add(bot().table(1).getTableItem(i).getText());
		}
		return resources;
	}

	public String getDefaultCDIQuickFix() {
		for (String fix : getAvailableFixes()) {
			if (fix.contains("Configure") 
					|| fix.contains("Add @Suppress")) continue;
			return fix;
		}
		throw new IllegalStateException("No default CDI quick fix is provided " +
				"for validation problem");
	}
	
	public String getCDIQuickFix(String text) {
		for (String fix : getAvailableFixes()) {
			if (fix.contains(text)) return fix;
			
		}
		throw new IllegalStateException("No CDI quick fix contains " + text);
	}
	
}
