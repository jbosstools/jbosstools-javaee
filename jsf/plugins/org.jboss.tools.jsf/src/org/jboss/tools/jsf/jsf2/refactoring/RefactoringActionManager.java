package org.jboss.tools.jsf.jsf2.refactoring;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jdt.internal.ui.refactoring.UserInterfaceStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.jsf.jsf2.refactoring.action.rename.IRenameDescriptor;
import org.jboss.tools.jsf.jsf2.refactoring.action.rename.RenameUserInterfaceManager;
import org.jboss.tools.jsf.messages.JSFUIMessages;

@SuppressWarnings("restriction")
public class RefactoringActionManager {

	public static final String REFACTOR_RENAME_ACTION_ID = "_refactor_rename_action_id"; //$NON-NLS-1$

	private static final RefactoringActionManager instance = new RefactoringActionManager();

	private RefactoringActionManager() {

	}

	public static RefactoringActionManager getManager() {
		return instance;
	}

	public boolean checkActionAvailable(String actionID, IDOMNode node) {
		return getAvailableActionIDs(node).contains(actionID);
	}

	public boolean checkActionAvailable(String actionID,
			EvaluationContext context) {
		return getAvailableActionIDs(context).contains(actionID);
	}

	public Set<String> getAvailableActionIDs(IDOMNode node) {
		Set<String> ids = new HashSet<String>();
		return ids;
	}

	public Set<String> getAvailableActionIDs(EvaluationContext context) {
		System.out.println(context.getDefaultVariable());
		Set<String> ids = new HashSet<String>();
		return ids;
	}

	public void renameWithAction(IAction action, IRenameDescriptor descriptor) {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		if (descriptor == null) {
			MessageDialog.openInformation(parent,
					RefactoringMessages.RenameAction_rename,
					JSFUIMessages.Refactoring_JSF_2_Isnt_Available);
			return;
		}
		descriptor.getRenameRefactoring().setProcessor(
				descriptor.getRefactoringProcessor());
		UserInterfaceStarter starter = RenameUserInterfaceManager.getDefault()
				.getStarter(descriptor.getRenameRefactoring());
		try {
			starter.activate(descriptor.getRenameRefactoring(), parent,
					RefactoringSaveHelper.SAVE_NOTHING);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
