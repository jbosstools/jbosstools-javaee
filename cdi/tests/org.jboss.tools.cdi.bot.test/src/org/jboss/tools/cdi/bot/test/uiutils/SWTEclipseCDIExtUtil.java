package org.jboss.tools.cdi.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;

public class SWTEclipseCDIExtUtil extends SWTEclipseExt {
	
	private SWTEclipseCDIExtUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Method disables folding used in editor in eclipse
	 * @param bot
	 * @param util
	 */
	public static void disableFolding(SWTBotExt bot, SWTUtilExt util) {
		editFolding(bot, util, false);
	}

	/**
	 * Method enable folding used in editor in eclipse
	 * @param bot
	 * @param util
	 */
	public static void enableFolding(SWTBotExt bot, SWTUtilExt util) {
		editFolding(bot, util, true);
	}

	private static void editFolding(SWTBotExt bot, SWTUtilExt util,
			boolean select) {
		bot.menu("Window").menu("Preferences").click();
		bot.shell("Preferences").activate();
		SWTBotTreeItem item = bot.tree(0).expandNode("Java", "Editor");
		item.select("Folding");
		SWTBotCheckBox foldCheckBox = bot.checkBox("Enable folding");
		if (select) {
			foldCheckBox.select();
		} else {
			foldCheckBox.deselect();
		}
		bot.button("OK").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
	}

}
