/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.osgi.framework.BundleContext;

public class SeamUiPagesPlugin extends BaseUIPlugin {
	public static String PLUGIN_ID = "org.jboss.tools.seam.xml.ui"; //$NON-NLS-1$
	static SeamUiPagesPlugin INSTANCE = null;

	public SeamUiPagesPlugin() {
		INSTANCE = this;
	}
	
	public void start(BundleContext context) throws Exception {
	    super.start(context);
	}
	
	public static SeamUiPagesPlugin getDefault() {
		return INSTANCE;
	}

	public static boolean isDebugEnabled() {
		return INSTANCE.isDebugging();
	}
	
	public static void log(String msg) {
		if(isDebugEnabled()) INSTANCE.getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));		
	}
	
	public static void log(IStatus status) {
		if(isDebugEnabled() || !status.isOK()) INSTANCE.getLog().log(status);
	}
	
	public static void log(String message, Throwable exception) {
		INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, message, exception));		
	}
	
	public static void log(Throwable ex) {
		INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, SeamUIPagesMessages.SEAM_UI_PAGES_PLUGIN_NO_MESSAGES, ex)); //$NON-NLS-1$
	}

	public static Shell getShell() {
		return INSTANCE.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}