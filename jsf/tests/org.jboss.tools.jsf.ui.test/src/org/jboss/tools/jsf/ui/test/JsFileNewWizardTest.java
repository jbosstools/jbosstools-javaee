package org.jboss.tools.jsf.ui.test;


public class JsFileNewWizardTest extends WizardTest {
	public JsFileNewWizardTest(){
		super("org.jboss.tools.jst.web.ui.wizards.newfile.NewJSFileWizard");
	}
	
	public void testJsFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
}
