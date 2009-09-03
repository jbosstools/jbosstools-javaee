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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.search.SearchMessages;
import org.eclipse.jdt.internal.ui.search.SearchUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.search.ELSearchQuery;

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

	public void run() {
		try {
			performNewSearch();
		} catch (JavaModelException jme) {
			SeamGuiPlugin.getPluginLog().logError(jme);
		} catch (InterruptedException ie) {
			SeamGuiPlugin.getPluginLog().logError(ie);
		}
	}
	
	private void performNewSearch() throws JavaModelException, InterruptedException {
		ELSearchQuery query= createQuery();
		if (query.canRunInBackground()) {
			SearchUtil.runQueryInBackground(query);
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			IStatus status= SearchUtil.runQueryInForeground(progressService, query);
			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
				ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
			}
		}
	}
	
	private ELSearchQuery createQuery() throws JavaModelException, InterruptedException {
		return new ELSearchQuery(javaFile, type, propertyName);
	}
	
	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IAction action) {
	}

	public void runWithEvent(IAction action, Event event) {
	}

	public void run(IAction action) {
		run();
	}

	
}
