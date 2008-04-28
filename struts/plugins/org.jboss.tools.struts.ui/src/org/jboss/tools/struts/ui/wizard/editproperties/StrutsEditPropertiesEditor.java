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

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.struts.model.helpers.StrutsEditPropertiesContext;

public class StrutsEditPropertiesEditor extends AbstractView {
	private GeneralView generalView = new GeneralView();
	private AdvancedView advancedView = new AdvancedView();
	private SetPropertyView setPropertyView = new SetPropertyView();
	protected TabFolder tabbedpane = null;

	public void dispose() {
		super.dispose();
		if (setPropertyView!=null) setPropertyView.dispose();
		setPropertyView = null;
		if (advancedView!=null) advancedView.dispose();
		advancedView = null;
		if (generalView!=null) generalView.dispose();
		generalView = null;
	}
	
	public void setContext(StrutsEditPropertiesContext context) {
		super.setContext(context);
		generalView.setContext(context);
		advancedView.setContext(context);
		setPropertyView.setContext(context);
	}

	public Control createControl(Composite parent) {
		tabbedpane = new TabFolder(parent, SWT.NONE);
		Control gvc = generalView.createControl(tabbedpane);
		TabItem item = new TabItem(tabbedpane, SWT.NONE);
		item.setControl(gvc);
		item.setText("General");
		if(advancedView.isEnabled()) {
			Control avc = advancedView.createControl(tabbedpane);
			item = new TabItem(tabbedpane, SWT.NONE);
			item.setControl(avc);
			item.setText("Advanced");
		}
		if(setPropertyView.isEnabled()) {
			Control avc = setPropertyView.createControl(tabbedpane);
			item = new TabItem(tabbedpane, SWT.NONE);
			item.setControl(avc);
			item.setText("Set Property");
		}
		
		return tabbedpane;
	}

	public void stopEditing() {
		if(generalView != null) generalView.stopEditing();
		if(advancedView != null) advancedView.stopEditing();
//		setPropertyView.stopEditing();
	}

}
