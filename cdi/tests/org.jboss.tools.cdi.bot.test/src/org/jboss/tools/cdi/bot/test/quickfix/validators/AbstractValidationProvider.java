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
import java.util.List;

import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;

public abstract class AbstractValidationProvider implements IValidationProvider {

	protected static List<ValidationProblem> problems = null;
	
	public AbstractValidationProvider() {		
		problems = new ArrayList<ValidationProblem>();		
		init();
	}
	
	abstract void init();

	public List<String> getAllErrorsForAnnotationType(
			ValidationType annotationType) {
		List<String> messages = new ArrayList<String>();
		for (ValidationProblem problem: problems) {
			if (problem.getProblemType() == ProblemsType.ERRORS && 
				problem.getValidationType() == annotationType) {
				messages.add(problem.getMessage());
			}
		}
		return messages;
	}
	
	public List<String> getAllWarningForAnnotationType(
			ValidationType annotationType) {
		List<String> messages = new ArrayList<String>();
		for (ValidationProblem problem: problems) {
			if (problem.getProblemType() == ProblemsType.WARNINGS && 
				problem.getValidationType() == annotationType) {
				messages.add(problem.getMessage());
			}
		}
		return messages;
	}
	
	public List<ValidationType> getAllWarningsAnnotation() {
		List<ValidationType> annotations = new ArrayList<ValidationType>();
		for (ValidationProblem problem: problems) {
			if (problem.getProblemType() == ProblemsType.WARNINGS) {
				annotations.add(problem.getValidationType());
			}
		}
		return annotations;
	}
	
	public List<ValidationType> getAllErrorsAnnotation() {
		List<ValidationType> annotations = new ArrayList<ValidationType>();
		for (ValidationProblem problem: problems) {
			if (problem.getProblemType() == ProblemsType.ERRORS) {
				annotations.add(problem.getValidationType());
			}
		}
		return annotations;
	}
	
	public List<ValidationType> getAllValidationProblemsType() {
		List<ValidationType> annotations = new ArrayList<ValidationType>();
		for (ValidationProblem problem: problems) {
			annotations.add(problem.getValidationType());
		}
		return annotations;
	}
	
}
