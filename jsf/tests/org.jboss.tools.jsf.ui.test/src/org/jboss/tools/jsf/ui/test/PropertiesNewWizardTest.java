/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.test.util.JobUtils;

public class PropertiesNewWizardTest extends WizardTest {
	public PropertiesNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewPropertiesFileWizard");
	}
	
	public void testPropertiesFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testPropertiesFileNewWizardValidation() {
		wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testPropertiesFileNewWizardValidation2() {
		// JBIDE-14029: Since testPropertiesFileNewWizardResults() test already creates the property file "aaa.properties"
		// we cannot use the same default file name ("aaa"). The possibility to specify a different file name is added
		validateFolderAndName("aaa2"); 
	}
	
	public void testPropertiesFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.properties");
		
		assertNotNull(res);
	}
}
