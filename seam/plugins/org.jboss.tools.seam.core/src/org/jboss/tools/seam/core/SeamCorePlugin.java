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
import java.util.HashMap;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
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
	
	// A Map to save a descriptor for each image
	private HashMap fImageDescRegistry = null;

	
	public static final String CA_SEAM_EL_IMAGE_PATH = "images/ca/icons_Seam_EL.gif";
	public static final String CA_SEAM_MESSAGES_IMAGE_PATH = "images/ca/icons_Message_Bundles.gif";
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

	/**
	 * Creates an image from the given resource and adds the image to the
	 * image registry.
	 * 
	 * @param resource
	 * @return Image
	 */
	private Image createImage(String resource) {
		ImageDescriptor desc = getImageDescriptorFromRegistry(resource);
		Image image = null;

		if (desc != null) {
			image = desc.createImage();
			// dont add the missing image descriptor image to the image
			// registry
			if (!desc.equals(ImageDescriptor.getMissingImageDescriptor())) {
				getImageRegistry().put(resource, image);
			}
		}
		return image;
	}

	/**
	 * Creates an image descriptor from the given imageFilePath and adds the
	 * image descriptor to the image descriptor registry. If an image
	 * descriptor could not be created, the default "missing" image descriptor
	 * is returned but not added to the image descriptor registry.
	 * 
	 * @param imageFilePath
	 * @return ImageDescriptor image descriptor for imageFilePath or default
	 *         "missing" image descriptor if resource could not be found
	 */
	private ImageDescriptor createImageDescriptor(String imageFilePath) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
		if (imageDescriptor != null) {
			getImageDescriptorRegistry().put(imageFilePath, imageDescriptor);
		}
		else {
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * Retrieves the image associated with resource from the image registry.
	 * If the image cannot be retrieved, attempt to find and load the image at
	 * the location specified in resource.
	 * 
	 * @param resource
	 *            the image to retrieve
	 * @return Image the image associated with resource or null if one could
	 *         not be found
	 */
	public Image getImage(String resource) {
		Image image = getImageRegistry().get(resource);
		if (image == null) {
			// create an image
			image = createImage(resource);
		}
		return image;
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image
	 * descriptor registry. If the image descriptor cannot be retrieved,
	 * attempt to find and load the image descriptor at the location specified
	 * in resource.
	 * 
	 * @param resource
	 *            the image descriptor to retrieve
	 * @return ImageDescriptor the image descriptor assocated with resource or
	 *         the default "missing" image descriptor if one could not be
	 *         found
	 */
	public ImageDescriptor getImageDescriptorFromRegistry(String resource) {
		ImageDescriptor imageDescriptor = null;
		Object o = getImageDescriptorRegistry().get(resource);
		if (o == null) {
			// create a descriptor
			imageDescriptor = createImageDescriptor(resource);
		}
		else {
			imageDescriptor = (ImageDescriptor) o;
		}
		return imageDescriptor;
	}

	/**
	 * Returns the image descriptor registry for this plugin.
	 * 
	 * @return HashMap - image descriptor registry for this plugin
	 */
	private HashMap getImageDescriptorRegistry() {
		if (fImageDescRegistry == null) {
			fImageDescRegistry = new HashMap();
		}
		return fImageDescRegistry;
	}

}