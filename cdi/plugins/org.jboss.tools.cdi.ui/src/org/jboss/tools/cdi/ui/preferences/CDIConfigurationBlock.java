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
			{CDIPreferences.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleScopeTypeAnnotations_label},
			{CDIPreferences.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingScopeWhenThereIsNoDefaultScope_label},
			{CDIPreferences.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeDeclaresMoreThanOneScope_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_MANAGED_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForManagedBean_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForSessionBean_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForProducerMethod_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForProducerField_label},
			{CDIPreferences.ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeWhenTypeInjectionPointIsInjected_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_INTERCEPTOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForInterceptor_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForDecorator_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_MEMBER = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_member,
		new String[][]{
			{CDIPreferences.PRODUCER_ANNOTATED_INJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerAnnotatedInject_label},
			{CDIPreferences.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerParameterIllegallyAnnotated_label},
			{CDIPreferences.OBSERVER_ANNOTATED_INJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_observerAnnotatedInject_label},
			{CDIPreferences.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_observerParameterIllegallyAnnotated_label},
			{CDIPreferences.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalProducerMethodInSessionBean_label},
			{CDIPreferences.MULTIPLE_DISPOSING_PARAMETERS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleDisposingParameters_label},
			{CDIPreferences.DISPOSER_ANNOTATED_INJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_disposerAnnotatedInject_label},
			{CDIPreferences.ILLEGAL_DISPOSER_IN_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalDisposerInSessionBean_label},
			{CDIPreferences.NO_PRODUCER_MATCHING_DISPOSER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_noProducerMatchingDisposer_label},
			{CDIPreferences.MULTIPLE_DISPOSERS_FOR_PRODUCER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleDisposersForProducer_label},
			{CDIPreferences.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalProducerFieldInSessionBean_label},
			{CDIPreferences.MULTIPLE_INJECTION_CONSTRUCTORS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleInjectionConstructors_label},
			{CDIPreferences.CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_constructorParameterIllegallyAnnotated_label},
			{CDIPreferences.GENERIC_METHOD_ANNOTATED_INJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_genericMethodAnnotatedInject_label},
			{CDIPreferences.MULTIPLE_OBSERVING_PARAMETERS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleObservingParameters_label},
			{CDIPreferences.ILLEGAL_OBSERVER_IN_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalObserverInSessionBean_label},
			{CDIPreferences.ILLEGAL_CONDITIONAL_OBSERVER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalConditionalObserver_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_INTERCEPTOR = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_interceptor_and_decorator,
		new String[][]{
				{CDIPreferences.BOTH_INTERCEPTOR_AND_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_bothInterceptorAndDecorator_label},
				{CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_sessionBeanAnnotatedInterceptorOrDecorator_label},
				{CDIPreferences.PRODUCER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerInInterceptorOrDecorator_label},
				{CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_disposerInInterceptorOrDecorator_label},
				{CDIPreferences.MULTIPLE_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleDelegate_label},
				{CDIPreferences.MISSING_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingDelegate_label},
				{CDIPreferences.ILLEGAL_INJECTION_POINT_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectionPointDelegate_label},
				{CDIPreferences.ILLEGAL_BEAN_DECLARING_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalBeanDeclaringDelegate_label},
				{CDIPreferences.DELEGATE_HAS_ILLEGAL_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_delegateHasIllegalType_label},
				{CDIPreferences.ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalLifecycleCallbackInterceptorBinding_label},
				{CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInterceptorBindingMethod_label},
				{CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_conflictingInterceptorBindings_label},
				{CDIPreferences.OBSERVER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_observerInInterceptorOrDecorator_label},
				{CDIPreferences.INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorOrDecoratorIsAlternative_label},
				{CDIPreferences.MISSING_INTERCEPTOR_BINDING, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingInterceptorBinding_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription SECTION_SPECIALIZATION = new SectionDescription(
			CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_miscellaneous,
			new String[][]{
				{CDIPreferences.ILLEGAL_SPECIALIZING_MANAGED_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalSpecializingManagedBean_label},
				{CDIPreferences.ILLEGAL_SPECIALIZING_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalSpecializingSessionBean_label},
				{CDIPreferences.ILLEGAL_SPECIALIZING_PRODUCER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalSpecializingProducer_label},
				{CDIPreferences.MISSING_TYPE_IN_SPECIALIZING_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingTypeInSpecializingBean_label},
				{CDIPreferences.CONFLICTING_NAME_IN_SPECIALIZING_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_conflictingNameInSpecializingBean_label},
				{CDIPreferences.INTERCEPTOR_ANNOTATED_SPECIALIZES, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorAnnotatedSpecializes_label},
				{CDIPreferences.DECORATOR_ANNOTATED_SPECIALIZES, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_decoratorAnnotatedSpecializes_label},
			},
			CDICorePlugin.PLUGIN_ID
		);

	private static SectionDescription SECTION_MISCELLANEOUS = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_miscellaneous,
		new String[][]{
			{CDIPreferences.ILLEGAL_INJECTING_USERTRANSACTION_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectingUserTransactionType_label},
			{CDIPreferences.ILLEGAL_INJECTING_INJECTIONPOINT_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectingInjectionPointType_label},
			{CDIPreferences.ILLEGAL_QUALIFIER_IN_STEREOTYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalQualifierInStereotype_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_NAME,
		SECTION_TYPE,
		SECTION_SCOPE,
		SECTION_MEMBER,
		SECTION_INTERCEPTOR,
		SECTION_SPECIALIZATION,
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