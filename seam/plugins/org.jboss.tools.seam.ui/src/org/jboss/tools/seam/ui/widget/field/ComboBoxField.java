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
package org.jboss.tools.seam.ui.widget.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author eskimo
 *
 */
public class ComboBoxField extends BaseField implements ISelectionChangedListener {

	ComboViewer comboControl = null;
	List values = new ArrayList();
	
	
	public ComboBoxField(Composite parent,List values, Object value, boolean floatStyle) {
		this.values = values;
		comboControl = new ComboViewer(parent,floatStyle?SWT.FLAT:SWT.READ_ONLY);

		comboControl.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {			
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				return ComboBoxField.this.values.toArray();
			}
			
			
		});

		comboControl.addSelectionChangedListener(this);
		comboControl.setLabelProvider(new ILabelProvider() {

			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getText(Object element) {
				// TODO Auto-generated method stub
				return element.toString();
			}
			
		});
		comboControl.setInput(values);
		comboControl.setSelection(new StructuredSelection(value), true);
		
		
	}
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void selectionChanged(SelectionChangedEvent event) {
		firePropertyChange("", ((StructuredSelection)event.getSelection()).getFirstElement());
	}
	
	public Combo getComboControl() {
		return comboControl.getCombo();
	}

	@Override
	public Control getControl() {
		return getComboControl();
	}
	
	public void setValue(Object newValue) {
		comboControl.setSelection(new StructuredSelection(newValue));
		comboControl.getCombo().setText(newValue.toString());
	}
	
	public void setTags(String[] tags) {
		values = Arrays.asList(tags);
		comboControl.removeSelectionChangedListener(this);
		comboControl.refresh(true);
		comboControl.addPostSelectionChangedListener(this);
		comboControl.setSelection(new StructuredSelection(tags.length>0?tags[0]:new String[]{}));
	}
}
