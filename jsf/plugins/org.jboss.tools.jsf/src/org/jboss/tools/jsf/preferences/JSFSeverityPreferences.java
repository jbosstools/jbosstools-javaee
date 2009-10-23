/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Alexey Kazakov
 */
public class JSFSeverityPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static JSFSeverityPreferences INSTANCE = new JSFSeverityPreferences();

	// Expression Language

	// Mark EL Variable name which we can't resolve.
	public static final String UNKNOWN_EL_VARIABLE_NAME = INSTANCE.createSeverityOption("unknownElVariableName"); //$NON-NLS-1$
	// Check "var" attributes.
	public static final String CHECK_VARS = INSTANCE.createSeverityOption("checkVars"); //$NON-NLS-1$
	// Re-validate unresolved ELs.
	public static final String RE_VALIDATE_UNRESOLVED_EL = INSTANCE.createSeverityOption("revalidateUnresolvedEl"); //$NON-NLS-1$
	// Mark EL Variable property name which we can't resolve.
	public static final String UNKNOWN_EL_VARIABLE_PROPERTY_NAME = INSTANCE.createSeverityOption("unknownElVariablePropertyName"); //$NON-NLS-1$
	// If Expression use property of bean and this property has only setter(getter) without getter(setter) then mark it.
	public static final String UNPAIRED_GETTER_OR_SETTER = INSTANCE.createSeverityOption("unpairedGetterOrSetter"); //$NON-NLS-1$

	public static final String EL_SYNTAX_ERROR = INSTANCE.createSeverityOption("elSyntaxError"); //$NON-NLS-1$

	/**
	 * @return the only instance of JSFSeverityPreferences
	 */
	public static JSFSeverityPreferences getInstance() {
		return INSTANCE;
	}

	private JSFSeverityPreferences() {
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
		return JSFModelPlugin.PLUGIN_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getSeverityOptionNames()
	 */
	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	public static boolean shouldValidateEL(IProject project) {
		return !(SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_EL_VARIABLE_NAME)) &&
				SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_EL_VARIABLE_PROPERTY_NAME)) && 
				SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, EL_SYNTAX_ERROR)) && 
				SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNPAIRED_GETTER_OR_SETTER)));
	}
}