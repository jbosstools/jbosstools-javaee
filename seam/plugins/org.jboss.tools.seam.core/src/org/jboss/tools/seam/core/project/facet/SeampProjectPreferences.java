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
public class SeampProjectPreferences {
	public static final String SEAM_DEFAULT_RUNTIME_NAME 
		= SeamCorePlugin.PLUGIN_ID + ".project.facet.default.runtime.name"; //$NON-NLS-1$
	public static final String SEAM_DEFAULT_CONNECTION_PROFILE 
		= SeamCorePlugin.PLUGIN_ID + ".project.facet.default.conn.profile"; //$NON-NLS-1$
	public static final String RUNTIME_CONFIG_FORMAT_VERSION 
		= SeamCorePlugin.PLUGIN_ID + ".runtime.config.format.version"; //$NON-NLS-1$
	public static final String RUNTIME_LIST 
		= SeamCorePlugin.PLUGIN_ID+".runtime.list"; //$NON-NLS-1$
	public static final String RUNTIME_DEFAULT 
		= SeamCorePlugin.PLUGIN_ID+".runtime.default"; //$NON-NLS-1$
	public static final String JBOSS_AS_DEFAULT_DEPLOY_AS 
		= SeamCorePlugin.PLUGIN_ID + ".project.default.deployment.type"; //$NON-NLS-1$
	public static final String HIBERNATE_DEFAULT_DB_TYPE 
		= SeamCorePlugin.PLUGIN_ID + ".hibernate.default.db.type"; //$NON-NLS-1$
	public static final String SEAM_LAST_SERVER_NAME 
	= SeamCorePlugin.PLUGIN_ID + ".project.facet.last.server.name"; //$NON-NLS-1$
	
	public static String getStringPreference(final String name) {
		return SeamCorePlugin.getDefault().getPreferenceStore().getString(name);
	}
}
