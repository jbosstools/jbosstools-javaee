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

public class ComboFieldEditor extends BaseFieldEditor implements ITaggedFieldEditor,PropertyChangeListener{

	List values = null;
	
	boolean flat = false;
	
	public ComboFieldEditor(String name, String label, List values,Object defaultValue,boolean flat) {
		super(name, label, defaultValue);
		this.values = Collections.unmodifiableList(values);
		this.flat = flat;
	}

	private ComboBoxField comboField;

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
		if(comboField == null) {
			comboField = new ComboBoxField(composite,values,getValue(),flat);
			comboField.addPropertyChangeListener(this);
		} else if(composite!=null) {
			Assert.isTrue(comboField.getControl().getParent()==composite);
		}
		return comboField.getControl();
	}

	@Override
	public Object[] getEditorControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(Object object) {
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setValue(evt.getNewValue());
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor#getTags()
	 */
	public String[] getTags() {
		return comboField.getComboControl().getItems();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor#setTags(java.lang.String[])
	 */
	public void setTags(String[] tags) {
		comboField.setTags(tags,getValueAsString());	
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 1;
	}
	
}
