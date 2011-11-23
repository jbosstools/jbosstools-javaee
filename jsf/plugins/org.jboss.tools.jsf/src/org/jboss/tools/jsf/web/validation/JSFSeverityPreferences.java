/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.web.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Alexey kazakov
 */
public class JSFSeverityPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static JSFSeverityPreferences INSTANCE = new JSFSeverityPreferences();

	// Mark tag which can't be found.
	public static final String UNKNOWN_COMPOSITE_COMPONENT_NAME = INSTANCE.createSeverityOption("unknownComponent"); //$NON-NLS-1$

	// Mark attribute which can't be found.
	public static final String UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE = INSTANCE.createSeverityOption("unknownAttribute"); //$NON-NLS-1$

	//Faces Config
		//Application
	public static final String INVALID_ACTION_LISTENER = INSTANCE.createSeverityOption("invalidActionListener"); //$NON-NLS-1$
	public static final String INVALID_NAVIGATION_HANDLER = INSTANCE.createSeverityOption("invalidNavigationHandler"); //$NON-NLS-1$
	public static final String INVALID_PROPERTY_RESOLVER = INSTANCE.createSeverityOption("invalidPropertyResolver"); //$NON-NLS-1$
	public static final String INVALID_STATE_MANAGER = INSTANCE.createSeverityOption("invalidStateManager"); //$NON-NLS-1$
	public static final String INVALID_VARIABLE_RESOLVER = INSTANCE.createSeverityOption("invalidVariableResolver"); //$NON-NLS-1$
	public static final String INVALID_VIEW_HANDLER = INSTANCE.createSeverityOption("invalidViewHandler"); //$NON-NLS-1$
	
	
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
		String name = getPluginId() + ".composite.validator.problem." + shortName; //$NON-NLS-1$
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

	public static boolean isValidationEnabled(IProject project) {
		return INSTANCE.isEnabled(project);
	}

	public static int getMaxNumberOfProblemMarkersPerFile(IProject project) {
		return INSTANCE.getMaxNumberOfProblemMarkersPerResource(project);
	}

	public static boolean shouldValidateEL(IProject project) {
		return !(SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_COMPOSITE_COMPONENT_NAME)) &&
				SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE)));
	}
}