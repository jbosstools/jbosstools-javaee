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

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kazakov
 */
public class JSFSeverityPreferencesMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.ui.preferences.JSFSeverityPreferencesMessages"; //$NON-NLS-1$

	public static String JSF_VALIDATION_CONFIGURATION_BLOCK_JSF_VALIDATION_CONFIGURATION_BLOCK;
	public static String JSF_VALIDATION_PREFERENCE_PAGE_JSF_VALIDATOR;

	//Validation Preference page
	public static String JSFValidationConfigurationBlock_common_description;
	
	//Expression Language
	public static String JSFValidationConfigurationBlock_section_composite_components;
	public static String JSFValidationConfigurationBlock_pb_unknownComponent_label;
	public static String JSFValidationConfigurationBlock_pb_unknownAttribute_label;

	//Faces Config
	public static String JSFValidationConfigurationBlock_section_faces_config;
	////Context Param
	public static String JSFValidationConfigurationBlock_section_context_param;
	public static String JSFValidationConfigurationBlock_pb_invalidConfigFiles_label;
	////Application
	public static String JSFValidationConfigurationBlock_section_application;
	public static String JSFValidationConfigurationBlock_pb_invalidActionListener_label;
	public static String JSFValidationConfigurationBlock_pb_invalidNavigationHandler_label;
	public static String JSFValidationConfigurationBlock_pb_invalidPropertyResolver_label;
	public static String JSFValidationConfigurationBlock_pb_invalidStateManager_label;
	public static String JSFValidationConfigurationBlock_pb_invalidVariableResolver_label;
	////Component
	public static String JSFValidationConfigurationBlock_section_component;
	public static String JSFValidationConfigurationBlock_pb_invalidComponentClass_label;
	////Converter
	public static String JSFValidationConfigurationBlock_section_converter;
	public static String JSFValidationConfigurationBlock_pb_invalidConverterClass_label;
	public static String JSFValidationConfigurationBlock_pb_invalidConverterForClass_label;
	////Factory
	public static String JSFValidationConfigurationBlock_section_factory;
	public static String JSFValidationConfigurationBlock_pb_invalidApplicationFactory_label;
	public static String JSFValidationConfigurationBlock_pb_invalidFacesContextFactory_label;
	public static String JSFValidationConfigurationBlock_pb_invalidLifecycleFactory_label;
	public static String JSFValidationConfigurationBlock_pb_invalidRenderKitFactory_label;
	////List & Map Entries
	public static String JSFValidationConfigurationBlock_section_entries;
	public static String JSFValidationConfigurationBlock_pb_invalidKeyClass_label;
	public static String JSFValidationConfigurationBlock_pb_invalidValueClass_label;
	////Managed & Referenced Bean
	public static String JSFValidationConfigurationBlock_section_bean;
	public static String JSFValidationConfigurationBlock_pb_invalidBeanClass_label;
	public static String JSFValidationConfigurationBlock_pb_invalidPropertyClass_label;
	////Navigation Rules
	public static String JSFValidationConfigurationBlock_section_navigation;
	public static String JSFValidationConfigurationBlock_pb_invalidFromViewId_label;
	public static String JSFValidationConfigurationBlock_pb_invalidToViewId_label;
	////Phase Listener
	public static String JSFValidationConfigurationBlock_section_phase_listener;
	public static String JSFValidationConfigurationBlock_pb_invalidPhaseListener_label;
	////Renderers
	public static String JSFValidationConfigurationBlock_section_renderers;
	public static String JSFValidationConfigurationBlock_pb_invalidRenderKitClass_label;
	public static String JSFValidationConfigurationBlock_pb_invalidRendererClass_label;
	////Validation
	public static String JSFValidationConfigurationBlock_section_validator;
	public static String JSFValidationConfigurationBlock_pb_invalidValidatorClass_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFSeverityPreferencesMessages.class);
	}
}