/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.validation.ValidationSeverityPreferences;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DeltaspikeSeverityPreferences extends ValidationSeverityPreferences {
	private static final DeltaspikeSeverityPreferences INSTANCE = new DeltaspikeSeverityPreferences();

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	public static final String WARNING_GROUP_ID = "deltaspike"; //$NON-NLS-1$
	public static final String AMBIGUOUS_AUTHORIZER = INSTANCE.createSeverityOption("ambiguousAuthorizer", "ambiguous-authorizer"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String UNRESOLVED_AUTHORIZER = INSTANCE.createSeverityOption("unresolvedAuthorizer", "unresolved-authorizer"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String INVALID_AUTHORIZER = INSTANCE.createSeverityOption("invalidAuthorizer", "invalid-authorizer"); //$NON-NLS-1$ //$NON-NLS-2$

	public static DeltaspikeSeverityPreferences getInstance() {
		return INSTANCE;
	}

	private DeltaspikeSeverityPreferences() {}

	@Override
	public String getWarningGroupID() {
		return WARNING_GROUP_ID;
	}

	@Override
	protected String[] getParentWarningGroupIDs() {
		return new String[]{CDIPreferences.WARNING_GROUP_ID};
	}

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
		return DeltaspikeCorePlugin.PLUGIN_ID;
	}

}
