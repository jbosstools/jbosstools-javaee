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

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.preferences.BatchSeverityPreferences;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchValidationConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = BatchSeverityPreferencesMessages.BATCH_VALIDATION_CONFIGURATION_BLOCK_BATCH_VALIDATION_CONFIGURATION_BLOCK;

	//Job XML
	
	private static SectionDescription SECTION_ARTIFACT_REF = new SectionDescription(
		BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_section_artifact_ref,
		new String[][]{
			{BatchSeverityPreferences.UNKNOWN_ARTIFACT_NAME, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_unknownArtifactName_label},
			{BatchSeverityPreferences.WRONG_ARTIFACT_TYPE, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_wrongArtifactType_label},
		},
		BatchCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_ARTIFACT_PROPERTIES = new SectionDescription(
		BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_section_artifact_properties,
		new String[][]{
			{BatchSeverityPreferences.UNUSED_PROPERTY, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_unusedProperty_label},
			{BatchSeverityPreferences.UNKNOWN_PROPERTY, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_unknownProperty_label},
		},
		BatchCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_TRANSITIONS = new SectionDescription(
		BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_section_transitions,
		new String[][]{
			{BatchSeverityPreferences.TARGET_NOT_FOUND, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_targetNotFound_label},
			{BatchSeverityPreferences.LOOP_IS_DETECTED, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_loopIsDetected_label},
		},
		BatchCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_EXCEPTION_CLASS_FILTER = new SectionDescription(
			BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_section_exception_class_filter,
			new String[][]{
				{BatchSeverityPreferences.UNKNOWN_EXCEPTION_CLASS, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_unknownExceptionClass_label},
				{BatchSeverityPreferences.WRONG_EXCEPTION_CLASS, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_wrongExceptionClass_label},
			},
			BatchCorePlugin.PLUGIN_ID
		);


	private static SectionDescription SECTION_JOB_XML = new SectionDescription(
		BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_section_job_xml,
		new SectionDescription[]{
			SECTION_ARTIFACT_REF,
			SECTION_ARTIFACT_PROPERTIES,
			SECTION_TRANSITIONS,
			SECTION_EXCEPTION_CLASS_FILTER,
		},
		new String[][]{
			{BatchSeverityPreferences.INVALID_JOB_RESTARTABLE, BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_pb_invalidJobRestartable_label},
		},
		BatchCorePlugin.PLUGIN_ID
	);

	public static SectionDescription[] ALL_SECTIONS = new SectionDescription[] {
		SECTION_JOB_XML
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		keys.add(ENABLE_BLOCK_KEY);
		for (SectionDescription s: ALL_SECTIONS) {
			s.collectKeys(keys);
		}
		keys.add(MAX_NUMBER_OF_PROBLEMS_KEY);
		keys.add(WRONG_BUILDER_ORDER_KEY);
		return keys.toArray(new Key[0]);
	}

	protected final static Key ENABLE_BLOCK_KEY = getKey(BatchCorePlugin.PLUGIN_ID, SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME);

	@Override
	protected Key getEnableBlockKey() {
		return ENABLE_BLOCK_KEY;
	}

	private static final Key MAX_NUMBER_OF_PROBLEMS_KEY = getKey(BatchCorePlugin.PLUGIN_ID, SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);

	@Override
	protected Key getMaxNumberOfProblemsKey() {
		return MAX_NUMBER_OF_PROBLEMS_KEY;
	}

	private static final Key WRONG_BUILDER_ORDER_KEY = getKey(BatchCorePlugin.PLUGIN_ID, SeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME);

	protected Key getWrongBuilderOrderKey() {
		return WRONG_BUILDER_ORDER_KEY;
	}

	public BatchValidationConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return BatchSeverityPreferencesMessages.BatchValidationConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return BatchUIPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}

	@Override
	protected String getQualifier() {
		return BatchCorePlugin.PLUGIN_ID;
	}
}