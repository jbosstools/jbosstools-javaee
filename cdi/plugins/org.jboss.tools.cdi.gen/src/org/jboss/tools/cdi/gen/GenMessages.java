/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.gen;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.gen.messages";//$NON-NLS-1$
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, GenMessages.class);		
	}
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public static String ERROR;
}
