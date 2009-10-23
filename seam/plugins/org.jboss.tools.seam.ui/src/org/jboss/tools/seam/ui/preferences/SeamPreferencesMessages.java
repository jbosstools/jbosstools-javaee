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

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages"; //$NON-NLS-1$

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_SEAM_PROJECT;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_EJB_PROJECT;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_DEPLOYMENT;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_DEPLOY_TYPE;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_VIEW;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_MODEL;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_ACTION;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_SOURCE_FOLDER;

	public static String SEAM_SETTINGS_PREFERENCES_PAGE_PACKAGE;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT;

	public static String SEAM_PREFERENCE_PAGE_SEAM_RUNTIMES;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_IS_NOT_SELECTED;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_DOES_NOT_EXIST;
	
	public static String SEAM_SETTINGS_PREFERENCE_PAGE_WRONG_SEAM_VERSION;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_EJB_PROJECT_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MAIN_SEAM_PROJECT_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MAIN_SEAM_PROJECT_IS_EMPTY;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MODEL_SOURCE_FOLDER_IS_EMPTY;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_ACTION_SOURCE_FOLDER_IS_EMPTY;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_SOURCE_FOLDER_IS_EMPTY;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_VIEW_FOLDER_IS_EMPTY;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_VIEW_FOLDER_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MODEL_SOURCE_FOLDER_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_PACKAGE_IS_BLANK;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MODEL_PACKAGE_IS_NOT_VALID;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_MODEL_PACKAGE_HAS_WARNING;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_ACTION_SOURCE_FOLDER_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_ACTION_PACKAGE_IS_NOT_VALID;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_ACTION_PACKAGE_HAS_WARNING;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_PROJECT_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_SOURCE_FOLDER_DOES_NOT_EXIST;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_PACKAGE_IS_NOT_VALID;

	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_PACKAGE_HAS_WARNING;

	public static String SEAM_VALIDATOR_CONFIGURATION_BLOCK_ERROR;

	public static String SEAM_VALIDATOR_CONFIGURATION_BLOCK_IGNORE;

	public static String SEAM_VALIDATOR_CONFIGURATION_BLOCK_SEAM_VALIDATOR_CONFIGURATION_BLOCK;

	public static String SEAM_VALIDATOR_CONFIGURATION_BLOCK_WARNING;

	public static String SEAM_VALIDATOR_PREFERENCE_PAGE_SEAM_VALIDATOR;
	
	public static String SEAM_SETTINGS_PREFERENCE_PAGE_CREATE_TEST;
	
	public static String SEAM_SETTINGS_PREFERENCES_PAGE_TEST;
	
	public static String SEAM_SETTINGS_PREFERENCE_PAGE_TEST_PROJECT;
	
	//Validator Preference page
	public static String SeamValidatorConfigurationBlock_common_description;
	
	public static String SeamValidatorConfigurationBlock_needsbuild_title;
	public static String SeamValidatorConfigurationBlock_needsfullbuild_message;
	public static String SeamValidatorConfigurationBlock_needsprojectbuild_message;

	//Section Components
	public static String SeamValidatorConfigurationBlock_section_component;
	public static String SeamValidatorConfigurationBlock_pb_nonUniqueComponentName_label;
	public static String SeamValidatorConfigurationBlock_pb_statefulComponentDoesNotContainRemove_label;
	public static String SeamValidatorConfigurationBlock_pb_statefulComponentDoesNotContainDestroy_label;
	public static String SeamValidatorConfigurationBlock_pb_statefulComponentHasWrongScope_label;
	public static String SeamValidatorConfigurationBlock_pb_unknownComponentClassName_label;
	public static String SeamValidatorConfigurationBlock_pb_unknownComponentProperty_label;

	//Section Entities
	public static String SeamValidatorConfigurationBlock_section_entities;
	public static String SeamValidatorConfigurationBlock_pb_entityComponentHasWrongScope_label;
	public static String SeamValidatorConfigurationBlock_pb_duplicateRemove_label;

	//Section Component life-cycle methods
	public static String SeamValidatorConfigurationBlock_section_lifecycle;
	public static String SeamValidatorConfigurationBlock_pb_duplicateDestroy_label;
	public static String SeamValidatorConfigurationBlock_pb_duplicateCreate_label;
	public static String SeamValidatorConfigurationBlock_pb_duplicateUnwrap_label;
	public static String SeamValidatorConfigurationBlock_pb_destroyMethodBelongsToStatelessSessionBean_label;
	public static String SeamValidatorConfigurationBlock_pb_createDoesNotBelongToComponent_label;
	public static String SeamValidatorConfigurationBlock_pb_unwrapDoesNotBelongToComponent_label;
	public static String SeamValidatorConfigurationBlock_pb_observerDoesNotBelongToComponent_label;
	
	//Section Factories
	public static String SeamValidatorConfigurationBlock_section_factory;
	public static String SeamValidatorConfigurationBlock_pb_duplicateVariableName_label;
	public static String SeamValidatorConfigurationBlock_pb_unknownFactoryName_label;
	
	//Section Bijections
	public static String SeamValidatorConfigurationBlock_section_bijection;
	public static String SeamValidatorConfigurationBlock_pb_multipleDataBinder_label;
	public static String SeamValidatorConfigurationBlock_pb_unknownDataModel_label;
	
	//Section Context variables
	public static String SeamValidatorConfigurationBlock_section_variable;
	public static String SeamValidatorConfigurationBlock_pb_unknownVariableName_label;

	//Invalid seam project settings
	public static String SeamValidatorConfigurationBlock_section_settings;
	public static String SeamValidatorConfigurationBlock_pb_invalidSeamProjectSettings_label;
	public static String SeamValidatorConfigurationBlock_pb_invalidXMLVersion_label;
	

	static {
		NLS.initializeMessages(BUNDLE_NAME, SeamPreferencesMessages.class);
	}
}