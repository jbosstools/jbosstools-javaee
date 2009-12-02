/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kazakov
 */
public class CDICoreMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.core.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDICoreMessages.class);
	}

	public static String CDI_FACET_INSTALL_ABSTRACT_DELEGATE_ERRORS_OCCURED;
	public static String CDI_FACET_INSTALL_ABSTRACT_DELEGATE_CHECK_ERROR_LOG_VIEW;
	public static String CDI_FACET_INSTALL_ABSTRACT_DELEGATE_ERROR;
}