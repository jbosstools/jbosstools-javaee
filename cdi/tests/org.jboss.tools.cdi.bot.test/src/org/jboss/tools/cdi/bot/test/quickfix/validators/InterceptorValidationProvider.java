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

public class InterceptorValidationProvider extends AbstractValidationProvider {

	public InterceptorValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.NAMED, 
				"Interceptor should not have a name"));
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.SPECIALIZES,
				"Interceptor should not be annotated @Specializes"));
				
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.PRODUCES, 
				"Producer cannot be declared in an interceptor"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.DISPOSES, 
				"Interceptor has a method annotated @Disposes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.OBSERVES, 
				"Interceptor cannot have a method with a parameter annotated @Observes"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.STATELESS, 
				"Bean class of a session bean cannot be annotated @Interceptor"));
		
	}

}

