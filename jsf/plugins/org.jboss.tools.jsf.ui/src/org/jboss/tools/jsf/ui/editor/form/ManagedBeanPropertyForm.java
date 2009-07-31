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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.editor.ExtendedFieldEditor;
import org.jboss.tools.common.model.ui.attribute.editor.IPropertyFieldEditor;
import org.jboss.tools.common.model.ui.attribute.editor.PropertyEditor;
import org.jboss.tools.common.model.ui.attribute.editor.StringButtonFieldEditorEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.FacesConfigEditorMessages;
import org.jboss.tools.common.model.ui.forms.ExpandableForm;
import org.jboss.tools.common.model.ui.widgets.BorderedControl;
import org.jboss.tools.common.model.ui.widgets.IWidgetSettings;
import org.jboss.tools.common.model.ui.widgets.WhiteSettings;

/**
 * @author Aleksey
 */
public class ManagedBeanPropertyForm extends ExpandableForm {

	private XAttributeSupport support;
	private XModelObject xmo;
	private IWidgetSettings settings = new WhiteSettings();
	
	private Composite switchComposite;
	private Control anyLabel;

	private static final String PROPERTY_NAME = "property-name"; //$NON-NLS-1$
	private static final String PROPERTY_CLASS = "property-class"; //$NON-NLS-1$
	private static final String VALUE_KIND = "value-kind"; //$NON-NLS-1$
	
	/*	
	0:element type
	1:description
	2:display-name
	3:small-icon
	4:large-icon
	5:property-name
	6:property-class
	7:value-kind
	8:value
	9:id
	10:comment
	*/		

	private static final String VALUE_NAME = "value"; //$NON-NLS-1$
	private static final String NULL_VALUE_NAME = "null-value"; //$NON-NLS-1$
	private static final String LIST_ENTRIES_NAME = "list-entries"; //$NON-NLS-1$
	private static final String MAP_ENTRIES_NAME = "map-entries"; //$NON-NLS-1$
	private static final String[] TAGS = new String[] {VALUE_NAME, NULL_VALUE_NAME, LIST_ENTRIES_NAME, MAP_ENTRIES_NAME};
	
	private static final String VALUE_ACTION = "CreateActions.ChangeToValue"; //$NON-NLS-1$
	private static final String NULL_VALUE_ACTION = "CreateActions.AddNullValue"; //$NON-NLS-1$
	private static final String LIST_VALUE_ACTION = "CreateActions.ChangeToList"; //$NON-NLS-1$
	private static final String MAP_VALUE_ACTION = "CreateActions.ChangeToMap"; //$NON-NLS-1$
	private static final String[] ACTIONS = new String[] {VALUE_ACTION, NULL_VALUE_ACTION, LIST_VALUE_ACTION, MAP_VALUE_ACTION};
	
	private boolean localChange = Boolean.FALSE.booleanValue();
	
	private ComboField comboField = new ComboField();
	private ValueControl valueControl = new ValueControl();
	
	public ManagedBeanPropertyForm() {
		support = new XAttributeSupport(settings);
		this.setCollapsable(Boolean.TRUE.booleanValue());
	}

