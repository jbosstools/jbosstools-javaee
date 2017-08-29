/******************************************************************************* 
 * Copyright (c) 2009-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.validation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIVersion;

/**
 *  This class collects CDI validation messages for all available versions,
 *  provided by contributor classes CDIValidationMessagesXX that are
 *  usually initialized with NLS.
 * 
 *  For each validation message included in at least one contributor class,
 *  there is a field with the same name in this class.
 *  
 *  Currently all field values in this class obey the rule as follows:
 *  	- Each value is an array with length equal to CDIVersion.versions().length,
 *  	  that has to be accessed with version.getIndex() for element
 *  	  corresponding to given CDIVersion version;
 *  	- Each element (say i-th element) in the array is either null 
 *  	  or equals to the value of the field with the same name from 
 *  	  contribution CDIValidationMessagesXX corresponding to version with
 *  	  index j such that j <= i and j is maximal among indexes j' <= i of 
 *  	  contributions declaring such field.
 *  
 *  This rule is checked by org.jboss.tools.cdi.core.test.CDIValidationMessagesTest
 *  
 *  For consistency it is strongly advisable to keep to this rule in future.
 *  For example: 
 *  	- if a new version is added, increase length of each array by 1,
 *  	  and either set value to null when rule is irrelevant, or set it to
 *  	  previous contribution when message fits well, or otherwise declare
 *  	  new message in the contribution for the new version.  
 *  	- if a new validation rule is added in the next version,
 *  	  new array constant for it will start with null values for all previous
 *  	  versions.
 *  
 *  Method init() implements this rule. To support it during development
 *  	- All contributions CDIValidationMessagesXX should be included
 *  	  to class array 'versionMessages' in the beginning of init().
 *  	- Declarations of fields for validation rules relevant in a range of versions 
 *  	  should be assigned to template by invoking createTemplate(sinceIndex, untilIndex).
 *  
 *  	- For messages that by some (strongly discouraged) reason do not fit into these rules, 
 *  	  values are to be assigned explicitly in the end of init().  
 * 
 * @author Alexey Kazakov and Viacheslav Kabanovich
 */
public class CDIValidationMessages {

	public static String[] STEREOTYPE_DECLARES_NON_EMPTY_NAME;
	public static String[] RESOURCE_PRODUCER_FIELD_SETS_EL_NAME;
	public static String[] PARAM_INJECTION_DECLARES_EMPTY_NAME;
	public static String[] INTERCEPTOR_HAS_NAME;
	public static String[] DECORATOR_HAS_NAME;

	public static String[] UNSATISFIED_INJECTION_POINTS;
	public static String[] AMBIGUOUS_INJECTION_POINTS;
	public static String[] UNPROXYABLE_BEAN_ARRAY_TYPE;
	public static String[] UNPROXYABLE_BEAN_PRIMITIVE_TYPE;
	public static String[] UNPROXYABLE_BEAN_TYPE_WITH_NPC;
	public static String[] UNPROXYABLE_BEAN_FINAL_TYPE;
	public static String[] UNPROXYABLE_BEAN_TYPE_WITH_FM;
	public static String[] UNPROXYABLE_BEAN_ARRAY_TYPE_2;
	public static String[] UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2;
	public static String[] UNPROXYABLE_BEAN_TYPE_WITH_NPC_2;
	public static String[] UNPROXYABLE_BEAN_FINAL_TYPE_2;
	public static String[] UNPROXYABLE_BEAN_TYPE_WITH_FM_2;
	public static String[] DECORATOR_RESOLVES_TO_FINAL_CLASS;
	public static String[] DECORATOR_RESOLVES_TO_FINAL_METHOD;
	public static String[] DUPLCICATE_EL_NAME;
	public static String[] UNRESOLVABLE_EL_NAME;
	public static String[] ILLEGAL_TYPE_IN_TYPED_DECLARATION;
	public static String[] PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD;
	public static String[] PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE;
	public static String[] PRODUCER_FIELD_TYPE_HAS_WILDCARD;
	public static String[] PRODUCER_FIELD_TYPE_IS_VARIABLE;
	public static String[] PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT;
	public static String[] INJECT_RESOLVES_TO_NULLABLE_BEAN;
	public static String[] INJECTION_TYPE_IS_VARIABLE;
	public static String[] STEREOTYPE_IS_ANNOTATED_TYPED;
	public static String[] MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER;
	public static String[] MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER;
	public static String[] MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER;
	public static String[] MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER;
	
	public static String[] MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE = createTemplate(0, 0);
	public static String[] MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE;

