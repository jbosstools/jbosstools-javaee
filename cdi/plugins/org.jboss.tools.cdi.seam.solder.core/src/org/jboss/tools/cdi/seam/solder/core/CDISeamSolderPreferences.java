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
package org.jboss.tools.cdi.seam.solder.core;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderPreferences extends SeverityPreferences {
	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static CDISeamSolderPreferences INSTANCE = new CDISeamSolderPreferences();

	public static final String AMBIGUOUS_GENERIC_CONFIGURATION_POINT = INSTANCE.createSeverityOption("ambiguousGenericConfigurationPoint");
	public static final String WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT = INSTANCE.createSeverityOption("wrongTypeOfGenericConfigurationPoint");

	public static CDISeamSolderPreferences getInstance() {
		return INSTANCE;
	}

	private CDISeamSolderPreferences() {}

	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	@Override
	protected String createSeverityOption(String shortName) {
		String name = getPluginId() + ".validator.problem." + shortName; //$NON-NLS-1$
		SEVERITY_OPTION_NAMES.add(name);
		return name;
	}

	@Override
	protected String getPluginId() {
		return CDISeamSolderCorePlugin.PLUGIN_ID;
	}

}
