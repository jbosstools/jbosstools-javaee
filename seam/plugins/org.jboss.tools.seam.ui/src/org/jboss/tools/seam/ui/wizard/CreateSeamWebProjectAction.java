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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

public class CreateSeamWebProjectAction extends Action implements IWorkbenchWindowActionDelegate {
	public CreateSeamWebProjectAction() {
		super(SeamCoreMessages.CREATE_SEAM_WEB_PROJECTACTION_CREATE_SEAM_PROJECT);
		setToolTipText(SeamCoreMessages.CREATE_SEAM_WEB_PROJECTACTION_CREATE_SEAM_PROJECT);
		ModelUIImages.setImageDescriptors(this, ModelUIImages.ACT_CREATE_PROJECT);
	}

	@Override
	public void run() {
		INewWizard wizard = ExtensionPointUtils.findNewWizardsItem(
			SeamGuiPlugin.PLUGIN_ID,
			"org.jboss.tools.seam.ui.wizards.SeamProjectWizard" //$NON-NLS-1$
		);
		if (wizard != null) {
			wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
			WizardDialog dialog = new WizardDialog(ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.open();  
		} else {
			SeamGuiPlugin.getPluginLog().logInfo("Unable to create wizard 'org.jboss.tools.seam.ui.wizards.SeamProjectWizard'."); //$NON-NLS-1$
		}
	}

	public void dispose() {}

	public void init(IWorkbenchWindow window) {}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {}

}