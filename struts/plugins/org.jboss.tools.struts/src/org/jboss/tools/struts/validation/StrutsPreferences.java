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
package org.jboss.tools.struts.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.WebModelPlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class StrutsPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static StrutsPreferences INSTANCE = new StrutsPreferences();

	public static final String INVALID_ACTION_FORWARD = INSTANCE.createSeverityOption("invalidActionForward"); //$NON-NLS-1$

	public static final String INVALID_ACTION_NAME = INSTANCE.createSeverityOption("invalidActionName"); //$NON-NLS-1$
	public static final String INVALID_ACTION_REFERENCE_ATTRIBUTE = INSTANCE.createSeverityOption("invalidActionReferenceAttribute"); //$NON-NLS-1$
	public static final String INVALID_ACTION_TYPE = INSTANCE.createSeverityOption("invalidActionType"); //$NON-NLS-1$

	public static final String INVALID_GLOBAL_FORWARD = INSTANCE.createSeverityOption("invalidGlobalForward"); //$NON-NLS-1$
	public static final String INVALID_GLOBAL_EXCEPTION = INSTANCE.createSeverityOption("invalidGlobalException"); //$NON-NLS-1$

	public static final String INVALID_CONTROLLER = INSTANCE.createSeverityOption("invalidController"); //$NON-NLS-1$
	public static final String INVALID_MESSAGE_RESOURCES = INSTANCE.createSeverityOption("invalidMessageResources"); //$NON-NLS-1$

	public static final String INVALID_INIT_PARAM = INSTANCE.createSeverityOption("invalidInitParam"); //$NON-NLS-1$

	/**
	 * @return the only instance of CDIPreferences
	 */
	public static StrutsPreferences getInstance() {
		return INSTANCE;
	}

	private StrutsPreferences() {
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
		return WebModelPlugin.PLUGIN_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getSeverityOptionNames()
	 */
	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	public static boolean shouldValidateCore(IProject project) {
		return true;
	}

	public static boolean isValidationEnabled(IProject project) {
		return INSTANCE.isEnabled(project);
	}

	public static int getMaxNumberOfProblemMarkersPerFile(IProject project) {
		return INSTANCE.getMaxNumberOfProblemMarkersPerResource(project);
	}
}