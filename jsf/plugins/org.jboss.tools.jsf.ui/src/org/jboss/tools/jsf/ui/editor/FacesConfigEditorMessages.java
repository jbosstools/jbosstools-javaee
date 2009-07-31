/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.editor;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.jsf.ui.JsfUiPlugin;

/**
 * @author Igels
 *
 */
public class FacesConfigEditorMessages extends NLS{

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.ui.editor.FacesConfigEditorMessages"; //$NON-NLS-1$
	private static ResourceBundle fResourceBundle; 
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, FacesConfigEditorMessages.class);		
	}
	
	public static String JSFDIAGRAM_SELECT;
	public static String JSFDIAGRAM_MARQUEE;
	public static String JSFDIAGRAM_CREATE_NEW_CONNECTION;
	public static String JSFDIAGRAM_VIEW_TEMPLATE;
	public static String LOCALECONFIGFORM_DESCRIPTION;
	public static String LOCALECONFIGFORM_HEADER;
	public static String LOCALECONFIGFORM_SUPPORTEDLOCAL_TITLE;
	public static String LOCALECONFIGFORM_SUPPORTEDLOCAL_COLUMN_LABEL;
	public static String APPLICATIONCONFIGFORM_DESCRIPTION;
	public static String APPLICATIONCONFIGFORM_HEADER;
	public static String APPLICATIONCONFIGFORM_MESSAGEBUNDLE_COLUMN_LABEL;
	public static String MANAGEDBEANPROPERTYFORM_DESCRIPTION;
	public static String MANAGEDBEANPROPERTYFORM_HEADER;
	public static String LISTENTRIESFORM_DESCRIPTION;
	public static String LISTENTRIESFORM_HEADER;
	public static String LISTENTRIESFORM_ENTRY_COLUMN_LABEL;
	public static String MAPENTRIESFORM_DESCRIPTION;
	public static String MAPENTRIESFORM_HEADER;
	public static String MAPENTRIESFORM_ENTRY_KEY_COLUMN_LABEL;
	public static String MAPENTRIESFORM_ENTRY_VALUE_COLUMN_LABEL;
	
	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
		}
		catch (MissingResourceException x) {
			JsfUiPlugin.getPluginLog().logError(x);
			fResourceBundle = null;
		}
		return fResourceBundle;
	}	

}