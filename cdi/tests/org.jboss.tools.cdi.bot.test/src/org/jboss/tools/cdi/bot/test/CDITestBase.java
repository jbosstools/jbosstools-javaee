/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test;

import org.jboss.tools.cdi.bot.test.uiutils.CDIProjectHelper;
import org.jboss.tools.cdi.bot.test.uiutils.EditorResourceHelper;
import org.jboss.tools.cdi.bot.test.uiutils.LibraryHelper;
import org.jboss.tools.cdi.bot.test.uiutils.OpenOnHelper;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizard;
import org.junit.After;
import org.junit.Before;

public class CDITestBase extends CDIBase {
	
	private String projectName = "CDIProject";
	private String packageName = "cdi";
	public final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static CDIProjectHelper projectHelper = new CDIProjectHelper(); 
	public static CDIWizard wizard = new CDIWizard();
	public static OpenOnHelper openOnUtil = new OpenOnHelper();
	public static LibraryHelper libraryUtil = new LibraryHelper();
	public static EditorResourceHelper editResourceUtil = new EditorResourceHelper();
	
	@Before
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProject(getProjectName());
		}
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
		
	public  String getProjectName() {
		return projectName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
}