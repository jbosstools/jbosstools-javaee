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

public class StereotypeValidationProvider extends AbstractValidationProvider {
	
	public StereotypeValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Stereotype annotation type must be annotated with one of");
		validationErrors.get("Warnings").add("Stereotype annotation type must be annotated " +
				"with @Retention(RUNTIME)");
		validationErrors.get("Warnings").add("A stereotype should not be annotated @Typed");
		validationErrors.get("Errors").add("Stereotype declares a non-empty @Named annotation");
		
		warningsAnnotation.add(CDIAnnotationsType.TARGET);
		warningsAnnotation.add(CDIAnnotationsType.RETENTION);
		warningsAnnotation.add(CDIAnnotationsType.TYPED);		
		
		errorsAnnotation.add(CDIAnnotationsType.NAMED);
	}
	
	public ArrayList<String> getAllErrorsForAnnotationType(
			CDIAnnotationsType annotationType) {
		int errorIndex = 0;
		switch (annotationType) {
		case NAMED:
			errorIndex = 0;
			break;
		}
		errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
		return errorsForAnnotationType;
	}

	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		int warningIndex = 0;
		switch(annotationType) {
		case TARGET:
			warningIndex = 0;
			break;
		case RETENTION:
			warningIndex = 1;
			break;
		case TYPED:
			warningIndex = 2;
			break;
		}
		warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
		return warningsForAnnotationType;
	}
		
}
