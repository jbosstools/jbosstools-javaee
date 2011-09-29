/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog.CDINamedBeanWrapper;

/**
 * Open CDI Named Bean Dialog Handler
 * 
 * @author Victor V. Rubezhny
 */
public class OpenCDINamedBeanHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectionDialog dialog;
		
		dialog= new OpenCDINamedBeanDialog(parent);
		
		dialog.setTitle(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_NAME);
		dialog.setMessage(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_MESSAGE);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return null;

		Object[] resultObjects = dialog.getResult();
		for (Object resultObject : resultObjects) {
			((CDINamedBeanWrapper) resultObject).getBean().open();
		}

		return null;
	}
}
