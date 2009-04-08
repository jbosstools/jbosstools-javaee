package org.jboss.tools.seam.pages.xml;

import org.eclipse.osgi.util.NLS;

public class SeamPagesXMLMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.pages.xml.messages"; //$NON-NLS-1$

	public static String SEAM_PAGES_XML_PLUGIN_NO_MESSAGE;
	public static String WARNING;
	public static String ATTRIBUTE_VIEW_ID_IS_NOT_CORRECT;
	public static String TEMPLATE_IS_NOT_SPECIFIED;
	public static String TEMPLATE_DOES_NOT_EXIST;
	public static String THE_VIEW_WITH_PATH_IS_ALREADY_CREATED;
	public static String TEMPLATE_IS_NOT_FOUND;
	public static String PAGES_CONFIG_CHANGES;
	public static String UPDATE_REFERENCE_TO_PAGE;

	private SeamPagesXMLMessages() {
	}

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SeamPagesXMLMessages.class);		
	}
	
}
