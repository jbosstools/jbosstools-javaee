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


public class BeansXmlValidationProvider extends AbstractValidationProvider {

	public BeansXmlValidationProvider() {
		super();
	}

	@Override
	void init() {
		validationErrors.get("Errors").add("There is no class with the specified name");
		validationErrors.get("Errors").add("There is no annotation with the specified name");
		validationErrors.get("Errors").add("<class> element must specify the name of an " +
				"alternative bean class");
		validationErrors.get("Errors").add("<stereotype> element must specify the name of " +
				"an @Alternative stereotype annotation");		
		
		errorsAnnotation.add(CDIAnnotationsType.INJECT);
	}
	
	public ArrayList<String> getAllErrorsForAnnotationType(
			CDIAnnotationsType annotationType) {		
		return validationErrors.get("Errors");
	}
	
}
