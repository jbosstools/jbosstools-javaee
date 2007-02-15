package org.jboss.ide.seam.gen;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SeamGenPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.ide.seam.gen";

	// The shared instance
	private static SeamGenPlugin plugin;
	
	/**
	 * The constructor
	 */
	public SeamGenPlugin() {
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
	public static SeamGenPlugin getDefault() {
		return plugin;
	}

	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static void logError(String message) {
		logError(message, null);
	}

	public static void logInfo(String message) {
		log(new Status(IStatus.INFO, PLUGIN_ID, 12345, message, null));
	}

	public static void logError(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 12345, message, e));
		
	}
}
