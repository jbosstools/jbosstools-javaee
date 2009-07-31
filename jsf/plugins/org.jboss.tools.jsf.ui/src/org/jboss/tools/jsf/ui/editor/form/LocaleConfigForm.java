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
package org.jboss.tools.jsf.ui.editor.form;

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.XChildrenTableStructuredAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.IFieldEditor;
import org.jboss.tools.common.model.ui.attribute.editor.IPropertyEditor;
import org.jboss.tools.common.model.ui.attribute.editor.TableStructuredEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.FacesConfigEditorMessages;
import org.jboss.tools.common.model.ui.forms.ExpandableForm;
import org.jboss.tools.common.model.ui.widgets.IWidgetSettings;
import org.jboss.tools.common.model.ui.widgets.WhiteSettings;

/**
 * @author Igels
 *
 */
public class LocaleConfigForm extends ExpandableForm {

	private XAttributeSupport support;
	private XModelObject xmo;

	private TableStructuredEditor tableEditor;
	private XChildrenTableStructuredAdapter tableAdapter;

	private IPropertyEditor defaultLocale;

	private IWidgetSettings settings = new WhiteSettings();

	public LocaleConfigForm() {
		support = new XAttributeSupport(settings);
		this.setCollapsable(Boolean.TRUE.booleanValue());
	}

	public void dispose() {
		super.dispose();
		if (support!=null) support.dispose();
		support = null;
		if (tableEditor!=null) tableEditor.dispose();
		tableEditor = null;
		if (tableAdapter!=null) tableAdapter.dispose();
		tableAdapter = null;
	}
	
	protected Control createClientArea(Composite parent, IWidgetSettings settings) {
		Composite composite = new Composite(parent, SWT.NONE);
		settings.setupControl(composite);
		GridLayout layout = new GridLayout(2, Boolean.FALSE.booleanValue());

		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		composite.setLayout(layout);
		Control[] control;
		GridData gd;

		String description = FacesConfigEditorMessages.LOCALECONFIGFORM_DESCRIPTION;
		if(description!=null && description.length()>0) {
			Label label = new Label(composite, SWT.WRAP);
			settings.setupControl(label);
			label.setText(description);
			gd = new GridData();
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		if(xmo == null) return composite;
		
		if(defaultLocale!=null) {
			FieldEditor f = defaultLocale.getFieldEditor(composite);
			control = ((IFieldEditor)f).getControls(composite);

			gd = new GridData();
			control[0].setLayoutData(gd);

			gd = new GridData(GridData.FILL_HORIZONTAL);
			control[1].setLayoutData(gd);
			support.registerFieldEditor(defaultLocale.getAttributeName(), f);
		} 

		Label label = new Label(composite, SWT.WRAP);
		settings.setupControl(label);
		label.setText(FacesConfigEditorMessages.LOCALECONFIGFORM_SUPPORTEDLOCAL_TITLE);
		gd = new GridData();
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		control = ((IFieldEditor)tableEditor.getFieldEditor(composite)).getControls(composite);

		control[0].dispose(); // cannot show label

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		control[1].setLayoutData(gd);

		return composite;
	}

	public void initialize(Object model) {
		this.setHeadingText(FacesConfigEditorMessages.LOCALECONFIGFORM_HEADER);
		XModelObject current = (XModelObject)model;
		if(current.getFileType() == XModelObject.FILE) {
			this.xmo = current.getChildByPath("application/Locale Config"); //$NON-NLS-1$
		} else {
			this.xmo = current.getChildByPath("Locale Config"); //$NON-NLS-1$
		}
		if(xmo == null) {
			JsfUiPlugin.getPluginLog().logInfo("Error to create form "+FacesConfigEditorMessages.LOCALECONFIGFORM_HEADER+". Model object cannot be null.", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		this.model = xmo.getModel();
		this.support.init(xmo);
		this.support.setAutoStore(Boolean.TRUE.booleanValue());
		/*TRIAL_JSF*/
		defaultLocale = support.getPropertyEditorByName("default-locale"); //$NON-NLS-1$

		this.tableAdapter = new XChildrenTableStructuredAdapter();
		this.tableAdapter.setShownEntities(new String[]{"JSFSupportedLocale"}); //$NON-NLS-1$

		this.tableAdapter.getActionMapping().clear();

		this.tableAdapter.getActionMapping().put(TableStructuredEditor.ADD_ACTION, "CreateActions.AddSupportedLocale"); //$NON-NLS-1$
		this.tableAdapter.getActionMapping().put(TableStructuredEditor.REMOVE_ACTION, "DeleteActions.Delete"); //$NON-NLS-1$
		this.tableAdapter.getActionMapping().put(TableStructuredEditor.EDIT_ACTION, "Properties.Properties"); //$NON-NLS-1$
		this.tableAdapter.getActionMapping().put(TableStructuredEditor.UP_ACTION, "%internal%"); //$NON-NLS-1$
		this.tableAdapter.getActionMapping().put(TableStructuredEditor.DOWN_ACTION, "%internal%"); //$NON-NLS-1$

		this.tableAdapter.setShownProperties(new String[] {"supported-locale"}); //$NON-NLS-1$
		this.tableAdapter.setColumnLabels(new String[] {FacesConfigEditorMessages.LOCALECONFIGFORM_SUPPORTEDLOCAL_COLUMN_LABEL});
		this.tableAdapter.setWidths(new int[] {100});
		this.tableAdapter.setModelObject(xmo);
		
		this.tableEditor = new TableStructuredEditor(settings);
		this.tableEditor.setLabelText(""); //$NON-NLS-1$
		this.tableEditor.setInput(this.tableAdapter); 

	}
	private long timeStamp = -1;
	
	public void update() {
		long ts = (xmo == null) ? -1 : xmo.getTimeStamp();
		if(ts == timeStamp) return;
		timeStamp = ts;
		if(support != null) {
			support.load();
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.support!=null) {
			support.setEnabled(enabled);
		}
	}

	public boolean doGlobalAction(String actionId) {
		return support.doGlobalAction(actionId);
	}
}