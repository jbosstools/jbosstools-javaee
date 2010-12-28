/******************************************************************************* 

 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui;

import org.eclipse.osgi.util.NLS;

public class CDIUIMessages extends NLS{
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.ui.CDIUIMessages"; //$NON-NLS-1$
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIUIMessages.class);
	}
	
	public static String INJECTION_POINT_LABEL_PROVIDER;
	public static String CDI_UI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL;
	public static String CDI_UI_IMAGESIMAGE_NAME_CANNOT_BE_NULL;
	
	public static String CDI_REFACTOR_CONTRIBUTOR_MENU_NAME;
	public static String CDI_REFACTOR_CONTRIBUTOR_ERROR;
	public static String CDI_REFACTOR_CONTRIBUTOR_RENAME_NAMED_BEAN_ACTION_NAME;
	public static String RENAME_NAMED_BEAN_WIZARD_FIELD_NAME;
	
	public static String NEW_QUALIFIER_WIZARD_TITLE;
	public static String NEW_QUALIFIER_WIZARD_PAGE_NAME;
	public static String NEW_STEREOTYPE_WIZARD_TITLE;
	public static String NEW_STEREOTYPE_WIZARD_PAGE_NAME;
	public static String NEW_SCOPE_WIZARD_TITLE;
	public static String NEW_SCOPE_WIZARD_PAGE_NAME;
	public static String NEW_INTERCEPTOR_BINDING_WIZARD_TITLE;
	public static String NEW_INTERCEPTOR_BINDING_WIZARD_PAGE_NAME;
	
	public static String NEW_INTERCEPTOR_WIZARD_TITLE;
	public static String NEW_INTERCEPTOR_WIZARD_PAGE_NAME;
	public static String NEW_INTERCEPTOR_WIZARD_DESCRIPTION;
	
	public static String NEW_DECORATOR_WIZARD_TITLE;
	public static String NEW_DECORATOR_WIZARD_PAGE_NAME;
	public static String NEW_DECORATOR_WIZARD_DESCRIPTION;
	
	public static String NEW_BEANS_XML_WIZARD_TITLE;
	public static String NEW_BEANS_XML_WIZARD_PAGE_NAME;
	public static String NEW_BEANS_XML_WIZARD_DESCRIPTION;

	public static String NEW_ANNOTATION_LITERAL_WIZARD_TITLE;
	public static String NEW_ANNOTATION_LITERAL_WIZARD_PAGE_NAME;
	public static String NEW_ANNOTATION_LITERAL_WIZARD_DESCRIPTION;

	public static String SELECT_STEREOTYPE;
	public static String SELECT_INTERCEPTOR_BINDING;
	public static String SELECT_QUALIFIER;

	public static String FIELD_EDITOR_SCOPE_LABEL;
	public static String FIELD_EDITOR_TARGET_LABEL;
	public static String FIELD_EDITOR_INTERCEPTOR_BINDINGS_LABEL;
	public static String FIELD_EDITOR_STEREOTYPES_LABEL;
	public static String FIELD_EDITOR_QUALIFIER_LABEL;
	
	public static String MESSAGE_METHOD_NAME_EMPTY;
	public static String MESSAGE_METHOD_NAME_NOT_VALID;
	
	public static String MESSAGE_FIELD_NAME_EMPTY;
	public static String MESSAGE_FIELD_NAME_NOT_VALID;
	
	public static String MESSAGE_INTERCEPTOR_BINDINGS_EMPTY;
	
	public static String MESSAGE_STEREOTYPE_CANNOT_BE_APPLIED_TO_TYPE;
	public static String MESSAGE_STEREOTYPE_IS_NOT_COMPATIBLE;
	public static String MESSAGE_INTERCEPTOR_BINDING_IS_NOT_COMPATIBLE;
	
	public static String MESSAGE_QUALIFIER_NOT_SET;
	
	public static String MAKE_FIELD_STATIC_MARKER_RESOLUTION_TITLE;
	public static String MAKE_METHOD_PUBLIC_MARKER_RESOLUTION_TITLE;
	public static String MAKE_METHOD_BUSINESS_MARKER_RESOLUTION_TITLE;
	public static String ADD_LOCAL_BEAN_MARKER_RESOLUTION_TITLE;
	public static String DELETE_ALL_DISPOSER_DUPLICANT_MARKER_RESOLUTION_TITLE;
	public static String DELETE_ALL_INJECTED_CONSTRUCTORS_MARKER_RESOLUTION_TITLE;
	public static String MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE;
	public static String COLLECTING_MARKER_RESOLUTIONS;
	
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_AVAILABLE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_IN_BEAN;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD_ALL;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE_ALL;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_CREATE_NEW_QUALIFIER;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_SET_IS_NOT_UNIQUE;
	
	public static String CDI_GENERATE_BEANS_XML;
	public static String CDI_INSTALL_WIZARD_PAGE_FACET;
	public static String CDI_INSTALL_WIZARD_PAGE_CONFIGURE;
}
