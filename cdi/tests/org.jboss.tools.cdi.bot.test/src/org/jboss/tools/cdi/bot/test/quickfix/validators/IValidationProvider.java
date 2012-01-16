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

import java.util.List;

import org.jboss.tools.cdi.bot.test.annotations.ValidationType;

public interface IValidationProvider {
		
	/**
	 * Method gets all validation problems type for component
	 *  
	 * @param annotationType
	 * @return
	 */
	List<ValidationType> getAllValidationProblemsType();
	
	/**
	 * Method gets all validation errors showed in Problems View as warnings 
	 * according to entered annotation 
	 *  
	 * @param annotationType
	 * @return
	 */
	List<String> getAllWarningForAnnotationType(ValidationType annotationType);
		
	/**
	 * Method gets all validation errors showed in Problems View as errors 
	 * according to entered annotation 
	 * 
	 * @param annotationType
	 * @return
	 */
	List<String> getAllErrorsForAnnotationType(ValidationType annotationType);

	/**
	 * Method gets all annotations for which exist validation errors showed in Problems View
	 * marked as warning
	 *  	
	 * @return
	 */
	List<ValidationType> getAllWarningsAnnotation();
	
	/**
	 * Method gets all annotations for which exist validation errors showed in Problems View
	 * marked as errors
	 * 
	 * @return
	 */
	List<ValidationType> getAllErrorsAnnotation();

}
