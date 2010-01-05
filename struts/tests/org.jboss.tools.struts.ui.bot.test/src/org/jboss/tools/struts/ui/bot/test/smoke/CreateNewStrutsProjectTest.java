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

package org.jboss.tools.struts.ui.bot.test.smoke;

import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.Test;
import org.jboss.tools.struts.ui.bot.test.StrutsAllBotTests;
/**
 * Test creating new Struts Project
 * @author Vladimir Pakan
 *
 */
public class CreateNewStrutsProjectTest extends SWTTestExt{
  
  public static final String STRUTS_PROJECT_NAME = "strutsTest";
  
  @Test
	public void testCreateNewStrutsProject() {
		
		// Test create new Struts Project
	  eclipse.showView(ViewType.WEB_PROJECTS);
	  eclipse.addServerRuntime(IDELabel.ServerRuntimeName.JBOSS_EAP_4_3,
	    IDELabel.ServerGroup.JBOSS_EAP_4_3,
	    IDELabel.ServerType.JBOSS_EAP_4_3,
	    StrutsAllBotTests.getProperty("JBossEap4.3Home"));
	  eclipse.createNew(EntityType.STRUTS_PROJECT);
	  bot.shell(IDELabel.Shell.NEW_STRUTS_PROJECT).activate();
	  bot.textWithLabel(IDELabel.NewStrutsProjectDialog.NAME).setText(STRUTS_PROJECT_NAME);
	  bot.comboBoxWithLabel(IDELabel.NewStrutsProjectDialog.TEMPLATE).setSelection(IDELabel.NewStrutsProjectDialog.TEMPLATE_KICK_START);
	  bot.button(IDELabel.Button.NEXT).click();
	  bot.button(IDELabel.Button.NEXT).click();
	  bot.button(IDELabel.Button.FINISH).click();
	  // if Open Associated Perspective Shell is opened close it
	  try{
	    bot.shell(IDELabel.Shell.OPEN_ASSOCIATED_PERSPECTIVE).activate();
	    bot.button(IDELabel.Button.NO).click();
	  } catch (WidgetNotFoundException wnfe){
	    // do nothing
	  }
	  
		assertTrue("Project "+ STRUTS_PROJECT_NAME + " was not created properly.",
		  SWTEclipseExt.treeContainsItemWithLabel(bot.viewByTitle(IDELabel.View.WEB_PROJECTS).bot().tree(),STRUTS_PROJECT_NAME));
		  
	}
	
}
