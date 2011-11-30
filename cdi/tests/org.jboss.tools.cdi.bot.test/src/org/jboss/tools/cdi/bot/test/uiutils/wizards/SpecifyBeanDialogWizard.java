package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;

public class SpecifyBeanDialogWizard extends Wizard {

	private static final String ADD = "Add >";
	private static final String ADD_ALL = "Add All >>";
	private static final String REMOVE = "< Remove";
	private static final String REMOVE_ALL = "<< Remove All";
	private static final String EDIT_VALUE = "Edit Value...";
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
		clickButton(ADD);
		return this;
	}
	
	public SpecifyBeanDialogWizard addAll() {
		clickButton(ADD_ALL);
		return this;
	}
	
	public SpecifyBeanDialogWizard remove() {
		clickButton(REMOVE);
		return this;
	}
	
	public SpecifyBeanDialogWizard removeAll() {
		clickButton(REMOVE_ALL);
		return this;
	}
	
	public SpecifyBeanDialogWizard edit() {
		clickButton(EDIT_VALUE);
		return this;
	}
	
	public CDIWizardBase createNewQualifier(String name, String packageName) {
		clickButton(CREATE_NEW_QUALIFIER);
		return new CDIWizardBase(CDIWizardType.QUALIFIER);
	}
	
	public boolean canAdd() {
		return canClick(ADD);
	}
	
	public boolean canAddAll() {
		return canClick(ADD_ALL);
	}
	
	public boolean canRemove() {
		return canClick(REMOVE);
	}
	
	public boolean canRemoveAll() {
		return canClick(REMOVE_ALL);
	}
	
	public boolean canEdit() {
		return canClick(EDIT_VALUE);
	}
	
	public boolean canCreateNewQualifier() {
		return canClick(CREATE_NEW_QUALIFIER);
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
