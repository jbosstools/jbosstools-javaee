/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.facelets.template.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author yradtsevich
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME
			= "org.jboss.tools.jsf.vpe.facelets.template.messages.messages";//$NON-NLS-1$
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);		
	}
	private Messages(){}

	public static String TEMPLATE_NOT_FOUND;
	public static String UNKNOWN_NAME;
	public static String NAME_NOT_SPECIFIED;
}
