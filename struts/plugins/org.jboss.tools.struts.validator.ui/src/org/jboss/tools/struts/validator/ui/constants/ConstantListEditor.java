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
package org.jboss.tools.struts.validator.ui.constants;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.model.ui.objecteditor.*;
import org.jboss.tools.struts.validator.ui.global.*;
import org.jboss.tools.struts.validators.model.helpers.*;
import org.jboss.tools.struts.validator.ui.formset.FEditorConstants;

public class ConstantListEditor extends XChildrenEditor implements GlobalEditorListener {
	protected XModelObject selected = null;
	protected GlobalEditor globalEditor = null;
	
	public ConstantListEditor() {
		setMnemonicEnabled(true);
	}

	public void dispose() {
		super.dispose();
		if (globalEditor!=null) globalEditor.dispose();
		globalEditor = null;
	}
	
	public Control createControl(Composite parent) {
		Control control = super.createControl(parent);
		return control;	
	}

	protected AbstractTableHelper createHelper() {
		return new ConstantsTableHelper();
	}

	public void setSelected(XModelObject selected) {
		if(this.selected == selected) return;
		this.selected = selected;
		update();
	}

	protected int[] getColumnWidthHints() {
		return new int[]{10, 20};	
	}
	
	protected Color getItemColor(int i) {
		XModelObject o = helper.getModelObject(i);
		boolean isSg = (o != null && selected == o.getParent());
		return (isSg) ? DEFAULT_COLOR : FEditorConstants.INHERITED;
	}
	
	protected void add() {
		if(selected != null) callAction(selected, "CreateActions.AddConstant");
	}	

	public void setGlobalEditor(GlobalEditor globalEditor) {
		this.globalEditor = globalEditor;
	}

	protected void updateBar() {
		super.updateBar();
		if (globalEditor != null) globalEditor.updateCommandsEnabled();
	}
}
