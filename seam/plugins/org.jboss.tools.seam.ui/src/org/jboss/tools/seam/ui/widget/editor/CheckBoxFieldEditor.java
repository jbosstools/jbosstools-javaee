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

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.seam.ui.widget.field.CheckBoxField;

/**
 * @author eskimo
 *
 */
public class CheckBoxFieldEditor extends BaseFieldEditor implements PropertyChangeListener {

	private Control checkBoxControl;

	/**
	 * @param name
	 * @param label
	 * @param defaultValue
	 */
	public CheckBoxFieldEditor(String name, String label, Object defaultValue) {
		super(name, label, defaultValue);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.internal.project.facet.BaseFieldEditor#createEditorControls(java.lang.Object)
	 */
	@Override
	public void createEditorControls(Object composite) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.internal.project.facet.BaseFieldEditor#getEditorControls()
	 */
	@Override
	public Object[] getEditorControls() {
		return new Object[] {getLabelControl(),getCheckBoxControl()};
	}

	public Control getCheckBoxControl() {
		return createCheckBoxControl(null);
	}

	private Control createCheckBoxControl(Composite parent) {
		if(checkBoxControl==null) {
			CheckBoxField checkBoxFild= new CheckBoxField(parent);
			checkBoxFild.addPropertyChangeListener(this);
			checkBoxControl = checkBoxFild.getCheckBox();
		} else if(parent!=null) {
			Assert.isTrue(checkBoxControl.getParent()==parent);
		}
		return checkBoxControl;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.internal.project.facet.IFieldEditor#isEditable()
	 */
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.internal.project.facet.IFieldEditor#save(java.lang.Object)
	 */
	public void save(Object object) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.internal.project.facet.IFieldEditor#setEditable(boolean)
	 */
	public void setEditable(boolean ediatble) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFillIntoGrid(Object parent, int columns) {
		Assert.isTrue(parent instanceof Composite);
		Composite aComposite = (Composite) parent;
		createLabelControl(aComposite);
		checkBoxControl = createCheckBoxControl(aComposite);

        GridData gd = new GridData();
        
        gd.horizontalSpan = columns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
        checkBoxControl.setLayoutData(gd);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setValue(evt.getNewValue());
	}

}
