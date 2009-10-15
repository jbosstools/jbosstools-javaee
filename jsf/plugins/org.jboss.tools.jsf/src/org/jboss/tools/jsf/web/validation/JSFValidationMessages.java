package org.jboss.tools.jsf.web.validation;

import org.eclipse.osgi.util.NLS;

public class JSFValidationMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFValidationMessages.class);
	}

	public static String EL_VALIDATOR_ERROR_VALIDATING;
	public static String EL_VALIDATOR_SETTER;
	public static String EL_VALIDATOR_GETTER;
}