package org.jboss.tools.cdi.bot.test.uiutils.actions;

import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;

public class CDIUtil {
	

	public static void addCDISupport(final SWTBotTree tree, SWTBotTreeItem item, SWTBotExt bot, SWTUtilExt util) {
		nodeContextMenu(tree, item, "Configure",
				"Add CDI (Context and Dependency Injection) support...")
				.click();
		bot.activeShell().bot().button("OK").click();
		bot.sleep(2000);
		util.waitForNonIgnoredJobs();
	}
	
	public static void resolveQuickFix(final SWTBotTree tree, SWTBotTreeItem item, SWTBotExt bot, SWTUtilExt util) {
		nodeContextMenu(bot.tree(), item, "Quick Fix")
				.click();
		bot.activeShell().bot().button("Finish").click();
		bot.sleep(2000);
		util.waitForNonIgnoredJobs();
	}
	
	public static void copyResourceToClass(SWTBotEditor classEdit,
			InputStream resource, boolean closeEdit) {
		SWTBotEclipseEditor st = classEdit.toTextEditor();
		st.selectRange(0, 0, st.getText().length());
		String code = readStream(resource);
		st.setText(code);
		classEdit.save();
		if (closeEdit) classEdit.close(); 		
	}
	
	public static SWTBotMenu nodeContextMenu(final SWTBotTree tree,
			SWTBotTreeItem item, final String... menu) {
		assert menu.length > 0;
		ContextMenuHelper.prepareTreeItemForContextMenu(tree, item);
		return UIThreadRunnable.syncExec(new Result<SWTBotMenu>() {

			public SWTBotMenu run() {
				SWTBotMenu m = new SWTBotMenu(ContextMenuHelper.getContextMenu(
						tree, menu[0], false));
				for (int i = 1; i < menu.length; i++) {
					m = m.menu(menu[i]);
				}
				return m;
			}
		});
	}
	
	private static String readStream(InputStream is) {
		// we don't care about performance in tests too much, so this should be
		// OK
		return new Scanner(is).useDelimiter("\\A").next();
	}

}
