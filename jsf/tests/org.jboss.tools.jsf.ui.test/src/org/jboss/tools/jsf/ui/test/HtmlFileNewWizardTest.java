package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.test.util.JobUtils;


public class HtmlFileNewWizardTest extends WizardTest {
	public HtmlFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewHTMLFileWizard");
	}
	
	public void testHtmlFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testHtmlFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testHtmlFileNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testHtmlFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		IWizard wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.html");
		
		assertNotNull(res);
	}
}
