/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.matcher;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

@SuppressWarnings("restriction")
public class TableExistsInShellMatcher extends TypeSafeMatcher<SWTBotShell> {

	public void describeTo(Description description) {
		description.appendText("shell does not contain any " +
				"table widget");
	}

	@Override
	public boolean matchesSafely(SWTBotShell shell) {
		try {
			shell.bot().table();
			return true;
		} catch (WidgetNotFoundException exc) {				
			return false;
		}
	}

}
