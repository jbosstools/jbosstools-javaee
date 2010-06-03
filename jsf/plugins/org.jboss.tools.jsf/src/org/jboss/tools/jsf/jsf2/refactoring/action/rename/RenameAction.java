package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.text.ext.IMultiPageEditor;
import org.jboss.tools.jsf.jsf2.refactoring.RefactoringActionFactory;
import org.jboss.tools.jsf.jsf2.refactoring.RefactoringActionManager;

@SuppressWarnings("restriction")
public class RenameAction extends AbstractHandler implements
		IEditorActionDelegate {

	private StructuredTextEditor textEditor;

	private IRenameDescriptor descriptor;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
	}

	@Override
	public boolean isHandled() {
		return descriptor != null;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IMultiPageEditor) {
			textEditor = ((IMultiPageEditor) targetEditor).getSourceEditor();
		}
	}

	public void run(IAction action) {
		if (textEditor == null) {
			reportRefactoringInfo();
		} else {
			IEditorInput input = textEditor.getEditorInput();
			if (!(input instanceof IFileEditorInput)) {
				reportRefactoringInfo();
				return;
			}
			if (((IFileEditorInput) textEditor.getEditorInput()).getFile()
					.getProject() == null) {
				reportRefactoringInfo();
				return;
			}
			descriptor = RefactoringActionFactory
					.createRenameDescriptor(textEditor);
			RefactoringActionManager.getManager().renameWithAction(action,
					descriptor);
		}
		descriptor = null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void reportRefactoringInfo() {
		MessageDialog.openInformation(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				RefactoringMessages.RenameAction_rename,
				"Refactoring is not available in the current place"); //$NON-NLS-1$
	}

}