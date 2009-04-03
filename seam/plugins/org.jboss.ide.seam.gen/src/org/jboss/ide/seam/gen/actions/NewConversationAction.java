package org.jboss.ide.seam.gen.actions;

public class NewConversationAction extends NewActionAction {

	protected String getTarget() {
		return "new-conversation"; //$NON-NLS-1$
	}
	

	public String getTitle() {
		return "Create new conversation";
	}
	
	public String getDescription() {
		return "Create a set of classes managing a conversation.\n";
		        
	}


}
