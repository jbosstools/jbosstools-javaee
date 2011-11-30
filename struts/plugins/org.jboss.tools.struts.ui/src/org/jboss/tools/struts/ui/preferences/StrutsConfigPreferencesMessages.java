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

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class StrutsConfigPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = StrutsConfigPreferencesMessages.class.getName();

	// Validator Preference page
	public static String StrutsConfigConfigurationBlock_common_description;

	// Section Struts Config
	public static String StrutsConfigConfigurationBlock_section_struts_config;
	public static String StrutsConfigConfigurationBlock_pb_invalidActionName_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidActionReferenceAttribute_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidActionType_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidActionForward_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidGlobalForward_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidGlobalException_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidController_label;
	public static String StrutsConfigConfigurationBlock_pb_invalidMessageResources_label;

	public static String StrutsConfigConfigurationBlock_section_web_xml;
	public static String StrutsConfigConfigurationBlock_pb_invalidInitParam_label;


	public static String PREFERENCE_PAGE_STRUTS_CORE_VALIDATOR;

	static {
		NLS.initializeMessages(BUNDLE_NAME, StrutsConfigPreferencesMessages.class);
	}
}