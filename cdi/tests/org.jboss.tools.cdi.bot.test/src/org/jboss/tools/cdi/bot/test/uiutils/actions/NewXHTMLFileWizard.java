package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.XHTMLDialogWizard;

public class NewXHTMLFileWizard extends NewFileWizardAction{

	public NewXHTMLFileWizard() {
		super();		
	}

	@Override
	public XHTMLDialogWizard run() {
		Wizard w = super.run();
		w.selectTemplate("JBoss Tools Web", "XHTML Page");
		w.next();
		return new XHTMLDialogWizard();
	}
	
}
