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
import org.jboss.tools.ui.bot.ext.wizards.SWTBotWizard;

public class Wizard extends SWTBotWizard {

	public Wizard(Shell shell) {
		super(shell);
	}

	protected void checkCheckbox(String text) {
		bot().checkBoxWithLabel(text).select();
	}
	
	protected void uncheckCheckbox(String text) {
		bot().checkBoxWithLabel(text).deselect();
	}

	protected void setTextInCombobox(String combobox, String text) {
		bot().comboBoxWithLabel(combobox).setSelection(text);		
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
