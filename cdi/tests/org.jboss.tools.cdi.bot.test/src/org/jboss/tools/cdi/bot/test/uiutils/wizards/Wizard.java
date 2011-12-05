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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.Timing;

public class Wizard extends SWTBotShell {

	protected static final String NEXT = "Next >";
	protected static final String BACK = "< Back";
	protected static final String CANCEL = "Cancel";
	protected static final String FINISH = "Finish";
	
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
		clickButton(BACK);
		return this;
	}

	public Wizard next() {
		clickButton(NEXT);
		return this;
	}

	public void cancel() {
		clickButton(CANCEL);
	}

	public void finish() {
		clickButton(FINISH);
	}
	
	public boolean canFinish() {
		return canClick(FINISH);
	}
	
	public boolean canNext() {
		return canClick(NEXT);
	}
		
	protected void checkCheckbox(String text) {
		bot().checkBoxWithLabel(text).select();
		bot().sleep(Timing.time500MS());
	}
	
	protected void uncheckCheckbox(String text) {
		bot().checkBoxWithLabel(text).deselect();
		bot().sleep(Timing.time500MS());
	}
	
	protected void clickButton(String text) {
		bot().button(text).click();
		bot().sleep(Timing.time500MS());
	}

	protected void setText(String label, String text) {
		SWTBotText t = bot().textWithLabel(label);
		t.setFocus();
		t.setText(text);
	}
	
	protected void setTextInCombobox(String combobox, String text) {
		bot().comboBoxWithLabel(combobox).setSelection(text);
		bot().sleep(Timing.time500MS());		
	}
		
	protected boolean canClick(String button) {
		return bot().button(button).isEnabled();
	}
	
	protected boolean canCheckInCombobox(String combobox, String text) {		
		for (int i = 0; i < bot().comboBoxWithLabel(combobox).itemCount(); i++) {
			if (bot().comboBoxWithLabel(combobox).items()[i].equals(text)) {
				return true;
			}
		}
		return false;
	}
}
