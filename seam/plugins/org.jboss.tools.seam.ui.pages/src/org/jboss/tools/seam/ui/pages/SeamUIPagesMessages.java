package org.jboss.tools.seam.ui.pages;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SeamUIPagesMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.ui.pages.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SeamUIPagesMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
