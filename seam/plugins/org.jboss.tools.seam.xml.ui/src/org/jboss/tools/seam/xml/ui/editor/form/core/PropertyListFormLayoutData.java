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
package org.jboss.tools.seam.xml.ui.editor.form.core;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.seam.xml.ui.editor.form.SeamXMLFormLayoutData;

public class PropertyListFormLayoutData implements SeamComponentConstants {
	static String ENT_PROCESS_DEFINITIONS = "SeamCoreProcessDefinitions";
	static String ENT_PAGEFLOW_DEFINITIONS = "SeamCorePageflowDefinitions";

	static String ENT_BUNDLE_NAMES = "SeamCoreBundleNames";
	static String ENT_FILTERS = "SeamCoreFilters";
	static String ENT_RULE_FILES = "SeamDroolsRuleFiles";
	
	static String ENT_RESTRICTIONS = "SeamFrameworkRestrictions";
	static String ENT_HINTS = "SeamFrameworkHints";

	static String ENT_THEME_SELECTOR = "SeamThemeSelector";
	static String ENT_AVAILABLE_THEMES = "SeamThemeAvailableThemes";

	static String ENT_CONFIG_LOCATIONS = "SeamSpringConfigLocations";
	static String ENT_CONTEXT_LOADER = "SeamSpringContextLoader";

	static String ENT_ENTITY_MANAGER_FACTORY = "SeamCoreEntityManagerFactory";
	static String ENT_PERSISTENCE_UNIT_PROPERTIES = "SeamCorePersistenceUnitProperties";
	
	static String ENT_FILTER = "SeamCoreFilter";
	static String ENT_PARAMETERS = "SeamCoreParameters";

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
				"Advanced",
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createAdvancedFormAttributeData(entity)
			),
		};
	}

	private final static IFormData[] PROCESS_DEFS_DEFINITIONS = new IFormData[] {
		createListDefinition("Process Definitions")
	};
	
	final static IFormData PROCESS_DEFS_FORM_DEFINITION = new FormData(
		ENT_PROCESS_DEFINITIONS, new String[]{null}, PROCESS_DEFS_DEFINITIONS);

	private final static IFormData[] PAGEFLOW_DEFS_DEFINITIONS = new IFormData[] {
		createListDefinition("Pageflow Definitions")
	};
	
	final static IFormData PAGEFLOW_DEFS_FORM_DEFINITION = new FormData(
		ENT_PAGEFLOW_DEFINITIONS, new String[]{null}, PAGEFLOW_DEFS_DEFINITIONS);

	private final static IFormData[] BUNDLE_NAMES_DEFINITIONS = new IFormData[] {
		createListDefinition("Bundle Names")
	};
	
	final static IFormData BUNDLE_NAMES_FORM_DEFINITION = new FormData(
		ENT_BUNDLE_NAMES, new String[]{null}, BUNDLE_NAMES_DEFINITIONS);

	private final static IFormData[] FILTERS_DEFINITIONS = new IFormData[] {
		createListDefinition("Filters")
	};
	
	final static IFormData FILTERS_FORM_DEFINITION = new FormData(
		ENT_FILTERS, new String[]{null}, FILTERS_DEFINITIONS);

	private final static IFormData[] RULE_FILES_DEFINITIONS = new IFormData[] {
		createListDefinition("Rule Files")
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
				"Advanced",
				SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
				FormLayoutDataUtil.createAdvancedFormAttributeData(entity)
			),
		};
	}

	private final static IFormData[] HINTS_DEFINITIONS = new IFormData[] {
		createMapDefinition("Hints")
	};
	
	final static IFormData HINTS_FORM_DEFINITION = new FormData(
		ENT_HINTS, new String[]{null}, HINTS_DEFINITIONS);

	private final static IFormData[] RESTRICTIONS_DEFINITIONS = new IFormData[] {
		createListDefinition("Restrictions")
	};
	
	final static IFormData RESTRICTIONS_FORM_DEFINITION = new FormData(
		ENT_RESTRICTIONS, new String[]{null}, RESTRICTIONS_DEFINITIONS);
	

	private final static IFormData[] PERSISTENCE_UNIT_PROPERTIES_DEFINITIONS = new IFormData[] {
		createMapDefinition("Persistence Unit Properties")
	};
	
	final static IFormData PERSISTENCE_UNIT_PROPERTIES_FORM_DEFINITION = new FormData(
		ENT_PERSISTENCE_UNIT_PROPERTIES, new String[]{null}, PERSISTENCE_UNIT_PROPERTIES_DEFINITIONS);

	private final static IFormData[] ENTITY_MANAGER_FACTORY_DEFINITIONS = 
		createDefinitionsForMapHolder("Entity Manager Factory", ENT_ENTITY_MANAGER_FACTORY, "Persistence Unit Properties", "persistence unit properties");

	final static IFormData ENTITY_MANAGER_FACTORY_FORM_DEFINITION = new FormData(
		ENT_ENTITY_MANAGER_FACTORY, new String[]{null}, ENTITY_MANAGER_FACTORY_DEFINITIONS);

	private final static IFormData[] PARAMETERS_DEFINITIONS = new IFormData[] {
		createMapDefinition("Parameters")
	};
	
	final static IFormData PARAMETERS_FORM_DEFINITION = new FormData(
		ENT_PARAMETERS, new String[]{null}, PARAMETERS_DEFINITIONS);

	private final static IFormData[] FILTER_DEFINITIONS = 
		createDefinitionsForMapHolder("Filter", ENT_FILTER, "Parameters", "parameters");

	final static IFormData FILTER_FORM_DEFINITION = new FormData(
		ENT_FILTER, new String[]{null}, FILTER_DEFINITIONS);

	/**
	 * Theme
	 */
	private final static IFormData[] AVAILABLE_THEMES_DEFINITIONS = new IFormData[] {
		createListDefinition("Available Themes")
	};
	
	final static IFormData AVAILABLE_THEMES_FORM_DEFINITION = new FormData(
		ENT_AVAILABLE_THEMES, new String[]{null}, AVAILABLE_THEMES_DEFINITIONS);

	private final static IFormData[] THEME_SELECTOR_DEFINITIONS = 
		createDefinitionsForListHolder("Theme Selector", ENT_THEME_SELECTOR, "Available Themes", "available themes");

	final static IFormData THEME_SELECTOR_FORM_DEFINITION = new FormData(
		ENT_THEME_SELECTOR, new String[]{null}, THEME_SELECTOR_DEFINITIONS);

	/**
	 * Spring
	 */
	private final static IFormData[] CONFIG_LOCATIONS_DEFINITIONS = new IFormData[] {
		createListDefinition("Config Locations")
	};
	
	final static IFormData CONFIG_LOCATIONS_FORM_DEFINITION = new FormData(
		ENT_CONFIG_LOCATIONS, new String[]{null}, CONFIG_LOCATIONS_DEFINITIONS);
	
	private final static IFormData[] CONTEXT_LOADER_DEFINITIONS = 
		createDefinitionsForListHolder("Context Loader", ENT_CONTEXT_LOADER, "Config Locations", "config locations");

	final static IFormData CONTEXT_LOADER_FORM_DEFINITION = new FormData(
		ENT_CONTEXT_LOADER, new String[]{null}, CONTEXT_LOADER_DEFINITIONS);

}
