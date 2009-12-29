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
package org.jboss.tools.cdi.internal.core.validation;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kazakov
 */
public class CDIValidationMessages {

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.internal.core.validation.messages"; //$NON-NLS-1$

	public static String testKey;

	public static String STEREOTYPE_DECLARES_NON_EMPTY_NAME;
	public static String RESOURCE_PRODUCER_FIELD_SETS_EL_NAME;
	public static String PARAM_INJECTION_DECLARES_EMPTY_NAME;
	public static String INTERCEPTOR_HAS_NAME;
	public static String DECORATOR_HAS_NAME;

	public static String ILLEGAL_TYPE_IN_TYPED_DECLARATION;
	public static String PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD;
	public static String PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE;
	public static String PRODUCER_FIELD_TYPE_HAS_WILDCARD;
	public static String PRODUCER_FIELD_TYPE_IS_VARIABLE;
	public static String PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT;
	public static String INJECTION_TYPE_IS_VARIABLE;
	public static String STEREOTYPE_IS_ANNOTATED_TYPED;
	public static String MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER;
	public static String MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER;

	public static String MULTIPLE_SCOPE_TYPE_ANNOTATIONS;
	public static String MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE;
	public static String STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE;
	public static String ILLEGAL_SCOPE_FOR_MANAGED_BEAN;
	public static String ILLEGAL_SCOPE_FOR_SESSION_BEAN;
	public static String ILLEGAL_SCOPE_FOR_PRODUCER_METHOD;
	public static String ILLEGAL_SCOPE_FOR_PRODUCER_FIELD;
	public static String ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED;
	public static String ILLEGAL_SCOPE_FOR_INTERCEPTOR;
	public static String ILLEGAL_SCOPE_FOR_DECORATOR;

	public static String PRODUCER_ANNOTATED_INJECT;
	public static String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED;
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
	public static String CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED;
	public static String GENERIC_METHOD_ANNOTATED_INJECT;
	public static String MULTIPLE_OBSERVING_PARAMETERS;
	public static String ILLEGAL_OBSERVER_IN_SESSION_BEAN;
	public static String ILLEGAL_CONDITIONAL_OBSERVER;

	public static String BOTH_INTERCEPTOR_AND_DECORATOR;
	public static String PRODUCER_IN_INTERCEPTOR_OR_DECORATOR;
	public static String DISPOSER_IN_INTERCEPTOR_OR_DECORATOR;
	public static String MULTIPLE_DELEGATE;
	public static String MISSING_DELEGATE;
	public static String ILLEGAL_INJECTION_POINT_DELEGATE;
	public static String ILLEGAL_BEAN_DECLARING_DELEGATE;
	public static String DELEGATE_HAS_ILLEGAL_TYPE;
	public static String ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING;
	public static String ILLEGAL_INTERCEPTOR_BINDING_METHOD;
	public static String CONFLICTING_INTERCEPTOR_BINDINGS;
	public static String OBSERVER_IN_INTERCEPTOR_OR_DECORATOR;
	public static String INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE;
	public static String MISSING_INTERCEPTOR_BINDING;

	public static String ILLEGAL_SPECIALIZING_MANAGED_BEAN;
	public static String ILLEGAL_SPECIALIZING_SESSION_BEAN;
	public static String ILLEGAL_SPECIALIZING_PRODUCER;
	public static String MISSING_TYPE_IN_SPECIALIZING_BEAN;
	public static String CONFLICTING_NAME_IN_SPECIALIZING_BEAN;
	public static String INTERCEPTOR_ANNOTATED_SPECIALIZES;
	public static String DECORATOR_ANNOTATED_SPECIALIZES;

	public static String ILLEGAL_INJECTING_USERTRANSACTION_TYPE;
	public static String ILLEGAL_INJECTING_INJECTIONPOINT_TYPE;
	public static String ILLEGAL_QUALIFIER_IN_STEREOTYPE;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIValidationMessages.class);
	}
}