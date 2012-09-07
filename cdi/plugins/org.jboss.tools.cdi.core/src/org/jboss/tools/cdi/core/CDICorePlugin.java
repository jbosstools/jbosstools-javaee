/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.cdi.internal.core.event.CDIProjectChangeEvent;
import org.jboss.tools.cdi.internal.core.event.ICDIProjectChangeListener;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.osgi.framework.Bundle;
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
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
		super.stop(context);
	}

	IResourceChangeListener resourceChangeListener = new RCL();

	class RCL implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			if(event.getType() == IResourceChangeEvent.PRE_DELETE || event.getType() == IResourceChangeEvent.PRE_CLOSE) {
				IResource r = event.getResource();
				if(r instanceof IProject) {
					IProject p = (IProject)r;
					CDICoreNature n = (CDICoreNature)getCDINature(p);
					if(n != null) {
						n.dispose();
					}
				}
			} else if(event.getType() == IResourceChangeEvent.POST_CHANGE) {
				IResourceDelta[] cs = event.getDelta().getAffectedChildren(IResourceDelta.CHANGED);
				for (IResourceDelta c: cs) {
					if((c.getFlags() & IResourceDelta.OPEN) != 0 && c.getResource() instanceof IProject) {
						IProject p = (IProject)c.getResource();
						getCDI(p, true);
					}
				}
			}
		}
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

	/**
	 * Returns CDI project.
	 * @param project
	 * @param resolve
	 * @return
	 */
	public static ICDIProject getCDIProject(IProject project, boolean resolve) {
		CDICoreNature nature = getCDI(project, resolve);
		if(nature!=null) {
			return nature.getDelegate();
		}
		return null;
	}

	private static CDICoreNature getCDINature(IProject project) {
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
		} catch (CoreException e) {
			getDefault().logError(e);
		}
		return n;
	}

	public static CDICoreNature getCDI(IProject project, boolean resolve) {
		CDICoreNature n = getCDINature(project);
		if(n != null) {
			if(resolve) {
				n.resolve();
			} else {
				n.loadProjectDependencies();
			}
		}
		return n;
	}
	
	private static List<ICDIProjectChangeListener> listeners = new ArrayList<ICDIProjectChangeListener>();

	/**
	 * Adds CDI Project listener
	 */
	public static void addCDIProjectListener(ICDIProjectChangeListener listener) {
		synchronized(listeners) {
			if(listeners.contains(listener)) return;
			listeners.add(listener);
		}
	}

	/**
	 * Removes CDI Project listener
	 */
	public static void removeCDIProjectListener(ICDIProjectChangeListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Fires CDI Project change event
	 * 
	 * @param event
	 */
	public static void fire(CDIProjectChangeEvent event) {
		ICDIProjectChangeListener[] ls = null;
		synchronized(listeners) {
			ls = listeners.toArray(new ICDIProjectChangeListener[listeners.size()]);
		}
		for (ICDIProjectChangeListener l : ls) {
			l.projectChanged(event);
		}
	}

	boolean cacheIsLoaded = false;
	ICDICache cache;

	public ICDICache getDBCache() {
		if(!cacheIsLoaded) {
			synchronized (this) {
				if(!cacheIsLoaded) {
					try {
						Bundle b = Platform.getBundle("org.jboss.tools.cdi.db");
						if(b != null) {
							try {
								Class c = b.loadClass("org.jboss.tools.cdi.db.CDIDataBase");
								if(c != null) {
									cache = (ICDICache)c.newInstance();
								}
							} catch (ClassNotFoundException e) {
								logError(e);
							} catch (InstantiationException e) {
								logError(e);
							} catch (IllegalAccessException e) {
								logError(e);
							}
						}
					} catch (Throwable t) {
						logError(t);
					} finally {
						cacheIsLoaded = true;
					}
				}				
			}
		}		
		return cache;
	}

}