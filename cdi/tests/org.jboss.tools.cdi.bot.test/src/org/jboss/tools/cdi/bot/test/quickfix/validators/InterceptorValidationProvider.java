/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.quickfix.validators;

import java.util.ArrayList;

import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;

public class InterceptorValidationProvider extends AbstractValidationProvider {

	public InterceptorValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Interceptor should not have a name");
		validationErrors.get("Warnings").add("Interceptor should not be annotated " +
				"@Specializes");
		validationErrors.get("Errors").add("Producer cannot be declared in an " +
				"interceptor");
		validationErrors.get("Errors").add("Interceptor has a method annotated " +
				"@Disposes");
		validationErrors.get("Errors").add("Interceptor cannot have a method with a " +
				"parameter annotated @Observes");
		validationErrors.get("Errors").add("Bean class of a session bean cannot be annotated " +
				"@Interceptor");
		
		warningsAnnotation.add(CDIAnnotationsType.NAMED);
		warningsAnnotation.add(CDIAnnotationsType.SPECIALIZES);
		
		errorsAnnotation.add(CDIAnnotationsType.PRODUCES);
		errorsAnnotation.add(CDIAnnotationsType.DISPOSES);
		errorsAnnotation.add(CDIAnnotationsType.OBSERVES);
		errorsAnnotation.add(CDIAnnotationsType.STATELESS);
	}

	public ArrayList<String> getAllErrorsForAnnotationType(
			CDIAnnotationsType annotationType) {
		int errorIndex = 0;
		switch(annotationType) {
		case PRODUCES:
			errorIndex = 0;
			break;
		case DISPOSES:
			errorIndex = 1;
			break;
		case OBSERVES:
			errorIndex = 2;
			break;
		case STATELESS:
			errorIndex = 3;
			break;
		}
		errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
		return errorsForAnnotationType;
	}

	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		int warningIndex = 0;
		switch(annotationType) {
		case NAMED:
			warningIndex = 0;
			break;
		case SPECIALIZES:
			warningIndex = 1;
			break;		
		}
		warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
		return warningsForAnnotationType;
	}

}

