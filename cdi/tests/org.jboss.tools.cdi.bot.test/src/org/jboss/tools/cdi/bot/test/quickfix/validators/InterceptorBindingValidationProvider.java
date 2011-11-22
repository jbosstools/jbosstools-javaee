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

public class InterceptorBindingValidationProvider extends AbstractValidationProvider {

	public InterceptorBindingValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Annotation-valued member of an interceptor " +
				"binding type should be annotated @Nonbinding");
		validationErrors.get("Warnings").add("Array-valued member of an interceptor " +
				"binding type must be annotated @Nonbinding");
		
		warningsAnnotation.add(CDIAnnotationsType.NONBINDING);
	}
	
	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		int warningIndex = 0;
		switch(annotationType) {
		case NONBINDING:
			warningIndex = 0;
			warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
			warningIndex = 1;
			break;	
		}
		warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
		return warningsForAnnotationType;
	}
	
}
