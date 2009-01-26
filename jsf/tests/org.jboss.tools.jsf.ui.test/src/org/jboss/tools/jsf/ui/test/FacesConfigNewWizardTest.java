package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.common.util.WorkbenchUtils;


public class FacesConfigNewWizardTest extends WizardTest {
	public FacesConfigNewWizardTest(){
		super("org.jboss.tools.jsf.ui.wizard.newfile.NewFacesConfigFileWizard");
	}
	
	public void testNewFacesConfigNewWizardIsCreated() {
		wizardIsCreated();
	}
	
	public void testFacesConfigNewWizardValidation() {
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		
		System.out.println("Wizard - "+wizard.getClass());
		boolean canFinish = wizard.canFinish();
		
		// Assert Finish button is enabled by default if wizard is called on Project
		assertTrue("Finish button is disabled at first wizard page.", canFinish);
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field is empty
		// 		All other fields are correct
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field points to folder that doesn't exist
		// 		All other fields are correct
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field is empty
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains forbidden characters
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains file name that already exists
	}
	
	public void testFacesConfigNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		// Assert that new file was not registered in web.xml if 'Register in web.xml' is not set
		// Assert that new file was registered in web.xml if 'Register in web.xml is set'
		fail("Not implemented yet");
	}
}
