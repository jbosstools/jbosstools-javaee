/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author eskimo
 *
 */
public abstract class SeamBaseWizard extends Wizard {

	private IUndoableOperation operation;
	
	private IWorkbench workbench;
	
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
		try {
			getContainer().run(false,false, new WorkspaceModifyOperation(){
				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException,
						InterruptedException {
					IUndoableOperation operation = getOperation();
					IOperationHistory operationHistory = workbench.getOperationSupport().getOperationHistory();
					IUndoContext undoContext = workbench.getOperationSupport().getUndoContext();
					operation.addContext(undoContext);
					try {
						operationHistory.execute(operation, monitor, (IAdaptable)getPages()[0]);
					} catch (ExecutionException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}
				}
			});
		} catch (Exception e) {
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
		throw new IllegalStateException("Operation is not defined for wizard");
	}
	
	/**
	 * 
	 * @param workbench
	 * @param selection
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}
}
