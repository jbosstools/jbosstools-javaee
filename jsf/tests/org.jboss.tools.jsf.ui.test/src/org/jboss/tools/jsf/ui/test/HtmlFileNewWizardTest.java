package org.jboss.tools.jsf.ui.test;


public class HtmlFileNewWizardTest extends WizardTest {
	public HtmlFileNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewHTMLFileWizard");
	}
	
	public void testHtmlFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
}
