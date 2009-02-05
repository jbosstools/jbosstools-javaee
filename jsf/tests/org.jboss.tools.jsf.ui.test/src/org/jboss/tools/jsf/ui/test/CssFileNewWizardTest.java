package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


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
}
