package org.jboss.tools.cdi.bot.test.quickfix.validators;

import java.util.ArrayList;

import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;

public class ScopeValidationProvider extends AbstractValidationProvider {

	public ScopeValidationProvider() {
		super();
	}
	
	@Override
	void init() {
		validationErrors.get("Warnings").add("Scope annotation type must be annotated " +
				"with @Retention(RUNTIME)");
		validationErrors.get("Warnings").add("Scope annotation type must be annotated with " +
				"@Target");
		
		warningsAnnotation.add(CDIAnnotationsType.RETENTION);
		warningsAnnotation.add(CDIAnnotationsType.TARGET);
	}
	
	public ArrayList<String> getAllWarningForAnnotationType(
			CDIAnnotationsType annotationType) {
		int warningIndex = 0;
		switch(annotationType) {
		case RETENTION:
			warningIndex = 0;
			break;
		case TARGET:
			warningIndex = 1;
			break;
		}
		warningsForAnnotationType.add(validationErrors.get("Warnings").get(warningIndex));
		return warningsForAnnotationType;
	}
	
}
