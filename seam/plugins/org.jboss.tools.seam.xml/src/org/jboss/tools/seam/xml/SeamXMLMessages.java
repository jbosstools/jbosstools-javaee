package org.jboss.tools.seam.xml;

import org.eclipse.osgi.util.NLS;

public final class SeamXMLMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.seam.xml.messages";//$NON-NLS-1$

	private SeamXMLMessages() {
		// Do not instantiate
	}

	public static String SEAM_XML_PLUGIN_NO_MESSAGE;

	static {
		NLS.initializeMessages(BUNDLE_NAME, SeamXMLMessages.class);
	}
}