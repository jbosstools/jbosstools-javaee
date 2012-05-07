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
				"There is no class"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_ANNOTATION,
				"There is no annotation"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_ALTERNATIVE_STEREOTYPE,
				"is not @Alternative stereotype annotation"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_ALTERNATIVE,
				"is not an alternative bean class"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_DECORATOR,
				"is not a decorator bean class"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NO_INTERCEPTOR,
				"is not an interceptor class"));
		
	}
	
}
