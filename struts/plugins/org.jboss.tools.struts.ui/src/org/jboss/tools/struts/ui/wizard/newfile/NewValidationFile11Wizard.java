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

import org.jboss.tools.common.model.ui.wizard.newfile.*;
import org.jboss.tools.struts.ui.StrutsUIImages;

public class NewValidationFile11Wizard extends NewFileWizardEx {
	
	public NewValidationFile11Wizard(){
		setDefaultPageImageDescriptor(StrutsUIImages.getInstance().getOrCreateImageDescriptor(StrutsUIImages.VALIDATION_FILE_IMAGE));
	}

	protected NewFileContextEx createNewFileContext() {
		return new NewValidationFile11Context();
	}
	
	class NewValidationFile11Context extends NewFileContextEx {
		protected String getActionPath() {
			return "CreateActions.CreateFiles.Struts.CreateValidator11";
		}
	}

}
