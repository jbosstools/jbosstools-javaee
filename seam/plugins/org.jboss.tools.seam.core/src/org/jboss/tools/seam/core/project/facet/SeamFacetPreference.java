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

package org.jboss.tools.seam.core.project.facet;

import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author eskimo
 *
 */
public class SeamFacetPreference {
	public static final String SEAM_HOME_FOLDER = SeamCorePlugin.PLUGIN_ID + ".project.facet.seamhome";
	public static final String SEAM_DEFAULT_CONNECTION_PROFILE = SeamCorePlugin.PLUGIN_ID + ".project.facet.default.conn.profile";
	
	public static String getStringPreference(final String name) {
		return SeamCorePlugin.getDefault().getPreferenceStore().getString(SeamFacetPreference.SEAM_HOME_FOLDER);
	}
}
