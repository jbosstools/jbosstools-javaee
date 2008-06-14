package org.jboss.tools.jsf.plugin;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class JsfTestPlugin extends AbstractUIPlugin {
	
	private static JsfTestPlugin INSTANCE;
	
	public JsfTestPlugin() {
		super();
		INSTANCE = this;
	}

	public static JsfTestPlugin getDefault() {
		return INSTANCE;
	}
	
}
