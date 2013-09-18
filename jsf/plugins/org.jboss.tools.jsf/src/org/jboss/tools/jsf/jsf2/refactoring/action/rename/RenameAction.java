/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.text.ext.IMultiPageEditor;
import org.jboss.tools.jsf.jsf2.refactoring.RefactoringActionFactory;
import org.jboss.tools.jsf.jsf2.refactoring.RefactoringActionManager;
import org.jboss.tools.jsf.messages.JSFUIMessages;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class RenameAction extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredTextEditor textEditor = null;
		
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if(editor instanceof IMultiPageEditor){
			textEditor = ((IMultiPageEditor) editor).getSourceEditor();
		}

		if (textEditor == null) {
			reportRefactoringInfo();
		} else {
			IEditorInput input = textEditor.getEditorInput();
			if (!(input instanceof IFileEditorInput)) {
				reportRefactoringInfo();
				return null;
			}
			if (((IFileEditorInput) textEditor.getEditorInput()).getFile()
					.getProject() == null) {
				reportRefactoringInfo();
				return null;
			}
			IRenameDescriptor descriptor = RefactoringActionFactory
					.createRenameDescriptor(textEditor);
			RefactoringActionManager.getManager().renameWithAction(null,
					descriptor);
		}
		return null;
	}

	private static void reportRefactoringInfo() {
		MessageDialog.openInformation(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				RefactoringMessages.RenameAction_rename,
				JSFUIMessages.Refactoring_JSF_2_Rename_Action);
	}

}