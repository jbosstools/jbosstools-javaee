package org.jboss.ide.seam.gen.actions;

import org.jboss.ide.seam.gen.Messages;

public class NewConversationAction extends NewActionAction {

	protected String getTarget() {
		return "new-conversation"; //$NON-NLS-1$
	}
	

	public String getTitle() {
		return Messages.NewConversationAction_Title;
	}
	
	public String getDescription() {
		return Messages.NewConversationAction_Description;
	}

}
