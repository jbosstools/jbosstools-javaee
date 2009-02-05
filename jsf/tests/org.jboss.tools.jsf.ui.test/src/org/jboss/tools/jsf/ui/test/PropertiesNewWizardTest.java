package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


public class PropertiesNewWizardTest extends WizardTest {
	public PropertiesNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewPropertiesFileWizard");
	}
	
	public void testPropertiesFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testPropertiesFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testPropertiesFileNewWizardValidation2() {
		validateFolderAndName();
	}
}
