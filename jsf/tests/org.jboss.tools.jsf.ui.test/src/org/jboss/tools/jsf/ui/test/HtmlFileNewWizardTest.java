package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


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
		validateFolderAndName();	}
}
