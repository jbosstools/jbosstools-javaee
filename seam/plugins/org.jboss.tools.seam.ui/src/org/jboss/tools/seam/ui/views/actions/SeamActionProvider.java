/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;

/**
 * Action provider for Seam Components view.
 * @author Viacheslav Kabanovich
 */
public class SeamActionProvider extends CommonActionProvider {
	
	public SeamActionProvider() {}

    @Override
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
		if(e instanceof ISeamComponent) {
			ISeamComponent component = (ISeamComponent)e;
			RenameComponentAction action = new RenameComponentAction(component);
			menu.add(action);
		}
    }

    @Override
	public void fillActionBars(IActionBars actionBars) {
		ActionContext c = getContext();
		ISelection s = c.getSelection();
		if(s == null || s.isEmpty() || !(s instanceof IStructuredSelection)) return;
		Object e = ((IStructuredSelection)s).getFirstElement();
		if(e instanceof ISeamElement) {
			ISeamElement element = (ISeamElement)e;
			
			if(element instanceof IOpenableElement) {
				SeamOpenAction action = new SeamOpenAction((IOpenableElement)element);
				actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, action);
				actionBars.setGlobalActionHandler(JdtActionConstants.OPEN, action);
			}

		}
		
    }
}
