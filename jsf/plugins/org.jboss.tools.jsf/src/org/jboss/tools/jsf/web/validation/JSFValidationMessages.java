package org.jboss.tools.jsf.web.validation;

import org.eclipse.osgi.util.NLS;

public class JSFValidationMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages"; //$NON-NLS-1$

	public static String UNKNOWN_EL_VARIABLE_NAME;
	public static String UNKNOWN_EL_VARIABLE_PROPERTY_NAME;
	public static String UNPAIRED_GETTER_OR_SETTER;
	public static String EL_SYNTAX_ERROR;

	public static String VALIDATING_EL_FILE;

	public static String EL_VALIDATOR_ERROR_VALIDATING;
	public static String EL_VALIDATOR_SETTER;
	public static String EL_VALIDATOR_GETTER;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFValidationMessages.class);
	}
}