/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class DeletePreviewWizard extends RefactoringWizard {

	public DeletePreviewWizard(Refactoring refactoring) {
		super(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE);
		//setForcePreviewReview(false);
	}

	@Override
	protected void addUserInputPages() {
	}
	
	public boolean showWizard() {
		final IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final Shell shell = win.getShell();
		final RefactoringStarter refactoringStarter = new RefactoringStarter();
		boolean res = refactoringStarter.activate(this, shell, getWindowTitle(), RefactoringSaveHelper.SAVE_ALL);
		//RefactoringStatus rs = refactoringStarter.getInitialConditionCheckingStatus();
		return res;
	}


}
