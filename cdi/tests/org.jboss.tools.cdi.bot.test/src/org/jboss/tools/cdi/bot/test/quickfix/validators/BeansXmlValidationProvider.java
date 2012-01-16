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

import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;


public class BeansXmlValidationProvider extends AbstractValidationProvider {

	public BeansXmlValidationProvider() {
		super();
	}

	@Override
	void init() {
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_CLASS, 
				"There is no class with the specified name"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_ANNOTATION,
				"There is no annotation with the specified name"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.ALTERNATIVE_BEAN_XML,
				"<class> element must specify the name of an alternative bean class"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.ALTERNATIVE_STEREOTYPE_BEAN_XML,
				"<stereotype> element must specify the name of an @Alternative stereotype annotation"));
		
	}
	
}
