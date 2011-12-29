/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
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
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class JSFValidationConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = JSFSeverityPreferencesMessages.JSF_VALIDATION_CONFIGURATION_BLOCK_JSF_VALIDATION_CONFIGURATION_BLOCK;

	private static SectionDescription SECTION_COMPOSITE_COMPONENTS = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_composite_components,
		new String[][]{
			{JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_NAME, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_unknownComponent_label},
			{JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_unknownAttribute_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);

	//Faces Config
	
	private static SectionDescription SECTION_APPLICATION = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_application,
		new String[][]{
			{JSFSeverityPreferences.INVALID_ACTION_LISTENER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidActionListener_label},
			{JSFSeverityPreferences.INVALID_NAVIGATION_HANDLER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidNavigationHandler_label},
			{JSFSeverityPreferences.INVALID_PROPERTY_RESOLVER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidPropertyResolver_label},
			{JSFSeverityPreferences.INVALID_STATE_MANAGER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidStateManager_label},
			{JSFSeverityPreferences.INVALID_VARIABLE_RESOLVER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidVariableResolver_label},
			{JSFSeverityPreferences.INVALID_VIEW_HANDLER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidViewHandler_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_COMPONENT = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_component,
		new String[][]{
			{JSFSeverityPreferences.INVALID_COMPONENT_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidComponentClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_CONVERTER = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_converter,
		new String[][]{
			{JSFSeverityPreferences.INVALID_CONVERTER_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidConverterClass_label},
			{JSFSeverityPreferences.INVALID_CONVERTER_FOR_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidConverterForClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_FACTORY = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_factory,
		new String[][]{
			{JSFSeverityPreferences.INVALID_APPLICATION_FACTORY, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidApplicationFactory_label},
			{JSFSeverityPreferences.INVALID_FACES_CONTEXT_FACTORY, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidFacesContextFactory_label},
			{JSFSeverityPreferences.INVALID_LIFECYCLE_FACTORY, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidLifecycleFactory_label},
			{JSFSeverityPreferences.INVALID_RENDER_KIT_FACTORY, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidRenderKitFactory_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_ENTRIES = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_entries,
		new String[][]{
			{JSFSeverityPreferences.INVALID_KEY_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidKeyClass_label},
			{JSFSeverityPreferences.INVALID_VALUE_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidValueClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_BEANS = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_bean,
		new String[][]{
			{JSFSeverityPreferences.INVALID_BEAN_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidBeanClass_label},
			{JSFSeverityPreferences.INVALID_PROPERTY_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidPropertyClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_PHASE_LISTENER = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_phase_listener,
		new String[][]{
			{JSFSeverityPreferences.INVALID_PHASE_LISTENER, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidPhaseListener_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_RENDERERS = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_renderers,
		new String[][]{
			{JSFSeverityPreferences.INVALID_RENDER_KIT_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidRenderKitClass_label},
			{JSFSeverityPreferences.INVALID_RENDERER_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidRendererClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_VALIDATOR = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_validator,
		new String[][]{
			{JSFSeverityPreferences.INVALID_VALIDATOR_CLASS, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidValidatorClass_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_NAVIGATION = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_navigation,
		new String[][]{
			{JSFSeverityPreferences.INVALID_FROM_VIEW_ID, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidFromViewId_label},
			{JSFSeverityPreferences.INVALID_TO_VIEW_ID, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidToViewId_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	private static SectionDescription SECTION_WEB_XML = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_context_param,
		new String[][]{
			{JSFSeverityPreferences.INVALID_CONFIG_FILES, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_invalidConfigFiles_label},
		},
		JSFModelPlugin.PLUGIN_ID
	);
	

	private static SectionDescription SECTION_FACES_CONFIG = new SectionDescription(
		JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_faces_config,
		new SectionDescription[]{
			SECTION_WEB_XML,
			SECTION_APPLICATION,
			SECTION_COMPONENT,
			SECTION_CONVERTER,
			SECTION_FACTORY,
			SECTION_ENTRIES,
			SECTION_BEANS,
			SECTION_PHASE_LISTENER,
			SECTION_RENDERERS,
			SECTION_NAVIGATION,
			SECTION_VALIDATOR,
		},
		new String[0][],
		JSFModelPlugin.PLUGIN_ID
	);

	public static SectionDescription[] ALL_SECTIONS = new SectionDescription[] {
		SECTION_COMPOSITE_COMPONENTS,
		SECTION_FACES_CONFIG
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (SectionDescription s: ALL_SECTIONS) {
			s.collectKeys(keys);
		}
		keys.add(MAX_NUMBER_OF_PROBLEMS_KEY);
		keys.add(WRONG_BUILDER_ORDER_KEY);
		return keys.toArray(new Key[0]);
	}

		private static final Key MAX_NUMBER_OF_PROBLEMS_KEY = getKey(JSFModelPlugin.PLUGIN_ID, SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);

		@Override
		protected Key getMaxNumberOfProblemsKey() {
			return MAX_NUMBER_OF_PROBLEMS_KEY;
		}

		private static final Key WRONG_BUILDER_ORDER_KEY = getKey(JSFModelPlugin.PLUGIN_ID, SeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME);

		protected Key getWrongBuilderOrderKey() {
			return WRONG_BUILDER_ORDER_KEY;
		}

		public JSFValidationConfigurationBlock(IStatusChangeListener context,
				IProject project, IWorkbenchPreferenceContainer container) {
			super(context, project, getKeys(), container);
		}

		@Override
		protected SectionDescription[] getAllSections() {
			return ALL_SECTIONS;
		}

		@Override
		protected String getCommonDescription() {
			return JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_common_description;
		}

		@Override
		protected IDialogSettings getDialogSettings() {
			return JSFModelPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		}

		@Override
		protected String getQualifier() {
			return JSFModelPlugin.PLUGIN_ID;
		}
	}