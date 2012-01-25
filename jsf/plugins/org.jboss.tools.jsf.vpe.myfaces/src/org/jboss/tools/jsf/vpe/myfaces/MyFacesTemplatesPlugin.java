package org.jboss.tools.jsf.vpe.myfaces;

import org.jboss.tools.common.log.BaseUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class MyFacesTemplatesPlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.jsf.vpe.myfaces"; //$NON-NLS-1$

	// The shared instance
	private static MyFacesTemplatesPlugin plugin;
	
	/**
	 * The constructor
	 */
	public MyFacesTemplatesPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static MyFacesTemplatesPlugin getDefault() {
		return plugin;
	}
}