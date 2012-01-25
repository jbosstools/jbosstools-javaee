package org.jboss.tools.seam.text.ext;

import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;

/**
 * The activator class controls the plug-in life cycle
 */
public class SeamExtPlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.seam.text.ext";

	// The shared instance
	private static SeamExtPlugin plugin;
	
	/**
	 * The constructor
	 */
	public SeamExtPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SeamExtPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
}