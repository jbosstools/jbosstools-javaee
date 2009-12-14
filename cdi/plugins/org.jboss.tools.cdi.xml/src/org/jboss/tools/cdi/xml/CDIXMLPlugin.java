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
package org.jboss.tools.cdi.xml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 */
public class CDIXMLPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.seam.xml"; //$NON-NLS-1$

	public CDIXMLPlugin() {
		super();
		INSTANCE = this;
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
	
	public static void log(Exception ex) {
		INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, CDIXMLMessages.SEAM_XML_PLUGIN_NO_MESSAGE, ex)); 
	}

	public static boolean isDebugEnabled() {
		return INSTANCE.isDebugging();
	}

	public static CDIXMLPlugin getDefault() {
		return INSTANCE;
	}
	
	static CDIXMLPlugin INSTANCE = null; 
}
