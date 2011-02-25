package org.jboss.tools.jsf.ui;

import org.eclipse.osgi.util.NLS;

public class JsfUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.ui.messages"; //$NON-NLS-1$
	public static String DataTableWizardPage_BeanProperties;
	public static String DataTableWizardPage_DeselectAll;
	public static String DataTableWizardPage_Properties;
	public static String DataTableWizardPage_SelectAll;
	public static String DataTableWizardPage_ValueELNotCorrect;
	public static String DataTableWizardPage_ValueMustBeSetWithEL;
	public static String FacesConfigGuiEditor_DiagramTitle;
	public static String JSFDiagramEditPart_JSFDiagram;
	public static String JSFKnowledgeBaseAdapter_Browse;
	public static String JSFKnowledgeBaseAdapter_Edit;
	public static String JSFManagedPropertyNameAdapter_Rename;
	public static String LinkEditPart_Link;
	
	public static String RENAME_METHOD_PARTICIPANT_GETTER_WARNING;
	public static String RENAME_METHOD_PARTICIPANT_SETTER_WARNING;
	public static String RENAME_METHOD_PARTICIPANT_OUT_OF_SYNC_PROJECT;
	public static String RENAME_METHOD_PARTICIPANT_ERROR_READ_ONLY_FILE;
	public static String RENAME_METHOD_PARTICIPANT_UPDATE_METHOD_REFERENCES;
	public static String RENAME_METHOD_PARTICIPANT_QUESTION_DIALOG_TITLE;
	public static String RENAME_METHOD_PARTICIPANT_QUESTION_DIALOG_MESSAGE;
	
	public static String RESOURCE_BUNDLES_RENAME_PARTICIPANT_UPDATE_BUNDLE_REFERENCES;
	
	public static String REFACTOR_CONTRIBUTOR_MAIN_MENU;
	public static String REFACTOR_CONTRIBUTOR_RENAME_EL_VARIABLE;
	public static String REFACTOR_CONTRIBUTOR_RENAME_MESSAGE_PROPERTY;
	public static String EL_REFACTOR_RENAME_HANDLER_ERROR;
	public static String RENAME_EL_VARIABLE_WIZARD_EL_VARIABLE_NAME;
	public static String RENAME_MESSAGE_PROPERTY_WIZARD_PROPERTY_NAME;
	
	public static String IS_JSF_CHECK_NEED;
	public static String IS_KB_NATURE_CHECK_NEED;
	public static String IS_JSF_NATURE_CHECK_NEED;
	public static String ADD_JSF_CAPABILITIES_BUTTTON_LABEL;
	public static String ENABLE_JSF_CAPABILITIES_TEXT;
	public static String ENABLE_JSF_CODE_COMPLETION_BUTTON_LABEL;
	public static String ENABLE_JSF_CODE_COMPLETION_TEXT;
	public static String DONT_SHOW_CHECKER_DIALOG;
	public static String MISSING_NATURES_INFO_MESSAGE_TITLE;
	public static String SKIP_BUTTON_LABEL;
	
	public static String MESSAGES_FILE_RENAME_PARTICIPANT_UPDATE_MESSAGE_BUNDLE_REFERENCES;
	public static String MESSAGES_FILE_RENAME_PARTICIPANT_FILE_NAME_SHOULD_BE_WITH_THE_SAME_EXTENSION;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JsfUIMessages.class);
	}

	private JsfUIMessages() {
	}
}
