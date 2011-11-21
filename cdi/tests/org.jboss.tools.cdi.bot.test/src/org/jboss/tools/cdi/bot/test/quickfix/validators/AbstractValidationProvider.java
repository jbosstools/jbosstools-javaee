package org.jboss.tools.cdi.bot.test.quickfix.validators;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;

public abstract class AbstractValidationProvider implements ValidationErrorProvider {

	protected Map<String, ArrayList<String>> validationErrors = null;
	protected ArrayList<String> warningsForAnnotationType = null;
	protected ArrayList<String> errorsForAnnotationType = null;
	protected ArrayList<CDIAnnotationsType> warningsAnnotation = null;
	protected ArrayList<CDIAnnotationsType> errorsAnnotation = null;
	
	public AbstractValidationProvider() {
		validationErrors = new LinkedHashMap<String, ArrayList<String>>();
		warningsForAnnotationType = new ArrayList<String>();
		errorsForAnnotationType = new ArrayList<String>();
		warningsAnnotation = new ArrayList<CDIAnnotationsType>();
		errorsAnnotation = new ArrayList<CDIAnnotationsType>();
		
		validationErrors.put("Warnings", new ArrayList<String>());
		validationErrors.put("Errors", new ArrayList<String>());
		
		init();
	}
	
	abstract void init();

	public Map<String, ArrayList<String>> getAllValidationErrors() {
		return validationErrors;
	}
	
	public ArrayList<String> getAllErrorsForAnnotationType(
			CDIAnnotationsType annotationType) {
		return errorsForAnnotationType;
	}

	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		return warningsForAnnotationType;
	}

	public ArrayList<CDIAnnotationsType> getAllWarningsAnnotation() {
		return warningsAnnotation;
	}

	public ArrayList<CDIAnnotationsType> getAllErrorsAnnotation() {
		return errorsAnnotation;
	}
	
}
