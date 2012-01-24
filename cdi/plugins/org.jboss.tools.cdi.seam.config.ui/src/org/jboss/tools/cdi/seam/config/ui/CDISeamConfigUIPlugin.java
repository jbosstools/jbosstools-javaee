/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.ui;

import org.jboss.tools.common.log.BaseUIPlugin;

public class CDISeamConfigUIPlugin extends BaseUIPlugin {
	//The shared instance.
	private static CDISeamConfigUIPlugin plugin;
	
	public static final String PLUGIN_ID = "org.jboss.tools.cdi.seam.config.ui";  //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public CDISeamConfigUIPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CDISeamConfigUIPlugin getDefault() {
		return plugin;
	}

}
