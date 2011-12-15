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
package org.jboss.tools.struts.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jboss.tools.common.model.ui.attribute.adapter.CheckListAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.CheckListEditor;
import org.jboss.tools.common.model.ui.attribute.editor.ExtendedFieldEditor;
import org.jboss.tools.common.model.ui.attribute.editor.IFieldEditor;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.common.model.ui.widgets.DefaultSettings;
import org.jboss.tools.struts.webprj.model.helpers.context.NewProjectWizardContext;

public class NewProjectAddTldPage extends WizardPage {
	public static final String PAGE_NAME = "WebPrjCreateStepTLD";

	private String lastStrutsVersion = "";
	private NewProjectWizardContext context;
	private CheckListAdapter checkListAdapter;
	private CheckListEditor checkListEditor;
	private Composite control;
//	private Control label;
//	private Control tree;
	
	StrutsUtils templates = new StrutsUtils();
		
	protected NewProjectAddTldPage(NewProjectWizardContext context) {
		super("Page 2");

		this.context = context;

		checkListAdapter = new CheckListAdapter();
		checkListAdapter.setTags(templates.getTldTemplates(lastStrutsVersion));
		checkListEditor = new CheckListEditor(DefaultSettings.getDefault());
		checkListEditor.setLabelText("TLDs");
		checkListEditor.setInput(checkListAdapter);
	}

	public void createControl(Composite parent)	{
		initializeDialogUnits(parent);
		//Control control = support.createControl(parent);
		control = new Composite(parent, SWT.NONE);
		//control.setBackground(new Color(null, 44, 77,100));
		GridLayout layout = new GridLayout();
		control.setLayout(layout);
		ExtendedFieldEditor fieldEditor = checkListEditor.getFieldEditor(parent);
		Control[] controls = ((IFieldEditor)fieldEditor).getControls(control);
		controls[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));			
		controls[1].setLayoutData(new GridData(GridData.FILL_BOTH));			
		setControl(control);
		initListeners();
		setErrorMessage(null);
		setMessage("Select tag library files you want to use in your project");
		setPageComplete(true);						
	}
	
	private void initListeners() {
		checkListAdapter.addValueChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					context.setTLDs(evt.getNewValue().toString());
				}
			}
		);
	}
		
	public void setVisible(boolean visible) {
		if (visible && !lastStrutsVersion.equals(context.getVersion())) {
			lastStrutsVersion = context.getVersion();			
			checkListAdapter.setTags(templates.getTldTemplates(lastStrutsVersion));
			String v = templates.getTldTemplateDefaultProperties(lastStrutsVersion).getProperty("tld_files");
			if(v != null) {
				checkListAdapter.setValue(v);
				context.setTLDs(v);
			}				 
		}
		super.setVisible(visible);
	}

}
