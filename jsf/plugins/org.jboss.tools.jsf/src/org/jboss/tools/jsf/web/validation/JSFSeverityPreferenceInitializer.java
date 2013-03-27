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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Alexey Kazakov
 */
public class JSFSeverityPreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultPreferences = ((IScopeContext) DefaultScope.INSTANCE).getNode(JSFModelPlugin.PLUGIN_ID);
		defaultPreferences.put(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, SeverityPreferences.ENABLE);
		defaultPreferences.put(SeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME, JSFSeverityPreferences.ERROR);
		for (String name : JSFSeverityPreferences.SEVERITY_OPTION_NAMES) {
			defaultPreferences.put(name, SeverityPreferences.WARNING);
		}
		defaultPreferences.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, SeverityPreferences.IGNORE);
		defaultPreferences.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, SeverityPreferences.IGNORE);
		defaultPreferences.putInt(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, SeverityPreferences.DEFAULT_MAX_NUMBER_OF_MARKERS_PER_FILE);
	}
}