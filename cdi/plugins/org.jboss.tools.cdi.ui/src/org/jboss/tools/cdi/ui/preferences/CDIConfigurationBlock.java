/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;

/**
 * @author Alexey Kazakov
 */
public class CDIConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = "CDIValidatorConfigurationBlock";

	private static SectionDescription SECTION_TEST = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_test,
		new String[][]{
			{CDIPreferences.TEST, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_test_label}
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_TEST
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
				keys.add(ALL_SECTIONS[i].options[j].key);
			}
		}
		return keys.toArray(new Key[0]);
	}

	public CDIConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return CDIPreferencesMessages.CDIValidatorConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return CDICorePlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}
}