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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class BaseFieldEditor implements IFieldEditor {

	public BaseFieldEditor(String name, String label,Object defaultValue) {
		this.value = defaultValue;
		this.labelText = label;
		this.nameText = name;
	}
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private Object value = new Object();
	
	private String labelText = "No label";
	
	private String nameText = null;
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public abstract void createEditorControls(Object composite);
	
	Label labelControl = null;
	
	public Label createLabelControl(Composite parent) {
		if(labelControl==null) {
			labelControl = new Label(parent,SWT.NO_BACKGROUND);
			labelControl.setText(this.labelText);
		} else if(parent!=null) {
			if(labelControl.getParent()!=parent)
				throw new IllegalArgumentException("Parent for label is different");
		}
		return labelControl;
	}

	public Label getLabelControl() {
		return createLabelControl(null);
	}
	
	abstract public void doFillIntoGrid(Object parent, int columns);

	/**
	 * 
	 */
	public abstract Object[] getEditorControls();
	
	public Control[] getSwtControls() {
		return (Control[])getEditorControls();
	}

	/**
	 * 
	 */
	public int getNumberOfControls() {
		return getEditorControls().length;
	}

	/**
	 * 
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 
	 */
	public String getValueAsString() {
		return getValue().toString();
	}

	/**
	 * 
	 */
	public boolean isEnabled() {
		if(getSwtControls().length==0)
			return true;
		else {
			return getSwtControls()[0].isEnabled();
		}
	}

	/**
	 * 
	 */
	public void setEnabled(boolean enabled) {
		Control[] controls = getSwtControls();
		for(int i=0;i<controls.length;i++) {
			Control control = controls[i];
			control.setEnabled(enabled);
		}
	}

	/**
	 * 
	 */
	public boolean setFocus() {
		return true;
	}

	/**
	 * 
	 * @param newValue
	 */
	public void setValue(Object newValue) {
		pcs.firePropertyChange(nameText,value,newValue);
		value = newValue;
		System.out.println("new value - " + newValue);
	}

	/**
	 * 
	 */
	public void setValueAsString(String stringValue) {
		value = stringValue;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return nameText;
	}
	
	/**
	 * 
	 */
	public void dispose() {
		PropertyChangeListener[] listeners = pcs.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			PropertyChangeListener propertyChangeListener = listeners[i];
			pcs.removePropertyChangeListener(propertyChangeListener);			
		}
	}
}
