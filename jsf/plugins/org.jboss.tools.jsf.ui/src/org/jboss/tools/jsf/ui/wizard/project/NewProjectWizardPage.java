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

import org.jboss.tools.jst.web.project.helpers.NewWebProjectContext;
import org.jboss.tools.jst.web.ui.wizards.project.NewWebProjectWizardPage;
import org.jboss.tools.jsf.JSFPreference;

public class NewProjectWizardPage extends NewWebProjectWizardPage {

	public NewProjectWizardPage(NewWebProjectContext context) {
		super(context);
	}

	protected String getKey() {
		return "newJSFProjectPage1"; //$NON-NLS-1$
	}

	protected String getProjectRootOption() {
		boolean useDefault = "yes".equals(JSFPreference.USE_DEFAULT_JSF_PROJECT_ROOT.getValue()); //$NON-NLS-1$
		return (useDefault) ? null : JSFPreference.DEFAULT_JSF_PROJECT_ROOT_DIR.getValue();
	}

}
