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
import org.eclipse.core.runtime.preferences.InstanceScope;

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

	// Duplicate names found in @Name annotations will be marked
	public static final String NONUNIQUE_COMPONENT_NAME = createSeverityOption("nonUniqueComponentName");
	// Components with @Stateful and without @Remove methods will be marked
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE = createSeverityOption("statefulComponentDoesNotContainRemove");
	// Components with @Stateful and without @Destroy methods will be marked
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY = createSeverityOption("statefulComponentDoesNotContainDestroy");
	// Components with @Stateful can't have PAGE or STATELESS scopes. If it has wrong scope then component will be marked.
	public static final String STATEFUL_COMPONENT_WRONG_SCOPE = createSeverityOption("statefulComponentHasWrongScope");
	// If className ('component' element) contains unknown class name then component.xml will be marked.
	public static final String UNKNOWN_COMPONENT_CLASS_NAME = createSeverityOption("unknownComponentClassName");
	// If component/property@name contains some property name which does not have setter then mark it.
	public static final String UNKNOWN_COMPONENT_PROPERTY = createSeverityOption("unknownComponentProperty");

	//Entities

	// Component marked as @Entity can't have STATELESS scope. If it has wrong scope mark it.
	public static final String ENTITY_COMPONENT_WRONG_SCOPE = createSeverityOption("entityComponentHasWrongScope");
	// Mark any duplicated @Remove methods within one component.
	public static final String DUPLICATE_REMOVE = createSeverityOption("duplicateRemove");

	//Component life-cycle methods

	// Mark duplicated @Destroy methods within one component.
	public static final String DUPLICATE_DESTROY = createSeverityOption("duplicateDestroy");
	// Mark duplicated @Create methods within one component.
	public static final String DUPLICATE_CREATE = createSeverityOption("duplicateCreate");
	// Mark duplicated @Unwrap methods within one component.
	public static final String DUPLICATE_UNWRAP = createSeverityOption("duplicateUnwrap");
	// Mark all @Destroy methods which are not declared in components' classes.
	public static final String DESTROY_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("destroyDoesNotBelongToComponent");
	// Mark all @Create methods which are not declared in components' classes.
	public static final String CREATE_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("createDoesNotBelongToComponent");
	// Mark all @Unwrap methods which are not declared in components' classes.
	public static final String UNWRAP_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("unwrapDoesNotBelongToComponent");
	// Mark all @Observer methods which are not declared in components' classes.
	public static final String OBSERVER_DOESNT_BELONG_TO_COMPONENT = createSeverityOption("observerDoesNotBelongToComponent");

	//Factories

	// Factory method with a void return type must have an associated @Out/Databinder. Mark factory otherwise.
	public static final String UNKNOWN_FACTORY_NAME = createSeverityOption("unknownFactoryName");

	//Bijections

	// @DataModelSelection and @DataModelSelectionIndex without name requires the only one @DataModel in the component. Mark @DataModelSelection or @DataModelSelectionIndex otherwise.
	public static final String MULTIPLE_DATA_BINDER = createSeverityOption("multipleDataBinder");
	// Mark @DataModelSelection or @DataModelSelectionIndex with unknown name. We should have @DataModel or @Out with the same name.
	public static final String UNKNOWN_DATA_MODEL = createSeverityOption("unknownDataModel");

	//Context variables

	// If factory uses a name of any components (roles) or other factories then mark all these context variables' names. 
	public static final String DUPLICATE_VARIABLE_NAME = createSeverityOption("duplicateVariableName");
	// If @In uses a unknown context variable name then mark it.
	public static final String UNKNOWN_VARIABLE_NAME = createSeverityOption("unknownVariableName");

	// Seam Expression language

	// Mark Expressions which we can't resolve.
	public static final String INVALID_EXPRESSION = createSeverityOption("invalidExpression");
	// If Expression use property of component and this property has only setter(getter) without getter(setter) then mark it.
	public static final String UNPAIRED_GETTER_OR_SETTER = createSeverityOption("unpairedGetterOrSetter");

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

	public static IEclipsePreferences getInstancePreferences() {
		return new InstanceScope().getNode(SeamCorePlugin.PLUGIN_ID);
	}

	public static String getProjectPreference(ISeamProject project, String key) {
		IEclipsePreferences p = getProjectPreferences(project);
		if(p == null) return null;
		String value = p.get(key, null);
		return value != null ? value : getInstancePreference(key);
	}

	public static String getInstancePreference(String key) {
		IEclipsePreferences p = getInstancePreferences();
		String value = p == null ? null : p.get(key, null);
		return value != null ? value : getDefaultPreference(key);
	}

	public static String getDefaultPreference(String key) {
		IEclipsePreferences p = getDefaultPreferences();
		if(p == null) return null;
		return p.get(key, null);
	}

	public static boolean isValidateCore(ISeamProject project) {
		return !(SeamPreferences.IGNORE.equals(getProjectPreference(project, NONUNIQUE_COMPONENT_NAME)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, STATEFUL_COMPONENT_WRONG_SCOPE)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNKNOWN_COMPONENT_CLASS_NAME)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNKNOWN_COMPONENT_PROPERTY)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, ENTITY_COMPONENT_WRONG_SCOPE)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DUPLICATE_REMOVE)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DUPLICATE_DESTROY)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DUPLICATE_CREATE)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DUPLICATE_UNWRAP)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DESTROY_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, CREATE_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNWRAP_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, OBSERVER_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNKNOWN_FACTORY_NAME)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, MULTIPLE_DATA_BINDER)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNKNOWN_DATA_MODEL)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, DUPLICATE_VARIABLE_NAME)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNKNOWN_VARIABLE_NAME)));
	}

	public static boolean isValidateEL(ISeamProject project) {
		return !(SeamPreferences.IGNORE.equals(getProjectPreference(project, INVALID_EXPRESSION)) &&
		SeamPreferences.IGNORE.equals(getProjectPreference(project, UNPAIRED_GETTER_OR_SETTER)));
	}
}