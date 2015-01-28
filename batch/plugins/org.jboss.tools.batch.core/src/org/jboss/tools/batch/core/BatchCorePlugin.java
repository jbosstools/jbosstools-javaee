/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.core;

import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;

public class BatchCorePlugin extends BaseUIPlugin {
	public static String PLUGIN_ID = "org.jboss.tools.batch.core"; //$NON-NLS-1$
	static BatchCorePlugin plugin = null;

	public BatchCorePlugin() {
		plugin = this;
	}
	
	public static BatchCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Get the IPluginLog for this plugin. This method 
	 * helps to make logging easier, for example:
	 * 
	 *     FoundationCorePlugin.pluginLog().logError(etc)
	 *  
	 * @return IPluginLog object
	 */
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

}
