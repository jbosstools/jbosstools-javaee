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
package org.jboss.tools.seam.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Constants for names of seam preferences.
 * Static accesses to seam preferences.
 * 
 * Framework for Severity preferences.
 * 1) Add constant using static method createSeverityOption(String)
 *    Put it under relevant section, e.g. //components, or create new section
 * 2) Create in org.jboss.tools.seam.internal.core.validation.messages.properties
 *    error message with the same name
 * 3) Add to org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages
 *    and to org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages.properties
 *    constant and property named SeamValidatorConfigurationBlock_pb_%name%_label,
 *    where %name% is produced from constant name in SeamPreferences like AAA_BBB_CCC -> aaaBbbCcc.
 *    Put these entries under relevant section. For a new section add constant and property 
 *    SeamValidatorConfigurationBlock_section_%newSectionName%
 * 4) In class org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages
 *    modify SectionDescription constants, according to instruction there.
 * 
 * @author Viacheslav Kabanovich
 */
public class SeamPreferences {
	
	public static final String ERROR = "error";
	public static final String WARNING = "warning";
	public static final String IGNORE = "ignore";

	public static final Set<String> severityOptionNames = new HashSet<String>();

	//Components
	public static final String NONUNIQUE_COMPONENT_NAME = createSeverityOption("nonUniqueComponentName");
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE = createSeverityOption("statefulComponentDoesNotContainRemove");
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY = createSeverityOption("statefulComponentDoesNotContainDestroy");
	public static final String STATEFUL_COMPONENT_WRONG_SCOPE = createSeverityOption("statefulComponentHasWrongScope");

	//Entities
	public static final String ENTITY_COMPONENT_WRONG_SCOPE = createSeverityOption("entityComponentHasWrongScope");
	public static final String DUPLICATE_REMOVE = createSeverityOption("duplicateRemove");

	//Component life-cycle methods
	public static final String DUPLICATE_DESTROY = createSeverityOption("duplicateDestroy");
	public static final String DUPLICATE_CREATE = createSeverityOption("duplicateCreate");
	public static final String DUPLICATE_UNWRAP = createSeverityOption("duplicateUnwrap");
	public static final String DESTROY_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("destroyDoesNotBelongToComponent");
	public static final String CREATE_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("createDoesNotBelongToComponent");
	public static final String UNWRAP_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("unwrapDoesNotBelongToComponent");
	public static final String OBSERVER_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("observerDoesNotBelongToComponent");

	//Factories
	public static final String UNKNOWN_FACTORY_NAME = createSeverityOption("unknownFactoryName");

	//Bijections
	public static final String MULTIPLE_DATA_BINDER = createSeverityOption("multipleDataBinder");
	public static final String UNKNOWN_DATA_MODEL = createSeverityOption("unknownDataModel");

	//Context variables
	public static final String DUPLICATE_VARIABLE_NAME = createSeverityOption("duplicateVariableName");
	public static final String UNKNOWN_INJECTION_NAME = createSeverityOption("unknownInjectionName");
	
	private static String createSeverityOption(String shortName) {
		String name = SeamCorePlugin.PLUGIN_ID + ".validator.problem." + shortName;
		severityOptionNames.add(name);
		return name;
	}
	
	public static final Set<String> allOptionNames = new HashSet<String>();
	
	static {
		allOptionNames.addAll(severityOptionNames);
	}
	
	public static IEclipsePreferences getProjectPreferences(ISeamProject project) {
		return new ProjectScope(project.getProject()).getNode(SeamCorePlugin.PLUGIN_ID);
	}
	
	public static IEclipsePreferences getDefaultPreferences() {
		return new DefaultScope().getNode(SeamCorePlugin.PLUGIN_ID);
	}
	
	public static String getProjectPreference(ISeamProject project, String key) {
		IEclipsePreferences p = getProjectPreferences(project);
		if(p == null) return null;
		return p.get(key, null);
	}

	public static String getDefaultPreference(String key) {
		IEclipsePreferences p = getDefaultPreferences();
		if(p == null) return null;
		return p.get(key, null);
	}

}
