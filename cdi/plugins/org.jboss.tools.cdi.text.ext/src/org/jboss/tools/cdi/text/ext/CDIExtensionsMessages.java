/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.text.ext;

import org.eclipse.osgi.util.NLS;

public class CDIExtensionsMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.text.ext.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIExtensionsMessages.class);
	}

	public static String CDI_EXT_PLUGIN_NO_MESSAGE;
	public static String CDI_INJECTED_POINT_HYPERLINK_OPEN_BEAN;
}
