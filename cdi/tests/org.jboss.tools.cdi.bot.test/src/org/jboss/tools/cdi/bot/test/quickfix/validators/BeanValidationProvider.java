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

public class BeanValidationProvider extends AbstractValidationProvider {

	public BeanValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.SERIALIZABLE, 
				"which declares a passivating scope SessionScoped "));
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.MULTIPLE_BEAN_ELIGIBLE,
				"Multiple beans are eligible for injection to the injection point"));
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.NO_BEAN_ELIGIBLE,
				"No bean is eligible for injection to the injection point"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.DISPOSES, 
				"Bean constructor cannot have a parameter annotated @Disposes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.OBSERVES, 
				"Bean constructor cannot have a parameter annotated @Observes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.DISPOSES, 
				"Producer method has a parameter annotated @Disposes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.OBSERVES, 
				"Producer method has a parameter annotated @Observes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.DISPOSES, 
				"Disposer method cannot be annotated @Inject"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.OBSERVES, 
				"Observer method cannot be annotated @Inject"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.OBSERVES, 
				"Observer method has a parameter annotated @Disposes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.PRODUCES, 
				"Producer method or field cannot be annotated @Inject"));		
	}

}
