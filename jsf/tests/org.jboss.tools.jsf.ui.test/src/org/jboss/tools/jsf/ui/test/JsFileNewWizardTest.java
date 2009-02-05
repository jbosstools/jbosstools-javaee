package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


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
}
