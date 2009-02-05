package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;


public class JspFileNewWizardTest extends WizardTest {
	public JspFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewJSPFileWizard");
	}
	
	public void testJspFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
	
	public void testJspFileNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testJspFileNewWizardValidation2() {
		validateFolderAndName();
	}
}
