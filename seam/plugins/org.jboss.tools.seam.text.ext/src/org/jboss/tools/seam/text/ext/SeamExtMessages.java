package org.jboss.tools.seam.text.ext;

import org.eclipse.osgi.util.NLS;

public class SeamExtMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.text.ext.Messages"; //$NON-NLS-1$

	public static String OpenAs; 
	public static String SeamComponent;
	public static String SeamRole;
	public static String SeamFactory;
	public static String SeamBijected;

	//
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, SeamExtMessages.class);
	}

	private  SeamExtMessages() {
	}
}
