/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.xml.ui;

import org.eclipse.osgi.util.NLS;

public class CDIXMLUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.xml.ui.messages"; //$NON-NLS-1$

	public static String CDI_XML_UI_PLUGIN_NO_MESSAGES;
	public static String STEREOTYPES;
	public static String DECORATORS;
	public static String INTERCEPTORS;
	public static String ALTERNATIVES;
	public static String CLASSES;
	public static String INCLUDE_AND_EXCLUDE;
	public static String CDI_BEANS_1_0_FILE;

	private CDIXMLUIMessages() {
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIXMLUIMessages.class);
	}
}
