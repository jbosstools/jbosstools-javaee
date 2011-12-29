/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.ui.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.jst.web.ui.WebUiPlugin;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.validation.StrutsPreferences;

/**
 * @author Viacheslav Kabanovich
 */
public class StrutsConfigConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = "StrutsConfigConfigurationBlock"; //$NON-NLS-1$

	private static SectionDescription SECTION_STRUTS_CONFIG = new SectionDescription(
		StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_section_struts_config,
		new String[][]{
			{StrutsPreferences.INVALID_ACTION_NAME, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidActionName_label},
			{StrutsPreferences.INVALID_ACTION_REFERENCE_ATTRIBUTE, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidActionReferenceAttribute_label},
			{StrutsPreferences.INVALID_ACTION_TYPE, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidActionType_label},
			{StrutsPreferences.INVALID_ACTION_FORWARD, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidActionForward_label},

			{StrutsPreferences.INVALID_GLOBAL_FORWARD, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidGlobalForward_label},
			{StrutsPreferences.INVALID_GLOBAL_EXCEPTION, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidGlobalException_label},

			{StrutsPreferences.INVALID_CONTROLLER, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidController_label},
			{StrutsPreferences.INVALID_MESSAGE_RESOURCES, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidMessageResources_label},
		},
		StrutsModelPlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_WEB_XML = new SectionDescription(
		StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_section_web_xml,
		new String[][]{
			{StrutsPreferences.INVALID_INIT_PARAM, StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_pb_invalidInitParam_label},
		},
		StrutsModelPlugin.PLUGIN_ID
	);
		
	public static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_STRUTS_CONFIG,
		SECTION_WEB_XML,
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (SectionDescription s: ALL_SECTIONS) {
			s.collectKeys(keys);
		}
		keys.add(MAX_NUMBER_OF_PROBLEMS_KEY);
		return keys.toArray(new Key[0]);
	}

	private static final Key MAX_NUMBER_OF_PROBLEMS_KEY = getKey(StrutsModelPlugin.PLUGIN_ID, SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);

	@Override
	protected Key getMaxNumberOfProblemsKey() {
		return MAX_NUMBER_OF_PROBLEMS_KEY;
	}

	public StrutsConfigConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return StrutsConfigPreferencesMessages.StrutsConfigConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return WebUiPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}

	@Override
	protected String getQualifier() {
		return StrutsModelPlugin.PLUGIN_ID;
	}
}