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
package org.jboss.tools.cdi.bot.test;

import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.cdi.bot.test.uiutils.SWTEclipseCDIExtUtil;
import org.jboss.tools.cdi.bot.test.wizard.CDIProjectWithDynamicWizardTest;
import org.jboss.tools.cdi.bot.test.wizard.WizardTest;
import org.jboss.tools.cdi.bot.test.wizard.ConfigurationPresetTest;
import org.jboss.tools.cdi.bot.test.wizard.FacetTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite requires JBoss AS 6 or newer
 * 
 * System properties:
 *  -Dswtbot.test.properties.file=$PATH
 *  -Dusage_reporting_enabled=$BOOLEAN
 *  
 *  Format of swtbot.properties file:
 *  SERVER=EAP|JBOSS_AS,<server version>,<jre version to run with>|default,<server home>
 *  
 *  Sample swtbot.properties file:
 *
 *  SERVER=JBOSS_AS,6.0,default,/home/jjankovi/Dokumenty/Red_Hat_Stuff/Runtimes/jboss-6.0.0.Final
 *  JAVA=1.6,/space/java/sdk/jdk1.6.0_22
 *  
 *  
 *  Suite duration: aprox. 3min
 * 
 * @author Jaroslav Jankovic
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({	
	//PerspectiveTest.class,	 
	ConfigurationPresetTest.class,
	FacetTest.class, 
	CDIProjectWithDynamicWizardTest.class,
	WizardTest.class,	
	BeansEditorTest.class,
	})
public class CDISmokeBotTests extends CDITestBase {
	
	/*
	 * init method "setup()" shows a project explorer view as default,
	 * disable folding (due to easier source code editing)
	 */
	@BeforeClass
	public static void setUpSuite() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTEclipseCDIExtUtil.disableFolding(bot, util);
	}
}
