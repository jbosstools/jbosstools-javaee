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

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.action.AddNatureActionDelegate;
import org.jboss.tools.struts.StrutsProjectUtil;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.wizard.project.ImportProjectWizard;

public class AddStrutsNatureActionDelegate extends AddNatureActionDelegate {
	
	protected IWizard getWizard(IProject project) throws Exception {
		ImportProjectWizard wizard = (ImportProjectWizard)ExtensionPointUtils.findImportWizardsItem(
				StrutsUIPlugin.PLUGIN_ID,
				"org.jboss.tools.struts.ui.wizard.project.ImportProjectWizard"
		);
		if (wizard == null) throw new Exception("Wizard org.jboss.tools.common.model.ui.wizards.ImportProjectWizard is not found.");	
		wizard.setInitialName(project.getName());
		wizard.setInitialLocation(findWebXML(project.getLocation().toString()));
		wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
		return wizard;
	}

	protected String getNatureID() {
		return StrutsProjectUtil.STRUTS_NATURE_ID;
	}
	
}
