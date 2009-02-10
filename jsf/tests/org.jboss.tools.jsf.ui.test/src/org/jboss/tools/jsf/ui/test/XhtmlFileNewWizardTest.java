package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.test.util.JobUtils;


public class XhtmlFileNewWizardTest extends WizardTest {
	public XhtmlFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewXHTMLFileWizard");
	}
	
	public void testXhtmlFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testXhtmlFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testXhtmlFileNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testXhtmlFileNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		IWizard wizard = getWizardOnProject("aaa");
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("aaa.xhtml");
		
		assertNotNull(res);
	}
}