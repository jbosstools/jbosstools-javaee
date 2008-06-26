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
package org.jboss.tools.struts.ui.wizard.sync;

import org.jboss.tools.common.model.ui.attribute.editor.*;
import org.jboss.tools.common.model.ui.wizards.special.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.*;

public class AddModuleStepView extends SpecialWizardStep {

	public Control createControl(Composite parent) {
		super.createControl(parent);
		setInitialPath();
		return stepControl;
	}
	
	void setInitialPath() {
		FileFieldEditorEx e = (FileFieldEditorEx)attributes.getFieldEditorByName("path");
		XModelObject f = FileSystemsHelper.getWebInf(support.getTarget().getModel());
		if(f instanceof FileSystemImpl) e.setLastPath(((FileSystemImpl)f).getAbsoluteLocation());
	}

}
