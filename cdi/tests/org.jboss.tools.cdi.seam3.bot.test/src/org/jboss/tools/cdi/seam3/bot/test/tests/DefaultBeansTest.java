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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.SolderTestBase;
import org.jboss.tools.cdi.seam3.bot.test.uiutils.AssignableBeansDialog;
import org.junit.Test;

public class DefaultBeansTest extends SolderTestBase {

	@Override
	public String getProjectName() {
		return "defaultBeans";
	}
	
	@Override
	public void waitForJobs() {
		projectExplorer.deleteProject(getProjectName(), true);		
	} 
	
	private String className = "Application.java";
	
	@Test
	public void testProperAssign() {
		
		packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				"cdi.seam", className);
		
		assertFalse(openOnUtil.openOnByOption("managerImpl", className, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		openOnUtil.openOnByOption("managerImpl", className, "Open @Inject Bean");
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("DefaultOne.java"));
		
	}
	
	@Test
	public void testProperAssignAlternativesDeactive() {
		
		wizardExt.bean("cdi.seam", "ManagerImpl", true, false, false, false, true, false, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				"cdi.seam", className);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", className, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		SWTBotTable allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 2);
		assignDialog.hideUnavailableBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		assertTrue(allBeans.getTableItem(0).getText().contains("DefaultOne"));
		
		openOnUtil.openOnByOption("managerImpl", className, "Open @Inject Bean");
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("DefaultOne.java"));
		
	}
	
	@Test
	public void testProperUnassign() {
		
		wizardExt.bean("cdi.seam", "ManagerImpl", true, false, false, false, false, false, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				"cdi.seam", className);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", className, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		SWTBotTable allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 2);
		assignDialog.hideDefaultBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		assertTrue(allBeans.getTableItem(0).getText().contains("ManagerImpl"));
		
		openOnUtil.openOnByOption("managerImpl", className, "Open @Inject Bean");
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("ManagerImpl.java"));
		
	}
	
	@Test
	public void testProperUnassignAlternativesActive() {
		
		wizardExt.bean("cdi.seam", "ManagerImpl", true, false, false, false, true, true, null,
				"Manager", null, null).finish();
		
		packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				"cdi.seam", className);
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", className, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		SWTBotTable allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 2);
		assignDialog.hideDefaultBeans();
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		assignDialog.showDefaultBeans();
		assignDialog.hideAmbiguousBeans();
		assertTrue(allBeans.rowCount() == 1);
		
		allBeans = assignDialog.getAllBeans();
		assertTrue(allBeans.rowCount() == 1);
		assertTrue(allBeans.getTableItem(0).getText().contains("ManagerImpl"));
		
		openOnUtil.openOnByOption("managerImpl", className, "Open @Inject Bean");
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("ManagerImpl.java"));
		
	}
	
}
