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

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;

abstract public class CDIValidationErrorManager extends KBValidator {

	public static final String MESSAGE_ID_ATTRIBUTE_NAME = "CDI_message_id"; //$NON-NLS-1$

	public static final int ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID = 1;
	public static final int ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID = 2;
	public static final int ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID = 3;
	public static final int ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID = 4;
	public static final int MULTIPLE_DISPOSERS_FOR_PRODUCER_ID = 5;
	public static final int MULTIPLE_INJECTION_CONSTRUCTORS_ID = 6;
	public static final int UNSATISFIED_INJECTION_POINTS_ID = 7;
	public static final int AMBIGUOUS_INJECTION_POINTS_ID = 8;
	public static final int NOT_PASSIVATION_CAPABLE_BEAN_ID = 9;
	public static final int ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID = 10;
	public static final int MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID = 11;
	public static final int MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID = 12;
	public static final int MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID = 13;
	public static final int MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID = 14;
	public static final int MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID = 15;
	public static final int MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID = 16;
	public static final int MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID = 17;
	public static final int MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID = 18;
	public static final int MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID = 19;
	public static final int MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID = 20;
	public static final int PRODUCER_ANNOTATED_INJECT_ID = 21;
	public static final int OBSERVER_ANNOTATED_INJECT_ID = 22;
	public static final int DISPOSER_ANNOTATED_INJECT_ID = 23;
	public static final int CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ID = 24;
	public static final int CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID = 25;
	public static final int PRODUCER_IN_INTERCEPTOR_ID = 26;
	public static final int PRODUCER_IN_DECORATOR_ID = 27;
	public static final int DISPOSER_IN_INTERCEPTOR_ID = 28;
	public static final int DISPOSER_IN_DECORATOR_ID = 29;
	public static final int STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID = 30;
	public static final int INTERCEPTOR_HAS_NAME_ID = 31;
	public static final int DECORATOR_HAS_NAME_ID = 32;
	public static final int STEREOTYPE_IS_ANNOTATED_TYPED_ID = 33;
	public static final int INTERCEPTOR_ANNOTATED_SPECIALIZES_ID = 34;
	public static final int DECORATOR_ANNOTATED_SPECIALIZES_ID = 35;
	public static final int PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES_ID = 36;
	public static final int PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ID = 37;
	public static final int OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED_ID = 38;


	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return severityPreferences.getProjectPreference(project, preferenceKey);
	}

	SeverityPreferences severityPreferences = CDIPreferences.getInstance();

	protected void setSeverityPreferences(SeverityPreferences severityPreferences) {
		this.severityPreferences = (severityPreferences == null) ? CDIPreferences.getInstance() : severityPreferences;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	@Override
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return CDIPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}
}