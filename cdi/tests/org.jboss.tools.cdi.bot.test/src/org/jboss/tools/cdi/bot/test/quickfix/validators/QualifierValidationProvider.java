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

public class QualifierValidationProvider extends AbstractValidationProvider {

	public QualifierValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Qualifier annotation type must be annotated " +
				"with @Retention(RUNTIME)");
		validationErrors.get("Warnings").add("Qualifier annotation type must be annotated with " +
				"@Target");
		validationErrors.get("Warnings").add("Annotation-valued member of a qualifier type " +
				"should be annotated @Nonbinding");
		validationErrors.get("Warnings").add("Array-valued member of a qualifier type " +
				"should be annotated @Nonbinding");
		
		warningsAnnotation.add(CDIAnnotationsType.RETENTION);
		warningsAnnotation.add(CDIAnnotationsType.TARGET);
		warningsAnnotation.add(CDIAnnotationsType.NONBINDING);
	}

	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		return validationErrors.get("Warnings");
	}
	
}
