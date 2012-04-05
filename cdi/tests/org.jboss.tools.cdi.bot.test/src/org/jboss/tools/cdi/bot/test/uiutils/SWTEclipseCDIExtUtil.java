/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.condition.ShellIsActiveCondition;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class SWTEclipseCDIExtUtil {
	
	private static final SWTBotExt bot = new SWTBotExt();
	
	private static final SWTUtilExt util = new SWTUtilExt(bot);
	
	private SWTEclipseCDIExtUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Method disables folding used in editor in eclipse
	 * @param bot
	 * @param util
	 */
	public static void disableFolding() {
		editFolding(false);
	}

	/**
	 * Method enable folding used in editor in eclipse
	 * @param bot
	 * @param util
	 */
	public static void enableFolding() {
		editFolding(true);
	}

	private static void editFolding(boolean select) {
		bot.menu(IDELabel.Menu.WINDOW).menu(IDELabel.Menu.PREFERENCES).click();
		SWTBotShell preferencesShell = bot.shell(IDELabel.Shell.PREFERENCES);
		preferencesShell.activate();
		SWTBotTreeItem item = bot.tree(0).expandNode("Java", "Editor");
		item.select("Folding");
		SWTBotCheckBox foldCheckBox = bot.checkBox("Enable folding");
		if (select) {
			foldCheckBox.select();
		} else {
			foldCheckBox.deselect();
		}
		bot.button(IDELabel.Button.OK).click();
		bot.waitWhile(new ShellIsActiveCondition(preferencesShell), 
				TaskDuration.LONG.getTimeout());
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
	}

}
