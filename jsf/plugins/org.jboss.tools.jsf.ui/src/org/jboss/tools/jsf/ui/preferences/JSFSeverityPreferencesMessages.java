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

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFSeverityPreferencesMessages.class);
	}
}