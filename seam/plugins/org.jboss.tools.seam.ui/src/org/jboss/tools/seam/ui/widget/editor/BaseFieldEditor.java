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
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author eskimo
 *
 */
public abstract class BaseFieldEditor implements IFieldEditor {

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private Object value = new Object();
	
	private String labelText = "No label";
	
	private String nameText = null;

	Label labelControl = null;
	
	/**
	 * 
	 * @param name
	 * @param label
	 * @param defaultValue
	 */
	public BaseFieldEditor(String name, String label,Object defaultValue) {
		this.value = defaultValue;
		this.labelText = label;
		this.nameText = name;
	}
	
	/**
	 * 
	 * @param parent
	 */
	public void doFillIntoGrid(Object parent) {
		Assert.isTrue(parent instanceof Composite, "Parent control should be Composite");
		Assert.isTrue(((Composite)parent).getLayout() instanceof GridLayout,"Editor supports only grid layout");
		Composite aComposite = (Composite) parent;
		Control[] controls = (Control[])getEditorControls(aComposite);
		GridLayout gl = (GridLayout)((Composite)parent).getLayout();
		
		doFillIntoGrid(aComposite,gl.numColumns);
	}
	
	/**
	 * @param composite
	 * @param numColumns
	 */
	protected void doFillIntoGrid(Composite composite, int numColumns) {
		
	}

	/**
	 * 
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * 
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
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

	/**
	 *
	 * @return
	 */
	public Label getLabelControl() {
		return createLabelControl(null);
	}

	/**
	 * 
	 */
	public abstract Object[] getEditorControls(Object composite);
	
	/**
	 * 
	 */
	public abstract Object[] getEditorControls();
	
	/**
	 * 
	 * @return
	 */
	public Control[] getSwtControls() {
		return (Control[])getEditorControls();
	}

	/**
	 * 
	 */
	public abstract int getNumberOfControls();

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
	}

	/**
	 * 
	 */
	public void setValueAsString(String stringValue) {
		value = stringValue;
	}

	/**
	 * 
	 */
	public String getName() {
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

	/**
	 * 
	 * @return
	 */
	public String getLabelText() {
		return labelText;
	}

	/**
	 * 
	 * @param labelText
	 */
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	
	/**
	 * 
	 */
	private boolean editable = true;
	
	/**
	 * 
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * 
	 */
	public void setEditable(boolean aEditable) {
		this.editable = aEditable;
	}
}
