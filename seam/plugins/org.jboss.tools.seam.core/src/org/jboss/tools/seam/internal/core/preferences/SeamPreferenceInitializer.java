package org.jboss.tools.seam.internal.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;

public class SeamPreferenceInitializer extends AbstractPreferenceInitializer {
	
	public SeamPreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {

		IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(SeamCorePlugin.PLUGIN_ID);
		for (String name : SeamPreferences.severityOptionNames) {
			defaultPreferences.put(name, SeamPreferences.ERROR);
		}

	}

}
