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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.Timing;

public class OpenOnHelper extends CDITestBase {
	
	/**
	 * Method simulates "OpenOn" - press Ctrl, move mouse over selected string and
	 * context menu appears. Method selects "openOnString", open context menu, select
	 * "chosenOption" and simulates click - after OpenOn, it sets active editor to 
	 * one, opened  by OpenOn
	 * @param openOnString
	 * @param titleName
	 * @param chosenOption
	 */
	public boolean openOnByOption(String openOnString, String titleName, String chosenOption) {
		selectTextForOpenOn(openOnString, titleName);
		bot.menu(CDIConstants.NAVIGATE).menu(CDIConstants.OPEN_HYPERLINK).click();			
		bot.sleep(Timing.time500MS());
		SWTBotTable table = bot.activeShell().bot().table(0);
		
		boolean optionFound = false;
		
		for (int i = 0; i < table.rowCount(); i++) {
			if (table.getTableItem(i).getText().contains(chosenOption)) {
				optionFound = true;
				table.click(i, 0);					
				break;
			}
		}									 
		bot.sleep(Timing.time1S());
		setEd(bot.activeEditor().toTextEditor());
		return optionFound;
	}
	
	/**
	 * Method simulates direct "OpenOn" - press F3 when selecting some string.
	 * Method selects "openOnString" and simulates F3 - after OpenOn, it sets 
	 * active editor to one, opened  by OpenOn
	 * @param openOnString
	 * @param titleName
	 */
	public void openOnDirect(String openOnString, String titleName) {
		selectTextForOpenOn(openOnString, titleName);
		bot.sleep(Timing.time3S());
		getEd().pressShortcut(Keystrokes.F3);
		setEd(bot.activeEditor().toTextEditor());		
	}
	
	/**
	 * Methods select text in editor. It has some workaround when "openOnString"
	 * contains @, these string cannot be opened by openon, so offset is set to 1
	 * @param openOnString
	 * @param titleName
	 */
	private void selectTextForOpenOn(String openOnString, String titleName) {
		SWTBotEditor ed = bot.editorByTitle(titleName);
		ed.show();
		ed.setFocus();		
		int offset = openOnString.contains("@")?1:0;		
		setEd(SWTJBTExt.selectTextInSourcePane(bot, titleName,
				openOnString, offset, openOnString.length() - offset));			
	}
}
