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
package org.jboss.tools.jsf.ui.wizard.newfile;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.jsf.model.handlers.CreateFacesConfigSupport;
import org.jboss.tools.jsf.ui.IJSFHelpContextIds;
import org.jboss.tools.common.model.ui.wizard.newfile.*;

public class NewFacesConfigFileWizard extends NewFileWizardEx {

	protected NewFileContextEx createNewFileContext() {
		return new NewFacesConfigFileContext();
	}
	
	public void createPageControls(Composite parent) {
		  super.createPageControls(parent);
		  PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJSFHelpContextIds.NEW_FACES_CONFIG_FILE_WIZARD);
    }

	
	class NewFacesConfigFileContext extends NewFileContextEx {
		protected SpecialWizardSupport createSupport() {
			return new CreateFacesConfigSupport();
		}
		protected String getActionPath() {
			return "CreateActions.CreateFiles.JSF.CreateFacesConfig"; //$NON-NLS-1$
		}
	}

}
