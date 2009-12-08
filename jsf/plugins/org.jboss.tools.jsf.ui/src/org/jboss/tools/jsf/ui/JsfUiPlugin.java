/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.osgi.framework.BundleContext;

public class JsfUiPlugin extends BaseUIPlugin {

	public static String PLUGIN_ID = "org.jboss.tools.jsf.ui"; //$NON-NLS-1$

	public JsfUiPlugin() {
	}

	public void start(BundleContext context) throws Exception {
	    super.start(context);
	}

	public static JsfUiPlugin getDefault() {
		return PluginHolder.INSTANCE;
	}

	public static boolean isDebugEnabled() {
		return PluginHolder.INSTANCE.isDebugging();
	}

	static class PluginHolder {
		static JsfUiPlugin INSTANCE = (JsfUiPlugin)Platform.getPlugin(PLUGIN_ID); 
	}

	public static Shell getShell() {
		return PluginHolder.INSTANCE.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
}