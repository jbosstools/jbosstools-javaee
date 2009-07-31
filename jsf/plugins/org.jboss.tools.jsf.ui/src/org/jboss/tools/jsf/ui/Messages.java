package org.jboss.tools.jsf.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
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
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
