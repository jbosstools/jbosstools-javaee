package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.ISeamElement;

public class SeamActionProvider extends CommonActionProvider {
	
	public SeamActionProvider() {}

    public void fillContextMenu(IMenuManager menu) {
		ActionContext c = getContext();
		ISelection s = c.getSelection();
		if(s == null || s.isEmpty() || !(s instanceof IStructuredSelection)) return;
		Object e = ((IStructuredSelection)s).getFirstElement();
		if(e instanceof ISeamElement) {
			ISeamElement element = (ISeamElement)e;
			
			if(element instanceof IOpenableElement) {
				SeamOpenAction action = new SeamOpenAction((IOpenableElement)element);
				menu.add(action);
			}

		}
    }

}
