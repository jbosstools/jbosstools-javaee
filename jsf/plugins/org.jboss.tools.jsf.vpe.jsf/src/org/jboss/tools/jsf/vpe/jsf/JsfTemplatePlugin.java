package org.jboss.tools.jsf.vpe.jsf;

import org.jboss.tools.common.log.BaseUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class JsfTemplatePlugin extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.jsf.vpe.jsf"; //$NON-NLS-1$

	// The shared instance
	private static JsfTemplatePlugin plugin;

	/**
	 * The constructor
	 */
	public JsfTemplatePlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JsfTemplatePlugin getDefault() {
		return plugin;
	}
}