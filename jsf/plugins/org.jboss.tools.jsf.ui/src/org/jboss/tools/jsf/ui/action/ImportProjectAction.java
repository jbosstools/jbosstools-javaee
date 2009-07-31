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
package org.jboss.tools.jsf.ui.action;

import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.ui.JsfUiImages;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard;
import org.jboss.tools.common.model.ui.*;

public class ImportProjectAction extends Action implements IWorkbenchWindowActionDelegate {
	public ImportProjectAction() {
		super(JSFUIMessages.IMPORT_JSF_PROJECT);
		setToolTipText(JSFUIMessages.IMPORT_JSF_PROJECT);
		setImageDescriptor(JsfUiImages.getImageDescriptor(JsfUiImages.JSF_IMPORT_PROJECT_ACTION));
	}

	public void run() {
		IImportWizard wizard = ExtensionPointUtils.findImportWizardsItem(
			JSFModelPlugin.PLUGIN_ID,
			"org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard" //$NON-NLS-1$
		);
		if (wizard != null) {
			((ImportProjectWizard)wizard).setHelpAvailable(false);
			wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
			WizardDialog dialog = new WizardDialog(ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			JsfUiPlugin.getDefault().getWorkbench().getHelpSystem().setHelp(dialog.getShell(), "org.eclipse.ui.import_wizard_context"); //$NON-NLS-1$
			dialog.open();  
		} else {
			JsfUiPlugin.getPluginLog().logError("Unable to create wizard 'org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard'."); //$NON-NLS-1$
		}
	}

	public void dispose() {}

	public void init(IWorkbenchWindow window) {}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {}

}
