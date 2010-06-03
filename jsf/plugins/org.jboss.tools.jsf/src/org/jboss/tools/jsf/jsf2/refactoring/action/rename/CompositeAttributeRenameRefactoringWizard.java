package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.jboss.tools.jsf.messages.JSFUIMessages;

@SuppressWarnings("restriction")
public class CompositeAttributeRenameRefactoringWizard extends
		org.eclipse.jdt.internal.ui.refactoring.reorg.RenameRefactoringWizard {

	public CompositeAttributeRenameRefactoringWizard(Refactoring refactoring) {
		super(refactoring,
				JSFUIMessages.Refactoring_JSF_2_Rename_Composite_Attribute,
				JSFUIMessages.Refactoring_JSF_2_Rename_Enter_New_Name,
				JavaPluginImages.DESC_WIZBAN_REFACTOR,
				IJavaHelpContextIds.RENAME_LOCAL_VARIABLE_WIZARD_PAGE);
	}

}
