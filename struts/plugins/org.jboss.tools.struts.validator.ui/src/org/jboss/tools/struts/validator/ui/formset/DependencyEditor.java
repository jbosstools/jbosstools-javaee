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
package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.jboss.tools.struts.validator.ui.formset.model.*;

import org.jboss.tools.struts.messages.StrutsUIMessages;

public class DependencyEditor {
	static String fgTitle = StrutsUIMessages.DEPENDS;
	static String msgTitle = StrutsUIMessages.MSG_CORRESPONDED_MESSAGE;
	static String argTitle = StrutsUIMessages.ARG_REPLACEMENT_VALUE_FOR_MESSAGE;
	static String varTitle = StrutsUIMessages.VAR_VALIDATOR_PARAMETER;

	protected Composite control;
	protected FModel fmodel;
	protected FieldEditor fieldEditor = new FieldEditor();
	protected MsgEditor msgEditor = new MsgEditor();
	protected ArgEditor argEditor = new ArgEditor();
	protected VarEditor varEditor = new VarEditor();
	
	public DependencyEditor() {}

	public void set11() {
		argEditor.set11();
	}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new VerticalFillLayout());
		
		Group fg = new Group(control, SWT.NONE);
		fg.setLayout(new FillLayout(SWT.VERTICAL));
		fieldEditor.createControl(fg);
		
		VerticalFillLayout.createSeparator(control, 3);
		
		Group mg = new Group(control, SWT.NONE);
		mg.setLayout(new FillLayout(SWT.VERTICAL));
		msgEditor.createControl(mg);
		mg.setText(msgTitle);

		VerticalFillLayout.createSeparator(control, 3);

		Group ag = new Group(control, SWT.NONE);
		ag.setLayout(new FillLayout(SWT.VERTICAL));
		argEditor.createControl(ag);
		ag.setText(argTitle);
		
		VerticalFillLayout.createSeparator(control, 3);

		Group vg = new Group(control, SWT.NONE);
		vg.setLayout(new FillLayout(SWT.VERTICAL));
		varEditor.createControl(vg);
		vg.setText(varTitle);

		return control;	
	}
	
	public Control getControl() {
		return control;	
	}
	
	public void update() {
		fieldEditor.update();
		msgEditor.update();
		argEditor.update();
		varEditor.update();
	}

	public void setFModel(FModel fmodel) {
		this.fmodel = fmodel;
		fieldEditor.setFModel((fmodel == null) ? null : fmodel.getParent());
		msgEditor.setFModel(fmodel);
		argEditor.setFModel(fmodel);
		varEditor.setFModel(fmodel);
		update();
	}
	
	public FieldEditor getFieldEditor() {
		return fieldEditor;
	}

}
