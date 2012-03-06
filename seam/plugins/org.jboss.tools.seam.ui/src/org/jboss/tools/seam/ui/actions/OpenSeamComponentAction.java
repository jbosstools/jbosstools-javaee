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

package org.jboss.tools.seam.ui.actions;


import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog.SeamComponentWrapper;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public OpenSeamComponentAction() {
		super();
		setText(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_ACTION_NAME);
		setDescription(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_DESCRIPTION);
		setToolTipText(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_TOOL_TIP);
	}

	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event e) {
		Shell parent= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectionDialog dialog;
		
		dialog= new OpenSeamComponentDialog(parent);
		
		dialog.setTitle(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_ACTION_NAME);
		dialog.setMessage(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_MESSAGE);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] components= dialog.getResult();
		if (components != null && components.length > 0) {
			SeamComponentWrapper wrapper= null;
			for (int i= 0; i < components.length; i++) {
				wrapper= (SeamComponentWrapper) components[i];
				SeamJavaComponentDeclaration javaDeclaration = (SeamJavaComponentDeclaration)wrapper.getComponent().getJavaDeclaration();
				if(javaDeclaration != null){
					javaDeclaration.open();
				}else{
					Set<ISeamComponentDeclaration> declarations = wrapper.getComponent().getAllDeclarations();
					if(declarations.iterator().hasNext()){
						((SeamComponentDeclaration)declarations.iterator().next()).open();
					}
				}
			}
		}
	}


	// ---- IWorkbenchWindowActionDelegate
	// ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// do nothing.
	}

	public void init(IWorkbenchWindow window) {
		// do nothing.
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
	
	// ---- IActionDelegate2
	// ------------------------------------------------

	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}
	
	public void init(IAction action) {
		// do nothing.
	}
}
