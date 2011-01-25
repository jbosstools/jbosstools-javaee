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
package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.jboss.tools.ui.bot.ext.SWTBotExt;

public abstract class Action<T> {

	private final SWTBotExt bot = new SWTBotExt();
	private final String[] actionPath;

	public Action(String... path) {
		assert path.length > 0;
		this.actionPath = path;
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
	
	protected String[] getActionPath() {
		return actionPath;
	}
}
