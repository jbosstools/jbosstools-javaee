package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.test.util.JobUtils;


public class CssFileNewWizardTest extends WizardTest {
	public CssFileNewWizardTest(){
		super("org.jboss.tools.jst.web.ui.wizards.newfile.NewCSSFileWizard");
	}
	public void testCssFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testCssFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testCssFileNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testCssFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		IWizard wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.css");
		
		assertNotNull(res);
	}
}
