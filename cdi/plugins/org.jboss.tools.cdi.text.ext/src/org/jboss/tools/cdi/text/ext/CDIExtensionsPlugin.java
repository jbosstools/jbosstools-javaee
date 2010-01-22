/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.text.ext;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class CDIExtensionsPlugin extends AbstractUIPlugin{
	//The shared instance.
	private static CDIExtensionsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	public static final String PLUGIN_ID = "org.jboss.tools.cdi.text.ext";  //$NON-NLS-1$

	
	/**
	 * The constructor.
	 */
	public CDIExtensionsPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CDIExtensionsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= CDIExtensionsPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	static public void log(String msg) {
		CDIExtensionsPlugin.getDefault().getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));
	}
	
	public static void log(IStatus status) {
		CDIExtensionsPlugin.getDefault().getLog().log(status);
	}
	public static void log(String message, Exception exception) {
		CDIExtensionsPlugin.getDefault().getLog().log(new Status(Status.ERROR, CDIExtensionsPlugin.PLUGIN_ID, Status.OK, message, exception));		
	}
	static public void log(Exception ex) {
		CDIExtensionsPlugin.getDefault().getLog().log(new Status(Status.ERROR, CDIExtensionsPlugin.PLUGIN_ID, Status.OK, "No message", ex));
	}


}
