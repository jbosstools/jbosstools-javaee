/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.struts.ui.bot.test.smoke;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.Test;

/**
 * Test creating new Struts Project
 * 
 * @author Vladimir Pakan
 * @author Lukas Jungmann
 * 
 */
@Require(server = @Server(state = ServerState.NotRunning), perspective = "Web Development")
public class CreateNewStrutsProjectTest extends SWTTestExt {

	/**
	 * Test create new Struts Project
	 */
	@Test
	public void testCreateNewStrutsProject() {
		eclipse.createNew(EntityType.STRUTS_PROJECT);
		bot.shell(IDELabel.Shell.NEW_STRUTS_PROJECT).activate();
		bot.textWithLabel(IDELabel.NewStrutsProjectDialog.NAME).setText(
				StrutsAllBotTests.STRUTS_PROJECT_NAME);
		bot.comboBoxWithLabel(IDELabel.NewStrutsProjectDialog.TEMPLATE)
				.setSelection(IDELabel.NewStrutsProjectDialog.TEMPLATE_KICK_START);
		bot.button(IDELabel.Button.NEXT).click();
		bot.sleep(1000L);
		bot.button(IDELabel.Button.NEXT).click();
		bot.button(IDELabel.Button.FINISH).click();
		bot.sleep(1500);

		SWTBot v = eclipse.showView(ViewType.PACKAGE_EXPLORER);
		SWTBotTree tree = v.tree();
		tree.setFocus();
		assertTrue("Project " + StrutsAllBotTests.STRUTS_PROJECT_NAME + " was not created properly.",
				SWTEclipseExt.treeContainsItemWithLabel(tree, StrutsAllBotTests.STRUTS_PROJECT_NAME));
	}

}
