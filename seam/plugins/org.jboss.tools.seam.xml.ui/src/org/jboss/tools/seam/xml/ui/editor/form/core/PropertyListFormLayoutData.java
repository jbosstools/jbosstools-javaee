/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.xml.ui.editor.form.core;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.seam.xml.ui.editor.form.SeamXMLFormLayoutData;

public class PropertyListFormLayoutData implements SeamComponentConstants {
	static String ENT_PROCESS_DEFINITIONS = "SeamCoreProcessDefinitions"; //$NON-NLS-1$
	static String ENT_PAGEFLOW_DEFINITIONS = "SeamCorePageflowDefinitions"; //$NON-NLS-1$
	static String ENT_PROCESS_DEFINITIONS_20 = "SeamBPMProcessDefinitions20"; //$NON-NLS-1$
	static String ENT_PAGEFLOW_DEFINITIONS_20 = "SeamBPMPageflowDefinitions20"; //$NON-NLS-1$

	static String ENT_BUNDLE_NAMES = "SeamCoreBundleNames"; //$NON-NLS-1$
	static String ENT_FILTERS = "SeamCoreFilters"; //$NON-NLS-1$
	static String ENT_FILTERS_20 = "SeamPersistenceFilters"; //$NON-NLS-1$
	static String ENT_RULE_FILES = "SeamDroolsRuleFiles"; //$NON-NLS-1$
	static String ENT_INTERCEPTORS = "SeamCoreInterceptors"; //$NON-NLS-1$
	static String ENT_SUPPORTED_LOCALES = "SeamInternationalSupportedLocales"; //$NON-NLS-1$
	
	static String ENT_RESTRICTIONS = "SeamFrameworkRestrictions"; //$NON-NLS-1$
	static String ENT_HINTS = "SeamFrameworkHints"; //$NON-NLS-1$

	static String ENT_NAVIGATION_PAGES = "SeamNavigationPages"; //$NON-NLS-1$
	static String ENT_NAVIGATION_RESOURCES = "SeamNavigationResources"; //$NON-NLS-1$

	static String ENT_THEME_SELECTOR = "SeamThemeSelector"; //$NON-NLS-1$
	static String ENT_AVAILABLE_THEMES = "SeamThemeAvailableThemes"; //$NON-NLS-1$

	static String ENT_CONFIG_LOCATIONS = "SeamSpringConfigLocations"; //$NON-NLS-1$
	static String ENT_CONTEXT_LOADER = "SeamSpringContextLoader"; //$NON-NLS-1$

	static String ENT_ENTITY_MANAGER_FACTORY = "SeamCoreEntityManagerFactory"; //$NON-NLS-1$
	static String ENT_PERSISTENCE_UNIT_PROPERTIES = "SeamCorePersistenceUnitProperties"; //$NON-NLS-1$
	
	static String ENT_FILTER = "SeamCoreFilter"; //$NON-NLS-1$
	static String ENT_PARAMETERS = "SeamCoreParameters"; //$NON-NLS-1$

