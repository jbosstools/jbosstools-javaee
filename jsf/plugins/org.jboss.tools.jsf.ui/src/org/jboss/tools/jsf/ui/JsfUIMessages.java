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
	public static String RENAME_METHOD_PARTICIPANT_OUT_OF_SYNC_FILE;
	public static String RENAME_METHOD_PARTICIPANT_ERROR_PHANTOM_FILE;
	public static String RENAME_METHOD_PARTICIPANT_ERROR_READ_ONLY_FILE;
	public static String RENAME_METHOD_PARTICIPANT_UPDATE_METHOD_REFERENCES;
	public static String RESOURCE_BUNDLES_RENAME_PARTICIPANT_UPDATE_BUNDLE_REFERENCES;
	
	public static String REFACTOR_CONTRIBUTOR_MAIN_MENU;
	public static String REFACTOR_CONTRIBUTOR_RENAME_EL_VARIABLE;
	public static String EL_REFACTOR_RENAME_HANDLER_ERROR;
	public static String RENAME_EL_VARIABLE_WIZARD_EL_VARIABLE_NAME;
	public static String UI_WIZARD_XHTML_NEW_TITLE;
	public static String UI_WIZARD_XHTML_NEW_Description;
	
	public static String NewXHTMLTemplatesWizardPage_0;
	public static String NewXHTMLTemplatesWizardPage_1;
	public static String NewXHTMLTemplatesWizardPage_2;
	public static String NewXHTMLTemplatesWizardPage_3;
	public static String NewXHTMLTemplatesWizardPage_4;
	public static String NewXHTMLTemplatesWizardPage_5;
	public static String NewXHTMLTemplatesWizardPage_6;
	public static String NewXHTMLTemplatesWizardPage_7;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JsfUIMessages.class);
	}

	private JsfUIMessages() {
	}
}
