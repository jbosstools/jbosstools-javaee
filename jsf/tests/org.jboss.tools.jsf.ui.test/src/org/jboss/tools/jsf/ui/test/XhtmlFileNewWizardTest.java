package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


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
}
