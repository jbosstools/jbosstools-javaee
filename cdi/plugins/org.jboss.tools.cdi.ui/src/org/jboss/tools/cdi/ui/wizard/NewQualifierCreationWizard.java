package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;

public class NewQualifierCreationWizard extends NewElementWizard {

    private NewAnnotationWizardPage fPage;
    private boolean fOpenEditorOnFinish = true;

    public NewQualifierCreationWizard() {}

	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage= new NewQualifierWizardPage();
			fPage.init(getSelection());
		}
		addPage(fPage);

	}

	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
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

	public IJavaElement getCreatedElement() {
		return fPage.getCreatedType();
	}

}
