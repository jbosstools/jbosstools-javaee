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
package org.jboss.tools.struts.debug.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class StrutsDebugPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.jboss.tools.struts.debug";

	public StrutsDebugPlugin() {
		super();
		StrutsDebugModel.init();
	}
	
	public void startup( ) throws CoreException  {
		super.startup();
	}

    public static void printStackTrace(Throwable throwable) {
        if (isDebugEnabled() && throwable != null) throwable.printStackTrace();
    }

    public static void println(String str) {
        if (isDebugEnabled()) {
            if (str != null) System.out.println(str);
            else System.out.println();
        }
    }

	public static Shell getShell() {
		return StrutsDebugPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	
	public static StrutsDebugPlugin getDefault() {
		return PluginHolder.INSTANCE;
	}

	public static boolean isDebugEnabled() {
		return PluginHolder.INSTANCE.isDebugging();
	}
	
	static class PluginHolder {
		static StrutsDebugPlugin INSTANCE = (StrutsDebugPlugin)Platform.getPlugin(PLUGIN_ID); 
	}

	public static void log(String msg) {
		if(isDebugEnabled()) PluginHolder.INSTANCE.getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));		
	}
	
	public static void log(IStatus status) {
		if(isDebugEnabled() || !status.isOK()) PluginHolder.INSTANCE.getLog().log(status);
	}
	
	public static void log(String message, Throwable exception) {
		PluginHolder.INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, message, exception));		
	}
	
	public static void log(Exception ex) {
		PluginHolder.INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, "No message", ex));
	}	
}