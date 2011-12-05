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

public class ScopeValidationProvider extends AbstractValidationProvider {

	public ScopeValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Scope annotation type must be annotated " +
				"with @Retention(RUNTIME)");
		validationErrors.get("Warnings").add("Scope annotation type must be annotated with " +
				"@Target");
		
		warningsAnnotation.add(CDIAnnotationsType.RETENTION);
		warningsAnnotation.add(CDIAnnotationsType.TARGET);
	}
	
	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		return validationErrors.get("Warnings");
	}
	
}
