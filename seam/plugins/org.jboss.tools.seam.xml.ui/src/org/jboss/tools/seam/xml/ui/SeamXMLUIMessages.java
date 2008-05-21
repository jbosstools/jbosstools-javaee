package org.jboss.tools.seam.xml.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SeamXMLUIMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.xml.ui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SeamXMLUIMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
