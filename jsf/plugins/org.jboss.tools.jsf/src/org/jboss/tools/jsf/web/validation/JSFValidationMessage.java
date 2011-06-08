package org.jboss.tools.jsf.web.validation;

import org.eclipse.osgi.util.NLS;

public class JSFValidationMessage {

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages"; //$NON-NLS-1$

	public static String UNKNOWN_COMPOSITION_COMPONENT_NAME;
	public static String UNKNOWN_COMPOSITION_COMPONENT_ATTRIBUTE;

	public static String SEARCHING_RESOURCES;
	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_PROJECT;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFValidationMessage.class);
	}
}