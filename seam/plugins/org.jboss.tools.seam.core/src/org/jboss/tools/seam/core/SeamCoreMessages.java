package org.jboss.tools.seam.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SeamCoreMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.core.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SeamCoreMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
