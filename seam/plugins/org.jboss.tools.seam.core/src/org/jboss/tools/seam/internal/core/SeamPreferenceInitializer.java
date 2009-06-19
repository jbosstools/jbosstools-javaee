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
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamPreferenceInitializer extends AbstractPreferenceInitializer {

	public SeamPreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {

		IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(SeamCorePlugin.PLUGIN_ID);
		for (String name : SeamPreferences.severityOptionNames) {
			defaultPreferences.put(name, SeamPreferences.ERROR);
		}
		defaultPreferences.put(SeamPreferences.UNKNOWN_EL_VARIABLE_NAME, SeamPreferences.IGNORE);
		defaultPreferences.put(SeamPreferences.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, SeamPreferences.WARNING);
		defaultPreferences.put(SeamPreferences.UNKNOWN_VARIABLE_NAME, SeamPreferences.WARNING);
		defaultPreferences.put(SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY, SeamPreferences.WARNING);
		defaultPreferences.put(SeamPreferences.UNPAIRED_GETTER_OR_SETTER, SeamPreferences.IGNORE);
		defaultPreferences.put(SeamPreferences.EL_SYNTAX_ERROR, SeamPreferences.WARNING);
		defaultPreferences.put(SeamPreferences.INVALID_XML_VERSION, SeamPreferences.WARNING);
		defaultPreferences.put(SeamPreferences.CHECK_VARS, SeamPreferences.ENABLE);
		defaultPreferences.put(SeamPreferences.RE_VALIDATE_UNRESOLVED_EL, SeamPreferences.ENABLE);

		//JBIDE-2958 temporary until JBIDE-2957 solved //TODO solve JBIDE-2957
		defaultPreferences.put(SeamPreferences.UNKNOWN_COMPONENT_CLASS_NAME_GUESS, SeamPreferences.WARNING);
	}
}