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
package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class NewCDIAnnotationCreationWizard extends NewElementWizard {
    protected NewAnnotationWizardPage fPage;
    protected boolean fOpenEditorOnFinish = true;

    public NewCDIAnnotationCreationWizard() {}

	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = createAnnotationWizardPage();
			fPage.init(getSelection());
		}
		addPage(fPage);

	}

	protected abstract NewAnnotationWizardPage createAnnotationWizardPage();

	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	public IJavaElement getCreatedElement() {
		return fPage.getCreatedType();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res= super.performFinish();
		if (res) {
			IResource resource= fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (fOpenEditorOnFinish) {
					openResource((IFile) resource);
				}
			}
		}
		return res;
	}

}
