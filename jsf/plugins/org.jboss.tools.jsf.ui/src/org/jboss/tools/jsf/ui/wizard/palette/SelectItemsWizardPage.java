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

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.ui.editors.dnd.*;

/**
 *  @author erick 
 */

public class SelectItemsWizardPage extends TagAttributesWizardPage {
	
	final XEntityData data;
	XAttributeSupport support = new XAttributeSupport();
		
	public SelectItemsWizardPage(){
		data = XEntityDataImpl.create(new String[][] {
				{ "JSFSelectItemsWizard", "yes" }, { "select items", "no" }});			 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
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
		layout.numColumns = 3;
		comp.setLayout(layout);		
		
		GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gdata);
		
		support.init(PreferenceModelUtilities.getPreferenceModel().getRoot(), data);
		
		Control c = support.createControl(comp);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		c.setLayoutData(data);
		
		comp.setVisible(true);		
		setControl(comp);
		updateTitle();
		
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
	
	public String getText() {		
		 return data.getValue("select items");		  //$NON-NLS-1$
	}
	
}
