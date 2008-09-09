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
package org.jboss.tools.seam.ui.wizard;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.jboss.tools.seam.core.ISeamComponent;

/**
 * @author Alexey Kazakov
 */
public class RenameComponentWizard extends RefactoringWizard {

	private ISeamComponent component;

	public RenameComponentWizard(Refactoring refactoring, ISeamComponent component) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.component = component;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
	 */
	@Override
	protected void addUserInputPages() {
	    setDefaultPageTitle(getRefactoring().getName());
//	    addPage( new RenamePropertyInputPage( info ) );
	}
}