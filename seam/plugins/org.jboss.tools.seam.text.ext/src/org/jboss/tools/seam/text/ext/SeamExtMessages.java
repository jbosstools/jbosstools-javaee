/*******************************************************************************
 * Copyright (c) 2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.text.ext;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class SeamExtMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.seam.text.ext.Messages"; //$NON-NLS-1$

	public static String OpenSeamDeclarationAs; 
	public static String InResource;
	public static String SeamComponent;
	public static String SeamRole;
	public static String SeamFactory;
	public static String SeamBijected;

	//
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, SeamExtMessages.class);
	}

	private  SeamExtMessages() {
	}
}
