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
package org.jboss.tools.cdi.xml;

import org.eclipse.osgi.util.NLS;

public final class CDIXMLMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.xml.messages";//$NON-NLS-1$

	private CDIXMLMessages() {
		// Do not instantiate
	}

	public static String SEAM_XML_PLUGIN_NO_MESSAGE;
	public static String CANNOT_FIND_MATCHING_RULE_FOR_PATH;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIXMLMessages.class);
	}
}