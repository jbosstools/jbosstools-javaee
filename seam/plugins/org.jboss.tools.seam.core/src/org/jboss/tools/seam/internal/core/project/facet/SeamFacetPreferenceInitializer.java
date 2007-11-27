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

import org.eclipse.core.internal.preferences.PreferencesService;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
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
		node.put(SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE, "DefaultDS"); //$NON-NLS-1$
		node.put(SeamProjectPreferences.JBOSS_AS_DEFAULT_DEPLOY_AS, "war"); //$NON-NLS-1$
		node.put(SeamProjectPreferences.HIBERNATE_DEFAULT_DB_TYPE, "HSQL"); //$NON-NLS-1$
		initializeDefault(node,getSeamGenBuildPath());
		try {
			node.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	public static final String SEAM_GEN_HOME = "../../../../jboss-eap/seam";  //$NON-NLS-1$
	
	public String getSeamGenBuildPath() {
		String pluginLocation=null;
		try {
			pluginLocation = FileLocator.resolve(SeamCorePlugin.getDefault().getBundle().getEntry("/")).getFile(); //$NON-NLS-1$
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		};
		File seamGenDir = new File(pluginLocation, SEAM_GEN_HOME);
		Path  p = new Path(seamGenDir.getPath());
		p.makeAbsolute();
		if(p.toFile().exists()) {
			return p.toOSString();
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @param node 
	 * @param seamGenBuildPath
	 * @return
	 */
	public void initializeDefault(IEclipsePreferences node, String seamGenBuildPath) {
		Map<String, SeamRuntime> map = new HashMap<String,SeamRuntime>();
		File seamFolder = new File(seamGenBuildPath);
		if(seamFolder.exists() && seamFolder.isDirectory()) {
			SeamRuntime rt = new SeamRuntime();
			rt.setHomeDir(seamGenBuildPath);
			rt.setName("Seam " + SeamVersion.V_1_2+".AP"); //$NON-NLS-1$ //$NON-NLS-2$
			rt.setDefault(true);
			rt.setVersion(SeamVersion.SEAM_1_2);
			map.put(rt.getName(), rt);
		}
		node.put(SeamProjectPreferences.RUNTIME_LIST, new SeamRuntimeListConverter().getString(map));
	}
}
