package org.jboss.tools.seam.ui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class SeamUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.ui.messages";//$NON-NLS-1$

	private static ResourceBundle fResourceBundle; 

	public static String CREATE_NEW_SEAM_PROJECT;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_TITLE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_MESSAGE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_ERROR;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_GROUP_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_REVERSE_ENGINEER_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_EXISTING_ENTITIES_LABEL;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SeamUIMessages.class);		
	}
	
	private SeamUIMessages() {
		// cannot create new instance of this class
	}
	
	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
		}
		catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}
}