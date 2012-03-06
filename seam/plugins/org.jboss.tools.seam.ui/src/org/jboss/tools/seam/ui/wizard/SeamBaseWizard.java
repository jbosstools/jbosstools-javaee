/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * @author eskimo
 *
 */
public abstract class SeamBaseWizard extends Wizard {

	private IUndoableOperation operation;
	
	private IWorkbench workbench;

	private IStructuredSelection selection;
	
	/**
	 * 
	 * @param operation
	 */
	public SeamBaseWizard(IUndoableOperation operation) {
		this.operation = operation;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean performFinish() {
	
			// TODO lock only current project, not entire workspace
			try {
				getContainer().run(false,false, new WorkspaceModifyOperation(){
					@Override
					protected void execute(IProgressMonitor monitor)
							throws CoreException, InvocationTargetException,
							InterruptedException {
						IStatus result;
						IUndoableOperation operation = getOperation();
						IOperationHistory operationHistory = workbench.getOperationSupport().getOperationHistory();
						IUndoContext undoContext = workbench.getOperationSupport().getUndoContext();
						operation.addContext(undoContext);
						try {
							result = operationHistory.execute(operation, monitor, (IAdaptable)getPages()[0]);
							if(operation instanceof SeamBaseOperation) {
								SeamBaseOperation seamOperation = (SeamBaseOperation)operation;
								seamOperation.openResultInEditor((IAdaptable)getPages()[0]);
							}
						} catch (ExecutionException e) {
							result = new Status(IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,e.getMessage(),e);
							SeamCorePlugin.getPluginLog().logError(e);
						}
						if(result.getSeverity()==IStatus.ERROR) {
							ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", MessageFormat.format("Error occur during creating {0}. Please, check Error Log View for details." , SeamBaseWizard.this.getWindowTitle()),result);
						}
					}
				});
			} catch (InvocationTargetException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			} catch (InterruptedException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}

		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public IUndoableOperation getOperation() {
		if(operation!=null) return operation;
		throw new IllegalStateException(SeamCoreMessages.SEAM_BASE_WIZARD_OPERATION_IS_NOT_DEFINED_FOR_WIZARD);
	}
	
	/**
	 * 
	 * @param workbench
	 * @param selection
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.setSelection(selection);
	}
	
	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate("template.jst.seam"); //$NON-NLS-1$
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}

	protected IStructuredSelection getInitialSelection() {
		return selection;
	}
}
