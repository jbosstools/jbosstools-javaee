package org.jboss.tools.seam.ui.dialog;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.ui.dialog.messages"; //$NON-NLS-1$
	public static String SeamFacetVersionChangeDialog_Add;
	public static String SeamFacetVersionChangeDialog_Libraries;
	public static String SeamFacetVersionChangeDialog_Libraries_to_be_added;
	public static String SeamFacetVersionChangeDialog_Libraries_to_be_removed;
	public static String SeamFacetVersionChangeDialog_New_Seam_Runtime;
	public static String SeamFacetVersionChangeDialog_Note;
	public static String SeamFacetVersionChangeDialog_Note_description;
	public static String SeamFacetVersionChangeDialog_Old_Seam_Runtime;
	public static String SeamFacetVersionChangeDialog_Project;
	public static String SeamFacetVersionChangeDialog_Seam_Runtime_Settings;
	public static String SeamFacetVersionChangeDialog_Set_Seam_Runtime;
	public static String SeamFacetVersionChangeDialog_Update_libraries;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
