package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

public class FacesConfigNewWizardTest extends TestCase {
	
	public void testNewFacesConfigNewWizardIsCreated() {
		// Assert wizard is created
		// Assert it is an instance of right class 
		fail("Not implemented yet");
	}
	
	public void testFacesConfigNewWizardValidation() {
		// Assert Finish button is enabled by default if wizard is called on Project
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
		fail("Not implemented yet");
	}
	
	public void testFacesConfigNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		// Assert that new file was not registered in web.xml if 'Register in web.xml' is not set
		// Assert that new file was registered in web.xml if 'Register in web.xml is set'
		fail("Not implemented yet");
	}
}
