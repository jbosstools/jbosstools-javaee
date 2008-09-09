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
package org.jboss.tools.seam.ui.refactoring;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentProcessor;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentRefactoring;
import org.jboss.tools.seam.ui.wizard.RenameComponentWizard;

/**
 * @author Alexey Kazakov
 */
public class SeamComponentRenameHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);

		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput)input).getFile();
			Shell activeShell = HandlerUtil.getActiveShell(event);
			RenameComponentProcessor processor = new RenameComponentProcessor(file);
			RenameComponentRefactoring refactoring = new RenameComponentRefactoring(processor);
			RenameComponentWizard wizard = new RenameComponentWizard(refactoring, processor.getComponent());
			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
			try {
				String titleForFailedChecks = "TestTestTest"; //$NON-NLS-1$
				op.run(activeShell, titleForFailedChecks);
			} catch (final InterruptedException irex) {
				// operation was canceled
			}

		}
		return null;
	}
}