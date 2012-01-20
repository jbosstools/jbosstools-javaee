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

import org.jboss.tools.cdi.seam.config.core.CDISeamConfigCorePlugin;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigPreferences;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderCorePlugin;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderPreferences;
import org.jboss.tools.cdi.ui.preferences.CDIPreferencesMessages;
import org.jboss.tools.cdi.ui.preferences.IConfigurationBlockDescriptionProvider;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;

public class CDISeamConfigurationBlockDescriptionProvider implements IConfigurationBlockDescriptionProvider {

	private static SectionDescription SECTION_CONFIG = new SectionDescription(
		CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_section_config,
		new String[][]{
			{CDISeamConfigPreferences.UNRESOLVED_TYPE, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_unresolvedType_label},
			{CDISeamConfigPreferences.UNRESOLVED_MEMBER, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_unresolvedMember_label},
			{CDISeamConfigPreferences.UNRESOLVED_CONSTRUCTOR, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_unresolvedConstructor_label},
			{CDISeamConfigPreferences.UNRESOLVED_METHOD, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_unresolvedMethod_label},
			{CDISeamConfigPreferences.ANNOTATION_EXPECTED, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_annotationExpected_label},
			{CDISeamConfigPreferences.INLINE_BEAN_TYPE_MISMATCH, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_inlineBeanTypeMismatch_label},
			{CDISeamConfigPreferences.ABSTRACT_TYPE_IS_CONFIGURED_AS_BEAN, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_abstractTypeIsConfiguredAsBean_label},
			{CDISeamConfigPreferences.BEAN_CONSTRUCTOR_IS_MISSING, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_beanConstructorIsMissing_label},
		},
		CDISeamConfigCorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_SOLDER = new SectionDescription(
		CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_section_solder,
		new String[][]{
			{CDISeamSolderPreferences.WRONG_GENERIC_CONFIGURATION_ANNOTATION_REFERENCE, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_wrongGenericConfigurationAnnotationReference_label},
			{CDISeamSolderPreferences.AMBIGUOUS_GENERIC_CONFIGURATION_POINT, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_ambiguousGenericConfigurationPoint_label},
			{CDISeamSolderPreferences.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_wrongTypeOfGenericConfigurationPoint_label},
			{CDISeamSolderPreferences.GENERIC_CONFIGURATION_TYPE_IS_A_GENERIC_BEAN, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_genericConfigurationTypeIsGenericBean_label},
			{CDISeamSolderPreferences.DEFAULT_PRODUCER_FIELD_ON_NORMAL_SCOPED_BEAN, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_defaultProducerFieldOnNormalScopedBean_label},
			{CDISeamSolderPreferences.IDENTICAL_DEFAULT_BEANS, CDISeamPreferencesMessages.CDIValidatorConfigurationBlock_pb_identicalDefaultBeans_label},
		},
		CDISeamSolderCorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = {
		SECTION_CONFIG,
		SECTION_SOLDER
	};

	@Override
	public SectionDescription[] getSections() {
		return ALL_SECTIONS;
	}

}
