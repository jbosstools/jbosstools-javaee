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

public class StereotypeValidationProvider extends AbstractValidationProvider {
	
	public StereotypeValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.TARGET, 
				"Stereotype annotation type must be annotated with one of"));
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.RETENTION,
				"Stereotype annotation type must be annotated with @Retention(RUNTIME)"));
		
		problems.add(new ValidationProblem(ProblemsType.WARNINGS, ValidationType.TYPED,
				"A stereotype should not be annotated @Typed"));
		
		problems.add(new ValidationProblem(ProblemsType.ERRORS, ValidationType.NAMED, 
				"Stereotype declares a non-empty @Named annotation"));
						
	}
		
}
