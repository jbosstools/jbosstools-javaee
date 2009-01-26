package org.jboss.tools.jsf.ui.test;


public class PropertiesNewWizardTest extends WizardTest {
	public PropertiesNewWizardTest(){
		super("org.jboss.tools.common.model.ui.wizard.newfile.NewPropertiesFileWizard");
	}
	
	public void testPropertiesFileNewWizardTestIsCreated() {
		wizardIsCreated();
	}
}
