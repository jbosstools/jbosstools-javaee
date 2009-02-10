package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.test.util.JobUtils;


public class JsFileNewWizardTest extends WizardTest {
	public JsFileNewWizardTest(){
		super("org.jboss.tools.jst.web.ui.wizards.newfile.NewJSFileWizard");
	}
	
	public void testJsFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testJsFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testJsFileNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testJsFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		IWizard wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.js");
		
		assertNotNull(res);
	}
}
