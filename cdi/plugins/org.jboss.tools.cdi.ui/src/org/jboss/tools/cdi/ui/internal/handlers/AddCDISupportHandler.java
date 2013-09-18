/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.cdi.ui.preferences.CDISettingsPreferencePage;

/**
 * @author Alexey Kazakov
 */
public class AddCDISupportHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = null;
		Object element = ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).getFirstElement();
		if(element instanceof IProject){
			project = (IProject)element;
		}else if(element instanceof IJavaProject){
			project = ((IJavaProject)element).getProject();
		}
		if(project != null){
			final PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), project, CDISettingsPreferencePage.ID, new String[] {CDISettingsPreferencePage.ID}, null);
			CDISettingsPreferencePage page = (CDISettingsPreferencePage)dialog.getSelectedPage();
			page.setEnabledCDISuport(shouldEnable());
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if(dialog.getShell() != null && !dialog.getShell().isDisposed()) {
						dialog.getTreeViewer().getControl().forceFocus();
					}
				}
			});
			dialog.open();
		}
		return null;
	}

	protected boolean shouldEnable() {
		return true;
	}

}