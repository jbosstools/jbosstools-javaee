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
package org.jboss.tools.jsf.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class JSFPreferenceInitializer extends AbstractPreferenceInitializer {

	public JSFPreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {

		IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(JSFModelPlugin.PLUGIN_ID);
		for (String name : JSFSeverityPreferences.SEVERITY_OPTION_NAMES) {
			defaultPreferences.put(name, JSFSeverityPreferences.ERROR);
		}
		defaultPreferences.put(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
		defaultPreferences.put(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, JSFSeverityPreferences.WARNING);
		defaultPreferences.put(JSFSeverityPreferences.UNPAIRED_GETTER_OR_SETTER, JSFSeverityPreferences.IGNORE);
		defaultPreferences.put(JSFSeverityPreferences.EL_SYNTAX_ERROR, JSFSeverityPreferences.WARNING);
		defaultPreferences.put(JSFSeverityPreferences.CHECK_VARS, JSFSeverityPreferences.ENABLE);
		defaultPreferences.put(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
	}
}