/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;

/**
 * @author eskimo
 *
 */
public class SeamFacetPreferenceInitializer extends
		AbstractPreferenceInitializer {

	/**
	 * 
	 */
	public SeamFacetPreferenceInitializer() {}

	@Override
	public void initializeDefaultPreferences() {
		IScopeContext context = new DefaultScope();
		IEclipsePreferences node = context.getNode(SeamCorePlugin.PLUGIN_ID);
		node.put(SeamFacetPreference.SEAM_HOME_FOLDER, getSeamGenBuildPath());
	}

	public static final String SEAM_GEN_HOME = "../../../../jboss-eap/seam"; 
	
	public String getSeamGenBuildPath() {
		String pluginLocation=null;
		try {
			pluginLocation = FileLocator.resolve(SeamCorePlugin.getDefault().getBundle().getEntry("/")).getFile();
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		};
		File seamGenDir = new File(pluginLocation, SEAM_GEN_HOME);
		Path  p = new Path(seamGenDir.getPath());
		p.makeAbsolute();
		if(p.toFile().exists()) {
			return p.toFile().getAbsolutePath();
		} else {
			return "";
		}
	}
	
}
