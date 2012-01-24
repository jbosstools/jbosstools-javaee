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
package org.jboss.tools.jsf.text.ext;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.common.log.BaseUIPlugin;

/**
 * @author Jeremy
 *
 */
public class JSFExtensionsPlugin extends BaseUIPlugin {
	//The shared instance.
	private static JSFExtensionsPlugin plugin;
	
	public static final String PLUGIN_ID = "org.jboss.tools.jsf.text.ext";  //$NON-NLS-1$

	
	/**
	 * The constructor.
	 */
	public JSFExtensionsPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JSFExtensionsPlugin getDefault() {
		return plugin;
	}

	public static void log(String message, Exception exception) {
		JSFExtensionsPlugin.getDefault().logError(message, exception);		
	}

}
