/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.seam.internal.core.refactoring.SeamRefactorSeacher;
import org.jboss.tools.seam.ui.SeamUIMessages;

public class FindUsagesInELAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {
	private IFile javaFile;
	private IType type;
	private IMethod method;
	private String propertyName;

	public FindUsagesInELAction(IFile file, IType type, IMethod method, String propertyName){
		super(SeamUIMessages.FIND_USAGES_IN_EL);
		this.javaFile = file;
		this.type = type;
		this.method = method;
		this.propertyName = propertyName;
	}
	
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		//ELResolverFactoryManager.getInstance().getResolvers(resource);
		
		
//		ELInvocationExpression expression = SeamELCompletionEngine.findExpressionAtOffset(
//				document, selectionOffset, 0, document.getLength()); 
//
//		if (expression == null)
//			return; // No EL Operand found
//
//		try {
//			performNewSearch(expression, file);
//		} catch (JavaModelException jme) {
//			SeamGuiPlugin.getPluginLog().logError(jme);
//		} catch (InterruptedException ie) {
//			SeamGuiPlugin.getPluginLog().logError(ie);
//		}
//		return;
	}
	

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IAction action) {
	}

	public void runWithEvent(IAction action, Event event) {
	}

	class ELSearcher extends SeamRefactorSeacher{
		public ELSearcher(IFile file, String name){
			super(file, name);
		}

		@Override
		protected boolean isFileCorrect(IFile file) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		protected void match(IFile file,
				ELPropertyInvocation elPropertyInvokation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void match(IFile file, String token) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}
