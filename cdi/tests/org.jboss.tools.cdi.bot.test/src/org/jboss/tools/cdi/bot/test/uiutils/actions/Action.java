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
package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;

public abstract class Action<T> {

	private final SWTBotExt bot = new SWTBotExt();
	private final String[] actionPath;
	private final String[] nodePath;
	private final SWTBotTree tree;

	public Action(String... path) {
		this(null, null, path);
	}
	
	public Action(SWTBotTree tree, String[] nodePath, String... actionPath) {
		assert actionPath.length > 0;
		this.tree = tree;
		this.nodePath = nodePath;
		this.actionPath = actionPath;
	}

	public abstract T run();

	protected SWTBot performMenu() {
		SWTBotMenu m = bot.menu(actionPath[0]);
		for (int i = 1; i < actionPath.length; i++) {
			m = m.menu(actionPath[i]);
		}
		m.click();
		return new SWTBot();
	}
	
	protected SWTBot pepformPopup() {
		assert tree != null : "Tree cannot be null!";
		ContextMenuHelper.prepareTreeItemForContextMenu(tree, tree.expandNode(nodePath));
		UIThreadRunnable.syncExec(new Result<SWTBotMenu>() {

			public SWTBotMenu run() {
				SWTBotMenu m = new SWTBotMenu(ContextMenuHelper.getContextMenu(
						tree, actionPath[0], false));
				for (int i = 1; i < actionPath.length; i++) {
					m = m.menu(actionPath[i]);
				}
				return m;
			}
		}).click();
		return new SWTBot();
	}
}
