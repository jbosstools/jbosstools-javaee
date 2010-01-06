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
package org.jboss.tools.struts.ui.bot.test;

import org.jboss.tools.struts.ui.bot.test.smoke.AddRemoveStrutsCapabilities;
import org.jboss.tools.struts.ui.bot.test.smoke.CreateNewStrutsProjectTest;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * This is struts swtbot testcase for JBoss Tools.
 * 
 * @author Vladimir Pakan
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( {CreateNewStrutsProjectTest.class,
  AddRemoveStrutsCapabilities.class})  
public class StrutsAllBotTests extends SWTTestExt {
  public static final String STRUTS_PROJECT_NAME = "strutsTest";
  @BeforeClass
  public static void setUp() {
    properties = util.loadProperties(Activator.PLUGIN_ID);
    eclipse.closeView(IDELabel.View.WELCOME);
  }

  @AfterClass
  public static void tearDown() {
    // Ready for later usage
  }
}