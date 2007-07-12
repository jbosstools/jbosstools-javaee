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
package org.jboss.tools.seam.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SeamCorePlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.seam.core";

	// The shared instance
	private static SeamCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public SeamCorePlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static SeamCorePlugin getDefault() {
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
	
	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
	
	/**
	 * Factory method creating seam project instance by project resource.
	 * Returns null if 
	 * (1) project does not exist 
	 * (2) project is closed 
	 * (3) project has no seam nature
	 * (4) creating seam project failed.
	 * @param project
	 * @param resolve if true and results of last build have not been resolved they are loaded.
	 * @return
	 */
	public static ISeamProject getSeamProject(IProject project, boolean resolve) {
		if(project == null || !project.exists() || !project.isOpen()) return null;
		try {
			if(!project.hasNature(ISeamProject.NATURE_ID)) return null;
		} catch (CoreException e) {
			//ignore - all checks are done above
			return null;
		}
		try {
			ISeamProject seamProject = (ISeamProject)project.getNature(ISeamProject.NATURE_ID);
			if(resolve) seamProject.resolve();
			return seamProject;
		} catch (Exception e) {
			getPluginLog().logError(e);
		}
		return null;
	}

}