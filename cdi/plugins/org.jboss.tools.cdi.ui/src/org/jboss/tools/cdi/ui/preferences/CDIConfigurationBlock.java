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
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;

/**
 * @author Alexey Kazakov
 */
public class CDIConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = "CDIValidatorConfigurationBlock";

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (SectionDescription s: CDIConfigurationBlockDescriptionProvider.getInstance().getSections()) {
			s.collectKeys(keys);
		}
		keys.add(MAX_NUMBER_OF_PROBLEMS_KEY);
		keys.add(WRONG_BUILDER_ORDER_KEY);
		return keys.toArray(new Key[0]);
	}

	private static final Key MAX_NUMBER_OF_PROBLEMS_KEY = getKey(CDICorePlugin.PLUGIN_ID, SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);

	@Override
	protected Key getMaxNumberOfProblemsKey() {
		return MAX_NUMBER_OF_PROBLEMS_KEY;
	}

	private static final Key WRONG_BUILDER_ORDER_KEY = getKey(CDICorePlugin.PLUGIN_ID, SeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME);

	protected Key getWrongBuilderOrderKey() {
		return WRONG_BUILDER_ORDER_KEY;
	}

	public CDIConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return CDIConfigurationBlockDescriptionProvider.getInstance().getSections();
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