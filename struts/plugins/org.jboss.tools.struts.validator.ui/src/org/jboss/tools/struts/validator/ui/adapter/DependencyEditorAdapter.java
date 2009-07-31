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
package org.jboss.tools.struts.validator.ui.adapter;

import java.util.*;
import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.common.model.ui.attribute.adapter.*;
import org.jboss.tools.common.model.ui.wizards.query.*;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.meta.action.SpecialWizardFactory;

public class DependencyEditorAdapter extends DefaultValueAdapter implements IActionHelper {	
	
	public String invoke(Control control) {
		return invoke0(control);			
	}
	
	public String getCommand() {
		return "..."; //$NON-NLS-1$
	}
	
	public String invoke0(Control control) {
		Properties p = new Properties();
		p.setProperty(_getAttributeName(), "" + getValue());
		p.put("model", getModel());
		if(getModelObject() != null) p.put("object", getModelObject());
		if(control != null) p.put("shell", control.getShell());
		p.put("help", getHelpKey());
		AbstractQueryWizard wizard = createWizard(); 
		wizard.setObject(p);
		if(wizard.execute() != 0) return null;
		return p.getProperty(_getAttributeName());
	}
	
	protected String getHelpKey() {
		return "Wizard_Validation_Dependency";
	}
	
	protected String _getAttributeName() {
		return "value";
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IActionHelper.class) return this;
		return super.getAdapter(adapter);
	}
	
	protected AbstractQueryWizard createWizard() {
		return (AbstractQueryWizard)SpecialWizardFactory.createSpecialWizard("org.jboss.tools.struts.validator.ui.wizard.depends.DependencyWizard");
	}
	
}
