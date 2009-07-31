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
package org.jboss.tools.jsf.messages;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;


public class JSFUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.messages.messages";//$NON-NLS-1$
	private static ResourceBundle fResourceBundle; 

	public static String IT_ISNT_CORRECT_TO_MAKE_LINK_TO_A_PATTERN;
	public static String WARNING;
	public static String YES;
	public static String NO;
	public static String THE_VIEW_WITH_PATH_IS_ALREADY_CREATED;
	public static String YOU_WANT_TO_ADD_ADDITIONAL_NAVIGATION_RULE_WITH_SAME_FROM_VIEW_ID;
	public static String YOU_WANT_TO_CREATE_AN_ADDITIONAL_VIEW_WITH_THE_SAME_FROM_VIEW_ID;
	public static String YOU_WANT_TO_CREATE_A_NAVIGATION_RULE_FOR_THIS_FROM_VIEW_ID;
	public static String OK;
	public static String CANCEL;
	public static String TEMPLATE_IS_NOT_FOUND;
	public static String ATTRIBUTE_FROM_VIEW_ID_IS_NOT_CORRECT;
	public static String TEMPLATE_IS_NOT_SPECIFIED;
	public static String TEMPLATE_DOES_NOT_EXIST;
	public static String THE_VIEW_EXISTS;
	public static String CONFIRMATION;
	public static String DELETE;
	public static String DELETE_TITLE_QUESTION;
	public static String DELETE_FILE_FROM_DISK;
	public static String PASTE;
	public static String SELECT_BEAN;
	public static String CLASS_IS_REFERENCED_BY_SEVERAL_BEANS;
	public static String ADD_GETTER_FOR_PROPERTY; 
	public static String ADD_SETTER_FOR_PROPERTY;  
	public static String ADD_GETTER_SETTER_FOR_PROPERTY;
	public static String APPLY_FOR_ALL_PROPERTIES;
	public static String REFERENCES;
	public static String RenameManagedBeanHandler_Rename;
	public static String UPDATE_FIELD_REFERENCE;
	public static String MANAGED_PROPERTY_RENAME;
	public static String UPDATE_WEB_XML;
	public static String FACES_CONFIG_CHANGES;
	public static String UPDATE_REFERENCE_TO_PAGE;
	public static String JSF_PAGE_UPDATE;
	public static String JSFCommentObjectImpl_Comment;
	public static String JSFPagesRefactoringChange_JSPRefactoring;
	public static String CONVERTER_ID_IS_NOT_SPECIFIED;
	public static String CANNOT_FIND_CONVERTER;
	public static String ATTRIBUTE_CONVERTER_CLASS_FOR_CONVERTER_ISNOT_SPECIFIED;
	public static String BUNDLE_IS_NOT_SPECIFIED;
	public static String CANNOT_FIND_BUNDLE;
	public static String KEY_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_PROPERTY;
	public static String CANNOT_FIND_RENDER_KIT;
	public static String ATTRIBUTE_RENDER_KIT_CLASS_FOR_RENDER_KIT_ISNOT_SPECIFIED;
	public static String URI_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_TAG_LIBRARY;
	public static String TAG_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_TAG_IN_LIBRARY;
	public static String ATTRIBUTE_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_ATTRIBUTE_IN_TAG;
	public static String VALIDATOR_ID_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_VALIDATOR;
	public static String ATTRIBUTE_VALIDATOR_CLASS_FOR_CONVERTER_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_MATCHING_RULE_FOR_PATH;
	public static String ERROR;
	public static String CLOSE;
	public static String PROJECT_ALREADY_HAS_SOME_OF_LIBRARIES_INCLUDED;
	public static String PROJECT_ALREADY_HAS_SOME_OF_LIBRARIES_INCLUDED_2;
	public static String PROJECT_HAS_COFLICTING_LIBRARIES;
	public static String OVERWRITE;
	public static String CONFIGURATION_FILE_ISNOT_FOUND_IN_PROJECT;
	public static String FILE_EXISTS;
	public static String CREATE_NEW_JSF_PROJECT;
	public static String IMPORT_JSF_PROJECT;
	public static String ImportJSFWarContext_NoJSFSupportFound;
	public static String FACES_CONFIG_EDITOR;
	public static String FacesConfigEditor_Diagram;
	public static String PRINT_DIAGRAM;
	public static String REPARENTING_JSFSUBPART;
	public static String OF_NORMAL_SIZE;
	public static String OpenRenderKitHelper_RENDER_KIT_ID_NOT_SPECIFIED;
	public static String PRINT;
	public static String ZOOM;
	public static String SELECT_ALL;
	public static String UNSELECT_ALL;
	public static String SELECTED_PAGES;
	public static String PRINT_PREVIEW;
	public static String LIBRARY_SETS;
	public static String CONFIGURATION_FILE_ADDITIONS;
	public static String CreateFaceletTaglibSupport_CreateFaceletsTaglib;
	public static String CreateFaceletTaglibSupport_WebXMLIncorrect;
	public static String CreateFaceletTaglibSupport_WebXMLNotFound;
	public static String CreateFaceletTaglibSupport_WebXMLReadOnly;
	public static String CreateFacesConfigHandler_CreateFacesConfig;
	public static String CreateFacesConfigHandler_WebXMLIncorrect;
	public static String CreateFacesConfigHandler_WebXMLNotFound;
	public static String CreateFacesConfigHandler_WebXMLReadOnly;
	public static String CreateFacesConfigSupport_CreateFacesConfig;
	public static String CreateFacesConfigSupport_WebXMLIncorrect;
	public static String CreateFacesConfigSupport_WebXMLNotFound;
	public static String CreateFacesConfigSupport_WebXMLReadOnly;
	public static String DELETE_JAVA_SOURCE;
	public static String DeleteFacesConfigHandler_DeleteFacesConfig;
	public static String DeleteFacesConfigHandler_DeleteReferenceFromWebXML;
	public static String DeleteGroupHandler_Delete;
	public static String DeleteGroupHandler_DeleteFileFromDisk;
	
	public static String DeleteManagedBeanHandler_CannotDeleteFile;
	public static String DeleteManagedBeanHandler_Failure;
	public static String DeleteManagedPropertyHandler_Delete;
	public static String DeleteManagedPropertyHandler_DeleteJavaProperty;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JSFUIMessages.class);		
	}
	
	private JSFUIMessages() {
		// cannot create new instance of this class
	}
	
	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
		}
		catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}
}
