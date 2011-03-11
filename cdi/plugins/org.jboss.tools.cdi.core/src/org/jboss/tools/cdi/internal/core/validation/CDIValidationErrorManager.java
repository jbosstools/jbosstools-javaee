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

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return CDIPreferences.getInstance().getProjectPreference(project, preferenceKey);
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