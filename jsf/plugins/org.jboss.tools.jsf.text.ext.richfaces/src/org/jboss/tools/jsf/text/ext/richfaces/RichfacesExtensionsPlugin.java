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
package org.jboss.tools.jsf.text.ext.richfaces;

import org.eclipse.ui.plugin.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class RichfacesExtensionsPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static RichfacesExtensionsPlugin plugin;
	
	public static final String PLUGIN_ID = "org.jboss.tools.jsf.text.ext.richfaces";  //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public RichfacesExtensionsPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RichfacesExtensionsPlugin getDefault() {
		return plugin;
	}

}
