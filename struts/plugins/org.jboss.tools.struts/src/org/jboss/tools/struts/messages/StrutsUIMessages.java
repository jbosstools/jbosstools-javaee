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
package org.jboss.tools.struts.messages;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class StrutsUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.messages.messages";//$NON-NLS-1$
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, StrutsUIMessages.class);		
	}
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public static String CAN_BE_EXPRESSED;
	public static String MAY_NOT_START_WITH_CHARACTER;
	public static String MAY_NOT_CONTAIN_CHARACTER;
	public static String MODULE_CANNOT_BE_REGISTERED_ISNOT_FOUND;
	public static String MODULE_CANNOT_BE_REGISTERED_IS_INCORRECT;
	public static String MODULE_CANNOT_BE_REGISTERED_IS_READONLY;
	public static String SELECT_AT_LEAST_ONE_CLASS_TYPE;
	public static String STEP_2_ACTIONS;
	public static String STEP_3_FORMBEANS;
	public static String STEP_4_FORWARDS;
	public static String STEP_5_EXCEPTIONS;
	public static String DELETE_REFERENCE_FROM_WEBXML;
	public static String ACTION_IS_REFERENCED_REMOVE_REFERENCES;
	public static String DELETE;
	public static String DELETE_NAME;
	public static String WARNING;
	public static String CANNOT_FIND_OBJECT_BY_PATH;
	public static String MODULE_NAME;
	public static String FILE_SYSTEM_IS_USED_AS_ROOT_FOR_STRUTS_MODULE;
	public static String FILE_SYSTEM_IS_USED_AS_ROOT_FOR_WEB_APPLICATION;
	public static String YES;
	public static String NO;
	public static String SOURCE_FOLDER_FOR_MODULE_ISNOT_FOUND;
	public static String GenBaseSupport_Generate;
	public static String GenBaseSupport_Stop;
	public static String GENERATE_JAVABEAN_PROPERTIES;
	public static String GENERATE_CONSTANTS_FOR_LOCAL_FORWARDS;
	public static String GENERATE_CONSTANTS_FOR_GLOBAL_FORWARDS;
	public static String GENERATE_CLASSES_FOR_FORWARDS;
	public static String GENERATE_CLASSES_FOR_ACTIONS;
	public static String GENERATE_CLASSES_FOR_FORMBEANS;
	public static String GENERATE_CLASSES_FOR_EXCEPTIONS;
	public static String OVERWRITE_EXISTING_FILES;
	public static String BASE_PACKAGE_FROM_WHICH_TO_START_CODE_GENERATION;
	public static String GENERATING_JAVA_CODE;
	public static String GENERATION;
	public static String GENERATION_INTERRUPTED;
	public static String FINISHED;
	public static String FAILED;
	public static String FINISH;
	public static String FORM_BEAN_GENERATED;
	public static String FORM_BEAN_ISNOT_GENERATED;
	public static String EXCEPTION_GENERATED;
	public static String EXCEPTION_ISNOT_GENERATED;
	public static String CLEAN_ATTRIBUTE;
	public static String CREATE_STRUTS_CONFIG;
	public static String PAGE_IS_REFERENCED;
	public static String DELETE_PAGE;
	public static String REMOVE_FILE_FROM_DISK;
	public static String FILE_IS_READONLY;
	public static String RETRY;
	public static String CANCEL;
	public static String TEMPLATE_ISNOT_SPECIFIED;
	public static String TEMPLATE_DOESNT_EXIST;
	public static String PATH_CANNOT_END_WITH;
	public static String PATH_EXTENSION_MUST_BE_JSP_HTM_HTML;
	public static String PATH_EXTENSION_MUST_BE;
	public static String ENTER_UNIQUE_MODULE_NAME;
	public static String QUESTION;
	public static String FOLDER_DOESNT_EXIST;
	public static String MODULES_DESCRIBED_IN_WEBXML_ARENT_SYNCHRONIZED;
	public static String MODULE_DOESNT_DEFINE_URI;
	public static String URI_FOR_MODULE_ISNOTT_SYNCHRONIZED;
	public static String ROOT_FOR_MODULE_ISNOT_FOUND;
	public static String CONFIG_FOR_MODULE_IS_MISSING;
	public static String CONFIG_FOR_URI_ISNOT_FOUND;
	public static String WEBXML_ISNOT_FOUND;
	public static String WEBXML_ISNOT_CORRECT;
	public static String PATH_IN_MODULE_MUST_REFERENCE_CONFIGFILE;
	public static String URI_ISNOT_UNIQUE;
	public static String EACH_URI_MUST_REFERENCE_UNIQUE_PATH;
	public static String ATTRIBUTE_FOR_MODULE_MUST_BE_SET;
	public static String ATTRIBUTE_OF_MODULE_CANNOT_REFERENCE_FOLDER;
	public static String LOCATION;
	public static String ENTER_PATH_TO_THE_FOLDER_WHERE_STRUTSCONFIGXML_IS_LOCATED;
	public static String FOLDER_ALREADY_CONTAINS_ADOPTED_PROJECT;
	public static String REOPEN;
	public static String OVERWRITE;
	public static String APPLICATION_NAME_AND_WEBXML_FOLDER;
	public static String CREATE_WEB_PROJECT;
	public static String ENTER_PROJECTNAME_AND_CONFIGURATIONFILE_VERSION;
	public static String STEP1_PROJECTNAME_AND_VERSION;
	public static String PROJECT_ROOTFOLDER_AND_SELECT_TEMPLATE;
	public static String STEP2_LOCATION_AND_TEMPLATE;
	public static String FOLDER_EXISTS_AND_ISNOT_EMPTY;
	public static String CONFIRMATION;
	public static String DELETE_RULE;
	public static String DELETE_REFERENCE;
	public static String LEAVE_FIELDS_EMPTY;
	public static String ALREADY_EXISTS;
	public static String ADD_FORMSET;
	public static String ADD;
	public static String ADD_NAME;
	public static String ADD_MESSAGES_FROM_TEMPLATE;
	public static String ADD_PLUGIN_BY_TEMPLATE;
	public static String PLUGIN_FOR_CLASS_EXISTS;
	public static String ADD_PLUGIN;
	public static String WRONG_TEXT_IN_PLUGIN_OPTION;
	public static String DELETE_MODULE;
	public static String DELETE_STRUTS_CONFIGFILE;
	public static String DELETE_STRUTS_CONFIGFILE_NAME;
	public static String LINK_IS_CONFIRMED_1;
	public static String LINK_IS_CONFIRMED_2;
	public static String BUNDLE_ID_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_BUNDLE;
	public static String PARAMETER_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_PARAMETER;
	public static String FORWARD_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_FORWARD;
	public static String CANNOT_FIND_OBJECT;
	public static String CANNOT_FIND_RESOURCE;
	public static String FORMBEAN_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_FORMBEAN;
	public static String CANNOT_FIND_FORM_PROPERTY;
	public static String CANNOT_FIND_ACTION;
	public static String CANNOT_FIND_FORM_BEAN_FOR_ACTION;
	public static String TYPE_OF_FORMBEAN_ISNOT_SET;
	public static String TYPE_ISNOT_SET;
	public static String PAGE_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_PAGE;
	public static String KEY_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_PROPERTY;
	public static String SET_REQUIRED_ATTRIBUTES;
	public static String ACTION_ISNOT_SPECIFIED;
	public static String GENERATE_JAVA_CODE;
	public static String FILE_ALREADY_EXISTS;
	public static String ABORT;
	public static String CONTINUE;
	public static String URI_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_TAGLIBRARY;
	public static String TAG_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_TAG_IN_LIBRARY;
	public static String ATTRIBUTE_ISNOT_SPECIFIED;
	public static String CANNOT_FIND_ATTRIBUTE_IN_TAG;
	public static String PAGE_CONTAINS_LINK;
	public static String ERROR;
	public static String OK;
	public static String UPDATE_REFERENCE_TO_PAGE;
	public static String STRUTS_CONFIG_CHANGES;
	public static String CREATE_NEW_STRUTS_PROJECT;
	public static String IMPORT_STRUTS_PROJECT;
	public static String LINK_RECOGNIZER;
	public static String TAG;
	public static String ATTRIBUTE;
	public static String REFER_TO;
	public static String LINK_TYPE;
	public static String PRINT_DIAGRAMM;
	public static String DEPENDS;
	public static String MSG_CORRESPONDED_MESSAGE;
	public static String ARG_REPLACEMENT_VALUE_FOR_MESSAGE;
	public static String VAR_VALIDATOR_PARAMETER;
	public static String CHANGE;
	public static String ADD_NEW;
	public static String ADD_ARROW_RIGHT;
	public static String REMOVE_ARROW_LEFT;
	public static String UP;
	public static String DOWN;
	public static String ADD_VALIDATOR;
	public static String SAVE_CHANGES;
	public static String PATH_TO_RESOURCE;
	public static String ADOPT_PROJECTS_TEPMODULES;
	public static String DIAGRAM_TOOLBAR_SELECTION;
	public static String DIAGRAM_TOOLBAR_MARQUEE;
	public static String DIAGRAM_TOOLBAR_CREATE_CONNECTION;
	public static String DIAGRAM_TOOLBAR_ADD_ACTION;
	public static String DIAGRAM_TOOLBAR_ADD_GLOBAL_FORWARD;
	public static String DIAGRAM_TOOLBAR_ADD_GLOBAL_EXCEPTION;
	public static String DIAGRAM_TOOLBAR_ADD_PAGE;
}
