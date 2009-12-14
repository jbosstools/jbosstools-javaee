package org.jboss.tools.cdi.xml.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CDIXMLUIMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.xml.ui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private CDIXMLUIMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
