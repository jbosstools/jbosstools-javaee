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
package org.jboss.tools.cdi.core.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CDIPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static CDIPreferences INSTANCE = new CDIPreferences();

	// Test group

	// Test
	public static final String TEST = INSTANCE.createSeverityOption("testKey"); //$NON-NLS-1$

	/**
	 * @return the only instance of CDIPreferences
	 */
	public static CDIPreferences getInstance() {
		return INSTANCE;
	}

	private CDIPreferences() {
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
		return CDICorePlugin.PLUGIN_ID;
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
}