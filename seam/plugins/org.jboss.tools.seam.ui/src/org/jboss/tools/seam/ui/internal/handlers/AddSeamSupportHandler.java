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
package org.jboss.tools.seam.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.seam.ui.preferences.SeamSettingsPreferencePage;

/**
 * @author Alexey Kazakov
 */
public class AddSeamSupportHandler extends AbstractHandler {

	protected boolean shouldEnable() {
		return true;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection structuredSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
		IProject project = (IProject) ((IStructuredSelection) structuredSelection).getFirstElement();
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), project, SeamSettingsPreferencePage.ID, new String[] {SeamSettingsPreferencePage.ID}, null);
		SeamSettingsPreferencePage page = (SeamSettingsPreferencePage)dialog.getSelectedPage();
		page.setEnabledSeamSuport(shouldEnable());
		dialog.open();
		return null;
	}
}