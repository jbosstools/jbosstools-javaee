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
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelFactory;
import org.jboss.tools.jst.web.ui.wizards.project.ImportWebWarWizard;
import org.jboss.tools.struts.ui.StrutsUIImages;
import org.jboss.tools.struts.ui.operation.ImportStrutsWarOperation;
import org.jboss.tools.struts.webprj.model.helpers.context.ImportStrutsWarContext;

public class ImportStrutsWarWizard extends ImportWebWarWizard {

	public ImportStrutsWarWizard() {
		this.setWindowTitle(WizardKeys.getString(ImportProjectWizard.IMPORT_STRUTS_PROJECT_WIZARD_WINDOW_TITLE));
		this.setDefaultPageImageDescriptor(StrutsUIImages.getInstance().getOrCreateImageDescriptor(StrutsUIImages.IMPORT_STRUTS_PROJECT_IMAGE));
	}

	public void addPages() {
		context = new ImportStrutsWarContext();
		context.setTarget(XModelFactory.getDefaultInstance().getRoot());
		installMainPage();
	}

	protected IRunnableWithProgress createOperation() {
		return new ImportStrutsWarOperation(context);
	}
}
