/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.validation;

import org.eclipse.osgi.util.NLS;

public class CDIValidationMessages20 {

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.internal.core.validation.messages20"; //$NON-NLS-1$

	public static String STEREOTYPE_DECLARES_NON_EMPTY_NAME;
	public static String RESOURCE_PRODUCER_FIELD_SETS_EL_NAME;
	public static String PARAM_INJECTION_DECLARES_EMPTY_NAME;
	public static String INTERCEPTOR_HAS_NAME;
	public static String DECORATOR_HAS_NAME;

	public static String UNSATISFIED_INJECTION_POINTS;
	public static String AMBIGUOUS_INJECTION_POINTS;
	public static String UNPROXYABLE_BEAN_ARRAY_TYPE;
	public static String UNPROXYABLE_BEAN_PRIMITIVE_TYPE;
	public static String UNPROXYABLE_BEAN_TYPE_WITH_NPC;
	public static String UNPROXYABLE_BEAN_FINAL_TYPE;
	public static String UNPROXYABLE_BEAN_TYPE_WITH_FM;
	public static String UNPROXYABLE_BEAN_ARRAY_TYPE_2;
	public static String UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2;
	public static String UNPROXYABLE_BEAN_TYPE_WITH_NPC_2;
	public static String UNPROXYABLE_BEAN_FINAL_TYPE_2;
	public static String UNPROXYABLE_BEAN_TYPE_WITH_FM_2;
	public static String DECORATOR_RESOLVES_TO_FINAL_CLASS;
	public static String DECORATOR_RESOLVES_TO_FINAL_METHOD;
	public static String DUPLCICATE_EL_NAME;
	public static String UNRESOLVABLE_EL_NAME;

	public static String ILLEGAL_TYPE_IN_TYPED_DECLARATION;
	public static String PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD;
	public static String PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE;
	public static String PRODUCER_FIELD_TYPE_HAS_WILDCARD;
	public static String PRODUCER_FIELD_TYPE_IS_VARIABLE;
	public static String PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT;
	public static String INJECT_RESOLVES_TO_NULLABLE_BEAN;
	public static String INJECTION_TYPE_IS_VARIABLE;
	public static String STEREOTYPE_IS_ANNOTATED_TYPED;
	public static String MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER;
	public static String MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER;
	public static String MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER;
	public static String MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER;
//	public static String MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE;
	public static String MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE;
//	public static String MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE;
	public static String MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE;
//	public static String MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE;
	public static String MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE;
	public static String ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF;
	public static String ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M;
	public static String ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F;
	public static String ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF;
	public static String ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE;
	public static String ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE_FOR_STEREOTYPE;
	public static String NOT_PASSIVATION_CAPABLE_BEAN;

	public static String MULTIPLE_SCOPE_TYPE_ANNOTATIONS;
	public static String MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_BEAN_CLASS;
	public static String MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_METHOD;
	public static String MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_FIELD;
	public static String MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE;
	public static String STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE;
	public static String ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD;
	public static String ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_GENERIC_TYPE;
	public static String ILLEGAL_SCOPE_FOR_SESSION_BEAN_WITH_GENERIC_TYPE;
	public static String ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN;
	public static String ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN;
	public static String ILLEGAL_SCOPE_FOR_PRODUCER_METHOD;
	public static String ILLEGAL_SCOPE_FOR_PRODUCER_FIELD;
	public static String ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED;
	public static String ILLEGAL_SCOPE_FOR_INTERCEPTOR;
	public static String ILLEGAL_SCOPE_FOR_DECORATOR;

	public static String PRODUCER_ANNOTATED_INJECT;
	public static String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES;
	public static String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES;
	public static String OBSERVER_ANNOTATED_INJECT;
	public static String OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED;
	public static String ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN;
	public static String MULTIPLE_DISPOSING_PARAMETERS;
	public static String DISPOSER_ANNOTATED_INJECT;
	public static String ILLEGAL_DISPOSER_IN_SESSION_BEAN;
	public static String NO_PRODUCER_MATCHING_DISPOSER;
	public static String MULTIPLE_DISPOSERS_FOR_PRODUCER;
	public static String ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN;
	public static String MULTIPLE_INJECTION_CONSTRUCTORS;
	public static String CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES;
	public static String CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES;
	public static String GENERIC_METHOD_ANNOTATED_INJECT;
	public static String STATIC_METHOD_ANNOTATED_INJECT;
	public static String MULTIPLE_OBSERVING_PARAMETERS;
	public static String ILLEGAL_OBSERVER_IN_SESSION_BEAN;
	public static String ILLEGAL_CONDITIONAL_OBSERVER;

