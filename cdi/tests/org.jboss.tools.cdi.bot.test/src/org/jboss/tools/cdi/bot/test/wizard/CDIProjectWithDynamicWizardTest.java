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

package org.jboss.tools.cdi.bot.test.wizard;

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({ CDIAllBotTests.class , CDISmokeBotTests.class })
public class CDIProjectWithDynamicWizardTest extends CDITestBase {
	
	@Override	
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProjectWithDynamicWizard(getProjectName());
		}
	}
	
	@Override
	public String getProjectName() {
		return "CDIDynamicWizardProject";
	}
	
	@Test
	public void testDynamicWizard() {
		if (projectHelper.projectExists(getProjectName())) {
			LOGGER.info("CDI project was sucessfully created by Dynamic Web Project wizard");
			assertTrue(projectHelper.checkCDISupport(getProjectName()));
			LOGGER.info("Project has correctly set CDI support");
		} else {
			fail("CDI project was not succesfully created with Dynamic Web Project wizard");
		}
		
	}

}
