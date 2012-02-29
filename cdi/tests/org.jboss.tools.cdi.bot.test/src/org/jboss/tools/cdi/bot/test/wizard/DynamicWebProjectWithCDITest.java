/*******************************************************************************
 * Copyright (c) 2011-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.wizard;

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.junit.Test;

public class DynamicWebProjectWithCDITest extends CDITestBase {
	
	@Override	
	public void prepareWorkspace() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProjectWithDynamicWizard(getProjectName());
		}
	}
	
	@Override
	public String getProjectName() {
		return "CDIDynamicWebProject";
	}
	
	@Test
	public void testDynamicWebProjectWizard() {
		if (projectHelper.projectExists(getProjectName())) {
			LOGGER.info("CDI project was sucessfully created by CDI Web Project wizard");
			assertTrue(projectHelper.checkCDISupport(getProjectName()));
			LOGGER.info("Project has correctly set CDI support");					
		} else {
			fail("CDI project was not succesfully created by CDI Dynamic Web Project wizard");
		}
		
	}

}
