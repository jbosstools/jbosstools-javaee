package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.jboss.tools.cdi.bot.test.uiutils.wizards.JSFWebProjectWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;

public class NewJSFProjectWizard extends NewFileWizardAction {
	
	public NewJSFProjectWizard() {
		super();		
	}

	@Override
	public JSFWebProjectWizard run() {
		Wizard w = super.run();
		w.selectTemplate("JBoss Tools Web", "JSF", "JSF Project");
		w.next();
		return new JSFWebProjectWizard();
	}

}
