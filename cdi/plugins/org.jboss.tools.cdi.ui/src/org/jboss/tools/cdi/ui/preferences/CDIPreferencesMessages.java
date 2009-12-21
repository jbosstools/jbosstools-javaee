/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kazakov
 */
public class CDIPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.ui.preferences.CDIPreferencesMessages"; //$NON-NLS-1$

	public static String CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT;

	// Validator Preference page
	public static String CDIValidatorConfigurationBlock_common_description;
	
	public static String CDIValidatorConfigurationBlock_needsbuild_title;
	public static String CDIValidatorConfigurationBlock_needsfullbuild_message;
	public static String CDIValidatorConfigurationBlock_needsprojectbuild_message;

	// Section Test
	public static String CDIValidatorConfigurationBlock_section_test;
	public static String CDIValidatorConfigurationBlock_pb_test_label;

	public static String CDI_VALIDATOR_PREFERENCE_PAGE_CDI_VALIDATOR;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIPreferencesMessages.class);
	}
}