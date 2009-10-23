/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.preferences.SeverityPreferences;

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
 * 4) In class org.jboss.tools.seam.ui.preferences.SeamValidatorConfigurationBlock
 *    modify SectionDescription constants, according to instruction there.
 * 
 * @author Viacheslav Kabanovich
 */
public class SeamPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static SeamPreferences INSTANCE = new SeamPreferences();

	//Components

	// Duplicate names found in @Name annotations will be marked
	public static final String NONUNIQUE_COMPONENT_NAME = INSTANCE.createSeverityOption("nonUniqueComponentName"); //$NON-NLS-1$
	// Components with @Stateful and without @Remove methods will be marked
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE = INSTANCE.createSeverityOption("statefulComponentDoesNotContainRemove"); //$NON-NLS-1$
	// Components with @Stateful and without @Destroy methods will be marked
	public static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY = INSTANCE.createSeverityOption("statefulComponentDoesNotContainDestroy"); //$NON-NLS-1$
	// Components with @Stateful can't have PAGE or STATELESS scopes. If it has wrong scope then component will be marked.
	public static final String STATEFUL_COMPONENT_WRONG_SCOPE = INSTANCE.createSeverityOption("statefulComponentHasWrongScope"); //$NON-NLS-1$
	// If className ('component' element) contains unknown class name then component.xml will be marked.
	public static final String UNKNOWN_COMPONENT_CLASS_NAME = INSTANCE.createSeverityOption("unknownComponentClassName"); //$NON-NLS-1$

	public static final String UNKNOWN_COMPONENT_CLASS_NAME_GUESS = INSTANCE.createSeverityOption("unknownComponentClassNameGuess"); //$NON-NLS-1$
	// If component/property@name contains some property name which does not have setter then mark it.
	public static final String UNKNOWN_COMPONENT_PROPERTY = INSTANCE.createSeverityOption("unknownComponentProperty"); //$NON-NLS-1$

	//Entities

	// Component marked as @Entity can't have STATELESS scope. If it has wrong scope mark it.
	public static final String ENTITY_COMPONENT_WRONG_SCOPE = INSTANCE.createSeverityOption("entityComponentHasWrongScope"); //$NON-NLS-1$
	// Mark any duplicated @Remove methods within one component.
	public static final String DUPLICATE_REMOVE = INSTANCE.createSeverityOption("duplicateRemove"); //$NON-NLS-1$

	//Component life-cycle methods

	// Mark duplicated @Destroy methods within one component.
	public static final String DUPLICATE_DESTROY = INSTANCE.createSeverityOption("duplicateDestroy"); //$NON-NLS-1$
	// Mark duplicated @Create methods within one component.
	public static final String DUPLICATE_CREATE = INSTANCE.createSeverityOption("duplicateCreate"); //$NON-NLS-1$
	// Mark duplicated @Unwrap methods within one component.
	public static final String DUPLICATE_UNWRAP = INSTANCE.createSeverityOption("duplicateUnwrap"); //$NON-NLS-1$
	// Mark all @Destroy methods which are not declared in components' classes.
	public static final String DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN = INSTANCE.createSeverityOption("destroyMethodBelongsToStatelessSessionBean"); //$NON-NLS-1$
	// Mark all @Create methods which are not declared in components' classes.
	public static final String CREATE_DOESNT_BELONG_TO_COMPONENT = INSTANCE.createSeverityOption("createDoesNotBelongToComponent"); //$NON-NLS-1$
	// Mark all @Unwrap methods which are not declared in components' classes.
	public static final String UNWRAP_DOESNT_BELONG_TO_COMPONENT = INSTANCE.createSeverityOption("unwrapDoesNotBelongToComponent"); //$NON-NLS-1$
	// Mark all @Observer methods which are not declared in components' classes.
	public static final String OBSERVER_DOESNT_BELONG_TO_COMPONENT = INSTANCE.createSeverityOption("observerDoesNotBelongToComponent"); //$NON-NLS-1$

	//Factories

	// Factory method with a void return type must have an associated @Out/Databinder. Mark factory otherwise.
	public static final String UNKNOWN_FACTORY_NAME = INSTANCE.createSeverityOption("unknownFactoryName"); //$NON-NLS-1$

	//Bijections

	// @DataModelSelection and @DataModelSelectionIndex without name requires the only one @DataModel in the component. Mark @DataModelSelection or @DataModelSelectionIndex otherwise.
	public static final String MULTIPLE_DATA_BINDER = INSTANCE.createSeverityOption("multipleDataBinder"); //$NON-NLS-1$
	// Mark @DataModelSelection or @DataModelSelectionIndex with unknown name. We should have @DataModel or @Out with the same name.
	public static final String UNKNOWN_DATA_MODEL = INSTANCE.createSeverityOption("unknownDataModel"); //$NON-NLS-1$

	//Context variables

	// If factory uses a name of any components (roles) or other factories then mark all these context variables' names. 
	public static final String DUPLICATE_VARIABLE_NAME = INSTANCE.createSeverityOption("duplicateVariableName"); //$NON-NLS-1$
	// If @In uses a unknown context variable name then mark it.
	public static final String UNKNOWN_VARIABLE_NAME = INSTANCE.createSeverityOption("unknownVariableName"); //$NON-NLS-1$

	// Seam project settings

	// Mark seam project if it has any invalid seam setting.
	public static final String INVALID_PROJECT_SETTINGS = INSTANCE.createSeverityOption("invalidProjectSettings"); //$NON-NLS-1$
	public static final String INVALID_XML_VERSION = INSTANCE.createSeverityOption("invalidXMLVersion"); //$NON-NLS-1$

	/**
	 * @return the only instance of SeamPreferences
	 */
	public static SeamPreferences getInstance() {
		return INSTANCE;
	}

	private SeamPreferences() {		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#createSeverityOption(java.lang.String)
	 */
	@Override
	protected String createSeverityOption(String shortName) {
		String name = getPluginId() + ".validator.problem." + shortName; //$NON-NLS-1$
		SEVERITY_OPTION_NAMES.add(name);
		return name;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getPluginId()
	 */
	@Override
	protected String getPluginId() {
		return SeamCorePlugin.PLUGIN_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getSeverityOptionNames()
	 */
	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	public static boolean shouldValidateCore(IProject project) {
		return !(SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, NONUNIQUE_COMPONENT_NAME)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, STATEFUL_COMPONENT_WRONG_SCOPE)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_COMPONENT_CLASS_NAME)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_COMPONENT_PROPERTY)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, ENTITY_COMPONENT_WRONG_SCOPE)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_REMOVE)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_DESTROY)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_CREATE)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_UNWRAP)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, CREATE_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNWRAP_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, OBSERVER_DOESNT_BELONG_TO_COMPONENT)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_FACTORY_NAME)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, MULTIPLE_DATA_BINDER)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_DATA_MODEL)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_VARIABLE_NAME)) &&
		SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_VARIABLE_NAME)));
	}

	public static boolean shouldValidateSettings(IProject project) {
		return !SeamPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, INVALID_PROJECT_SETTINGS));
	}
}