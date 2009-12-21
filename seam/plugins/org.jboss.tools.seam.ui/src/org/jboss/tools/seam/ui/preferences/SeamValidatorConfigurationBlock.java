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

package org.jboss.tools.seam.ui.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * Find the Severity Framework instruction in SeamPreferences
 * To modify section descriptions:
 * 1) If new option is to be added to existing description,
 *    add array of two String objects, where first is the preference name 
 *    defined in SeamPreferences, and second is label defined in 
 *    SeamPreferencesMessages (do not forget put property to SeamPreferencesMessages.properties
 *    and constant to SeamPreferencesMessages.java)
 *    
 * 2) If new section named A is to be created create constant
 *		private static SectionDescription SECTION_A = new SectionDescription(
 *			SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_a,
 *			new String[][]{
 *			}
 *		);
 *    create required constant and property in SeamPreferencesMessages, 
 *    and add SECTION_A to array ALL_SECTIONS.
 * 
 * @author Viacheslav Kabanovich
 */
public class SeamValidatorConfigurationBlock extends SeverityConfigurationBlock {
	private static final String SETTINGS_SECTION_NAME = SeamPreferencesMessages.SEAM_VALIDATOR_CONFIGURATION_BLOCK_SEAM_VALIDATOR_CONFIGURATION_BLOCK;

	private static SectionDescription SECTION_COMPONENT = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_component,
		new String[][]{
			{SeamPreferences.NONUNIQUE_COMPONENT_NAME, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_nonUniqueComponentName_label},
			{SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_statefulComponentDoesNotContainRemove_label},
			{SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_statefulComponentDoesNotContainDestroy_label},
			{SeamPreferences.STATEFUL_COMPONENT_WRONG_SCOPE, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_statefulComponentHasWrongScope_label},
			{SeamPreferences.UNKNOWN_COMPONENT_CLASS_NAME, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unknownComponentClassName_label},
			{SeamPreferences.UNKNOWN_COMPONENT_PROPERTY, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unknownComponentProperty_label}
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_ENTITY = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_entities,
		new String[][]{
			{SeamPreferences.ENTITY_COMPONENT_WRONG_SCOPE, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_entityComponentHasWrongScope_label}
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_LIFECYCLE = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_lifecycle,
		new String[][]{
			{SeamPreferences.DUPLICATE_REMOVE, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_duplicateRemove_label},
			{SeamPreferences.DUPLICATE_DESTROY, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_duplicateDestroy_label},
			{SeamPreferences.DUPLICATE_CREATE, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_duplicateCreate_label},
			{SeamPreferences.DUPLICATE_UNWRAP, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_duplicateUnwrap_label},
			{SeamPreferences.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_destroyMethodBelongsToStatelessSessionBean_label},
			{SeamPreferences.CREATE_DOESNT_BELONG_TO_COMPONENT, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_createDoesNotBelongToComponent_label},
			{SeamPreferences.UNWRAP_DOESNT_BELONG_TO_COMPONENT, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unwrapDoesNotBelongToComponent_label},
			{SeamPreferences.OBSERVER_DOESNT_BELONG_TO_COMPONENT, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_observerDoesNotBelongToComponent_label},
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_FACTORY = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_factory,
		new String[][]{
			{SeamPreferences.DUPLICATE_VARIABLE_NAME, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_duplicateVariableName_label},
			{SeamPreferences.UNKNOWN_FACTORY_NAME, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unknownFactoryName_label},
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_BIJECTION = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_bijection,
		new String[][]{
			{SeamPreferences.MULTIPLE_DATA_BINDER, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_multipleDataBinder_label},
			{SeamPreferences.UNKNOWN_DATA_MODEL, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unknownDataModel_label},
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_VARIABLE = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_variable,
		new String[][]{
			{SeamPreferences.UNKNOWN_VARIABLE_NAME, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_unknownVariableName_label},
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_SETTINGS = new SectionDescription(
		SeamPreferencesMessages.SeamValidatorConfigurationBlock_section_settings,
		new String[][]{
			{SeamPreferences.INVALID_PROJECT_SETTINGS, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_invalidSeamProjectSettings_label},
			{SeamPreferences.INVALID_XML_VERSION, SeamPreferencesMessages.SeamValidatorConfigurationBlock_pb_invalidXMLVersion_label}
		},
		SeamCorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_COMPONENT,
		SECTION_ENTITY,
		SECTION_LIFECYCLE, 
		SECTION_FACTORY,
		SECTION_BIJECTION, 
		SECTION_VARIABLE,
		SECTION_SETTINGS
	};

	//private PixelConverter fPixelConverter;

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
				keys.add(ALL_SECTIONS[i].options[j].key);
			}
		}
		return keys.toArray(new Key[0]);
	}

	public SeamValidatorConfigurationBlock(IStatusChangeListener context,
			IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return SeamPreferencesMessages.SeamValidatorConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return SeamCorePlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}
}