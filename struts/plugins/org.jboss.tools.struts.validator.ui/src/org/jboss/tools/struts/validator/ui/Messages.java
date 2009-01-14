package org.jboss.tools.struts.validator.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.validator.ui.messages"; //$NON-NLS-1$
	public static String ActionNames_Default;
	public static String ActionNames_Overwrite;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
