/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.jsf.ui.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.model.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.common.model.ui.preferences.SeverityPreferencesMessages;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.preferences.JSFSeverityPreferences;

/**
 * Find the instruction to Framework for Severity preferences in SeverityConfigurationBlock.java
 * 
 * @author Viacheslav Kabanovich
 */
public class JSFValidatorConfigurationBlock extends SeverityConfigurationBlock {
	private static final String SETTINGS_SECTION_NAME = JSFSeverityPreferencesMessages.JSF_VALIDATOR_CONFIGURATION_BLOCK_JSF_VALIDATOR_CONFIGURATION_BLOCK;

	private Button recognizeVarsCheckBox;
	private Button revalidateUnresolvedElCheckBox;
	private Combo elVariablesCombo;
	private Combo elPropertiesCombo;

	private static SectionDescription SECTION_EL = new SectionDescription(
			JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_section_el,
		new String[][] {
			{JSFSeverityPreferences.EL_SYNTAX_ERROR, JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_elSyntaxError_label},
			{JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariableName_label},
			{JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariablePropertyName_label},
			{JSFSeverityPreferences.UNPAIRED_GETTER_OR_SETTER, JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unpairedGetterOrSetter_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_EL,
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
				keys.add(ALL_SECTIONS[i].options[j].key);
			}
		}
		keys.add(getKey(JSFModelPlugin.PLUGIN_ID, JSFSeverityPreferences.CHECK_VARS));
		keys.add(getKey(JSFModelPlugin.PLUGIN_ID, JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL));
		return keys.toArray(new Key[0]);
	}

	public JSFValidatorConfigurationBlock(IStatusChangeListener context,
			IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected Composite createStyleTabContent(Composite folder) {
		String[] errorWarningIgnore = new String[] {ERROR, WARNING, IGNORE};
		String[] enableDisableValues= new String[] {ENABLED, DISABLED};

		String[] errorWarningIgnoreLabels = new String[] {
				SeverityPreferencesMessages.VALIDATOR_CONFIGURATION_BLOCK_ERROR,  
				SeverityPreferencesMessages.VALIDATOR_CONFIGURATION_BLOCK_WARNING, 
				SeverityPreferencesMessages.VALIDATOR_CONFIGURATION_BLOCK_IGNORE
		};

		int nColumns = 3;

		final ScrolledPageContent sc1 = new ScrolledPageContent(folder);

		Composite composite = sc1.getBody();
		GridLayout layout= new GridLayout(nColumns, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);

		Label description= new Label(composite, SWT.LEFT | SWT.WRAP);
		description.setFont(description.getFont());
		description.setText(JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_common_description); 
		description.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, nColumns - 1, 1));

		int defaultIndent = 0;

		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			SectionDescription section = ALL_SECTIONS[i];
			String label = section.label; 
			ExpandableComposite excomposite = createStyleSection(composite, label, nColumns);

			Composite inner = new Composite(excomposite, SWT.NONE);
			inner.setFont(composite.getFont());
			inner.setLayout(new GridLayout(nColumns, false));
			excomposite.setClient(inner);

			for (int j = 0; j < section.options.length; j++) {
				OptionDescription option = section.options[j];
				label = option.label;
				Combo combo = addComboBox(inner, label, option.key, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
				if(option.label == JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariableName_label) {
					elVariablesCombo = combo;
					combo.addSelectionListener(new SelectionListener(){
						public void widgetDefaultSelected(SelectionEvent e) {
							updateELCombox();
						}
						public void widgetSelected(SelectionEvent e) {
							updateELCombox();
						}
					});
				} else if(option.label == JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_unknownElVariablePropertyName_label) {
					elPropertiesCombo = combo;
					combo.addSelectionListener(new SelectionListener(){
						public void widgetDefaultSelected(SelectionEvent e) {
							updateELCombox();
						}
						public void widgetSelected(SelectionEvent e) {
							updateELCombox();
						}
					});
				}
			}

			if(section==SECTION_EL) {
				label = JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_checkVars_label; 
				recognizeVarsCheckBox = addCheckBox(inner, label, getKey(JSFModelPlugin.PLUGIN_ID, JSFSeverityPreferences.CHECK_VARS), enableDisableValues, defaultIndent);

				label = JSFSeverityPreferencesMessages.JSFValidatorConfigurationBlock_pb_revalidateUnresolvedEl_label; 
				revalidateUnresolvedElCheckBox = addCheckBox(inner, label, getKey(JSFModelPlugin.PLUGIN_ID, JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL), enableDisableValues, defaultIndent);
			}
		}

		IDialogSettings section = JSFModelPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(section);

		updateELCombox();

		return sc1;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		updateELCombox();
	}

	private void updateELCombox() {
		boolean enable = elPropertiesCombo.getSelectionIndex()!=2 || elVariablesCombo.getSelectionIndex()!=2;
		recognizeVarsCheckBox.setEnabled(enable);
		revalidateUnresolvedElCheckBox.setEnabled(enable);
	}
}