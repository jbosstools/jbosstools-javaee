package org.jboss.tools.cdi.xml;

import org.eclipse.osgi.util.NLS;

public final class CDIXMLMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.xml.messages";//$NON-NLS-1$

	private CDIXMLMessages() {
		// Do not instantiate
	}

	public static String SEAM_XML_PLUGIN_NO_MESSAGE;
	public static String CANNOT_FIND_MATCHING_RULE_FOR_PATH;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIXMLMessages.class);
	}
}