	public static String[] MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE = createTemplate(0, 0);
	public static String[] MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE;

	public static String[] MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE = createTemplate(0, 0);
	public static String[] MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE;

	public static String[] ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF;
	public static String[] ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M;
	public static String[] ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F;
	public static String[] ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF;
	public static String[] ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE;
	public static String[] ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE_FOR_STEREOTYPE;
	public static String[] NOT_PASSIVATION_CAPABLE_BEAN;

	public static String[] MULTIPLE_SCOPE_TYPE_ANNOTATIONS;
	public static String[] MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_BEAN_CLASS;
	public static String[] MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_METHOD;
	public static String[] MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_FIELD;
	public static String[] MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE;
	public static String[] STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE;
	public static String[] ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD;
	public static String[] ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_GENERIC_TYPE;
	public static String[] ILLEGAL_SCOPE_FOR_SESSION_BEAN_WITH_GENERIC_TYPE;
	public static String[] ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN;
	public static String[] ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN;
	public static String[] ILLEGAL_SCOPE_FOR_PRODUCER_METHOD;
	public static String[] ILLEGAL_SCOPE_FOR_PRODUCER_FIELD;
	public static String[] ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED;
	public static String[] ILLEGAL_SCOPE_FOR_INTERCEPTOR;
	public static String[] ILLEGAL_SCOPE_FOR_DECORATOR;

	public static String[] PRODUCER_ANNOTATED_INJECT;
	public static String[] PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES;
	public static String[] PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES;
	public static String[] OBSERVER_ANNOTATED_INJECT;
	public static String[] OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED;
	public static String[] ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN;
	public static String[] MULTIPLE_DISPOSING_PARAMETERS;
	public static String[] DISPOSER_ANNOTATED_INJECT;
	public static String[] ILLEGAL_DISPOSER_IN_SESSION_BEAN;
	public static String[] NO_PRODUCER_MATCHING_DISPOSER;
	public static String[] MULTIPLE_DISPOSERS_FOR_PRODUCER;
	public static String[] ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN;
	public static String[] MULTIPLE_INJECTION_CONSTRUCTORS;
	public static String[] CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES;
	public static String[] CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES;
	public static String[] GENERIC_METHOD_ANNOTATED_INJECT;
	public static String[] STATIC_METHOD_ANNOTATED_INJECT;
	public static String[] MULTIPLE_OBSERVING_PARAMETERS;
	public static String[] ILLEGAL_OBSERVER_IN_SESSION_BEAN;
	public static String[] ILLEGAL_CONDITIONAL_OBSERVER;

	public static String[] BOTH_INTERCEPTOR_AND_DECORATOR;
	public static String[] SESSION_BEAN_ANNOTATED_INTERCEPTOR;
	public static String[] SESSION_BEAN_ANNOTATED_DECORATOR;
	public static String[] PRODUCER_IN_INTERCEPTOR;
	public static String[] PRODUCER_IN_DECORATOR;
	public static String[] DISPOSER_IN_INTERCEPTOR;
	public static String[] DISPOSER_IN_DECORATOR;
	public static String[] MULTIPLE_DELEGATE;
	public static String[] MISSING_DELEGATE;
	public static String[] ILLEGAL_INJECTION_POINT_DELEGATE;
	public static String[] ILLEGAL_BEAN_DECLARING_DELEGATE;
	public static String[] DELEGATE_HAS_ILLEGAL_TYPE;
	public static String[] ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING;
	public static String[] ILLEGAL_INTERCEPTOR_BINDING_CLASS;
	public static String[] ILLEGAL_INTERCEPTOR_BINDING_METHOD;
	public static String[] CONFLICTING_INTERCEPTOR_BINDINGS;
	public static String[] OBSERVER_IN_INTERCEPTOR;
	public static String[] OBSERVER_IN_DECORATOR;
	public static String[] INTERCEPTOR_IS_ALTERNATIVE;
	public static String[] DECORATOR_IS_ALTERNATIVE;
	public static String[] MISSING_INTERCEPTOR_BINDING;

	public static String[] ILLEGAL_SPECIALIZING_MANAGED_BEAN;
	public static String[] ILLEGAL_SPECIALIZING_SESSION_BEAN;
	public static String[] ILLEGAL_SPECIALIZING_PRODUCER_STATIC;
	public static String[] ILLEGAL_SPECIALIZING_PRODUCER_OVERRIDE;
	public static String[] MISSING_TYPE_IN_SPECIALIZING_BEAN;
	public static String[] CONFLICTING_NAME_IN_SPECIALIZING_BEAN;
	public static String[] INTERCEPTOR_ANNOTATED_SPECIALIZES;
	public static String[] DECORATOR_ANNOTATED_SPECIALIZES;
	public static String[] INCONSISTENT_SPECIALIZATION;

