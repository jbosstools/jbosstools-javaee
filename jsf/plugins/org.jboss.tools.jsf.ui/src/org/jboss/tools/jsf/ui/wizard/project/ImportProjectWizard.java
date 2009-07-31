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
package org.jboss.tools.jsf.ui.wizard.project;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelFactory;
import org.jboss.tools.jsf.ui.IJSFHelpContextIds;
import org.jboss.tools.jsf.ui.operation.JSFProjectAdoptOperation;
import org.jboss.tools.jsf.web.helpers.context.*;
import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.jst.web.ui.wizards.project.ImportWebProjectWizard;

public class ImportProjectWizard extends ImportWebProjectWizard {
	private ImportProjectFoldersPage foldersPage;
	static final String IMPORT_JSF_PROJECT_WIZARD_WINDOW_TITLE = "IMPORT_JSF_PROJECT_WIZARD_WINDOW_TITLE"; //$NON-NLS-1$
	
	public ImportProjectWizard() {
		this.setWindowTitle(WizardKeys.getString(IMPORT_JSF_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_IMPORT_PROJECT));
	}
	
	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJSFHelpContextIds.IMPORT_JSF_PROJECT);
	}
	
	public void addPages() {
		context = new ImportProjectWizardContext(XModelFactory.getDefaultInstance().getRoot());
		context.setInitialName(initialName);
		context.setInitialLocation(initialLocation);
		installMainPage();
		foldersPage = new ImportProjectFoldersPage(context);
		installFoldersPage(foldersPage);
	}
	
	protected IRunnableWithProgress createOperation() {
		return new JSFProjectAdoptOperation(context);
	}

	protected String getFinalPerspective() {
		return "org.jboss.tools.jst.web.ui.WebDevelopmentPerspective"; //$NON-NLS-1$
	}

	protected boolean checkOldVersion() {
		//see org.jboss.tools.struts.ui.wizard.project.ImportProjectWizard for example
		return true;
	}
	
}
