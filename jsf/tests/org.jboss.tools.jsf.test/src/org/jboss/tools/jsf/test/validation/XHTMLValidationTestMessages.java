package org.jboss.tools.jsf.test.validation;

import org.eclipse.osgi.util.NLS;

public class XHTMLValidationTestMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.test.validation.messages"; //$NON-NLS-1$

	public static String XHTML_CONTENT_TEMPLATE;
	public static String XHTML_GOOD_PUBLIC_ID;
	public static String XHTML_WRONG_PUBLIC_ID;
	public static String XHTML_GOOD_URI;
	public static String XHTML_WRONG_URI;
	public static String XHTML_GOOD_TAGNAME;
	public static String XHTML_WRONG_TAGNAME;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, XHTMLValidationTestMessages.class);
	}

}