	public static String BOTH_INTERCEPTOR_AND_DECORATOR;
	public static String SESSION_BEAN_ANNOTATED_INTERCEPTOR;
	public static String SESSION_BEAN_ANNOTATED_DECORATOR;
	public static String PRODUCER_IN_INTERCEPTOR;
	public static String PRODUCER_IN_DECORATOR;
	public static String DISPOSER_IN_INTERCEPTOR;
	public static String DISPOSER_IN_DECORATOR;
	public static String MULTIPLE_DELEGATE;
	public static String MISSING_DELEGATE;
	public static String ILLEGAL_INJECTION_POINT_DELEGATE;
	public static String ILLEGAL_BEAN_DECLARING_DELEGATE;
	public static String DELEGATE_HAS_ILLEGAL_TYPE;
	public static String ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING;
	public static String ILLEGAL_INTERCEPTOR_BINDING_CLASS;
	public static String ILLEGAL_INTERCEPTOR_BINDING_METHOD;
	public static String CONFLICTING_INTERCEPTOR_BINDINGS;
	public static String OBSERVER_IN_INTERCEPTOR;
	public static String OBSERVER_IN_DECORATOR;
	public static String INTERCEPTOR_IS_ALTERNATIVE;
	public static String DECORATOR_IS_ALTERNATIVE;
	public static String MISSING_INTERCEPTOR_BINDING;

	public static String ILLEGAL_SPECIALIZING_MANAGED_BEAN;
	public static String ILLEGAL_SPECIALIZING_SESSION_BEAN;
	public static String ILLEGAL_SPECIALIZING_PRODUCER_STATIC;
	public static String ILLEGAL_SPECIALIZING_PRODUCER_OVERRIDE;
	public static String MISSING_TYPE_IN_SPECIALIZING_BEAN;
	public static String CONFLICTING_NAME_IN_SPECIALIZING_BEAN;
	public static String INTERCEPTOR_ANNOTATED_SPECIALIZES;
	public static String DECORATOR_ANNOTATED_SPECIALIZES;
	public static String INCONSISTENT_SPECIALIZATION;

	public static String ILLEGAL_INJECTING_USERTRANSACTION_TYPE;
	public static String ILLEGAL_INJECTING_INJECTIONPOINT_TYPE;
	public static String ILLEGAL_QUALIFIER_IN_STEREOTYPE;
	public static String EMPTY_ALTERNATIVE_BEAN_CLASS_NAME;
	public static String UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME;
	public static String ILLEGAL_ALTERNATIVE_BEAN_CLASS;
	public static String EMPTY_ALTERNATIVE_ANNOTATION_NAME;
	public static String UNKNOWN_ALTERNATIVE_ANNOTATION_NAME;
	public static String ILLEGAL_ALTERNATIVE_ANNOTATION;
	public static String DUPLICATE_ALTERNATIVE_TYPE;
	public static String EMPTY_DECORATOR_BEAN_CLASS_NAME;
	public static String UNKNOWN_DECORATOR_BEAN_CLASS_NAME;
	public static String ILLEGAL_DECORATOR_BEAN_CLASS;
	public static String DUPLICATE_DECORATOR_CLASS;
	public static String EMPTY_INTERCEPTOR_CLASS_NAME;
	public static String UNKNOWN_INTERCEPTOR_CLASS_NAME;
	public static String ILLEGAL_INTERCEPTOR_CLASS;
	public static String DUPLICATE_INTERCEPTOR_CLASS;

	public static String SEARCHING_RESOURCES;
	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_PROJECT;
	public static String VALIDATING_BEANS_XML;
//	public static String MISSING_BEANS_XML;
	
	public static String OBSERVER_ASYNC_PRIORITY;
	public static String ILLEGAL_CONDITIONAL_OBSERVER_ASYNC;
	public static String MULTIPLE_OBSERVING_PARAMETERS_ASYNC;
	public static String CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ASYNC;
	public static String OBSERVER_ANNOTATED_INJECT_ASYNC;
	public static String OBSERVER_ASYNC_IN_DECORATOR;
	public static String OBSERVER_ASYNC_IN_INTERCEPTOR;
	public static String ILLEGAL_OBSERVER_ASYNC_IN_SESSION_BEAN;
	public static String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ASYNC;
	public static String OBSERVER_ASYNC_PARAMETER_ILLEGALLY_ANNOTATED;
	public static String OBSERVER_AND_OBSERVER_ASYNC_ERROR;
	public static String OBSERVER_AND_OBSERVER_ASYNC_METHOD_ERROR;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIValidationMessages20.class);
	}
}