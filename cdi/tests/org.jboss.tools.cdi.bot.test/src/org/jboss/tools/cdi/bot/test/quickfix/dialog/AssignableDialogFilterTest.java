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

public class AssignableDialogFilterTest extends CDITestBase {
	
	private String appClass = "App.java";
	
	@Override
	public String getProjectName() {
		return "AssignableDialogFilterTest";
	}

	@Test
	public void testFilterAssignableBeans() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("animal", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		/** test lower and upper case */
		assignDialog.typeInFilter("cat");
		assertTrue(assignDialog.getAllBeans().size() == 1);
		assertTrue(assignDialog.getAllBeans().get(0).
				equals("Cat - " + getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		assignDialog.typeInFilter("CAT");
		assertTrue(assignDialog.getAllBeans().size() == 1);
		assertTrue(assignDialog.getAllBeans().get(0).
				equals("Cat - " + getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		/** test '*' asterisk */
		assignDialog.typeInFilter("*at");
		assertTrue(assignDialog.getAllBeans().size() == 2);
		assertTrue(assignDialog.getAllBeans().contains("Cat - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertTrue(assignDialog.getAllBeans().contains("@Decorator AnimalDecorator - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		/** test '?' asterisk */
		assignDialog.typeInFilter("??g");
		assertTrue(assignDialog.getAllBeans().size() == 1);
		assertTrue(assignDialog.getAllBeans().get(0).equals("Dog - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		/** test non-existing bean */
		assignDialog.typeInFilter("?*?s");
		assertTrue(assignDialog.getAllBeans().size() == 0);
		
	}
	
	@Test
	public void testFilterNonAssignableBeans() {
		
		setEd(packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				getPackageName(), appClass).toTextEditor());
		
		assertTrue(openOnUtil.openOnByOption("animal", appClass, CDIConstants.SHOW_ALL_ASSIGNABLE));			
		
		AssignableBeansDialog assignDialog = new AssignableBeansDialog(bot.shell("Assignable Beans"));
		
		assignDialog.hideDecorators();
		
		/** test lower and upper case */
		assignDialog.typeInFilter("animaldecorator");
		assertTrue(assignDialog.getAllBeans().size() == 0);
		
		assignDialog.typeInFilter("ANIMALDECORATOR");
		assertTrue(assignDialog.getAllBeans().size() == 0);
		
		/** test '*' asterisk */
		assignDialog.typeInFilter("*at");
		assertTrue(assignDialog.getAllBeans().size() == 1);
		assertTrue(assignDialog.getAllBeans().contains("Cat - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		assertFalse(assignDialog.getAllBeans().contains("@Decorator AnimalDecorator - " 
				+ getPackageName() + " - /" 
				+ getProjectName() + "/src"));
		
		/** test '?' asterisk */
		assignDialog.typeInFilter("??i");
		assertTrue(assignDialog.getAllBeans().size() == 0);
		
	}

}
