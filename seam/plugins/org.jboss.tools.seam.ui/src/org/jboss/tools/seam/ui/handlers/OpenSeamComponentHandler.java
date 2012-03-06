/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
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
public class OpenSeamComponentHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectionDialog dialog;
		
		dialog= new OpenSeamComponentDialog(parent);
		
		dialog.setTitle(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_ACTION_NAME);
		dialog.setMessage(SeamCoreMessages.OPEN_SEAM_COMPONENT_ACTION_MESSAGE);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return null;

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

		return null;
	}
	
}
