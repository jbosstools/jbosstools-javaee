/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.pages.xml;

import org.jboss.tools.common.log.BaseUIPlugin;

/**
 */
public class SeamPagesXMLPlugin extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.seam.xml"; //$NON-NLS-1$

	public SeamPagesXMLPlugin() {
		plugin = this;
	}
	
	public static SeamPagesXMLPlugin getDefault() {
		return plugin;
	}
	
	private static SeamPagesXMLPlugin plugin = null; 
}
