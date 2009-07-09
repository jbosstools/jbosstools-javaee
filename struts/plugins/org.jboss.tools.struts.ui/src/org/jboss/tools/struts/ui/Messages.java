package org.jboss.tools.struts.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.ui.messages"; //$NON-NLS-1$
	public static String SyncProjectStepView_Add;
	public static String SyncProjectStepView_Cancel;
	public static String SyncProjectStepView_Confirmation;
	public static String SyncProjectStepView_Delete;
	public static String SyncProjectStepView_DeleteModule;
	public static String SyncProjectStepView_DeleteURI;
	public static String SyncProjectStepView_OK;
	public static String SyncProjectStepView_Restore;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
