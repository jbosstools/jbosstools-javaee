package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


public class FacesConfigNewWizardTest extends WizardTest {
	public FacesConfigNewWizardTest(){
		super("org.jboss.tools.jsf.ui.wizard.newfile.NewFacesConfigFileWizard");
	}
	
	public void testNewFacesConfigNewWizardIsCreated() {
		wizardIsCreated();
	}
	
	public void testFacesConfigNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testFacesConfigNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testFacesConfigNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		// Assert that new file was not registered in web.xml if 'Register in web.xml' is not set
		// Assert that new file was registered in web.xml if 'Register in web.xml is set'
		fail("Not implemented yet");
	}
}
