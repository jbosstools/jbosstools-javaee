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

public class BeanValidationProvider extends AbstractValidationProvider {

	public BeanValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("which declares a passivating scope SessionScoped " +
				"must be passivation capable");
		validationErrors.get("Warnings").add("Multiple beans are eligible for injection to " +
				"the injection point");
		validationErrors.get("Errors").add("Bean constructor cannot have a parameter annotated " +
				"@Disposes");
		validationErrors.get("Errors").add("Bean constructor cannot have a parameter annotated " +
				"@Observes");
		validationErrors.get("Errors").add("Producer method has a parameter annotated @Disposes");
		validationErrors.get("Errors").add("Producer method has a parameter annotated @Observes");
		validationErrors.get("Errors").add("Disposer method cannot be annotated @Inject");
		validationErrors.get("Errors").add("Observer method cannot be annotated @Inject");
		validationErrors.get("Errors").add("Observer method has a parameter annotated @Disposes");
		validationErrors.get("Errors").add("Producer method or field cannot be annotated @Inject");
		
		warningsAnnotation.add(CDIAnnotationsType.SERIALIZABLE);
		warningsAnnotation.add(CDIAnnotationsType.INJECT);
		
		errorsAnnotation.add(CDIAnnotationsType.DISPOSES);
		errorsAnnotation.add(CDIAnnotationsType.OBSERVES);
		errorsAnnotation.add(CDIAnnotationsType.PRODUCES);
	}

	public ArrayList<String> getAllErrorsForAnnotationType(
			CDIAnnotationsType annotationType) {
		int errorIndex = 0;
		switch(annotationType) {
		case DISPOSES:
			errorIndex = 0;
			errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
			errorIndex = 2;
			errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
			errorIndex = 4;
			break;
		case OBSERVES:
			errorIndex = 1;
			errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
			errorIndex = 3;
			errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
			errorIndex = 5;
			errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
			errorIndex = 6;
			break;
		case PRODUCES:
			errorIndex = 7;
			break;
		}
		errorsForAnnotationType.add(validationErrors.get("Errors").get(errorIndex));
		return errorsForAnnotationType;
	}
	
	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		int warningIndex = 0;
		switch(annotationType) {
		case SERIALIZABLE:
			warningIndex = 0;
			break;		
		case INJECT:
			warningIndex = 1;
			break;
		}
		warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
		return warningsForAnnotationType;
	}

}
