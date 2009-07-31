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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.ui.IJSFHelpContextIds;
import org.jboss.tools.jsf.ui.operation.JSFProjectCreationOperation;
import org.jboss.tools.jsf.web.helpers.context.NewProjectWizardContext;
import org.jboss.tools.jst.web.project.helpers.ProjectTemplate;
import org.jboss.tools.jst.web.ui.wizards.appregister.NewProjectRegisterPage;
import org.jboss.tools.jst.web.ui.wizards.project.NewWebProjectWizard;
import org.jboss.tools.jst.web.ui.wizards.project.NewWebProjectWizardPreprocessingPage;

public class NewProjectWizard extends NewWebProjectWizard {
	private NewProjectWizardPage fMainPage;
	private static final String NEW_JSF_PROJECT_WIZARD_WINDOW_TITLE = "NEW_JSF_PROJECT_WIZARD_WINDOW_TITLE"; //$NON-NLS-1$
	private static final String NEW_JSF_PROJECT_WIZARD_CREATE_STRUTS_PROJECT = "NEW_JSF_PROJECT_WIZARD_CREATE_STRUTS_PROJECT"; //$NON-NLS-1$
	
	NewWebProjectWizardPreprocessingPage preprocessingPage;
	
	public NewProjectWizard() {
		context = new NewProjectWizardContext();
		this.setWindowTitle(WizardKeys.getString(NEW_JSF_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_NEW_PROJECT));
	}
	
	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJSFHelpContextIds.NEW_JSF_PROJECT);
	}
	
	public void addPages() {
		fMainPage = new NewProjectWizardPage(context); 
		fMainPage.setTitle(WizardKeys.getString(NEW_JSF_PROJECT_WIZARD_CREATE_STRUTS_PROJECT));
		addPage(fMainPage);
		NewProjectRegisterPage registerPage = new NewProjectRegisterPage(context, JSFPreference.DEFAULT_JSF_SERVLET_VERSION);
		registerPage.setTitle("Web");
		addPage(registerPage);
		preprocessingPage = new NewWebProjectWizardPreprocessingPage(context);
		preprocessingPage.setTitle("Preprocessing Properties");
		addPage(preprocessingPage);
	}
	
	protected IRunnableWithProgress createOperation() {
		return new JSFProjectCreationOperation(context);
	}

    public IWizardPage getNextPage(IWizardPage page) {
    	IWizardPage next = super.getNextPage(page);
    	if(next == preprocessingPage) {
    		ProjectTemplate t = context.getProjectTemplate();
    		if(t == null) return null;
    		if(t.getProperties().getChildren().length == 0) return null;
    	}
    	return next;    	
    }

}
