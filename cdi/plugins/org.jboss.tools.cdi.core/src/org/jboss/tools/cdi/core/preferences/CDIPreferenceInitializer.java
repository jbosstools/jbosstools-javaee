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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CDIPreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(CDICorePlugin.PLUGIN_ID);
		defaultPreferences.putBoolean(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
		for (String name : CDIPreferences.SEVERITY_OPTION_NAMES) {
			defaultPreferences.put(name, SeverityPreferences.ERROR);
		}
		defaultPreferences.put(CDIPreferences.INTERCEPTOR_HAS_NAME, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.DECORATOR_HAS_NAME, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.ILLEGAL_SCOPE_FOR_INTERCEPTOR, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.ILLEGAL_SCOPE_FOR_DECORATOR, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.INTERCEPTOR_ANNOTATED_SPECIALIZES, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.DECORATOR_ANNOTATED_SPECIALIZES, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.UNSATISFIED_INJECTION_POINTS, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.AMBIGUOUS_INJECTION_POINTS, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.INJECT_RESOLVES_TO_NULLABLE_BEAN, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.INJECTED_DECORATOR, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.INJECTED_INTERCEPTOR, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER, CDIPreferences.WARNING);
		defaultPreferences.put(CDIPreferences.ILLEGAL_CONDITIONAL_OBSERVER, CDIPreferences.WARNING);
	}
}