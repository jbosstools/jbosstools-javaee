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

import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;

import java.util.ArrayList;
import java.util.Map;

public interface ValidationErrorProvider {
	
	/**
	 * Method gets all validation errors showed in Problems View for actual component
	 * @return
	 */
	Map<String, ArrayList<String>> getAllValidationErrors();
	
	/**
	 * Method gets all validation errors showed in Problems View as warnings 
	 * according to entered annotation 
	 *  
	 * @param annotationType
	 * @return
	 */
	ArrayList<String> getAllWarningForAnnotationType(CDIAnnotationsType annotationType);
		
	/**
	 * Method gets all validation errors showed in Problems View as errors 
	 * according to entered annotation 
	 * 
	 * @param annotationType
	 * @return
	 */
	ArrayList<String> getAllErrorsForAnnotationType(CDIAnnotationsType annotationType);

	/**
	 * Method gets all annotations for which exist validation errors showed in Problems View
	 * marked as warning
	 *  	
	 * @return
	 */
	ArrayList<CDIAnnotationsType> getAllWarningsAnnotation();
	
	/**
	 * Method gets all annotations for which exist validation errors showed in Problems View
	 * marked as errors
	 * 
	 * @return
	 */
	ArrayList<CDIAnnotationsType> getAllErrorsAnnotation();

}
