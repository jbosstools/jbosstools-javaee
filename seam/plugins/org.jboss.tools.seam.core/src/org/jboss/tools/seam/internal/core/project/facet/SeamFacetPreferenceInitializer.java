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

package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeListConverter;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author eskimo
 *
 */
public class SeamFacetPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public static String RUNTIME_CONFIG_FORMAT_VERSION = "1.0"; //$NON-NLS-1$
	
	public static final String SEAM_1_2_HOME = "../../../../jboss-eap/seam";  //$NON-NLS-1$
	public static final String SEAM_2_0_HOME = "../../../../jboss-eap/seamfp";  //$NON-NLS-1$

	/**
	 * 
	 */
	public SeamFacetPreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = (IEclipsePreferences)
			Platform.getPreferencesService()
				.getRootNode()
				.node(DefaultScope.SCOPE)
				.node(SeamCorePlugin.PLUGIN_ID);

		node.put(SeamProjectPreferences.RUNTIME_CONFIG_FORMAT_VERSION, RUNTIME_CONFIG_FORMAT_VERSION);
		node.put(SeamProjectPreferences.JBOSS_AS_DEFAULT_DEPLOY_AS, "war"); //$NON-NLS-1$
		node.put(SeamProjectPreferences.HIBERNATE_DEFAULT_DB_TYPE, "HSQL"); //$NON-NLS-1$
		node.put(SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE, "DefaultDS"); //$NON-NLS-1$
		Map<String, SeamRuntime> map = new HashMap<String,SeamRuntime>();
		
		try {
			node.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

}
