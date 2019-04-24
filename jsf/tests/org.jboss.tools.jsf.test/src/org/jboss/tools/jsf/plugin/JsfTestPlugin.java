package org.jboss.tools.jsf.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;

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
