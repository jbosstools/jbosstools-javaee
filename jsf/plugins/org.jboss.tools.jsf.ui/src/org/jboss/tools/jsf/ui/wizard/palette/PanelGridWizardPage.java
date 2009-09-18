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
package org.jboss.tools.jsf.ui.wizard.palette;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import org.jboss.tools.common.model.ui.editors.dnd.*;
import org.jboss.tools.common.model.ui.editors.dnd.composite.TagAttributesComposite.AttributeDescriptorValue;
import org.jboss.tools.jst.jsp.jspeditor.dnd.TagProposal;

/**
 * @author erick
 */
public class PanelGridWizardPage extends TagAttributesWizardPage {
	private Button chbHeader = null;
	private Button chbFooter = null;
	IDropWizardModel fWizardModel;

	public void createControl(Composite parent) {
		Composite maincomposite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		maincomposite.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		maincomposite.setLayoutData(data);
		
		Group topGroup = new Group(maincomposite, SWT.NONE);		
		topGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		topGroup.setLayoutData(data);
		topGroup.setText("Tag Options");
		showOptions(topGroup);		
		
		Group bottomGroup = new Group(maincomposite, SWT.NONE);
		bottomGroup.setLayout(layout);
		data = new GridData(GridData.FILL_BOTH);
		bottomGroup.setLayoutData(data);
		bottomGroup.setText("Tag Attributes");
		showAttributes(bottomGroup);
		
		setControl(maincomposite);
		getSpecificWizard().getWizardModel().addPropertyChangeListener(IDropWizardModel.TAG_PROPOSAL,this);
		updateTitle();
		runValidation();
	}
	
	public void showOptions(Group gr){
		Composite comp =  new Composite(gr, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1; 
		
		comp.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		comp.setLayoutData(data);		
		
		chbHeader = new Button(comp, SWT.CHECK);
		chbHeader.setText("Add header to the panelGrid");
		chbFooter = new Button(comp, SWT.CHECK);
		chbFooter.setText("Add footer to the panelGrid");		
		
		comp.setVisible(true);		
		setControl(comp);
		updateTitle();
		
	}


	public boolean isOptionHeaderChecked() {
		return chbHeader.getSelection();
	}
	
	public boolean isOptionFooterChecked() {
		return chbFooter.getSelection();
	}
	
	protected void updateTitle() {
		TagProposal tagProposal = (TagProposal)getDropWizardModel().getTagProposal();
		StringBuffer titleText = new StringBuffer();
		titleText.append("<"); //$NON-NLS-1$
		if(!TagProposal.EMPTY_PREFIX.equals(tagProposal.getPrefix())) {
			titleText
				.append(tagProposal.getPrefix())
				.append(":"); //$NON-NLS-1$
		}
		titleText
			.append(tagProposal.getName())
			.append(">");			 //$NON-NLS-1$
		setTitle(titleText.toString());
	}
	
	public boolean isColomns() {

		fWizardModel = getSpecificWizard().getWizardModel();
		AttributeDescriptorValue[] value = fWizardModel
				.getAttributeValueDescriptors();
		int i = 0;
		while (i < value.length) {

			if (value[i].getName().equals("columns")) //$NON-NLS-1$
				if ((String) value[0].getValue() != null
						&& ((String) value[0].getValue()).trim().length() > 0)
					return true;		
			i++;
		}
		return false;
	}
	
}
