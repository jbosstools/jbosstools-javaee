package org.jboss.tools.jsf.vpe.richfaces;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.vpe.richfaces.messages"; //$NON-NLS-1$
	public static String RichFacesOrderingList_DownLabel;
	public static String RichFacesOrderingList_FirstLabel;
	public static String RichFacesOrderingList_LastLabel;
	public static String RichFacesOrderingList_UpLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
