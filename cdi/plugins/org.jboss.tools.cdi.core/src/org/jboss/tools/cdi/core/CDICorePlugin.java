/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CDICorePlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.cdi.core";

	public static final String CA_CDI_EL_IMAGE_PATH = "images/ca/icons_CDI_EL.gif";

	// The shared instance
	private static CDICorePlugin plugin;

	/**
	 * The constructor
	 */
	public CDICorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CDICorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static CDICoreNature getCDI(IProject project, boolean resolve) {
		if(project == null || !project.exists() || !project.isOpen()) return null;
		try {
			if(!project.hasNature(CDICoreNature.NATURE_ID)) return null;
		} catch (CoreException e) {
			//ignore - all checks are done above
			return null;
		}
		CDICoreNature n = null;
		try {
			n = (CDICoreNature)project.getNature(CDICoreNature.NATURE_ID);
			if(resolve) n.resolve();
		} catch (CoreException e) {
			getDefault().logError(e);
		}
		return n;
	}
}