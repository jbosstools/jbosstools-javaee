package org.jboss.tools.cdi.bot.test.quickfix.validators;

import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;

import java.util.ArrayList;
import java.util.Map;

public interface ValidationErrorProvider {
	
	Map<String, ArrayList<String>> getAllValidationErrors();
	
	ArrayList<String> getAllWarningForAnnotationType(CDIAnnotationsType annotationType);
		
	ArrayList<String> getAllErrorsForAnnotationType(CDIAnnotationsType annotationType);

	ArrayList<CDIAnnotationsType> getAllWarningsAnnotation();
	
	ArrayList<CDIAnnotationsType> getAllErrorsAnnotation();
}
