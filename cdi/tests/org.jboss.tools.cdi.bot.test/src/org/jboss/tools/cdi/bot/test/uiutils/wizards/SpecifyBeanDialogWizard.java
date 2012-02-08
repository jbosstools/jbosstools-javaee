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

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class SpecifyBeanDialogWizard extends Wizard {

	private static final String CREATE_NEW_QUALIFIER = "Create New Qualifier...";
	private List<String> availableQualifiers = null;
	private List<String> inBeanQualifiers = null;
	
	public SpecifyBeanDialogWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("Specify CDI Bean for the Injection Point").equals(getText());
		availableQualifiers = new ArrayList<String>();
		inBeanQualifiers = new ArrayList<String>();
	}
	
	public SpecifyBeanDialogWizard add() {
		clickButton(IDELabel.Button.ADD_WITH_ARROW);
		return this;
	}
	
	public SpecifyBeanDialogWizard addAll() {
		clickButton(IDELabel.Button.ADD_ALL);
		return this;
	}
	
	public SpecifyBeanDialogWizard remove() {
		clickButton(IDELabel.Button.REMOVE_WITH_ARROW);
		return this;
	}
	
	public SpecifyBeanDialogWizard removeAll() {
		clickButton(IDELabel.Button.REMOVE_ALL);
		return this;
	}
	
	public SpecifyBeanDialogWizard edit() {
		clickButton(IDELabel.Button.EDIT_VALUE_WITH_DOTS);
		return this;
	}
	
	public CDIWizardBase createNewQualifier(String name, String packageName) {
		clickButton(CREATE_NEW_QUALIFIER);
		return new CDIWizardBase(CDIWizardType.QUALIFIER);
	}
	
	public boolean canAdd() {
		return canClickButton(IDELabel.Button.ADD_WITH_ARROW);
	}
	
	public boolean canAddAll() {
		return canClickButton(IDELabel.Button.ADD_ALL);
	}
	
	public boolean canRemove() {
		return canClickButton(IDELabel.Button.REMOVE_WITH_ARROW);
	}
	
	public boolean canRemoveAll() {
		return canClickButton(IDELabel.Button.REMOVE_ALL);
	}
	
	public boolean canEdit() {
		return canClickButton(IDELabel.Button.EDIT_VALUE_WITH_DOTS);
	}
	
	public boolean canCreateNewQualifier() {
		return canClickButton(CREATE_NEW_QUALIFIER);
	}
	
	public List<String> getAvailableQualifiers() {
		int tableItemsCount = bot().table(0).rowCount();
		for (int i = 0; i < tableItemsCount; i++) {
			availableQualifiers.add(bot().table(0).getTableItem(i).getText());
		}
		return availableQualifiers;
	}
	
	public List<String> getInBeanQualifiers() {
		int tableItemsCount = bot().table(1).rowCount();
		for (int i = 0; i < tableItemsCount; i++) {
			inBeanQualifiers.add(bot().table(1).getTableItem(i).getText());
		}
		return inBeanQualifiers;
	}
	
	public SpecifyBeanDialogWizard addQualifier(String qualifier) {
		return selectAvailableQualifier(qualifier).add();		
	}
	
	public SpecifyBeanDialogWizard removeQualifier(String qualifier) {
		return selectInBeanQualifier(qualifier).remove();		
	}
	
	private SpecifyBeanDialogWizard selectAvailableQualifier(String qualifier) {
		bot().table(0).select(qualifier);
		return this;
	} 
	
	private SpecifyBeanDialogWizard selectInBeanQualifier(String qualifier) {
		bot().table(1).select(qualifier);
		return this;
	} 

}
