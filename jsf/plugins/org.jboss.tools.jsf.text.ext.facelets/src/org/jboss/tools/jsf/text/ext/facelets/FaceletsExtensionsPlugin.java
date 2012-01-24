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
package org.jboss.tools.jsf.text.ext.facelets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.common.log.BaseUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class FaceletsExtensionsPlugin extends BaseUIPlugin {

	//The shared instance.
	private static FaceletsExtensionsPlugin plugin;
	
	public static final String PLUGIN_ID = "org.jboss.tools.jsf.text.ext.facelets"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public FaceletsExtensionsPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static FaceletsExtensionsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.jboss.tools.jsf.text.ext.facelets", path); //$NON-NLS-1$
	}
	
}
