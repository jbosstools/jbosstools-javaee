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
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class JSFValidationConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = JSFSeverityPreferencesMessages.JSF_VALIDATION_CONFIGURATION_BLOCK_JSF_VALIDATION_CONFIGURATION_BLOCK;

	private static SectionDescription SECTION_COMPOSITION_COMPONENTS = new SectionDescription(
			JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_section_composition_components,
			new String[][]{
				{JSFSeverityPreferences.UNKNOWN_COMPOSITION_COMPONENT_NAME, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_unknownComponent_label},
				{JSFSeverityPreferences.UNKNOWN_COMPOSITION_COMPONENT_ATTRIBUTE, JSFSeverityPreferencesMessages.JSFValidationConfigurationBlock_pb_unknownAttribute_label},
			},
			JSFModelPlugin.PLUGIN_ID
		);

		private static SectionDescription[] ALL_SECTIONS = new SectionDescription[] {
			SECTION_COMPOSITION_COMPONENTS
		};

		private static Key[] getKeys() {
			ArrayList<Key> keys = new ArrayList<Key>();
			for (int i = 0; i < ALL_SECTIONS.length; i++) {
				for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
					keys.add(ALL_SECTIONS[i].options[j].key);
				}
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
	}