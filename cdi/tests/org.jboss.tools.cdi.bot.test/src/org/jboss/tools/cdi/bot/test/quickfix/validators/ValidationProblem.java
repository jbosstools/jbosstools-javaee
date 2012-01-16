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

public class ValidationProblem {
	
	private ProblemsType problemType;
	
	private ValidationType validationType;
	
	private String message;
	
	public ValidationProblem(ProblemsType problemType, ValidationType validationType, String message) {
		this.problemType = problemType;
		this.validationType = validationType;
		this.message = message;
	}

	public ProblemsType getProblemType() {
		return problemType;
	}

	public ValidationType getValidationType() {
		return validationType;
	}

	public String getMessage() {
		return message;
	}
}
