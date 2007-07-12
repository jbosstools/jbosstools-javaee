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
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.seam.ui.widget.field.ComboBoxField;

public class ComboFieldEditor extends BaseFieldEditor implements PropertyChangeListener{

	List values = null;
	
	boolean flat = false;
	
	public ComboFieldEditor(String name, String label, List values,Object defaultValue,boolean flat) {
		super(name, label, defaultValue);
		this.values = Collections.unmodifiableList(values);
		this.flat = flat;
	}

	private Control comboControl;

	@Override
	public Object[] getEditorControls(Object composite) {
		// TODO Auto-generated method stub
		return new Control[] {getComboControl((Composite)composite)};
	}

	@Override
	public void doFillIntoGrid(Object parent) {
	}

	public Control getComboControl(Composite composite) {
		// TODO Auto-generated method stub
		if(comboControl == null) {
			ComboBoxField comboField = new ComboBoxField(composite,values,getValue(),flat);
			comboControl = comboField.getComboControl();
			comboField.addPropertyChangeListener(this);
		} else if(composite!=null) {
			Assert.isTrue(comboControl.getParent()==composite);
		}
		return comboControl;
	}

	@Override
	public Object[] getEditorControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void save(Object object) {
		// TODO Auto-generated method stub

	}

	public void setEditable(boolean ediatble) {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		setValue(evt.getNewValue());
	}

}