	public static String[] ILLEGAL_INJECTING_USERTRANSACTION_TYPE;
	public static String[] ILLEGAL_INJECTING_INJECTIONPOINT_TYPE;
	public static String[] ILLEGAL_QUALIFIER_IN_STEREOTYPE;
	public static String[] EMPTY_ALTERNATIVE_BEAN_CLASS_NAME;
	public static String[] UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME;
	public static String[] ILLEGAL_ALTERNATIVE_BEAN_CLASS;
	public static String[] EMPTY_ALTERNATIVE_ANNOTATION_NAME;
	public static String[] UNKNOWN_ALTERNATIVE_ANNOTATION_NAME;
	public static String[] ILLEGAL_ALTERNATIVE_ANNOTATION;
	public static String[] DUPLICATE_ALTERNATIVE_TYPE;
	public static String[] EMPTY_DECORATOR_BEAN_CLASS_NAME;
	public static String[] UNKNOWN_DECORATOR_BEAN_CLASS_NAME;
	public static String[] ILLEGAL_DECORATOR_BEAN_CLASS;
	public static String[] DUPLICATE_DECORATOR_CLASS;
	public static String[] EMPTY_INTERCEPTOR_CLASS_NAME;
	public static String[] UNKNOWN_INTERCEPTOR_CLASS_NAME;
	public static String[] ILLEGAL_INTERCEPTOR_CLASS;
	public static String[] DUPLICATE_INTERCEPTOR_CLASS;

	public static String[] MISSING_BEANS_XML = createTemplate(0, 0);
	
	public static String[] OBSERVER_ASYNC_PRIORITY;
	public static String[] ILLEGAL_CONDITIONAL_OBSERVER_ASYNC;
	public static String[] MULTIPLE_OBSERVING_PARAMETERS_ASYNC;
	public static String[] CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ASYNC;
	public static String[] OBSERVER_ANNOTATED_INJECT_ASYNC;
	public static String[] OBSERVER_ASYNC_IN_DECORATOR;
	public static String[] OBSERVER_ASYNC_IN_INTERCEPTOR;
	public static String[] ILLEGAL_OBSERVER_ASYNC_IN_SESSION_BEAN;
	public static String[] PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ASYNC;
	public static String[] OBSERVER_ASYNC_PARAMETER_ILLEGALLY_ANNOTATED;
	public static String[] OBSERVER_AND_OBSERVER_ASYNC_ERROR;
	public static String[] OBSERVER_AND_OBSERVER_ASYNC_METHOD_ERROR;
	
	static {
		try {
			init();
		} catch (NoSuchFieldException e) {
			CDICorePlugin.getDefault().logError(e);
		} catch (IllegalAccessException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * null values in template stand for null values in the final result built in init().
	 * non-nulls in template stand for values to be initialized in init() by default rules.
	 * 
	 * @param sinceIndex minimal version index for which validation rule is relevant 
	 * @param untilIndex maximum version index for which validation rule is relevant 
	 * @return
	 */
	static String[] createTemplate(int sinceIndex, int untilIndex) {
		String[] result = new String[CDIVersion.getVersionCount()];
		for (int i = sinceIndex; i <= untilIndex; i++) {
			result[i] = "";
		}
		return result;
	}

	static void init() throws NoSuchFieldException, IllegalAccessException {
		Class<?>[] versionMessages = {
			CDIValidationMessages10.class,
			CDIValidationMessages11.class,
			CDIValidationMessages12.class,
			CDIValidationMessages20.class
		};
		for (Field f: CDIValidationMessages.class.getFields()) {
			if(!Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			String name = f.getName();
			f.setAccessible(true);
			String[] template = (String[])f.get(null);
			String[] values = new String[CDIVersion.getVersionCount()];
			f.set(null, values);

			String[] contributions = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				try {
					Field fi = versionMessages[i].getField(name);
					fi.setAccessible(true);
					contributions[i] = (String)fi.get(null);
				} catch (NoSuchFieldException e) {
					//field may be missing, it is not an error.
				}
			}
			for (int i = 0; i < values.length; i++) {
				if(template != null && template[i] == null) {
					continue;
				}
				for (int j = i; values[i] == null && j >= 0; j--) {
					if(contributions[j] != null) {
						values[i] = contributions[j];
					}
				}
			}
		}

	}

}