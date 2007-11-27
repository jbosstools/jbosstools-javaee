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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.validator.ui.ValidatorAttributeEditor;
import org.jboss.tools.struts.validator.ui.formset.model.*;

public class FieldEditor {
	protected Composite control;
	protected FieldAttributeEditor pageEditor = 
	  new PageAttributeEditor("page", "Page", "EditActions.EditPage");
	protected FieldAttributeEditor indexEditor = 
	  new PropertyIndexAttributeEditor("indexedListProperty", "Indexed List Property", "EditActions.EditIndex");
	
	public FieldEditor() {}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4; 
		control.setLayout(vfl);
		pageEditor.createControl(control);
		VerticalFillLayout.createSeparator(control, 2);
		indexEditor.createControl(control); 
		VerticalFillLayout.createSeparator(control, 2);
		return control;	
	}
	
	public Control getControl() {
		return control;
	}
	
	public void setFModel(FModel fmodel) {
		pageEditor.setFModel(fmodel);
		indexEditor.setFModel(fmodel);
	}

	public void setEnabled(boolean b) {
		pageEditor.setEnabled(b);
		indexEditor.setEnabled(b);
	}

	public void update() {
		pageEditor.update();
		indexEditor.update();
	}

}

abstract class FieldAttributeEditor extends ValidatorAttributeEditor {
	static String EDIT = "Edit";
	static String OVERWRITE = "Override";
	static String DEFAULT = "Default";
	protected FieldModel fmodel = null;
	protected boolean enabled = true;
	protected int status = 0;
	protected String displayName;
	Label label;
	protected Text text;

	public FieldAttributeEditor(String name, String displayName, String command) {
		super(name, new String[]{EDIT}, new String[]{command});		
		this.displayName = displayName;
	}
	
	public void setFModel(FModel fmodel) {
		if(this.fmodel == fmodel) return;
		this.fmodel = (FieldModel)fmodel;
		update();
	}

	public void update() {
		text.setText(getText());
		int s = getStatus();
		if(s == status) return;
		status = s;
		if(s == FieldModel.INHERITED) text.setForeground(FEditorConstants.INHERITED);
		else text.setForeground(FEditorConstants.DEFAULT_COLOR);
		if(s == FieldModel.DEFINED) bar.setCommands(new String[]{EDIT});
		else if(s == FieldModel.OVERWRITTEN) bar.setCommands(new String[]{EDIT, DEFAULT});
		else  bar.setCommands(new String[]{OVERWRITE});
		bar.update();
		bar.getControl().getParent().layout();
	}
	
	public void setEnabled(boolean b) {
		enabled = fmodel != null && fmodel.isEditable();
		int s = getStatus();
		if (s == FieldModel.DEFINED) {
			bar.setEnabled(EDIT, enabled);
		} else if (s == FieldModel.OVERWRITTEN) {
			bar.setEnabled(EDIT, enabled);
			bar.setEnabled(DEFAULT, enabled);
			bar.setCommands(new String[]{EDIT, DEFAULT});
		} else  {
			bar.setEnabled(OVERWRITE, enabled);
		} 
	}

	public void action(String name) {
		if(OVERWRITE.equals(name)) {
			overwrite();
		} else if(DEFAULT.equals(name)) {
			setDefault();
		} else if(EDIT.equals(name)) {
			edit();
		}
	}

	public XModelObject getModelObject() {
		if(fmodel == null) return null;
		XModelObject[] os = fmodel.getModelObjects();
		return (os.length == 0) ? null : os[0];
	}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		control.setLayout(gl);

		label = new Label(control, SWT.NONE);
		label.setText(displayName);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = LABEL_WIDTH;
		label.setLayoutData(gd);

		text = new Text(control, SWT.READ_ONLY | SWT.BORDER);
		text.setBackground(new Color(null, 255, 255, 255));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);

		bar.createControl(control);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		bar.getControl().setLayoutData(gd);

		return control;
	}
	
	protected abstract String getText();
	protected abstract int getStatus();
	protected abstract void setDefault();
	protected abstract void overwrite();
	
	protected void edit() {
		XModelObject o = getModelObject();
		if(o != null) invoke(commands[0], o);
	}

}

class PageAttributeEditor extends FieldAttributeEditor {

	public PageAttributeEditor(String name, String displayName, String command) {
		super(name, displayName, command);
	}

	protected String getText() {
		return (fmodel == null) ? "" : fmodel.getPage();
	}

	protected int getStatus() {
		return (fmodel == null) ? 0 : fmodel.getPageStatus();
	}

	protected void setDefault() {
		XModelObject o = getModelObject();
		if(o != null) o.getModel().changeObjectAttribute(o, "page", "");
	}

	protected void overwrite() {
		XModelObject[] ts = FieldDataEditor.getTarget(fmodel);
		if(ts == null || ts[0] == null) return;
		if(!ts[0].isActive()) ts[0].setAttributeValue("page", getText());
		long t = ts[0].getTimeStamp();
		invoke("EditActions.EditPage", ts[0]); 
		if(t != ts[0].getTimeStamp() && ts[1] != null) {
			ts[0].setAttributeValue("indexedListProperty", "");
			DefaultCreateHandler.addCreatedObject(ts[1], ts[2], FindObjectHelper.IN_EDITOR_ONLY);
		}
	}

}

class PropertyIndexAttributeEditor extends FieldAttributeEditor {

	public PropertyIndexAttributeEditor(String name, String displayName, String command) {
		super(name, displayName, command);
	}

	protected String getText() {
		return (fmodel == null) ? "" : fmodel.getIndex();
	}

	protected int getStatus() {
		return (fmodel == null) ? 0 : fmodel.getIndexStatus();
	}

	protected void setDefault() {
		XModelObject o = getModelObject();
		if(o != null) o.getModel().changeObjectAttribute(o, "indexedListProperty", "");
	}

	protected void overwrite() {
		XModelObject[] ts = FieldDataEditor.getTarget(fmodel);
		if(ts == null || ts[0] == null) return;
		if(!ts[0].isActive()) ts[0].setAttributeValue("indexedListProperty", getText());
		long t = ts[0].getTimeStamp();
		invoke("EditActions.EditIndex", ts[0]);
		if(t != ts[0].getTimeStamp() && ts[1] != null) {
			ts[0].setAttributeValue("page", "");
			DefaultCreateHandler.addCreatedObject(ts[1], ts[2], FindObjectHelper.IN_EDITOR_ONLY);
		}
	}

}

