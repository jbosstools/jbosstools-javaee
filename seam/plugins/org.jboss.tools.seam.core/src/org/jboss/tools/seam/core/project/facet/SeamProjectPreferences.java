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
 * Seam project related preferences constants
 * 
 * @author eskimo
 */
public class SeamProjectPreferences {
	/**
	 * Last selected connection profile
	 */
	public static final String SEAM_DEFAULT_CONNECTION_PROFILE = SeamCorePlugin.PLUGIN_ID
			+ ".project.facet.default.conn.profile"; //$NON-NLS-1$

	/**
	 * TODO
	 */
	public static final String RUNTIME_CONFIG_FORMAT_VERSION = SeamCorePlugin.PLUGIN_ID
			+ ".runtime.config.format.version"; //$NON-NLS-1$

	/**
	 * String represents list of available runtimes
	 */
	public static final String RUNTIME_LIST = SeamCorePlugin.PLUGIN_ID
			+ ".runtime.list"; //$NON-NLS-1$

	/**
	 * Last selected deployment type
	 */
	public static final String JBOSS_AS_DEFAULT_DEPLOY_AS = SeamCorePlugin.PLUGIN_ID
			+ ".project.default.deployment.type"; //$NON-NLS-1$

	/**
	 * Last selected Seam configuration template
	 */
	public static final String SEAM_CONFIG_TEMPLATE = SeamCorePlugin.PLUGIN_ID
			+ ".project.facet.config.template"; //$NON-NLS-1$

	/**
	 * Last selected DB type
	 */
	public static final String HIBERNATE_DEFAULT_DB_TYPE = SeamCorePlugin.PLUGIN_ID
			+ ".hibernate.default.db.type"; //$NON-NLS-1$

	/**
	 * Last selected server
	 */
	public static final String SEAM_LAST_SERVER_NAME = SeamCorePlugin.PLUGIN_ID
			+ ".project.facet.last.server.name"; //$NON-NLS-1$

	/**
	 * Return String value from SeamCore preferences by name
	 * 
	 * @param name
	 *            preference property name
	 * @return preference property value
	 */
	public static String getStringPreference(final String name) {
		return SeamCorePlugin.getDefault().getPreferenceStore().getString(name);
	}
}