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
package org.jboss.tools.struts.ui.bot.test;

import java.util.logging.Logger;

import org.jboss.tools.struts.ui.bot.test.smoke.AddRemoveStrutsCapabilities;
import org.jboss.tools.struts.ui.bot.test.smoke.CreateNewStrutsProjectTest;
import org.jboss.tools.struts.ui.bot.test.smoke.ImportStrutsProjectTest;
import org.jboss.tools.struts.ui.bot.test.smoke.RenameStrutsConfigXmlFile;
import org.jboss.tools.struts.ui.bot.test.smoke.RenameTldFile;
import org.jboss.tools.struts.ui.bot.test.smoke.RunStrutsProjectOnServer;
import org.jboss.tools.struts.ui.bot.test.tutorial.TutorialTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * This is struts swtbot testcase for JBoss Tools.
 * 
 * System properties:
 *  -Dswtbot.test.properties.file=$PATH
 *  -Dusage_reporting_enabled=$BOOLEAN
 *  
 *  Suite duration: aprox. 10min
 * 
 * @author Vladimir Pakan
 * @author Lukas Jungmann
 * 
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	TutorialTest.class,
	CreateNewStrutsProjectTest.class,
	RunStrutsProjectOnServer.class,
	AddRemoveStrutsCapabilities.class,
	ImportStrutsProjectTest.class,
	RenameStrutsConfigXmlFile.class,
	RenameTldFile.class})
public class StrutsAllBotTests {
	public static final String STRUTS_PROJECT_NAME = "strutsTest";
}