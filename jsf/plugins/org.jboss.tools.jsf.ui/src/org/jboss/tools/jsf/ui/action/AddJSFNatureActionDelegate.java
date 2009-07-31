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

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.action.AddNatureActionDelegate;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard;

public class AddJSFNatureActionDelegate extends AddNatureActionDelegate {
	
	protected IWizard getWizard(IProject project) {
		ImportProjectWizard wizard = (ImportProjectWizard)ExtensionPointUtils.findImportWizardsItem(
				JSFModelPlugin.PLUGIN_ID,
				"org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard" //$NON-NLS-1$
		);
		if (wizard == null) throw new IllegalArgumentException("Wizard org.jboss.tools.common.model.ui.wizards.ImportProjectWizard is not found.");	 //$NON-NLS-1$
		wizard.setInitialName(project.getName());
		wizard.setInitialLocation(findWebXML(project.getLocation().toString()));
		wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
		wizard.setWindowTitle(WizardKeys.getString("ADD_JSF_NATURE")); //$NON-NLS-1$
		return wizard;
	}

	protected String getNatureID() {
		return JSFNature.NATURE_ID;
	}
	
}
