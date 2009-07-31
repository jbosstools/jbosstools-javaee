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
import org.jboss.tools.common.model.ui.attribute.editor.JavaHyperlinkLineFieldEditor;
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
public class ApplicationConfigForm extends ExpandableForm {

	private XAttributeSupport support;
	private XModelObject xmo;
	
	private ChildTable messageBundleTable;
	private ChildTable resourceBundleTable;

	private IWidgetSettings settings = new WhiteSettings();

	public ApplicationConfigForm() {
		support = new XAttributeSupport(settings);
		this.setCollapsable(Boolean.TRUE.booleanValue());
	}

	public void dispose() {
		super.dispose();
		if (support!=null) support.dispose();
		support = null;
		if(messageBundleTable != null) {
			messageBundleTable.dispose();
			messageBundleTable = null;
		}
		if(resourceBundleTable != null) {
			resourceBundleTable.dispose();
			resourceBundleTable = null;
		}
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
		
		GridData gd;

		String description = FacesConfigEditorMessages.APPLICATIONCONFIGFORM_DESCRIPTION;
		if(description!=null && description.length()>0) {
			Label label = new Label(composite, SWT.WRAP);
			settings.setupControl(label);
			label.setText(description);
			gd = new GridData();
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		String[] attributes = new String[]{"action-listener", "navigation-handler", "view-handler", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"state-manager"}; //$NON-NLS-1$
		
		if(xmo == null) return composite;

		for (int i = 0; i < attributes.length; i++) {
			IPropertyEditor editor = support.getPropertyEditorByName(attributes[i]);
			putFieldEditorInToComposit(composite, editor);
		}

		IPropertyEditor editor = support.getPropertyEditorByName("default-render-kit-id"); //$NON-NLS-1$
		FieldEditor f = editor.getFieldEditor(composite);
		f.fillIntoGrid(composite, 2);
		support.registerFieldEditor(editor.getAttributeName(), f);
		
		if(messageBundleTable != null) messageBundleTable.fill(composite);
		if(resourceBundleTable != null) resourceBundleTable.fill(composite);

		return composite;
	}

	private void putFieldEditorInToComposit(Composite composite, IPropertyEditor propertyEditor) {
		if(propertyEditor!=null) {
			JavaHyperlinkLineFieldEditor sb = new JavaHyperlinkLineFieldEditor(settings);       
			sb.setLabelText(propertyEditor.getLabelText());
			sb.setPropertyEditor(propertyEditor);
			sb.fillIntoGrid(composite, 2);
			support.registerFieldEditor(propertyEditor.getAttributeName(), sb);
		}
	}

	public void initialize(Object model) {
		this.setHeadingText(FacesConfigEditorMessages.APPLICATIONCONFIGFORM_HEADER);
		if(model == null) {
			JsfUiPlugin.getPluginLog().logInfo("Error to create form "+FacesConfigEditorMessages.APPLICATIONCONFIGFORM_HEADER +". Model object cannot be null.", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		this.xmo = ((XModelObject)model).getChildByPath("application"); //$NON-NLS-1$
		if(xmo == null) {
			JsfUiPlugin.getPluginLog().logInfo("Error to create form "+FacesConfigEditorMessages.APPLICATIONCONFIGFORM_HEADER+". Model object cannot be null.", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		this.support.init(xmo);
		this.support.setAutoStore(Boolean.TRUE.booleanValue());

		messageBundleTable = new ChildTable();
		messageBundleTable.create(
				"JSFMessageBundle",  //$NON-NLS-1$
				new String[]{"message-bundle"},  //$NON-NLS-1$
				new String[]{FacesConfigEditorMessages.APPLICATIONCONFIGFORM_MESSAGEBUNDLE_COLUMN_LABEL},
				new int[]{100},
				"CreateActions.AddMessageBundle"); //$NON-NLS-1$
		if(xmo.getModelEntity().getChild("JSFResourceBundle") != null) { //$NON-NLS-1$
			resourceBundleTable = new ChildTable();
			resourceBundleTable.create(
					"JSFResourceBundle",  //$NON-NLS-1$
					new String[]{"base-name", "var"},  //$NON-NLS-1$ //$NON-NLS-2$
					new String[]{"Resource Bundle", "Var"}, //$NON-NLS-1$ //$NON-NLS-2$
					new int[]{70, 30},
					"CreateActions.AddResourceBundle"); //$NON-NLS-1$
		}
		/*TRIAL_JSF*/
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.support!=null) {
			support.updateEnablementByModelObject();
		}
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
	/*TRIAL_JSF_CLASS*/

	public boolean doGlobalAction(String actionId) {
		return support.doGlobalAction(actionId);
	}
	
	class ChildTable {
		private TableStructuredEditor tableEditor;
		private XChildrenTableStructuredAdapter tableAdapter;
		
		void dispose() {
			if (tableEditor!=null) tableEditor.dispose();
			tableEditor = null;
			if (tableAdapter!=null) tableAdapter.dispose();
			tableAdapter = null;
		}
		
		public void create(String childEntity, String[] attributes, String[] attributeLabels, int[] widths, String createActionPath) {
			tableAdapter = new XChildrenTableStructuredAdapter();
			tableAdapter.setShownEntities(new String[]{childEntity});

			tableAdapter.getActionMapping().clear();

			tableAdapter.getActionMapping().put(TableStructuredEditor.ADD_ACTION, createActionPath);
			tableAdapter.getActionMapping().put(TableStructuredEditor.REMOVE_ACTION, "DeleteActions.Delete"); //$NON-NLS-1$
			tableAdapter.getActionMapping().put(TableStructuredEditor.EDIT_ACTION, "Properties.Properties"); //$NON-NLS-1$
			tableAdapter.getActionMapping().put(TableStructuredEditor.UP_ACTION, "%internal%"); //$NON-NLS-1$
			tableAdapter.getActionMapping().put(TableStructuredEditor.DOWN_ACTION, "%internal%"); //$NON-NLS-1$

			tableAdapter.setShownProperties(attributes);
			tableAdapter.setColumnLabels(attributeLabels);
			tableAdapter.setWidths(widths);
			tableAdapter.setModelObject(xmo);
			tableEditor = new TableStructuredEditor(settings);
			tableEditor.setLabelText(""); //$NON-NLS-1$
			tableEditor.setInput(this.tableAdapter); 
		}
		
		void fill(Composite composite) {
			Control[] control = ((IFieldEditor)tableEditor.getFieldEditor(composite)).getControls(composite);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			control[1].setLayoutData(gd);
		}
	}
}