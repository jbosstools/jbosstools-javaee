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

import java.util.Collections;
import java.util.Map;

import org.jboss.tools.common.model.ui.forms.ArrayToMap;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.common.model.ui.forms.IFormLayoutData;
import org.jboss.tools.common.model.ui.forms.ModelFormLayoutData;

public class SeamComponentsCoreFormLayoutData implements IFormLayoutData {

	private static SeamComponentsCoreFormLayoutData INSTANCE = new SeamComponentsCoreFormLayoutData();

	public static IFormLayoutData getInstance() {
		return INSTANCE;
	}
	
	private final static IFormData[] FORM_LAYOUT_DEFINITIONS = new IFormData[] {
		ActorFormLayoutData.SEAM_ACTOR_FORM_DEFINITION,
		PropertyListFormLayoutData.PROCESS_DEFS_FORM_DEFINITION,
		PropertyListFormLayoutData.PAGEFLOW_DEFS_FORM_DEFINITION,
		PropertyListFormLayoutData.PROCESS_20_DEFS_FORM_DEFINITION,
		PropertyListFormLayoutData.PAGEFLOW_20_DEFS_FORM_DEFINITION,
		PropertyListFormLayoutData.BUNDLE_NAMES_FORM_DEFINITION,
		PropertyListFormLayoutData.INTERCEPTORS_DEFINITION,
		PropertyListFormLayoutData.SUPPORTED_LOCALES_DEFINITION,
		PropertyListFormLayoutData.FILTERS_FORM_DEFINITION,
		PropertyListFormLayoutData.FILTERS_20_FORM_DEFINITION,
		
		PropertyListFormLayoutData.RULE_FILES_FORM_DEFINITION,
		
		PropertyListFormLayoutData.HINTS_FORM_DEFINITION,
		PropertyListFormLayoutData.RESTRICTIONS_FORM_DEFINITION,
		
		PropertyListFormLayoutData.NAVIGATION_PAGES_FORM_DEFINITION,
		PropertyListFormLayoutData.NAVIGATION_RESOURCES_FORM_DEFINITION,
		
		PropertyListFormLayoutData.THEME_SELECTOR_FORM_DEFINITION,
		PropertyListFormLayoutData.AVAILABLE_THEMES_FORM_DEFINITION,
		
		PropertyListFormLayoutData.CONFIG_LOCATIONS_FORM_DEFINITION,
		PropertyListFormLayoutData.CONTEXT_LOADER_FORM_DEFINITION,
		
		PropertyListFormLayoutData.ENTITY_MANAGER_FACTORY_FORM_DEFINITION,
		PropertyListFormLayoutData.PERSISTENCE_UNIT_PROPERTIES_FORM_DEFINITION,
		
		PropertyListFormLayoutData.FILTER_FORM_DEFINITION,
		PropertyListFormLayoutData.PARAMETERS_FORM_DEFINITION,
		
		PropertyListFormLayoutData.MAIL_ALIASES_FORM_DEFINITION,
		PropertyListFormLayoutData.MAIL_MELDWARE_FORM_DEFINITION,
		PropertyListFormLayoutData.MAIL_MELDWARE_USER_FORM_DEFINITION,
		PropertyListFormLayoutData.MAIL_USERS_FORM_DEFINITION,
		
		PropertyListFormLayoutData.PERS_MAPPING_CLASSES_FORM_DEFINITION,
		PropertyListFormLayoutData.PERS_MAPPING_FILES_FORM_DEFINITION,
		PropertyListFormLayoutData.PERS_MAPPING_JARS_FORM_DEFINITION,
		PropertyListFormLayoutData.PERS_MAPPING_PACKAGES_FORM_DEFINITION,
		PropertyListFormLayoutData.PERS_MAPPING_RESOURCES_FORM_DEFINITION,

	};

	private static Map<String,IFormData> FORM_LAYOUT_DEFINITION_MAP = Collections.unmodifiableMap(new ArrayToMap(FORM_LAYOUT_DEFINITIONS));

	private SeamComponentsCoreFormLayoutData() {}
	
	public IFormData getFormData(String entityName) {
		IFormData data = (IFormData)FORM_LAYOUT_DEFINITION_MAP.get(entityName);
		if(data == null) {
			data = ModelFormLayoutData.getInstance().getFormData(entityName);
		}
		return data;
	}

}
