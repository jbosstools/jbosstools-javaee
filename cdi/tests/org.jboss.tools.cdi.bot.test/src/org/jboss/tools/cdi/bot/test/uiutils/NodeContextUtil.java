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

import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;

public class NodeContextUtil {
	
	private NodeContextUtil() {
		throw new AssertionError();
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
	
}
