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

import org.jboss.tools.common.model.ui.wizards.MessageWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelFactory;
import org.jboss.tools.jsf.ui.operation.ImportJSFWarOperation;
import org.jboss.tools.jsf.web.helpers.context.ImportJSFWarContext;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.jst.web.ui.wizards.project.ImportWebWarWizard;

public class ImportJSFWarWizard extends ImportWebWarWizard {

	public ImportJSFWarWizard() {
		this.setWindowTitle(WizardKeys.getString(ImportProjectWizard.IMPORT_JSF_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_IMPORT_PROJECT));
	}
	
	public void addPages() {
		if(true) {
			context = new ImportJSFWarContext();
			context.setTarget(XModelFactory.getDefaultInstance().getRoot());
			installMainPage();
		} else { // dead code
			addPage(new MessageWizardPage(WizardKeys.getString(ImportProjectWizard.IMPORT_JSF_PROJECT_WIZARD_WINDOW_TITLE),"no license"));	 //$NON-NLS-1$
		}
	}
	protected IRunnableWithProgress createOperation() {
		return new ImportJSFWarOperation(context);
	}

}
