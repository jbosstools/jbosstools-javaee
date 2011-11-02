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
import org.jboss.tools.cdi.bot.test.openon.CDIFindObserverForEventTest;
import org.jboss.tools.cdi.bot.test.openon.CDIOpenOnTest;
import org.jboss.tools.cdi.bot.test.quickfix.CDIQuickFixTest;
import org.jboss.tools.cdi.bot.test.seam3.CDISeam3Test;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.cdi.bot.test.wizard.CDIATWizardTest;
import org.jboss.tools.cdi.bot.test.wizard.CDIConfigurationPresetTest;
import org.jboss.tools.cdi.bot.test.wizard.CDIPerspectiveTest;
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
 *  SERVER=JBOSS_AS,6.0,default,/home/lukas/latest/jboss-6.0.0.Final
 *  JAVA=1.6,/space/java/sdk/jdk1.6.0_22
 *  
 *  
 *  Suite duration: aprox. 14min
 * 
 * @author Lukas Jungmann
 * @author Jaroslav Jankovic
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({	
	//CDIPerspectiveTest.class,
	CDIConfigurationPresetTest.class, 
	CDIATWizardTest.class,
	BeansEditorTest.class,
	CDIQuickFixTest.class,
	CDIOpenOnTest.class,
	CDIFindObserverForEventTest.class, 
	CDISeam3Test.class
	})
public class CDIAllBotTests extends CDIBase {
		
	/*
	 * init method "setup()" shows a project explorer view as default,
	 * disable folding ( to easier source code editing)
	 */
	@BeforeClass
	public static void setUpSuite() {		
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		CDIUtil.disableFolding(bot, util);		
	}
	
}
