package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class SpecifyBeanDialogWizard extends Wizard {

	public SpecifyBeanDialogWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("Specify CDI Bean for the Injection Point").equals(getText());		
	}
	
	
	/**
	 * not finished yet, what should be implemented:
	 * 
	 * 1. gets all qualifier
	 * 2. select proper qualifier
	 * 3. add qualifier
	 * 4. remove qualifier
	 * 5. create a new qualifier
	 * 
	 */
}
