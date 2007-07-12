package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.jboss.tools.seam.core.IOpenableElement;

public class SeamOpenAction extends Action {
	IOpenableElement element;
	
	public SeamOpenAction(IOpenableElement element) {
		setText("Open");
		this.element = element;
	}
	
	public void run() {
		if(element != null) {
			element.open();
		}		
	}	

}
