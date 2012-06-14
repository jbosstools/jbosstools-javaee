/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.tests;

import java.util.List;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.SolderAnnotationTestBase;
import org.jboss.tools.cdi.seam3.bot.test.uiutils.AssignableBeansDialogExt;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class DefaultBeansTest extends SolderAnnotationTestBase {

	private static String projectName = "defaultBeans";
	
	@After
	public void waitForJobs() {
		projectExplorer.deleteAllProjects();
	} 
	
	@Test
	public void testProperAssign() {
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS);
		
		assertFalse(openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.OPEN_INJECT_BEAN);
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("DefaultOne.java"));
		
	}
	
	@Test
	public void testProperAssignAlternativesDeactive() {

		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		wizardExt.bean(getPackageName(), "ManagerImpl", true, false, false, false, true, false, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialogExt assignDialog = new AssignableBeansDialogExt(bot.shell("Assignable Beans"));
		
		List<String> allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 2);
		assignDialog.hideUnavailableBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		assertTrue(allBeans.get(0).contains("DefaultOne"));
		
		openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.OPEN_INJECT_BEAN);
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("DefaultOne.java"));
		
	}
	
	@Test
	public void testProperUnassign() {

		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		wizardExt.bean(getPackageName(), "ManagerImpl", true, false, false, false, false, false, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialogExt assignDialog = new AssignableBeansDialogExt(bot.shell("Assignable Beans"));
		
		List<String> allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 2);
		assignDialog.hideDefaultBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		assertTrue(allBeans.get(0).contains("ManagerImpl"));
		
		openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.OPEN_INJECT_BEAN);
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("ManagerImpl.java"));
		
	}
	
	@Test
	public void testProperUnassignAlternativesActive() {

		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		wizardExt.bean(getPackageName(), "ManagerImpl", true, false, false, false, true, true, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialogExt assignDialog = new AssignableBeansDialogExt(bot.shell("Assignable Beans"));
		
		List<String> allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 2);
		assignDialog.hideDefaultBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		assignDialog.showDefaultBeans();
		assignDialog.hideAmbiguousBeans();
		assertTrue(allBeans.size() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.size() == 1);
		assertTrue(allBeans.get(0).contains("ManagerImpl"));
		
		openOnUtil.openOnByOption("managerImpl", APPLICATION_CLASS, CDIConstants.OPEN_INJECT_BEAN);
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("ManagerImpl.java"));
		
	}
	
}
