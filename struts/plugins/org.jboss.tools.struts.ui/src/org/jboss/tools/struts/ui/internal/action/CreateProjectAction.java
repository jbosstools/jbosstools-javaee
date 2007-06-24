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
package org.jboss.tools.struts.ui.internal.action;

import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class CreateProjectAction extends Action implements IWorkbenchWindowActionDelegate {
	public CreateProjectAction() {
		super(StrutsUIMessages.CREATE_NEW_STRUTS_PROJECT);
		setToolTipText(StrutsUIMessages.CREATE_NEW_STRUTS_PROJECT);
		ModelUIImages.setImageDescriptors(this, ModelUIImages.ACT_CREATE_PROJECT);
	}

	public void run() {
		INewWizard wizard = ExtensionPointUtils.findNewWizardsItem(
			StrutsUIPlugin.PLUGIN_ID,
			"org.jboss.tools.struts.ui.wizard.project.NewStrutsProjectWizard" //$NON-NLS-1$
		);
		if (wizard != null) {
			wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
			WizardDialog dialog = new WizardDialog(ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
			dialog.open();  
		} else {
			ModelUIPlugin.log("Unable to create wizard 'org.jboss.tools.struts.ui.wizard.project.NewStrutsProjectWizard'."); //$NON-NLS-1$
		}
	}

	public void dispose() {}

	public void init(IWorkbenchWindow window) {}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {}

}
