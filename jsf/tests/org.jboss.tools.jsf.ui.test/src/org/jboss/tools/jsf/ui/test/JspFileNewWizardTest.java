package org.jboss.tools.jsf.ui.test;


public class JspFileNewWizardTest extends WizardTest {
	public JspFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewJSPFileWizard");
	}
	
	public void testJspFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
}
