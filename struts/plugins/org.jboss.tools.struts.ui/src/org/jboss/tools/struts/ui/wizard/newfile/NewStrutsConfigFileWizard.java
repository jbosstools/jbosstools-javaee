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
package org.jboss.tools.struts.ui.wizard.newfile;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.ui.wizard.newfile.*;
import org.jboss.tools.struts.ui.IStrutsHelpContextIds;
import org.jboss.tools.struts.ui.StrutsUIImages;

public class NewStrutsConfigFileWizard extends NewFileWizardEx {

	public NewStrutsConfigFileWizard() {
		setDefaultPageImageDescriptor(StrutsUIImages.getInstance().getOrCreateImageDescriptor(StrutsUIImages.STRUTS_CONFIG_IMAGE));
	}
	
	protected int getVersion() {
		return 1;
	}
	
	protected NewFileContextEx createNewFileContext() {
		return new NewStrutsConfigFileContext(getVersion());
	}
	
	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IStrutsHelpContextIds.NEW_STRUTS_CONFIG);
	}
}
