/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.ui;

import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;

public class JSTJobUiPlugin extends BaseUIPlugin {
	public static String PLUGIN_ID = "org.jboss.tools.batch.ui"; //$NON-NLS-1$
	static JSTJobUiPlugin plugin = null;

	public JSTJobUiPlugin() {
		plugin = this;
	}
	
	public static JSTJobUiPlugin getDefault() {
		return plugin;
	}

}
