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

package org.jboss.tools.cdi.bot.test.quickfix.dialog;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.AssignableBeansDialog;
import org.junit.Test;

public class AllAssignableDialogTest extends CDITestBase {

	private String appClass = "App.java";
	
	@Override
	public String getProjectName() {
		return "CDIAssignableDialogTest";
	}

	@Test
	public void testDecorator() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("manager", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 6);
		assertTrue(assignDialog.getAllBeans().contains("@Decorator D1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		assignDialog.hideDecorators();
		assertTrue(assignDialog.getAllBeans().size() == 5);
		assertFalse(assignDialog.getAllBeans().contains("@Decorator D1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
	@Test
	public void testInterceptor() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("manager", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 6);
		assertTrue(assignDialog.getAllBeans().contains("@Interceptor I1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assignDialog.hideInterceptors();
		assertTrue(assignDialog.getAllBeans().size() == 5);
		assertFalse(assignDialog.getAllBeans().contains("@Interceptor I1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
	@Test
	public void testUnselectedAlternative() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("manager", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 6);
		assertTrue(assignDialog.getAllBeans().contains("@Alternative BasicManager - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assignDialog.hideUnselectedAlternatives();
		assertTrue(assignDialog.getAllBeans().size() == 5);
		assertFalse(assignDialog.getAllBeans().contains("@Alternative BasicManager - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
	@Test
	public void testUnavailableProducer() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("manager", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 6);
		assertTrue(assignDialog.getAllBeans().contains("@Produces BasicManager.getManager() - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assignDialog.hideUnavailableProducers();
		assertTrue(assignDialog.getAllBeans().size() == 5);
		assertFalse(assignDialog.getAllBeans().contains("@Produces BasicManager.getManager() - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
	@Test
	public void testSpecializedBeans() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("manager", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 6);
		assertTrue(assignDialog.getAllBeans().contains("AbstractManager - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assignDialog.hideSpecializedBeans();
		assertTrue(assignDialog.getAllBeans().size() == 5);
		assertFalse(assignDialog.getAllBeans().contains("AbstractManager - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
	@Test
	public void testAmbiguousBeans() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("managerImpl", appClass, 
				CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(
				bot.shell("Assignable Beans"));
		
		assertTrue(assignDialog.getAllBeans().size() == 3);
		assertTrue(assignDialog.getAllBeans().contains("@Alternative Manager1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertTrue(assignDialog.getAllBeans().contains("Manager2 - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertTrue(assignDialog.getAllBeans().contains("Manager3 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assignDialog.hideAmbiguousBeans();
		assertTrue(assignDialog.getAllBeans().size() == 1);
		assertTrue(assignDialog.getAllBeans().contains("@Alternative Manager1 - "
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertFalse(assignDialog.getAllBeans().contains("Manager2 - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertFalse(assignDialog.getAllBeans().contains("Manager3 - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
	}
	
}