	public void dispose() {
		super.dispose();
		if (support!=null) support.dispose();
		support = null;
		if (switchComposite!=null && !switchComposite.isDisposed()) switchComposite.dispose();
		switchComposite = null;
		if (anyLabel!=null && !anyLabel.isDisposed()) anyLabel.dispose();
		anyLabel = null;
		if (comboField!=null) comboField.dispose();
		comboField = null;
		if (valueControl!=null) valueControl.dispose();
		valueControl = null;
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

		String description = FacesConfigEditorMessages.MANAGEDBEANPROPERTYFORM_DESCRIPTION;
		if(description!=null && description.length()>0) {
			Label label = new Label(composite, SWT.WRAP);
			settings.setupControl(label);
			label.setText(description);
			gd = new GridData();
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		if(xmo == null) return composite;
		
		List editors = support.getEditorList();
		Iterator i = editors.iterator();
		while (i.hasNext()) {
			PropertyEditor propertyEditor = (PropertyEditor)i.next();
			if (PROPERTY_NAME.equals(propertyEditor.getAttributeName())) {
				Control[] controls = support.fillComposite(composite, propertyEditor, null);
				if (controls != null) anyLabel = controls[0];
			}
			if (PROPERTY_CLASS.equals(propertyEditor.getAttributeName())) {
				IPropertyFieldEditor wraper = (IPropertyFieldEditor)propertyEditor.getFieldEditor(composite);
				ExtendedFieldEditor fe = (ExtendedFieldEditor)wraper;
				fe.setLabelText(propertyEditor.getLabelText());
				wraper.setPropertyEditor(propertyEditor);
				fe.fillIntoGrid(composite, 2);
				fe.setEnabled(xmo.isAttributeEditable(propertyEditor.getAttributeName()));
				support.registerFieldEditor(propertyEditor.getAttributeName(), (ExtendedFieldEditor)wraper);
				anyLabel = fe.getLabelComposite(composite);
			}
		}

		comboField.createControls(composite);

		switchComposite = new Composite(composite, SWT.NONE);
		switchComposite.setBackground(composite.getBackground());
		layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		switchComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		switchComposite.setLayoutData(gd);
		
		switchComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.Resize) {
					if (valueControl.getControl()!=null) {
						GridData gd = new GridData();
						gd.widthHint = anyLabel.getSize().x; 
						valueControl.setLabelLayoutData(gd);
					}
				}
			}
		});
		
		valueControl.setParent(switchComposite);

		int index = getSelectedIndex();
		if (index!=-1) {
			valueControl.activate();
			gd = new GridData(GridData.FILL_HORIZONTAL);
			if (valueControl.getControl() != null) valueControl.getControl().setLayoutData(gd);
			
		}
		editors = support.getEditorList();
		i = editors.iterator();

		return composite;
	}
	
	private String getSelectedValueKind() {
		if (xmo!=null) {
			int index = getIndex(TAGS,xmo.getAttributeValue(VALUE_KIND));
			if (index==-1) return ""; //$NON-NLS-1$
			return TAGS[index];
		}
		return ""; //$NON-NLS-1$
	}
	
	public void update() {
		if(support == null) return;
		support.load();
		comboField._update();
		valueControl.update();
	}
	
	private int getIndex(String[] array, String object) {
		for (int i=0;i<array.length;++i) if (object.equals(array[i])) return i;
		return -1;
	}
	
	private int getSelectedIndex() {
		return getIndex(TAGS, getSelectedValueKind());
	}
	
	private void doComboModifyText() {
		localChange = Boolean.TRUE.booleanValue();
		int index = Arrays.asList(TAGS).indexOf(comboField.combo.getText());
		XActionInvoker.invoke(ACTIONS[index], xmo, null, new Properties());
		comboField.combo.setText(getSelectedValueKind());
		localChange = Boolean.FALSE.booleanValue();
		
		if (index!=-1) {
			((ValueControl)valueControl).update();
			
			valueControl.activate();
			if (valueControl.getControl() != null) {
				valueControl.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		
	}

	public void initialize(Object model) {
		setHeadingText(FacesConfigEditorMessages.MANAGEDBEANPROPERTYFORM_HEADER);
		xmo = ((XModelObject)model);
		if(xmo == null) {
			JsfUiPlugin.getPluginLog().logInfo( "Error to create form "+FacesConfigEditorMessages.MANAGEDBEANPROPERTYFORM_HEADER+". Model object cannot be null.", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		model = xmo.getModel();
		support.init(xmo);
		valueControl.init(xmo); 
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.support!=null) {
			support.setEnabled(enabled);
			support.updateEnablementByModelObject();
			comboField._updateEnablement();
		}
	}
	
	class ComboField {
		private Label vklabel;
		private Combo combo;
		
		public void createControls(Composite composite) { 
			vklabel = new Label(composite, SWT.NONE);
			vklabel.setText("Value Kind:"); //$NON-NLS-1$
			vklabel.setBackground(composite.getBackground());
			settings.setupControl(vklabel);

			BorderedControl border = new BorderedControl(composite, SWT.NONE, settings.getBorder("Combo.Border"));  //$NON-NLS-1$
			combo = new Combo(border, SWT.FLAT | SWT.READ_ONLY);
			combo.setItems(TAGS);
			combo.setText(getSelectedValueKind());
			combo.setBackground(settings.getColor("Combo.Background")); //$NON-NLS-1$
			combo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (!localChange) doComboModifyText();
				}
			});
		
			border.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		public void dispose() {
			if (vklabel!=null && !vklabel.isDisposed()) vklabel.dispose();
			vklabel = null;
			if (combo!=null && !combo.isDisposed()) combo.dispose();
			combo = null;
		}
		
		public void _update() {
			if(combo != null && !combo.isDisposed()) {
				localChange = true;
				combo.setText(getSelectedValueKind());
				localChange = false;
			}
		}

		public void _updateEnablement() {
			if(combo != null && !combo.isDisposed()) {
				boolean e = xmo != null && xmo.isAttributeEditable("value-kind"); //$NON-NLS-1$
				combo.setEnabled(e);
				vklabel.setEnabled(e);
			}
		}
	
	}

	/*TRIAL_JSF_CLASS*/
	
	class ValueControl {
		protected Composite parent;
		protected Control control;
		private XAttributeSupport support;
		private static final String ATTRIBUTE_NAME = "value"; //$NON-NLS-1$
		private Control label;
		protected BF sb;
		
		public ValueControl() {}

		public Control getControl() {
			return control;
		}

		public void activate() {
			if (control == null) {
				control = createControl(parent);
			}
		}
		
		public void deactivate() {
			if (control!=null && !control.isDisposed()) {
				control.setVisible(Boolean.FALSE.booleanValue());
				control.dispose();
				control = null;
			}
		}
		
		public void dispose() {
			if (control!=null && !control.isDisposed()) deactivate();
			this.parent = null;
			if (sb!=null) sb.dispose();
			sb = null;
		}

		public void setParent(Composite parent) {
			this.parent = parent;
		};
		
		public Control createControl(Composite parent) {
			if (control == null) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setBackground(parent.getBackground());
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = 5;
				layout.verticalSpacing = 5;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				layout.numColumns = 2;
				composite.setLayout(layout);

				List editors = support.getEditorList();
				Iterator i = editors.iterator();
				while (i.hasNext()) {
					PropertyEditor propertyEditor = (PropertyEditor)i.next();
					if (ATTRIBUTE_NAME.equals(propertyEditor.getAttributeName())) {
						sb = new BF();
						support.fillComposite(composite, propertyEditor, sb);
						sb.setChangeButtonText("View/Edit"); //$NON-NLS-1$
						label = sb.getLabelComposite();
					}
				}

				control = composite;

				support.updateEnablementByModelObject();
			}
			return control;
		}
		
		public void init(Object model) {
			support = new XAttributeSupport(settings);
			XModelObject object = (XModelObject)model;
			String entity = object.getModelEntity().getName();
			XEntityData ed = XEntityDataImpl.create(new String[][]{{entity}, {"value"}}); //$NON-NLS-1$
			support.init(object, ed, true);
		}
		
		public void update() {
			if(control == null || control.isDisposed()) return;
			support.load();
			support.updateEnablementByModelObject();
		}
		
		public void setLabelLayoutData(Object layoutData) {
			if (label!=null) {
				 label.setLayoutData(layoutData);
				 label.getParent().layout(true);
			} 
		}
		
		class BF extends StringButtonFieldEditorEx {
			public BF() {
				super(support.getSettings());
			}
			protected String changePressed() {
				String vk = xmo.getAttributeValue("value-kind"); //$NON-NLS-1$
				if("null-value".equals(vk)) { //$NON-NLS-1$
					return null;
				} else if("map-entries".equals(vk) || "list-entries".equals(vk)) { //$NON-NLS-1$ //$NON-NLS-2$
					XModelObject c = xmo.getChildByPath("Entries"); //$NON-NLS-1$
					FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
					return null;
				}
				return super.changePressed();
			}
			public void setEnabled(boolean enabled){
				if(xmo == null || !xmo.isObjectEditable()) {
					super.setEnabled(enabled);
				} else {
					String vk = xmo.getAttributeValue("value-kind"); //$NON-NLS-1$
					if (getTextControl() != null) {
						getTextControl().setEnabled(enabled);
					}
					if (getChangeControl() != null) {
						getChangeControl().setEnabled(!"null-value".equals(vk)); //$NON-NLS-1$
					}
					if (getLabelControl() != null) {
						getLabelControl().setEnabled(!"null-value".equals(vk)); //$NON-NLS-1$
					}
				}
			}
		}
	}

	public boolean doGlobalAction(String actionId) {
		return support.doGlobalAction(actionId);
	}

}
