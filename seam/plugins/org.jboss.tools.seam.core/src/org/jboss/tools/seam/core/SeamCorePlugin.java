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
package org.jboss.tools.seam.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SeamCorePlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.seam.core"; //$NON-NLS-1$

	// The shared instance
	private static SeamCorePlugin plugin;

	public static final String CA_SEAM_EL_IMAGE_PATH = "images/ca/icons_Seam_EL.gif";
	public static final String CA_SEAM_MESSAGES_IMAGE_PATH = "images/ca/icons_Message_Bundles.gif";

	static final String M2_FACET_ID = "jboss.m2"; //$NON-NLS-1$

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
		cleanCachedProjects();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	static void cleanCachedProjects() {
		IPath path = SeamCorePlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects"); //$NON-NLS-1$
		if(file.isDirectory()) {
			File[] fs = file.listFiles();
			if(fs != null) {
				for (int i = 0; i < fs.length; i++) {
					if(!fs[i].isFile()) continue;
					String n = fs[i].getName();
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(n);
					if(p == null || !p.isAccessible()) {
						fs[i].delete();
					}
				}
			}
		}
	}

	IResourceChangeListener resourceChangeListener = new RCL();

	class RCL implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			if(event.getType() == IResourceChangeEvent.PRE_DELETE
				|| event.getType() == IResourceChangeEvent.PRE_CLOSE) {
				IResource r = event.getResource();
				if(r instanceof IProject) {
					IProject p = (IProject)r;
					SeamProject sp = (SeamProject)getSeamProject(p, false);
					if(sp != null) {
						sp.clearStorage();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
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

		ISeamProject seamProject;
		try {
			seamProject = (ISeamProject)project.getNature(ISeamProject.NATURE_ID);
			if(resolve) seamProject.resolve();
			return seamProject;
		} catch (CoreException e) {
			getPluginLog().logError(e);
		}
		return null;
	}

	public static IEclipsePreferences getSeamPreferences(IProject project) {
		IScopeContext projectScope = new ProjectScope(project);
		return projectScope.getNode(PLUGIN_ID);
	}

	/**
	 * @param string
	 * @return
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, -1, message, exception);
	}

	private static List<ISeamProjectChangeListener> listeners = new ArrayList<ISeamProjectChangeListener>();

	/**
	 * 
	 */
	public static void addSeamProjectListener(ISeamProjectChangeListener listener) {
		synchronized(listeners) {
			if(listeners.contains(listener)) return;
			listeners.add(listener);
		}
	}

	/**
	 * 
	 */
	public static void removeSeamProjectListener(ISeamProjectChangeListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	public static void fire(SeamProjectChangeEvent event) {
		ISeamProjectChangeListener[] ls = null;
		synchronized(listeners) {
			ls = listeners.toArray(new ISeamProjectChangeListener[0]);
		}
		if(ls != null) {
			for (int i = 0; i < ls.length; i++) {
				ls[i].projectChanged(event);
			}
		}
	}

	public boolean hasM2Facet(IProject project) {
		try {
			return FacetedProjectFramework.hasProjectFacet(project, M2_FACET_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.log.BaseUIPlugin#getId()
	 */
	@Override
	public String getId() {
		return PLUGIN_ID;
	}
}