package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.test.util.JobUtils;


public class PropertiesNewWizardTest extends WizardTest {
	public PropertiesNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewPropertiesFileWizard");
	}
	
	public void testPropertiesFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testPropertiesFileNewWizardValidation() {
		wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testPropertiesFileNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testPropertiesFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.properties");
		
		assertNotNull(res);
	}
}
