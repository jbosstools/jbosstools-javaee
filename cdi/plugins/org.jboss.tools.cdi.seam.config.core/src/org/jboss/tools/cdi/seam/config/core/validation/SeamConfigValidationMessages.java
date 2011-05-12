package org.jboss.tools.cdi.seam.config.core.validation;

import org.eclipse.osgi.util.NLS;

public class SeamConfigValidationMessages {

	private static final String BUNDLE_NAME = SeamConfigValidationMessages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
	
	public static String UNRESOLVED_TYPE;
	public static String UNRESOLVED_MEMBER;
	public static String UNRESOLVED_METHOD;
	public static String UNRESOLVED_CONSTRUCTOR;
	public static String ANNOTATION_EXPECTED;

	static {
		NLS.initializeMessages(BUNDLE_NAME, SeamConfigValidationMessages.class);
	}

}
