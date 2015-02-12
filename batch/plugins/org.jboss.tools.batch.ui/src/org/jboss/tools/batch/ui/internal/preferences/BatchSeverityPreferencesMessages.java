/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchSeverityPreferencesMessages extends NLS {

	private static final String BUNDLE_NAME = BatchSeverityPreferencesMessages.class.getName();

	public static String BATCH_VALIDATION_CONFIGURATION_BLOCK_BATCH_VALIDATION_CONFIGURATION_BLOCK;
	public static String BATCH_VALIDATION_PREFERENCE_PAGE_BATCH_VALIDATOR;

	//Validation Preference page
	public static String BatchValidationConfigurationBlock_common_description;
	
	//Batch XML
	public static String BatchValidationConfigurationBlock_section_job_xml;

	////Artifact References
	public static String BatchValidationConfigurationBlock_section_artifact_ref;
	public static String BatchValidationConfigurationBlock_pb_unknownArtifactName_label;
	public static String BatchValidationConfigurationBlock_pb_wrongArtifactType_label;

	////Artifact Properties
	public static String BatchValidationConfigurationBlock_section_artifact_properties;
	public static String BatchValidationConfigurationBlock_pb_unusedProperty_label;
	public static String BatchValidationConfigurationBlock_pb_unknownProperty_label;

	////Transitions
	public static String BatchValidationConfigurationBlock_section_transitions;
	public static String BatchValidationConfigurationBlock_pb_targetNotFound_label;
	public static String BatchValidationConfigurationBlock_pb_loopIsDetected_label;

	////Exception Class Filter
	public static String BatchValidationConfigurationBlock_section_exception_class_filter;
	public static String BatchValidationConfigurationBlock_pb_unknownExceptionClass_label;
	public static String BatchValidationConfigurationBlock_pb_wrongExceptionClass_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, BatchSeverityPreferencesMessages.class);
	}
}