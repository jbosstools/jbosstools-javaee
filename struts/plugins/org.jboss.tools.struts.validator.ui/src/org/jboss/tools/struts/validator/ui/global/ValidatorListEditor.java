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
package org.jboss.tools.struts.validator.ui.global;

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.common.model.util.*;

import org.jboss.tools.struts.validator.ui.formset.FEditorConstants;
import org.jboss.tools.common.model.ui.objecteditor.*;
import org.jboss.tools.struts.validators.model.helpers.*;
import org.jboss.tools.common.model.ui.swt.util.BorderLayout;

public class ValidatorListEditor extends XChildrenEditor implements GlobalEditorListener, SelectionListener {
	protected XModelObject selected = null;
	protected ValidatorEditor listener = null;
	protected GlobalEditor globalEditor = null;
	protected boolean lock2 = false;

	public void dispose() {
		super.dispose();
		listener = null;
		if (globalEditor!=null) globalEditor.dispose();
		globalEditor = null;
	}

	public void addListener(ValidatorEditor listener) {
		this.listener = listener;
	}

	public void setGlobalEditor(GlobalEditor globalEditor) {
		this.globalEditor = globalEditor;
	}

	public Control createControl(Composite parent) {
		control = new Group(parent, SWT.NONE);
		((Group)control).setText("Validators");
		BorderLayout bl = new BorderLayout();
		control.setLayout(bl);
		xtable.createControl(control);		
		bl.centerComposite = xtable.getControl();

		Composite c = new Composite(control, SWT.NONE);
		bl.northComposite = c;
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 4;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		c.setLayout(gl);

		createCommandBar();
		bar.createControl(c);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
		bar.getControl().setLayoutData(gd);

		enableSelectionListener();
		update();
		xtable.getTable().addSelectionListener(this);
		return control;	
	}

	protected AbstractTableHelper createHelper() {
		return new ValidatorsTableHelper();
	}

	protected void createCommandBar() {
		bar.getLayout().asToolBar = true;
		bar.setCommands(new String[]{ADD, DELETE, EDIT});
		bar.getLayout().direction = SWT.HORIZONTAL;
		bar.getLayout().iconsOnly = true;
		bar.setImage(ADD, FEditorConstants.IMAGE_CREATE);
		bar.setImage(EDIT, FEditorConstants.IMAGE_EDIT);
		bar.setImage(DELETE, FEditorConstants.IMAGE_DELETE);
	}

	public void setSelected(XModelObject selected) {
		if(this.selected == selected) return;
		this.selected = selected;
		update();
	}

	protected Color getItemColor(int i) {
		XModelObject o = helper.getModelObject(i);
		boolean isSg = (o != null && selected == o.getParent());
		return (isSg) ? DEFAULT_COLOR : FEditorConstants.INHERITED;
	}

	public void update() {
		lock2 = true;
		if(xtable.getControl() == null || xtable.getControl().isDisposed()) return;
		lock = true;
		xtable.update();
		lock = false;
		if(selectionListener != null) updateBar();
		lock2 = false;
		widgetSelected(null);
	}

	protected void updateBar() {
		super.updateBar();
		if(!globalEditor.isGlobalSelected()) {
			bar.setEnabled(ADD, false);
		}
	}

	public void structureChanged(XModelTreeEvent event) {
		if(helper.getModelObject() == null) return;
		if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
			if(helper.getModelObject() != event.getModelObject().getParent()) return;
			XModelObject added = (XModelObject)event.getInfo();
			for (int i = 0; i < helper.size(); i++)
			  if(added == helper.getModelObject(i)) 
				xtable.setSelection(i);
		}
	}

	public void widgetSelected(SelectionEvent e) {
		if (globalEditor != null) globalEditor.updateCommandsEnabled();
		if(lock2 || listener == null) return;
		if(xtable.getControl() == null || xtable.getControl().isDisposed()) return;
		int r = xtable.getSelectionIndex();
		listener.setObject(helper.getModelObject(r));
	}
	public void widgetDefaultSelected(SelectionEvent e) {}

	protected void add() {
		if(selected == null) return;
		Set set = getKeys();
		callAction(selected, "CreateActions.AddValidator");
		update();
		int i = getAddedKey(set);
		if(i >= 0) xtable.setSelection(i);
	}
}