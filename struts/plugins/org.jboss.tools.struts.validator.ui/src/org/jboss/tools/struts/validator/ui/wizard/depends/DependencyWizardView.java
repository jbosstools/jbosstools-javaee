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
package org.jboss.tools.struts.validator.ui.wizard.depends;

import org.jboss.tools.common.model.ui.wizards.query.AbstractQueryWizardView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.meta.key.WizardKeys;

public class DependencyWizardView extends AbstractQueryWizardView {
	protected ValidationPathView path = new ValidationPathView();
	protected DependencyLRView lists = new DependencyLRView();
	
	public DependencyWizardView() {
		path.addPathListener(lists);
		this.setMessage(WizardKeys.getString("DependencyWizardView.Message"));
		this.setTitle(WizardKeys.getString("DependencyWizardView.Title"));
		this.setWindowTitle(WizardKeys.getString("DependencyWizardView.WindowTitle"));
	}

	public Control createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout g = new GridLayout();
		c.setLayout(g);
		Control pc = path.createControl(c);
		GridData d = new GridData(GridData.FILL_HORIZONTAL);
		pc.setLayoutData(d);
		
		Control lc = lists.createControl(c);
		d = new GridData(GridData.FILL_BOTH);
		lc.setLayoutData(d);
		
		path.updateSelection();
		return c;		
	}
	
	public void setObject(Object data) {
		super.setObject(data);
		path.setObject(data);
		lists.setObject(data);
	}
	
	public void action(String command) {
		if(!OK.equalsIgnoreCase(command)) {
			super.action(command);
		} else {
			lists.saveTarget();
			setCode(0);
			dispose();
		}
	}
}
