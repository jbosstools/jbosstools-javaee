/******************************************************************************* 

 * Copyright (c) 2010-2011 Red Hat, Inc. 
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
	
	public static String INJECTION_POINT_LABEL_PROVIDER_INJECT_BEAN;
	public static String INJECTION_POINT_LABEL_PROVIDER_OBSERVER_METHOD;
	public static String INJECTION_POINT_LABEL_PROVIDER_EVENT;
	
	public static String CDI_BEAN_QUERY_PARTICIPANT_TASK;
	public static String CDI_BEAN_QUERY_PARTICIPANT_INJECT_FIELD;
	public static String CDI_BEAN_QUERY_PARTICIPANT_INJECT_METHOD;
	public static String CDI_BEAN_QUERY_PARTICIPANT_INJECT_PARAMETER;
	
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
	public static String NEW_DECORATOR_WIZARD_INTERFACES_LABEL;
	public static String NEW_DECORATOR_WIZARD_DESCRIPTION;
	
	public static String NEW_BEAN_WIZARD_TITLE;
	public static String NEW_BEAN_WIZARD_PAGE_NAME;
	public static String NEW_BEAN_WIZARD_DESCRIPTION;
	
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
	
	public static String MESSAGE_BEAN_SHOULD_BE_SERIALIZABLE;
	
	public static String MAKE_FIELD_STATIC_MARKER_RESOLUTION_TITLE;
	public static String MAKE_METHOD_PUBLIC_MARKER_RESOLUTION_TITLE;
	public static String MAKE_METHOD_BUSINESS_MARKER_RESOLUTION_TITLE;
	public static String ADD_LOCAL_BEAN_MARKER_RESOLUTION_TITLE;
	public static String DELETE_ALL_DISPOSER_DUPLICANT_MARKER_RESOLUTION_TITLE;
	public static String DELETE_ALL_INJECTED_CONSTRUCTORS_MARKER_RESOLUTION_TITLE;
	public static String MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE;
	public static String SELECT_BEAN_TITLE;
	public static String ADD_SERIALIZABLE_INTERFACE_MARKER_RESOLUTION_TITLE;
	public static String MAKE_BEAN_SCOPED_DEPENDENT_MARKER_RESOLUTION_TITLE;
	public static String MAKE_FIELD_PROTECTED_MARKER_RESOLUTION_TITLE;
	public static String ADD_RETENTION_MARKER_RESOLUTION_TITLE;
	public static String ADD_TARGET_MARKER_RESOLUTION_TITLE;
	public static String ADD_ANNOTATION_MARKER_RESOLUTION_TITLE;
	public static String DELETE_ANNOTATION_MARKER_RESOLUTION_TITLE;
	public static String CHANGE_ANNOTATION_MARKER_RESOLUTION_TITLE;
	public static String CREATE_BEAN_CLASS_TITLE;
	public static String CREATE_STEREOTYPE_TITLE;
	public static String CREATE_INTERCEPTOR_TITLE;
	public static String CREATE_DECORATOR_TITLE;
	
	public static String CDI_QUICK_FIXES_ANNOTATION;
	public static String CDI_QUICK_FIXES_INTERFACE;
	public static String CDI_QUICK_FIXES_CLASS;
	public static String CDI_QUICK_FIXES_TYPE;
	public static String CDI_QUICK_FIXES_METHOD;
	public static String CDI_QUICK_FIXES_FIELD;
	public static String CDI_QUICK_FIXES_PARAMETER;
	
	public static String QUESTION;
	public static String DECREASING_FIELD_VISIBILITY_MAY_CAUSE_COMPILATION_PROBLEMS;
	
	public static String SELECT_BEAN_WIZARD_TITLE;
	public static String SELECT_BEAN_WIZARD_ENTER_BEAN_NAME;
	public static String SELECT_BEAN_WIZARD_SELECT_BEAN;
	
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_AVAILABLE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_IN_BEAN;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_ADD_ALL;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_REMOVE_ALL;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_CREATE_NEW_QUALIFIER;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_EDIT_QUALIFIER_VALUE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_SET_IS_NOT_UNIQUE;
	public static String ADD_QUALIFIERS_TO_BEAN_WIZARD_ENTER_QUALIFIER_NAME;
	
	public static String CDI_GENERATE_BEANS_XML;
	public static String CDI_INSTALL_WIZARD_PAGE_FACET;
	public static String CDI_INSTALL_WIZARD_PAGE_CONFIGURE;
	
	public static String OPEN_CDI_NAMED_BEAN_DIALOG_LOADING;
	public static String OPEN_CDI_NAMED_BEAN_DIALOG_NAME;
	public static String OPEN_CDI_NAMED_BEAN_DIALOG_WAIT;
	
	public static String OPEN_CDI_NAMED_BEAN_ACTION_NAME;
	public static String OPEN_CDI_NAMED_BEAN_ACTION_MESSAGE;

	public static String CDI_PROJECT_WIZARD_NEW_PROJECT;
}