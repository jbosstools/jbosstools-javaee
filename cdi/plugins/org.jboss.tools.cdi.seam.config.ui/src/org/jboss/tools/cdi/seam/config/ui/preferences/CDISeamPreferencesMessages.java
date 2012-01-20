/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class CDISeamPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = CDISeamPreferencesMessages.class.getName();

	//Section Config
	public static String CDIValidatorConfigurationBlock_section_config;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedType_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedMember_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedMethod_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedConstructor_label;
	public static String CDIValidatorConfigurationBlock_pb_annotationExpected_label;
	public static String CDIValidatorConfigurationBlock_pb_inlineBeanTypeMismatch_label;
	public static String CDIValidatorConfigurationBlock_pb_abstractTypeIsConfiguredAsBean_label;
	public static String CDIValidatorConfigurationBlock_pb_beanConstructorIsMissing_label;

	//Section Solder
	public static String CDIValidatorConfigurationBlock_section_solder;
	public static String CDIValidatorConfigurationBlock_pb_ambiguousGenericConfigurationPoint_label;
	public static String CDIValidatorConfigurationBlock_pb_wrongTypeOfGenericConfigurationPoint_label;
	public static String CDIValidatorConfigurationBlock_pb_wrongGenericConfigurationAnnotationReference_label;
	public static String CDIValidatorConfigurationBlock_pb_genericConfigurationTypeIsGenericBean_label;
	public static String CDIValidatorConfigurationBlock_pb_defaultProducerFieldOnNormalScopedBean_label;
	public static String CDIValidatorConfigurationBlock_pb_identicalDefaultBeans_label;

	public static String CDI_SEAM_VALIDATOR_PREFERENCE_PAGE_TITLE;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CDISeamPreferencesMessages.class);
	}
}