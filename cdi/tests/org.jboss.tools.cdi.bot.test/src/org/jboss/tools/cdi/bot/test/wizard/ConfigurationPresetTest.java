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

/**
* Test checks if CDI configuration preset sets CDI support correctly
* 
* @author Jaroslav Jankovic
*/

@SuiteClasses({ CDIAllBotTests.class , CDISmokeBotTests.class })
public class ConfigurationPresetTest extends CDITestBase {

	@Override	
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createDynamicWebProjectWithCDIPreset(getProjectName());
		}
	}
	
	@Override
	public String getProjectName() {
		return "CDIPresetProject";
	}
			
	@Test
	public void testCDIPreset() {
		LOGGER.info("Dynamic Web Project with CDI Configuration Preset created");
		assertTrue(projectHelper.checkCDISupport(getProjectName()));
	}
	
}
