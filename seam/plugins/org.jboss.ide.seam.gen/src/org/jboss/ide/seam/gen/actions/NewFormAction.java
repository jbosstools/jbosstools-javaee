package org.jboss.ide.seam.gen.actions;

import org.jboss.ide.seam.gen.Messages;

public class NewFormAction extends NewActionAction {
	/**
	 * The constructor.
	 */
	public NewFormAction() {
	}
	
	protected String getTarget() {
		return "new-form"; //$NON-NLS-1$
	}
	
	public String getTitle() {
		return Messages.NewFormAction_Title;
	}
	
	public String getDescription() {
		return Messages.NewFormAction_Description;
	}


}