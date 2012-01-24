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
package org.jboss.tools.cdi.xml.ui;

import org.jboss.tools.common.log.BaseUIPlugin;

public class CDIXMLUiPlugin extends BaseUIPlugin {
	public static String PLUGIN_ID = "org.jboss.tools.cdi.xml.ui"; //$NON-NLS-1$
	static CDIXMLUiPlugin plugin = null;

	public CDIXMLUiPlugin() {
		plugin = this;
	}
	
	public static CDIXMLUiPlugin getDefault() {
		return plugin;
	}

}
