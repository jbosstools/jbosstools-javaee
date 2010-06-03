package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;

@SuppressWarnings("restriction")
public class CompositeAttributeRenameRefactoringWizard extends
		org.eclipse.jdt.internal.ui.refactoring.reorg.RenameRefactoringWizard {

	public CompositeAttributeRenameRefactoringWizard(Refactoring refactoring) {
		super(refactoring,
				"Rename Composite Attribute", //$NON-NLS-1$
				"Enter New Name for Composite Attribute", //$NON-NLS-1$
				JavaPluginImages.DESC_WIZBAN_REFACTOR,
				IJavaHelpContextIds.RENAME_LOCAL_VARIABLE_WIZARD_PAGE);
	}

}
