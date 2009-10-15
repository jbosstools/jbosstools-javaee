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

package org.jboss.tools.jsf.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class JSFSeverityPreferencesMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.ui.preferences.JSFSeverityPreferencesMessages"; //$NON-NLS-1$

	public static String JSF_VALIDATOR_CONFIGURATION_BLOCK_JSF_VALIDATOR_CONFIGURATION_BLOCK;
	public static String JSF_VALIDATOR_PREFERENCE_PAGE_JSF_VALIDATOR;

	//Validator Preference page
	public static String JSFValidatorConfigurationBlock_common_description;
	
	//Expression Language
	public static String JSFValidatorConfigurationBlock_section_el;
	public static String JSFValidatorConfigurationBlock_pb_elSyntaxError_label;
	public static String JSFValidatorConfigurationBlock_pb_unknownElVariableName_label;
	public static String JSFValidatorConfigurationBlock_pb_checkVars_label;
	public static String JSFValidatorConfigurationBlock_pb_revalidateUnresolvedEl_label;
	public static String JSFValidatorConfigurationBlock_pb_unknownElVariablePropertyName_label;
	public static String JSFValidatorConfigurationBlock_pb_unpairedGetterOrSetter_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFSeverityPreferencesMessages.class);
	}
}