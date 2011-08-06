package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.jboss.tools.common.model.ui.wizards.INewClassWizard;
import org.jboss.tools.common.model.ui.wizards.NewTypeWizardAdapter;

public class NewCDIElementWizard extends NewElementWizard implements INewClassWizard {
    protected boolean fOpenEditorOnFinish = true;

    protected NewTypeWizardPage fPage;
    protected NewTypeWizardAdapter adapter;
   
    public NewCDIElementWizard() {}

	public void setAdapter(NewTypeWizardAdapter adapter) {
		this.adapter = adapter;
	}

	public boolean isOpenEditorAfterFinish() {
	    return fOpenEditorOnFinish;
	}

	public void setOpenEditorAfterFinish(boolean set) {
	    this.fOpenEditorOnFinish = set;
	}
	
	public String getQualifiedClassName() {
		IType type = fPage.getCreatedType();
		return type == null ? "" : type.getFullyQualifiedName();
	}

	protected void initPageFromAdapter() {
		if(adapter != null) {
			fPage.setPackageFragmentRoot(adapter.getPackageFragmentRoot(), true);
			fPage.setPackageFragment(adapter.getPackageFragment(), true);
			fPage.setTypeName(adapter.getTypeName(), true);				
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
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
