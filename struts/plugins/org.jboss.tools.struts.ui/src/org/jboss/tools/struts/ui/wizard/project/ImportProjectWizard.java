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

import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.struts.ui.operation.StrutsProjectAdoptOperation;
import org.jboss.tools.jst.web.ui.wizards.project.ImportWebProjectWizard;
import org.jboss.tools.struts.webprj.model.helpers.context.ImportProjectWizardContext;

public class ImportProjectWizard extends ImportWebProjectWizard {
	private ImportProjectModulesPage modulesPage;
	private ImportProjectFoldersPage foldersPage;	
	protected static final String IMPORT_STRUTS_PROJECT_WIZARD_WINDOW_TITLE = "IMPORT_STRUTS_PROJECT_WIZARD_WINDOW_TITLE";
	protected static final String IMPORT_STRUTS_PROJECT_WIZARD_PROJECT_MODULES = "IMPORT_STRUTS_PROJECT_WIZARD_PROJECT_MODULES";
	protected static final String IMPORT_STRUTS_PROJECT_CONFIGURE_PROJECT_MODULES = "IMPORT_STRUTS_PROJECT_CONFIGURE_PROJECT_MODULES";
	
	public ImportProjectWizard() {
		this.setWindowTitle(WizardKeys.getString(IMPORT_STRUTS_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_IMPORT_PROJECT));
	}
	
	public void addPages() {
		context = new ImportProjectWizardContext(ModelUtilities.getPreferenceModel().getRoot());
		context.setInitialName(initialName);
		context.setInitialLocation(initialLocation);
		installMainPage();
		modulesPage = new ImportProjectModulesPage(context); 
		modulesPage.setTitle(WizardKeys.getString(IMPORT_STRUTS_PROJECT_WIZARD_PROJECT_MODULES)); 
		modulesPage.setDescription(WizardKeys.getString(IMPORT_STRUTS_PROJECT_CONFIGURE_PROJECT_MODULES));
		addPage(modulesPage);
		foldersPage = new ImportProjectFoldersPage(context);
		installFoldersPage(foldersPage);
	}
	
	protected String getFinalPerspective() {
		return "org.jboss.tools.jst.web.ui.RedHat4WebPerspective";
	}
	
	protected IRunnableWithProgress createOperation() {
		return new StrutsProjectAdoptOperation(context);
	}

	protected boolean checkOldVersion() {
		if(context.getProjectHandle() != null && context.getProjectHandle().exists()) return true;
		return true;
	}
	
}
