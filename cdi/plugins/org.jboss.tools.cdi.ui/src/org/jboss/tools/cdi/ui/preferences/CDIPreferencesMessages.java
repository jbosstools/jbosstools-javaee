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

	// Section Name
	public static String CDIValidatorConfigurationBlock_section_name;
	public static String CDIValidatorConfigurationBlock_pb_stereotypeDeclaresNonEmptyName_label;
	public static String CDIValidatorConfigurationBlock_pb_resourceProducerFieldSetsElName_label;
	public static String CDIValidatorConfigurationBlock_pb_paramInjectionDeclaresEmptyName_label;
	public static String CDIValidatorConfigurationBlock_pb_interceptorHasName_label;
	public static String CDIValidatorConfigurationBlock_pb_decoratorHasName_label;

	// Section Type
	public static String CDIValidatorConfigurationBlock_section_type;
	public static String CDIValidatorConfigurationBlock_pb_illegalTypeInTypedDeclaration_label;
	public static String CDIValidatorConfigurationBlock_pb_producerMethodReturnTypeHasWildcard_label;
	public static String CDIValidatorConfigurationBlock_pb_producerMethodReturnTypeIsVariable_label;
	public static String CDIValidatorConfigurationBlock_pb_producerFieldTypeHasWildcard_label;
	public static String CDIValidatorConfigurationBlock_pb_producerFieldTypeIsVariable_label;
	public static String CDIValidatorConfigurationBlock_pb_producerFieldTypeDoesNotMatchJavaEeObject_label;
	public static String CDIValidatorConfigurationBlock_pb_injectionTypeIsVariable_label;
	public static String CDIValidatorConfigurationBlock_pb_stereotypeIsAnnotatedTyped_label;
	public static String CDIValidatorConfigurationBlock_pb_missingNonbindingInQualifierTypeMember_label;
	public static String CDIValidatorConfigurationBlock_pb_missingNonbindingInInterceptorBindingTypeMember_label;

	// Scope
	public static String CDIValidatorConfigurationBlock_section_scope;
	public static String CDIValidatorConfigurationBlock_pb_multipleScopeTypeAnnotations_label;
	public static String CDIValidatorConfigurationBlock_pb_missingScopeWhenThereIsNoDefaultScope_label;
	public static String CDIValidatorConfigurationBlock_pb_stereotypeDeclaresMoreThanOneScope_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForManagedBean_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForProducerMethod_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForProducerField_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeWhenTypeInjectionPointIsInjected_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForInterceptor_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalScopeForDecorator_label;

	// Member
	public static String CDIValidatorConfigurationBlock_section_member;
	public static String CDIValidatorConfigurationBlock_pb_producerAnnotatedInject_label;
	public static String CDIValidatorConfigurationBlock_pb_producerParameterIllegallyAnnotated_label;
	public static String CDIValidatorConfigurationBlock_pb_observerAnnotatedInject_label;
	public static String CDIValidatorConfigurationBlock_pb_observerParameterIllegallyAnnotated_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalProducerMethodInSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_multipleDisposingParameters_label;
	public static String CDIValidatorConfigurationBlock_pb_disposerAnnotatedInject_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalDisposerInSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_noProducerMatchingDisposer_label;
	public static String CDIValidatorConfigurationBlock_pb_multipleDisposersForProducer_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalProducerFieldInSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_multipleInjectionConstructors_label;
	public static String CDIValidatorConfigurationBlock_pb_constructorParameterIllegallyAnnotated_label;
	public static String CDIValidatorConfigurationBlock_pb_genericMethodAnnotatedInject_label;
	public static String CDIValidatorConfigurationBlock_pb_multipleObservingParameters_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalObserverInSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalConditionalObserver_label;

	// Interceptor & Decorator
	public static String CDIValidatorConfigurationBlock_section_interceptor_and_decorator;
	public static String CDIValidatorConfigurationBlock_pb_bothInterceptorAndDecorator_label;
	public static String CDIValidatorConfigurationBlock_pb_sessionBeanAnnotatedInterceptorOrDecorator_label;
	public static String CDIValidatorConfigurationBlock_pb_producerInInterceptorOrDecorator_label;
	public static String CDIValidatorConfigurationBlock_pb_disposerInInterceptorOrDecorator_label;
	public static String CDIValidatorConfigurationBlock_pb_multipleDelegate_label;
	public static String CDIValidatorConfigurationBlock_pb_missingDelegate_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalInjectionPointDelegate_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalBeanDeclaringDelegate_label;
	public static String CDIValidatorConfigurationBlock_pb_delegateHasIllegalType_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalLifecycleCallbackInterceptorBinding_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalInterceptorBindingMethod_label;
	public static String CDIValidatorConfigurationBlock_pb_conflictingInterceptorBindings_label;
	public static String CDIValidatorConfigurationBlock_pb_observerInInterceptorOrDecorator_label;
	public static String CDIValidatorConfigurationBlock_pb_interceptorOrDecoratorIsAlternative_label;
	public static String CDIValidatorConfigurationBlock_pb_missingInterceptorBinding_label;

	// Specializing
	public static String CDIValidatorConfigurationBlock_section_specializing;
	public static String CDIValidatorConfigurationBlock_pb_illegalSpecializingManagedBean_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalSpecializingSessionBean_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalSpecializingProducer_label;
	public static String CDIValidatorConfigurationBlock_pb_missingTypeInSpecializingBean_label;
	public static String CDIValidatorConfigurationBlock_pb_conflictingNameInSpecializingBean_label;
	public static String CDIValidatorConfigurationBlock_pb_interceptorAnnotatedSpecializes_label;
	public static String CDIValidatorConfigurationBlock_pb_decoratorAnnotatedSpecializes_label;

	// Miscellaneous
	public static String CDIValidatorConfigurationBlock_section_miscellaneous;
	public static String CDIValidatorConfigurationBlock_pb_illegalInjectingUserTransactionType_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalInjectingInjectionPointType_label;
	public static String CDIValidatorConfigurationBlock_pb_illegalQualifierInStereotype_label;


	public static String CDI_VALIDATOR_PREFERENCE_PAGE_CDI_VALIDATOR;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIPreferencesMessages.class);
	}
}