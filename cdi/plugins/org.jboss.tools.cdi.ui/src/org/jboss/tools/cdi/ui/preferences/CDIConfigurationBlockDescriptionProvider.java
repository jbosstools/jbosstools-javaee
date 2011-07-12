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
package org.jboss.tools.cdi.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;

/**
 * 
 * @author Alexey Kazakov & Viacheslav Kabanovich
 *
 */
public class CDIConfigurationBlockDescriptionProvider {
	private static final String POINT_ID = "org.jboss.tools.cdi.ui.configBlockDescriptionProvider";

	private static CDIConfigurationBlockDescriptionProvider INSTANCE = null;

	private CDIConfigurationBlockDescriptionProvider() {
		init();
	}

	public static CDIConfigurationBlockDescriptionProvider getInstance() {
		if(INSTANCE == null) {
			CDIConfigurationBlockDescriptionProvider q = new CDIConfigurationBlockDescriptionProvider();
			INSTANCE = q;
		}
		return INSTANCE;
	}

	private SectionDescription SECTION_NAME = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_name,
		new String[][]{
			{CDIPreferences.STEREOTYPE_DECLARES_NON_EMPTY_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeDeclaresNonEmptyName_label},
			{CDIPreferences.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_resourceProducerFieldSetsElName_label},
			{CDIPreferences.PARAM_INJECTION_DECLARES_EMPTY_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_paramInjectionDeclaresEmptyName_label},
			{CDIPreferences.INTERCEPTOR_OR_DECORATOR_HAS_NAME, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorOrDecoretorHasName_label},
			{CDIPreferences.AMBIGUOUS_EL_NAMES, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_ambiguousElNames_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_TYPE = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_type,
		new String[][]{
			{CDIPreferences.UNSATISFIED_OR_AMBIGUOUS_INJECTION_POINTS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_unsatisfiedOrAmbiguousInjectionPoints_label},
			{CDIPreferences.UNPROXYABLE_BEAN_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_unproxyableBeanType_label},
			{CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalTypeInTypedDeclaration_label},
			{CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerMethodReturnTypeHasWildcardOrVariable_label},
//			{CDIPreferences.PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerFieldTypeDoesNotMatchJavaEeObject_label},
			{CDIPreferences.INJECT_RESOLVES_TO_NULLABLE_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_injectResolvesToNullableBean_label},
			{CDIPreferences.INJECTION_TYPE_IS_VARIABLE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_injectionTypeIsVariable_label},
			{CDIPreferences.STEREOTYPE_IS_ANNOTATED_TYPED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeIsAnnotatedTyped_label},
			{CDIPreferences.MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingNonbindingInQualifierTypeMember_label},
			{CDIPreferences.MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingNonbindingInInterceptorBindingTypeMember_label},
			{CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingOrIncorrectTargetOrRetentionInAnnotationType_label},
			{CDIPreferences.NOT_PASSIVATION_CAPABLE_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_notPassivationCapableBean_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_SCOPE = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_scope,
		new String[][]{
			{CDIPreferences.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleScopeTypeAnnotations_label},
			{CDIPreferences.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingScopeWhenThereIsNoDefaultScope_label},
			{CDIPreferences.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_stereotypeDeclaresMoreThanOneScope_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForBean_label},
			{CDIPreferences.ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeWhenTypeInjectionPointIsInjected_label},
			{CDIPreferences.ILLEGAL_SCOPE_FOR_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalScopeForInterceptorOrDecorator_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_MEMBER = new SectionDescription(
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
			{CDIPreferences.MULTIPLE_INJECTION_CONSTRUCTORS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleInjectionConstructors_label},
			{CDIPreferences.CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_constructorParameterIllegallyAnnotated_label},
			{CDIPreferences.GENERIC_METHOD_ANNOTATED_INJECT, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_genericMethodAnnotatedInject_label},
			{CDIPreferences.MULTIPLE_OBSERVING_PARAMETERS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleObservingParameters_label},
			{CDIPreferences.ILLEGAL_OBSERVER_IN_SESSION_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalObserverInSessionBean_label},
			{CDIPreferences.ILLEGAL_CONDITIONAL_OBSERVER, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalConditionalObserver_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_INTERCEPTOR = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_interceptor_and_decorator,
		new String[][]{
			{CDIPreferences.BOTH_INTERCEPTOR_AND_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_bothInterceptorAndDecorator_label},
			{CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_sessionBeanAnnotatedInterceptorOrDecorator_label},
			{CDIPreferences.PRODUCER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_producerInInterceptorOrDecorator_label},
			{CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_disposerInInterceptorOrDecorator_label},
			{CDIPreferences.MULTIPLE_OR_MISSING_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_multipleOrMissingDelegate_label},
			{CDIPreferences.ILLEGAL_INJECTION_POINT_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectionPointDelegate_label},
			{CDIPreferences.ILLEGAL_BEAN_DECLARING_DELEGATE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalBeanDeclaringDelegate_label},
			{CDIPreferences.DELEGATE_HAS_ILLEGAL_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_delegateHasIllegalType_label},
			{CDIPreferences.ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalLifecycleCallbackInterceptorBinding_label},
			{CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInterceptorBindingMethod_label},
			{CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_conflictingInterceptorBindings_label},
			{CDIPreferences.OBSERVER_IN_INTERCEPTOR_OR_DECORATOR, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_observerInInterceptorOrDecorator_label},
			{CDIPreferences.INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorOrDecoratorIsAlternative_label},
			{CDIPreferences.MISSING_INTERCEPTOR_BINDING, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingInterceptorBinding_label},
			{CDIPreferences.DECORATOR_RESOLVES_TO_FINAL_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_decoratorResolvesToFinalBean_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_SPECIALIZATION = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_specializing,
		new String[][]{
			{CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalSpecializingBean_label},
			{CDIPreferences.MISSING_TYPE_IN_SPECIALIZING_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_missingTypeInSpecializingBean_label},
			{CDIPreferences.CONFLICTING_NAME_IN_SPECIALIZING_BEAN, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_conflictingNameInSpecializingBean_label},
			{CDIPreferences.INTERCEPTOR_ANNOTATED_SPECIALIZES, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_interceptorAnnotatedSpecializes_label},
			{CDIPreferences.INCONSISTENT_SPECIALIZATION, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_inconsistentSpecialization_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_MISCELLANEOUS = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_miscellaneous,
		new String[][]{
//			{CDIPreferences.ILLEGAL_INJECTING_USERTRANSACTION_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectingUserTransactionType_label},
//			{CDIPreferences.ILLEGAL_INJECTING_INJECTIONPOINT_TYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalInjectingInjectionPointType_label},
			{CDIPreferences.ILLEGAL_QUALIFIER_IN_STEREOTYPE, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalQualifierInStereotype_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_BEANSXML = new SectionDescription(
		CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_beansxml,
		new String[][]{
			{CDIPreferences.ILLEGAL_TYPE_NAME_IN_BEANS_XML, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_illegalTypeInBeansXml_label},
			{CDIPreferences.DUPLICATE_TYPE_IN_BEANS_XML, CDIPreferencesMessages.CDIValidatorConfigurationBlock_pb_duplicateTypeInBeansXml_label},
		},
		CDICorePlugin.PLUGIN_ID
	);

	private SectionDescription SECTION_JSR_299 = new SectionDescription(CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_jsr299, 
		new SectionDescription[]{
		SECTION_NAME,
		SECTION_TYPE,
		SECTION_SCOPE,
		SECTION_MEMBER,
		SECTION_INTERCEPTOR,
		SECTION_SPECIALIZATION,
		SECTION_MISCELLANEOUS,
		SECTION_BEANSXML
	}, new String[0][], CDICorePlugin.PLUGIN_ID);

	private SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_JSR_299
	};

	void init() {
		List<SectionDescription> exs = new ArrayList<SectionDescription>();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(POINT_ID);
		for (IExtension e: point.getExtensions()) {
			IConfigurationElement[] es = e.getConfigurationElements();
			for (IConfigurationElement element: es) {
				try {
					IConfigurationBlockDescriptionProvider provider = (IConfigurationBlockDescriptionProvider)element.createExecutableExtension("class");
					SectionDescription[] ds = provider.getSections();
					for (SectionDescription d: ds) {
						exs.add(d);
					}
				} catch (CoreException exc) {
					CDICorePlugin.getDefault().logError(exc);
				}
			}
		}

		SectionDescription extensions = new SectionDescription(CDIPreferencesMessages.CDIValidatorConfigurationBlock_section_extensions, exs.toArray(new SectionDescription[0]), new String[0][], CDICorePlugin.PLUGIN_ID);

		ALL_SECTIONS = new SectionDescription[]{
			SECTION_JSR_299,
			extensions
		};
	}

	public SectionDescription[] getSections() {
		return ALL_SECTIONS;
	}


}
