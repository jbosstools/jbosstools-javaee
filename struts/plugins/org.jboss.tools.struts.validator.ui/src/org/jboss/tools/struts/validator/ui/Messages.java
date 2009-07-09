package org.jboss.tools.struts.validator.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.validator.ui.messages"; //$NON-NLS-1$
	public static String AbstractResourcePathView_Browse;
	public static String AbstractResourcePathView_Label;
	public static String ActionNames_Default;
	public static String ActionNames_Overwrite;
	public static String FieldEditor_Default;
	public static String FieldEditor_Edit;
	public static String FieldEditor_IndexedListProperty;
	public static String FieldEditor_Override;
	public static String FieldEditor_Page;
	public static String FormsetsBar_AddValidationRule;
	public static String FormsetsBar_Create;
	public static String FormsetsBar_CreateField;
	public static String FormsetsBar_CreateForm;
	public static String FormsetsBar_CreateFormset;
	public static String FormsetsBar_DeleteFormset;
	public static String FormsetsBar_Help;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
