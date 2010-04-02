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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.jsf.ui.editor.check.ProjectNaturesChecker;
import org.jboss.tools.jsf.ui.editor.check.ProjectNaturesPartListener;
import org.osgi.framework.BundleContext;

public class JsfUiPlugin extends BaseUIPlugin {

	private ProjectNaturesPartListener partListener = new ProjectNaturesPartListener();
	
	public static String PLUGIN_ID = "org.jboss.tools.jsf.ui"; //$NON-NLS-1$

	public JsfUiPlugin() {
	}

	public void start(BundleContext context) throws Exception {
	    super.start(context);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window.getPartService().addPartListener(partListener);
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
	
	@Override
	public void stop(BundleContext context) throws Exception {
		if (partListener != null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			window.getPartService().removePartListener(partListener);
			partListener = null;
		}
		ProjectNaturesChecker naturesChecker = ProjectNaturesChecker.getInstance();
		naturesChecker.dispose();
		naturesChecker = null;
		super.stop(context);
	}
	
}