package org.jboss.tools.jsf.ui.test;


public class XhtmlFileNewWizardTest extends WizardTest {
	public XhtmlFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewXHTMLFileWizard");
	}
	
	public void testXhtmlFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
}
