/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.Timing;

public class Wizard extends SWTBotShell {

	public Wizard(Shell shell) {
		super(shell);
		assert getText().contains("New ");
	}

	public Wizard selectTemplate(String... item) {
		assert item.length > 0;
		SWTBotTree tree = bot().tree();
		SWTBotTreeItem ti = null;
		for (int i = 0; i < item.length - 1; i++) {
			ti = ti != null ? ti.expandNode(item[i]).select() : tree.expandNode(item[i]).select();
		}
		if (ti != null) {
			ti.select(item[item.length - 1]);
		} else {
			tree.select(item[item.length - 1]);
		}
		return this;
	}

	public Wizard back() {
		clickButton("< Back");
		return this;
	}

	public Wizard next() {
		clickButton("Next >");
		return this;
	}

	public void cancel() {
		clickButton("Cancel");
	}

	public void finish() {
		clickButton("Finish");
	}
	
	public boolean canFinish() {
		return canClick("Finish");
	}
	
	public Wizard setCDIPreset() {
		bot().comboBoxInGroup("Configuration", 0).
			setSelection("Dynamic Web Project with CDI (Context and Dependency Injection)");			
		return this;
	}
	
	public Wizard setCDIFacet() {
		clickButton("Modify...");
		SWTBot bot = bot().shell("Project Facets").bot();
		setCDIFacet(bot);
		bot().sleep(Timing.time1S());
		return this;
	}
	
	private void setCDIFacet(SWTBot bot) {
		SWTBotTree tree= bot.tree();
		for (SWTBotTreeItem ti: tree.getAllItems())  {							
			if (ti.cell(0).contains("CDI (Contexts and Dependency Injection)")) {				
				ti.check();
				break;
			}
		}
		bot.sleep(Timing.time1S());
		bot.button("OK").click();
	}
	
	

	protected void clickButton(String text) {
		bot().button(text).click();
		bot().sleep(500);
	}

	protected void setText(String label, String text) {
		SWTBotText t = bot().textWithLabel(label);
		t.setFocus();
		t.setText(text);
	}
		
	protected boolean canClick(String button) {
		return bot().button(button).isEnabled();
	}
}
