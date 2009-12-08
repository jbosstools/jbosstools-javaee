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
package org.jboss.tools.struts.ui;

import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.osgi.framework.BundleContext;

public class StrutsUIPlugin extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.struts.ui";
	private static StrutsUIPlugin INSTANCE;

	public StrutsUIPlugin() {
	    INSTANCE = this;
	}

	public static Shell getShell() {
		try {
			return StrutsUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		} catch(Exception e){
			StrutsUIPlugin.getPluginLog().logError(e);
			return null;
		}
	}

	public static StrutsUIPlugin getDefault() {
		return INSTANCE;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
}