	private static IFormData createListDefinition(String header) {
		return new FormData(
			header,
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_VALUE, 100)}, 
			new String[]{ENT_SEAM_LIST_ENTRY},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
		);
	}

	private static IFormData createListDefinition2(String header, String child) {
		return new FormData(
			header,
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			child,
			new FormAttributeData[]{new FormAttributeData(ATTR_VALUE, 100)}, 
			new String[]{ENT_SEAM_LIST_ENTRY},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
		);
	}
	
	private static IFormData[] createDefinitionsForListHolder(String header, String entity, String listHeader, String child) {
		return new IFormData[] {
			new FormData(
				header,
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createGeneralFormAttributeData(entity)
			),
			createListDefinition2(listHeader, child),
			new FormData(
				"Advanced", //$NON-NLS-1$
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createAdvancedFormAttributeData(entity)
			),
		};
	}
	
	
	//BPM

	private final static IFormData[] PROCESS_DEFS_DEFINITIONS = new IFormData[] {
		createListDefinition("Process Definitions") //$NON-NLS-1$
	};
	
	final static IFormData PROCESS_DEFS_FORM_DEFINITION = new FormData(
		ENT_PROCESS_DEFINITIONS, new String[]{null}, PROCESS_DEFS_DEFINITIONS);

	private final static IFormData[] PAGEFLOW_DEFS_DEFINITIONS = new IFormData[] {
		createListDefinition("Pageflow Definitions") //$NON-NLS-1$
	};
	
	final static IFormData PAGEFLOW_DEFS_FORM_DEFINITION = new FormData(
		ENT_PAGEFLOW_DEFINITIONS, new String[]{null}, PAGEFLOW_DEFS_DEFINITIONS);
	
	//BPM-2.0
	
	final static IFormData PROCESS_20_DEFS_FORM_DEFINITION = new FormData(
		ENT_PROCESS_DEFINITIONS_20, new String[]{null}, PROCESS_DEFS_DEFINITIONS);

	final static IFormData PAGEFLOW_20_DEFS_FORM_DEFINITION = new FormData(
		ENT_PAGEFLOW_DEFINITIONS_20, new String[]{null}, PAGEFLOW_DEFS_DEFINITIONS);


	private final static IFormData[] BUNDLE_NAMES_DEFINITIONS = new IFormData[] {
		createListDefinition("Bundle Names") //$NON-NLS-1$
	};
	
	final static IFormData BUNDLE_NAMES_FORM_DEFINITION = new FormData(
		ENT_BUNDLE_NAMES, new String[]{null}, BUNDLE_NAMES_DEFINITIONS);

	private final static IFormData[] INTERCEPTORS_DEFINITIONS = new IFormData[] {
		createListDefinition("Interceptors") //$NON-NLS-1$
	};
	
	final static IFormData INTERCEPTORS_DEFINITION = new FormData(
		ENT_INTERCEPTORS, new String[]{null}, INTERCEPTORS_DEFINITIONS);

	private final static IFormData[] SUPPORTED_LOCALES_DEFINITIONS = new IFormData[] {
		createListDefinition("Supported Locales") //$NON-NLS-1$
	};
	
	final static IFormData SUPPORTED_LOCALES_DEFINITION = new FormData(
		ENT_SUPPORTED_LOCALES, new String[]{null}, SUPPORTED_LOCALES_DEFINITIONS);

	private final static IFormData[] FILTERS_DEFINITIONS = new IFormData[] {
		createListDefinition("Filters") //$NON-NLS-1$
	};
	
	final static IFormData FILTERS_FORM_DEFINITION = new FormData(
		ENT_FILTERS, new String[]{null}, FILTERS_DEFINITIONS);

	final static IFormData FILTERS_20_FORM_DEFINITION = new FormData(
		ENT_FILTERS_20, new String[]{null}, FILTERS_DEFINITIONS);

	private final static IFormData[] RULE_FILES_DEFINITIONS = new IFormData[] {
		createListDefinition("Rule Files") //$NON-NLS-1$
	};
	
	final static IFormData RULE_FILES_FORM_DEFINITION = new FormData(
		ENT_RULE_FILES, new String[]{null}, RULE_FILES_DEFINITIONS);

	private static IFormData createMapDefinition(String header) {
		return new FormData(
			header,
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_KEY, 40), new FormAttributeData(ATTR_VALUE, 60)}, 
			new String[]{ENT_SEAM_MAP_ENTRY},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
		);
	}

	private static IFormData createMapDefinition2(String header, String child) {
		return new FormData(
			header,
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			child,
			new FormAttributeData[]{new FormAttributeData(ATTR_KEY, 40), new FormAttributeData(ATTR_VALUE, 60)}, 
			new String[]{ENT_SEAM_MAP_ENTRY},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
		);
	}
	
	private static IFormData[] createDefinitionsForMapHolder(String header, String entity, String listHeader, String child) {
		return new IFormData[] {
			new FormData(
				header,
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createGeneralFormAttributeData(entity)
			),
			createMapDefinition2(listHeader, child),
			new FormData(
				"Advanced", //$NON-NLS-1$
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createAdvancedFormAttributeData(entity)
			),
		};
	}

	private final static IFormData[] HINTS_DEFINITIONS = new IFormData[] {
		createMapDefinition("Hints") //$NON-NLS-1$
	};
	
	final static IFormData HINTS_FORM_DEFINITION = new FormData(
		ENT_HINTS, new String[]{null}, HINTS_DEFINITIONS);

	private final static IFormData[] RESTRICTIONS_DEFINITIONS = new IFormData[] {
		createListDefinition("Restrictions") //$NON-NLS-1$
	};
	
	final static IFormData RESTRICTIONS_FORM_DEFINITION = new FormData(
		ENT_RESTRICTIONS, new String[]{null}, RESTRICTIONS_DEFINITIONS);
	

	private final static IFormData[] PERSISTENCE_UNIT_PROPERTIES_DEFINITIONS = new IFormData[] {
		createMapDefinition("Persistence Unit Properties") //$NON-NLS-1$
	};
	
	final static IFormData PERSISTENCE_UNIT_PROPERTIES_FORM_DEFINITION = new FormData(
		ENT_PERSISTENCE_UNIT_PROPERTIES, new String[]{null}, PERSISTENCE_UNIT_PROPERTIES_DEFINITIONS);

	private final static IFormData[] ENTITY_MANAGER_FACTORY_DEFINITIONS = 
		createDefinitionsForMapHolder("Entity Manager Factory", ENT_ENTITY_MANAGER_FACTORY, "Persistence Unit Properties", "persistence unit properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData ENTITY_MANAGER_FACTORY_FORM_DEFINITION = new FormData(
		ENT_ENTITY_MANAGER_FACTORY, new String[]{null}, ENTITY_MANAGER_FACTORY_DEFINITIONS);

	private final static IFormData[] PARAMETERS_DEFINITIONS = new IFormData[] {
		createMapDefinition("Parameters") //$NON-NLS-1$
	};
	
	final static IFormData PARAMETERS_FORM_DEFINITION = new FormData(
		ENT_PARAMETERS, new String[]{null}, PARAMETERS_DEFINITIONS);

	private final static IFormData[] FILTER_DEFINITIONS = 
		createDefinitionsForMapHolder("Filter", ENT_FILTER, "Parameters", "parameters"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData FILTER_FORM_DEFINITION = new FormData(
		ENT_FILTER, new String[]{null}, FILTER_DEFINITIONS);

	/**
	 * Navigation
	 */
	private final static IFormData[] NAVIGATION_RESOURCES_DEFINITIONS = new IFormData[] {
		createListDefinition("Resources") //$NON-NLS-1$
	};
	
	final static IFormData NAVIGATION_RESOURCES_FORM_DEFINITION = new FormData(
		ENT_NAVIGATION_RESOURCES, new String[]{null}, NAVIGATION_RESOURCES_DEFINITIONS);

	private final static IFormData[] NAVIGATION_PAGES_DEFINITIONS = 
		createDefinitionsForListHolder("Navigation Pages", ENT_NAVIGATION_PAGES, "Resources", "resources"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData NAVIGATION_PAGES_FORM_DEFINITION = new FormData(
		ENT_NAVIGATION_PAGES, new String[]{null}, NAVIGATION_PAGES_DEFINITIONS);


	/**
	 * Theme
	 */
	private final static IFormData[] AVAILABLE_THEMES_DEFINITIONS = new IFormData[] {
		createListDefinition("Available Themes") //$NON-NLS-1$
	};
	
	final static IFormData AVAILABLE_THEMES_FORM_DEFINITION = new FormData(
		ENT_AVAILABLE_THEMES, new String[]{null}, AVAILABLE_THEMES_DEFINITIONS);

	private final static IFormData[] THEME_SELECTOR_DEFINITIONS = 
		createDefinitionsForListHolder("Theme Selector", ENT_THEME_SELECTOR, "Available Themes", "available themes"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData THEME_SELECTOR_FORM_DEFINITION = new FormData(
		ENT_THEME_SELECTOR, new String[]{null}, THEME_SELECTOR_DEFINITIONS);

	/**
	 * Spring
	 */
	private final static IFormData[] CONFIG_LOCATIONS_DEFINITIONS = new IFormData[] {
		createListDefinition("Config Locations") //$NON-NLS-1$
	};
	
	final static IFormData CONFIG_LOCATIONS_FORM_DEFINITION = new FormData(
		ENT_CONFIG_LOCATIONS, new String[]{null}, CONFIG_LOCATIONS_DEFINITIONS);
	
	private final static IFormData[] CONTEXT_LOADER_DEFINITIONS = 
		createDefinitionsForListHolder("Context Loader", ENT_CONTEXT_LOADER, "Config Locations", "config locations"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData CONTEXT_LOADER_FORM_DEFINITION = new FormData(
		ENT_CONTEXT_LOADER, new String[]{null}, CONTEXT_LOADER_DEFINITIONS);

	/**
	 * Mail
	 */
	static String ENT_MAIL_ALIASES = "SeamMailAliases"; //$NON-NLS-1$
	static String ENT_MAIL_USERS = "SeamMailUsers"; //$NON-NLS-1$
	static String ENT_MAIL_MELDWARE = "SeamMailMeldware"; //$NON-NLS-1$
	static String ENT_MAIL_MELDWARE_USER = "SeamMailMeldwareUser"; //$NON-NLS-1$

	private final static IFormData[] MAIL_ALIASES_DEFINITIONS = new IFormData[] {
		createListDefinition("Aliases") //$NON-NLS-1$
	};
	
	final static IFormData MAIL_ALIASES_FORM_DEFINITION = new FormData(
		ENT_MAIL_ALIASES, new String[]{null}, MAIL_ALIASES_DEFINITIONS);

	private final static IFormData[] MAIL_USERS_DEFINITIONS = new IFormData[] {
		createListDefinition("Users") //$NON-NLS-1$
	};
	
	final static IFormData MAIL_USERS_FORM_DEFINITION = new FormData(
		ENT_MAIL_USERS, new String[]{null}, MAIL_USERS_DEFINITIONS);

	private final static IFormData[] MAIL_MELDWARE_DEFINITIONS = 
		createDefinitionsForListHolder("Meldware", ENT_MAIL_MELDWARE, "Users", "users"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private final static IFormData[] MAIL_MELDWARE_USER_DEFINITIONS = 
		createDefinitionsForListHolder("Meldware User", ENT_MAIL_MELDWARE_USER, "Aliases", "aliases"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	final static IFormData MAIL_MELDWARE_FORM_DEFINITION = new FormData(
		ENT_MAIL_MELDWARE, new String[]{null}, MAIL_MELDWARE_DEFINITIONS);

	final static IFormData MAIL_MELDWARE_USER_FORM_DEFINITION = new FormData(
		ENT_MAIL_MELDWARE_USER, new String[]{null}, MAIL_MELDWARE_USER_DEFINITIONS);

	/**
	 * Persistence
	 */
	static String ENT_PERS_MAPPING_CLASSES = "SeamPersistenceMappingClasses"; //$NON-NLS-1$
	static String ENT_PERS_MAPPING_FILES = "SeamPersistenceMappingFiles"; //$NON-NLS-1$
	static String ENT_PERS_MAPPING_JARS = "SeamPersistenceMappingJars"; //$NON-NLS-1$
	static String ENT_PERS_MAPPING_PACKAGES = "SeamPersistenceMappingPackages"; //$NON-NLS-1$
	static String ENT_PERS_MAPPING_RESOURCES = "SeamPersistenceMappingResources"; //$NON-NLS-1$
	
	final static IFormData PERS_MAPPING_CLASSES_FORM_DEFINITION = new FormData(
		ENT_PERS_MAPPING_CLASSES, new String[]{null}, new IFormData[] {createListDefinition("Mapping Classes")}); //$NON-NLS-1$
	final static IFormData PERS_MAPPING_FILES_FORM_DEFINITION = new FormData(
		ENT_PERS_MAPPING_FILES, new String[]{null}, new IFormData[] {createListDefinition("Mapping Files")}); //$NON-NLS-1$
	final static IFormData PERS_MAPPING_JARS_FORM_DEFINITION = new FormData(
		ENT_PERS_MAPPING_JARS, new String[]{null}, new IFormData[] {createListDefinition("Mapping Jars")}); //$NON-NLS-1$
	final static IFormData PERS_MAPPING_PACKAGES_FORM_DEFINITION = new FormData(
		ENT_PERS_MAPPING_PACKAGES, new String[]{null}, new IFormData[] {createListDefinition("Mapping Packages")}); //$NON-NLS-1$
	final static IFormData PERS_MAPPING_RESOURCES_FORM_DEFINITION = new FormData(
		ENT_PERS_MAPPING_RESOURCES, new String[]{null}, new IFormData[] {createListDefinition("Mapping Resources")}); //$NON-NLS-1$
	

}
