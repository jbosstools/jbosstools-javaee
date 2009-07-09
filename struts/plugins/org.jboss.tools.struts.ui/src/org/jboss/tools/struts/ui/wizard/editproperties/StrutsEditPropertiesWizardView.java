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
package org.jboss.tools.struts.ui.wizard.editproperties;

import java.util.Properties;

import org.jboss.tools.common.model.ui.wizards.query.AbstractQueryWizardView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.struts.model.helpers.StrutsEditPropertiesContext;

public class StrutsEditPropertiesWizardView extends AbstractQueryWizardView {
	private StrutsEditPropertiesEditor editor = new StrutsEditPropertiesEditor();
	private StrutsEditPropertiesContext context = null;

	public StrutsEditPropertiesWizardView(){ 
		//this.setMessage(WizardKeys.getString("StrutsEditPropertiesWizardView.Message"));
		//this.setTitle(WizardKeys.getString("StrutsEditPropertiesWizardView.Title"));
		//this.setWindowTitle(WizardKeys.getString("StrutsEditPropertiesWizardView.WindowTitle"));
	}
	
	public void dispose() {
		super.dispose();
		if (editor!=null) editor.dispose();
		editor = null;
		if (context!=null) context.dispose();
		context = null;
	}
	
	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		return editor.createControl(composite);
	}

	public String[] getCommands() {
		return new String[]{CLOSE};
	}
	
	public void setObject(Object data) {
		super.setObject(data);
		Properties properties = (Properties)data;
		context = (StrutsEditPropertiesContext)properties.get("context"); //$NON-NLS-1$
		editor.setContext(context);
		if(context.getObject() != null) {
			setModel(context.getObject().getModel());
			setHelpKey(context.getObject().getModelEntity().getName() + "_Properties"); //$NON-NLS-1$
		}
		this.setTitle(WizardKeys.getTitle(getHelpKey()));
		this.setWindowTitle(WizardKeys.getHeader(getHelpKey()));
	}

	public void stopEditing() {
		if(editor != null) editor.stopEditing();
	}

	public void action(String command) {
		super.action(command);		
		if(CLOSE.equals(command)) {
			setCode(0);
			dispose();
		}
	}
}
