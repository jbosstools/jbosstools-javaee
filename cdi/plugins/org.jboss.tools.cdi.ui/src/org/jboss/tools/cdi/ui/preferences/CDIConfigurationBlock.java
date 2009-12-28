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

	private static SectionDescription SECTION_NAME = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_name,
		new String[][]{
			{CDIPreferences.STEREOTYPE_DECLARES_NON_EMPTY_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeDeclaresNonEmptyName_label},
			{CDIPreferences.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_resourceProducerFieldSetsElName_label},
			{CDIPreferences.PARAM_INJECTION_DECLARES_EMPTY_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_paramInjectionDeclaresEmptyName_label},
			{CDIPreferences.INTERCEPTOR_HAS_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorHasName_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_TYPE = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_type,
		new String[][]{
			{CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalTypeInTypedDeclaration_label},
			{CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerMethodReturnTypeHasWildcard_label},
			{CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerMethodReturnTypeIsVariable_label},
			{CDIPreferences.PRODUCER_FIELD_TYPE_HAS_WILDCARD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerFieldTypeHasWildcard_label},
			{CDIPreferences.PRODUCER_FIELD_TYPE_IS_VARIABLE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerFieldTypeIsVariable_label},
			{CDIPreferences.PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerFieldTypeDoesNotMatchJavaEeObject_label},
			{CDIPreferences.INJECTION_TYPE_IS_VARIABLE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_injectionTypeIsVariable_label},
			{CDIPreferences.STEREOTYPE_IS_ANNOTATED_TYPED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeIsAnnotatedTyped_label},
			{CDIPreferences.MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingNonbindingInQualifierTypeMember_label},
			{CDIPreferences.MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingNonbindingInInterceptorBindingTypeMember_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_SCOPE = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_scope,
		new String[][]{
//			{CDIPreferences., CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_}
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_MEMBER = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_member,
		new String[][]{
			{CDIPreferences.TEST, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_test_label}
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_INTERCEPTOR = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_interceptor_and_decorator,
		new String[][]{
			{CDIPreferences.TEST, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_test_label}
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_MISCELLANEOUS = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_miscellaneous,
		new String[][]{
			{CDIPreferences.TEST, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_test_label}
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_TEST,
		SECTION_NAME,
		SECTION_TYPE,
		SECTION_SCOPE,
		SECTION_MEMBER,
		SECTION_INTERCEPTOR,
		SECTION_MISCELLANEOUS
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