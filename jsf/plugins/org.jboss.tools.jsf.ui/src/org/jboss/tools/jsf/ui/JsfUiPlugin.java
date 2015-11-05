/*******************************************************************************
 * Copyright (c) 2007, 2015 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui;

import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.jsf.ui.editor.check.ProjectNaturesChecker;
import org.osgi.framework.BundleContext;

public class JsfUiPlugin extends BaseUIPlugin {
	
	public static String PLUGIN_ID = "org.jboss.tools.jsf.ui"; //$NON-NLS-1$
	
	private static JsfUiPlugin plugin;
	
	public JsfUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
	    super.start(context);
		ProjectNaturesChecker.getInstance();
	}

	public static JsfUiPlugin getDefault() {
		return plugin;
	}

	public static boolean isDebugEnabled() {
		return plugin != null && plugin.isDebugging();
	}

	public static Shell getShell() {
		return getDefault() == null ? null : getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

}