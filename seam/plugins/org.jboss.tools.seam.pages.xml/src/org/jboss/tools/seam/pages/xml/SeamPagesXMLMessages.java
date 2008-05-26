package org.jboss.tools.seam.pages.xml;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SeamPagesXMLMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.pages.xml.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SeamPagesXMLMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
