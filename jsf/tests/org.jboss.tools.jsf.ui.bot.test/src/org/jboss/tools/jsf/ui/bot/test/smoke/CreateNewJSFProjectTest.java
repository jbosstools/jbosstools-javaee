 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.ui.bot.test.smoke;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;
/**
 * Test adding new JSF Project
 * @author Vladimir Pakan
 *
 */
public class CreateNewJSFProjectTest extends JSFAutoTestCase{
	
	public void testCreateNewJSFProject() {
		
		// Test create new JSF Project
	  // New JSF Project is created via setUp() method of super class VPEAutoTestCase.java
	  openPackageExplorer();
	  SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER).bot();
    SWTBotTree tree = innerBot.tree();
    boolean isOk = false;
    try {
      tree.getTreeItem(JBT_TEST_PROJECT_NAME);
      isOk = true;
    } catch (WidgetNotFoundException e) {
    }
		assertTrue("Project "
		  + JBT_TEST_PROJECT_NAME 
		  + " was not created properly.",isOk);
	}

	@Override
	protected void closeUnuseDialogs() {
		// not used
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		return false;
	}
	
}
