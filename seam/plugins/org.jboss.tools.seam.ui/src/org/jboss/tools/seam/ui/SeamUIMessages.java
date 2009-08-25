package org.jboss.tools.seam.ui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class SeamUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.ui.messages";//$NON-NLS-1$

	private static ResourceBundle fResourceBundle; 

	public static String ABSTRACT_SEAM_CONTENT_PROVIDER_SEAM_PROJECT_CHANGE_EVENT_OCCURS_BUT_NO_SORCE_OF_PROJECT_PROVIDED;

	public static String BASE_FIELD_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT;

	public static String BASE_FIELD_EDITOR_NO_LABEL;

	public static String BASE_FIELD_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE;

	public static String BASE_FIELD_EDITOR_PARENT_FOR_LABEL_IS_DIFFERENT;

	public static String BUTTON_FIELD_EDITOR_BROWSE;

	public static String BUTTON_FIELD_EDITOR_NOT_IMPLEMENTED_YET;

	public static String COMPOSITE_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT;

	public static String COMPOSITE_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE;

	public static String COMPOSITE_EDITOR_THIS_METOD_CAN_BE_INVOKED;

	public static String CREATE_NEW_SEAM_PROJECT;

	public static String CREATE_PROJECT_ACTION_UNABLE_TO_CREATE_WIZARD;

	public static String CREATE_SEAM_WEB_PROJECTACTION_CREATE_SEAM_PROJECT;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_TITLE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_MESSAGE;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_ERROR;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_GROUP_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_REVERSE_ENGINEER_LABEL;
	public static String GENERATE_SEAM_ENTITIES_WIZARD_EXISTING_ENTITIES_LABEL;

	public static String OPEN_SEAM_COMPONENT_ACTION_ACTION_NAME;

	public static String OPEN_SEAM_COMPONENT_ACTION_DESCRIPTION;

	public static String OPEN_SEAM_COMPONENT_ACTION_MESSAGE;

	public static String OPEN_SEAM_COMPONENT_ACTION_TOOL_TIP;

	public static String OPEN_SEAM_COMPONENT_DIALOG_LOADING;

	public static String OPEN_SEAM_COMPONENT_DIALOG_NAME;
	
	public static String OPEN_SEAM_COMPONENT_DIALOG_WAIT;

	public static String SEAM_ACTION_WIZARD_ACTION_CREATING_OPERATION;

	public static String SEAM_ACTION_WIZARD_NEW_SEAM_ACTION;

	public static String SEAM_ACTION_WIZARD_PAGE1_SEAM_ACTION;

	public static String SEAM_BASE_WIZARD_OPERATION_IS_NOT_DEFINED_FOR_WIZARD;

	public static String SEAM_BASE_WIZARD_PAGE_LOCAL_CLASS_NAME;

	public static String SEAM_BASE_WIZARD_PAGE_LOCAL_INTERFACE;

	public static String SEAM_BASE_WIZARD_PAGE_POJO_CLASS_NAME;

	public static String SEAM_BASE_WIZARD_PAGE_SEAM_COMPONENTS;

	public static String SEAM_CONVERSATION_WIZARD_CREATE_NEW_CONVERSATION;

	public static String SEAM_CONVERSATION_WIZARD_ENTITY_CREATING_OPERATION;

	public static String SEAM_CONVERSATION_WIZARD_PAGE1_SEAM_CONVERSATION;

	public static String SEAM_ENTITY_WIZARD_ENTITY_CREATING_OPERATION;

	public static String SEAM_ENTITY_WIZARD_NEW_SEAM_ENTITY;

	public static String SEAM_ENTITY_WIZARD_PAGE1_ENTITY_CLASS_NAME;

	public static String SEAM_ENTITY_WIZARD_PAGE1_ENTITY_MASTER_PAGE;

	public static String SEAM_ENTITY_WIZARD_PAGE1_PAGE;

	public static String SEAM_FORM_WIZARD_FORM_CREATING_OPERATION;

	public static String SEAM_FORM_WIZARD_NEW_SEAM_FORM;

	public static String SEAM_FORM_WIZARD_PAGE1_SEAM_FORM;

	public static String SEAM_GENERATE_ENTITIES_WIZARD_54;

	public static String SEAM_GENERATE_ENTITIES_WIZARD_ACTION_CREATING_OPERATION;

	public static String SEAM_GENERATE_ENTITIES_WIZARD_CAN_NOT_FIND_SEAM_RUNTIME;

	public static String SEAM_GENERATE_ENTITIES_WIZARD_CAN_NOT_GENERATE_SEAM_ENTITIES;

	public static String SEAM_INSTALL_WIZARD_PAGE_ADD;

	public static String SEAM_INSTALL_WIZARD_PAGE_CANNOT_USE_SELECTED_DEPLOYMENT6;

	public static String SEAM_INSTALL_WIZARD_PAGE_CODE_GENERATION;

	public static String SEAM_INSTALL_WIZARD_PAGE_CONFIGURE_SEAM_FACET_SETTINGS;

	public static String SEAM_INSTALL_WIZARD_PAGE_CONNECTION_PROFILE;

	public static String SEAM_INSTALL_WIZARD_PAGE_DATABASE;

	public static String SEAM_INSTALL_WIZARD_PAGE_DATABASE_CATALOG_NAME;

	public static String SEAM_INSTALL_WIZARD_PAGE_DATABASE_SCHEMA_NAME;

	public static String SEAM_INSTALL_WIZARD_PAGE_DATABASE_TYPE;

	public static String SEAM_INSTALL_WIZARD_PAGE_DB_TABLES_ALREADY_EXISTS;

	public static String SEAM_INSTALL_WIZARD_PAGE_DEPLOY_AS;

	public static String SEAM_INSTALL_WIZARD_PAGE_EDIT;

	public static String SEAM_INSTALL_WIZARD_PAGE_ENTITY_BEAN_PACKAGE_NAME;

	public static String SEAM_INSTALL_WIZARD_PAGE_GENERAL;

	public static String SEAM_INSTALL_WIZARD_PAGE_NEW;

	public static String SEAM_INSTALL_WIZARD_PAGE_PACKAGE_NAME_NOT_VALID;

	public static String SEAM_INSTALL_WIZARD_PAGE_RECREATE_DATABASE_TABLES_AND_DATA_ON_DEPLOY;

	public static String SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET;

	public static String SEAM_INSTALL_WIZARD_PAGE_SEAM_RUNTIME;

	public static String SEAM_INSTALL_WIZARD_PAGE_SESSION_BEAN_PACKAGE_NAME;

	public static String SEAM_INSTALL_WIZARD_PAGE_TEST_PACKAGE_NAME;

	public static String SEAM_OPEN_ACTION_OPEN;

	public static String SEAM_PROJECT_LABEL_PROVIDER_SEAM_COMPONENTS;

	public static String SEAM_PROJECT_SELECTION_DIALOG_SEAM_WEB_PROJECT;
	
	public static String SEAM_PROJECT_SELECTION_DIALOG_SHOW_ALL_PROJECTS;

	public static String SEAM_PROJECT_SELECTION_DIALOG_SELECT_SEAM_WEB_PROJECT;

	public static String SEAM_PROJECT_WIZARD_CREATE_STANDALONE_SEAM_WEB_PROJECT;

	public static String SEAM_PROJECT_WIZARD_EAR_MEMBERSHIP;

	public static String SEAM_PROJECT_WIZARD_NEW_SEAM_PROJECT;

	public static String SEAM_PROJECT_WIZARD_PAGE1_SEAM_FACET_MUST_BE_SPECIFIED;

	public static String SEAM_PROJECT_WIZARD_SEAM_WEB_PROJECT;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_FIND_JBOSS_SEAM_JAR;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_CANNOT_OBTAIN_SEAM_VERSION_NUMBER;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_CREATE_A_SEAM_RUNTIME;
	
	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_MODIFY_SEAM_RUNTIME;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_HOME_FOLDER;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_INPUTELEMENT_MUST_BE_LIST;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME_CANNOT_BE_EMPTY;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_NAME2;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_NEW_SEAM_RUNTIME;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_EDIT_SEAM_RUNTIME;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_PATH_TO_SEAM_HOME_DIRECTORY_CANNOT_BE_EMPTY;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME_ALREADY_EXISTS;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_RUNTIME_NAME_IS_NOT_CORRECT;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_SEAM_RUNTIME;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_THE_SELECTED_SEAM_APPEARS_TO_BE_OF_INCOMATIBLE_VERSION;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION;

	public static String SEAM_RUNTIME_LIST_FIELD_EDITOR_VERSION2;

	public static String SEAM_UI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL;

	public static String SEAM_UI_IMAGESIMAGE_NAME_CANNOT_BE_NULL;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_FLAT;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_HIERARCHICAL;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_LABEL;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_NODE;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_SCOPE_PRESENTATION;

	public static String SEAM_VIEW_LAYOUT_ACTION_GROUP_SEAM_PACKAGES;

	public static String SEAM_WIZARD_FACTORY_BEAN_NAME;

	public static String SEAM_WIZARD_FACTORY_LOCAL_INTERFACE_NAME;

	public static String SEAM_WIZARD_FACTORY_MASTER_PAGE_NAME;

	public static String SEAM_WIZARD_FACTORY_METHOD_NAME;

	public static String SEAM_WIZARD_FACTORY_PAGE_NAME;

	public static String SEAM_WIZARD_FACTORY_SEAM_COMPONENT_NAME;

	public static String SEAM_WIZARD_FACTORY_PACKAGE_NAME;

	public static String SEAM_WIZARD_FACTORY_SEAM_ENTITY_CLASS_NAME;

	public static String SEAM_WIZARD_FACTORY_SEAM_PROJECT;

	public static String SELECT_SEAM_PROJECT_ACTION_BROWSE;
	
	public static String SHOW_PROJECT_SETTINGS_ACTION;

	public static String SWT_FIELD_EDITOR_FACTORY_BROWS;

	public static String SWT_FIELD_EDITOR_FACTORY_ERROR;

	public static String SWT_FIELD_EDITOR_FACTORY_NOT_IMPLEMENTED_YET;

	public static String SWT_FIELD_EDITOR_FACTORY_OK;

	public static String SWT_FIELD_EDITOR_FACTORY_SELECT_SEAM_HOME_FOLDER;

	public static String TEXT_FIELD_EDITOR_EDITOR_SUPPORTS_ONLY_GRID_LAYOUT;

	public static String TEXT_FIELD_EDITOR_PARENT_CONTROL_SHOULD_BE_COMPOSITE;

	public static String VALIDATOR_FACTORY_CANNOT_FIND_SEAM_RUNTIME;

	public static String VALIDATOR_FACTORY_COMPONENT_NAME_IS_NOT_VALID;


	public static String VALIDATOR_FACTORY_CONNECTION_PROFILE_IS_NOT_SELECTED;

	public static String VALIDATOR_FACTORY_DRIVER_CLASS_PROPERTY_IS_EMPTY_FOR_SELECTED_CONNECTION_PROFILE;

	public static String VALIDATOR_FACTORY_FOLDER_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_HOME_FOLDER_POINTS_TO_LOCATION_THAT_DOES_NOT_LOOK_LIKE_SEAM_HOME_FOLDER;

	public static String VALIDATOR_FACTORY_JBOSS_AS_HOME_FOLDER_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_JBOSS_AS_HOME_FOLDER_POINT_TO_LOCATION_THAT_DOES_NOT_LOOK_LIKE_JBOSS_AS_HOME_FOLDER;

	public static String VALIDATOR_FACTORY_LOCAL_INTERFACE_NAME_IS_NOT_VALID;

	public static String VALIDATOR_FACTORY_NAME_IS_NOT_VALID;
	
	public static String VALIDATOR_FACTORY_COMPONENT_ALREADY_EXISTS;

	public static String VALIDATOR_FACTORY_PATH_POINTS_TO_FILE;

	public static String VALIDATOR_FACTORY_PATH_TO_A_FOLDER_CANNOT_BE_NULL;

	public static String VALIDATOR_FACTORY_PRJ_NOT_SELECTED;

	public static String VALIDATOR_FACTORY_PROJECT_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_SEAM_HOME_FOLDER_DOES_NOT_EXISTS;

	public static String VALIDATOR_FACTORY_SEAM_RT_CANNOT_BE_FOUND;

	public static String VALIDATOR_FACTORY_SEAM_RT_HOME_DIR_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_SEAM_RT_HOME_DIR_IS_MISSING;

	public static String VALIDATOR_FACTORY_SEAM_RT_NOT_CONFIGURED;

	public static String VALIDATOR_FACTORY_SEAM_RT_RESOURCES_DIR_IS_MISSING;

	public static String VALIDATOR_FACTORY_SEAM_RT_SOURCE_TEMPLATES_DIR_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_SEAM_RT_SRC_DIR_IS_MISSING;

	public static String VALIDATOR_FACTORY_SEAM_RT_TEMPLATES_DIR_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_SEAM_RT_TEMPLATES_DIR_IS_MISSING;

	public static String VALIDATOR_FACTORY_SEAM_RT_VIE_TEMPLATE_DIR_DOES_NOT_EXIST;

	public static String VALIDATOR_FACTORY_SEAM_RT_VIEW_DIR_IS_MISSING;

	public static String VALIDATOR_FACTORY_SEAM_RUNTIME_IS_NOT_SELECTED;

	public static String VALIDATOR_FACTORY_SELECTED_PRJ_IS_CLOSED;

	public static String VALIDATOR_FACTORY_SELECTED_PROJECT_IS_NOT_A_SEAM_WEB_PROJECT;
	
	public static String VALIDATOR_FACTORY_TEST_PROJECT_ALREADY_EXISTS;
	
	public static String VALIDATOR_FACTORY_EAR_PROJECT_ALREADY_EXISTS;
	
	public static String VALIDATOR_FACTORY_EJB_PROJECT_ALREADY_EXISTS;
	
	public static String VALIDATOR_INVALID_SETTINGS;

	public static String SEAM_TARGET_SERVER;

	public static String RUNTIME_DELETE_CONFIRM_TITLE;

	public static String RUNTIME_DELETE_NOT_USED_CONFIRM;

	public static String RUNTIME_DELETE_USED_CONFIRM;

	public static String VIEW_FOLDER_FILED_EDITOR;
	
	public static String POJO_CLASS_ALREADY_EXISTS;
	public static String ENTITY_CLASS_ALREADY_EXISTS;
	public static String PAGE_ALREADY_EXISTS;
	public static String MASTER_PAGE_ALREADY_EXISTS;
	
	public static String FIND_DECLARATIONS_ACTION_ACTION_NAME;
	public static String FIND_DECLARATIONS_ACTION_DESCRIPTION;
	public static String FIND_DECLARATIONS_ACTION_TOOL_TIP;

	public static String FIND_REFERENCES_ACTION_ACTION_NAME;
	public static String FIND_REFERENCES_ACTION_DESCRIPTION;
	public static String FIND_REFERENCES_ACTION_TOOL_TIP;

	public static String SELECT_WORKSPACE_FOLDER_DIALOG_TITLE;
	public static String SELECT_WORKSPACE_FOLDER_DIALOG_MESSAGE;
	
	public static String SeamSearchQuery_label;

	public static String SeamSearchQuery_singularPatternWithLimitTo;
	public static String SeamSearchQuery_pluralPatternWithLimitTo;

	public static String SeamSearchScope_scope_empty;
	public static String SeamSearchScope_scope_single;
	public static String SeamSearchScope_scope_double;
	public static String SeamSearchScope_scope_multiple;

	public static String SeamSearchScope_scope_LimitToDeclarations;
	public static String SeamSearchScope_scope_LimitToReferences;
	public static String SeamSearchQuery_pluralPattern;
	public static String SeamSearchQuery_singularLabel;

	public static String SeamSearchVisitor_scanning;

	public static String SeamSearch;
	
	public static String SeamQuickFixFindDeclarations;
	public static String SeamQuickFixFindReferences;

	public static String SeamRuntimeListFieldEditor_ActionAdd;

	public static String SeamRuntimeListFieldEditor_ActionEdit;

	public static String SeamRuntimeListFieldEditor_ActionRemove;
	
	public static String SEAM_REFACTOR;

	public static String RENAME_SEAM_COMPONENT;
	
	public static String RENAME_SEAM_CONTEXT_VARIABLE;
	
	public static String FIND_USAGES_IN_EL;
	
	public static String SEAM_COMPONENT_RENAME_HANDLER_ERROR;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SeamUIMessages.class);		
	}
	
	private SeamUIMessages() {
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