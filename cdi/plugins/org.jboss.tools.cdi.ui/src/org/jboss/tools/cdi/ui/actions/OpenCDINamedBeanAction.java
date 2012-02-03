/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.actions;

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
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog.CDINamedBeanWrapper;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class OpenCDINamedBeanAction  extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public OpenCDINamedBeanAction() {
		super();
		setText(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_NAME);
		setDescription(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_DESCRIPTION);
		setToolTipText(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_TOOL_TIP);
	}

	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event e) {
		Shell parent= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectionDialog dialog;
		
		dialog= new OpenCDINamedBeanDialog(parent);
		
		dialog.setTitle(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_NAME);
		dialog.setMessage(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_MESSAGE);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] resultObjects = dialog.getResult();
		for (Object resultObject : resultObjects) {
			((CDINamedBeanWrapper) resultObject).getBean().open();
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
