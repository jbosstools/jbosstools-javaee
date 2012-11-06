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
package org.jboss.tools.struts.ui.wizard.project;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.jst.web.project.helpers.ProjectTemplate;
import org.jboss.tools.struts.ui.IStrutsHelpContextIds;
import org.jboss.tools.struts.ui.StrutsUIImages;
import org.jboss.tools.struts.ui.operation.StrutsProjectCreationOperation;
import org.jboss.tools.jst.web.WebPreference;
import org.jboss.tools.jst.web.ui.wizards.appregister.NewProjectRegisterPage;
import org.jboss.tools.jst.web.ui.wizards.project.NewWebProjectWizard;
import org.jboss.tools.jst.web.ui.wizards.project.NewWebProjectWizardPreprocessingPage;
import org.jboss.tools.struts.webprj.model.helpers.context.*;

public class NewProjectWizard extends NewWebProjectWizard {
	private NewProjectWizardPage fMainPage;
	private NewProjectAddTldPage fTldPage;
	NewWebProjectWizardPreprocessingPage preprocessingPage;
	private static final String NEW_STRUTS_PROJECT_WIZARD_WINDOW_TITLE = "NEW_STRUTS_PROJECT_WIZARD_WINDOW_TITLE";
	private static final String NEW_STRUTS_PROJECT_WIZARD_CREATE_STRUTS_PROJECT = "NEW_STRUTS_PROJECT_WIZARD_CREATE_STRUTS_PROJECT";
	
	public NewProjectWizard() {
		context = new NewProjectWizardContext();
		this.setWindowTitle(WizardKeys.getString(NEW_STRUTS_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(StrutsUIImages.getInstance().getOrCreateImageDescriptor(StrutsUIImages.STRUTS_PROJECT_IMAGE));
	}
	
	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IStrutsHelpContextIds.NEW_STRUTS_PROJECT);
	}
	
	public void addPages() {
		fMainPage = new NewProjectWizardPage(context); 
		fMainPage.setTitle(WizardKeys.getString(NEW_STRUTS_PROJECT_WIZARD_CREATE_STRUTS_PROJECT));
		addPage(fMainPage);
		NewProjectRegisterPage registerPage = new NewProjectRegisterPage(context, WebPreference.DEFAULT_SERVLET_VERSION);
		registerPage.setTitle("Web");
		addPage(registerPage);
		fTldPage = new NewProjectAddTldPage((NewProjectWizardContext)context);
		fTldPage.setTitle("Tag Libraries");
		addPage(fTldPage);
		preprocessingPage = new NewWebProjectWizardPreprocessingPage(context);
		preprocessingPage.setTitle("Preprocessing Properties");
		addPage(preprocessingPage);
	}
	
	protected IRunnableWithProgress createOperation() {
		return new StrutsProjectCreationOperation(context);
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
