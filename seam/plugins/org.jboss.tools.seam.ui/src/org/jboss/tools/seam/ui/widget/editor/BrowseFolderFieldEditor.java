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

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowseFolderFieldEditor extends TextFieldEditor {

	private Button browseControl;

	public BrowseFolderFieldEditor(String name, String labelText, String defaultvalue) {
		super(name, labelText, defaultvalue);
	}

	@Override
	public void createEditorControls(Object composite) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFillIntoGrid(Object parent, int columns) {
		Assert.isTrue(parent instanceof Composite);
		Composite aComposite = (Composite) parent;
		createLabelControl(aComposite);
		fTextField = getTextControl(aComposite);

        GridData gd = new GridData();
        
        gd.horizontalSpan = columns - 2;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
        fTextField.setLayoutData(gd);
        
        createBrowseButtonControl(aComposite);
        
        gd = new GridData();
        
        gd.horizontalSpan = columns - 2;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = false;
        browseControl.setLayoutData(gd);
        
	}

	private void createBrowseButtonControl(Composite composite) {
		if(browseControl==null) {
			browseControl = new Button(composite,SWT.PUSH);
			browseControl.setText("Browse");
		} else if(composite!=null){
			Assert.isTrue(browseControl.getParent()==composite);
		}
	}
	
	public Button getBrowseControl() {
		createBrowseButtonControl(null);
		return browseControl;
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

}
