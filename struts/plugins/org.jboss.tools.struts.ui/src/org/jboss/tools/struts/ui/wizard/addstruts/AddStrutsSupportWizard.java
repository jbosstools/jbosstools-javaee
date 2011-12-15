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
package org.jboss.tools.struts.ui.wizard.addstruts;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.jst.web.context.ImportWebDirProjectContext;
import org.jboss.tools.struts.ui.IStrutsHelpContextIds;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class AddStrutsSupportWizard extends Wizard {
	ImportWebDirProjectContext context;
	private AddStrutsSupportPage addStrutsPage;
	
	public static int run(Shell shell, ImportWebDirProjectContext context) {
		AddStrutsSupportWizard wizard = new AddStrutsSupportWizard(context);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		dialog.getShell().setText("Add Struts Support");
		dialog.setTitle("Struts Project");
		dialog.setTitleImage(ModelUIImages.getImage(ModelUIImages.WIZARD_DEFAULT));
		return dialog.open();		
	}
	
	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IStrutsHelpContextIds.ADD_STRUTS_SUPPORT);
	}
	
	public AddStrutsSupportWizard(ImportWebDirProjectContext context) {
		this.context = context;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	public void addPages() {
		addStrutsPage = new AddStrutsSupportPage(context);
		addPage(addStrutsPage);		
	}
	
	public boolean canFinish() {
		return true;
	}
	
	public boolean performCancel() {
		context.rollbackSupportDelta();
		return true;
	}
	
	public boolean performFinish() {
		try {
			context.addSupportDelta(addStrutsPage.getData());
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
		return true;
	}

}
