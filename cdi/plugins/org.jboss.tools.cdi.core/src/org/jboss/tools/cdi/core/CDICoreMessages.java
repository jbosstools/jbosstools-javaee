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
	
	public static String CDI_RENAME_PROCESSOR_ERROR_OUT_OF_SYNC_PROJECT;
	public static String CDI_RENAME_PROCESSOR_ERROR_PHANTOM_FILE;
	public static String CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE;
	public static String CDI_RENAME_PROCESSOR_ERROR_BEAN_NOT_FOUND;
	public static String CDI_RENAME_PROCESSOR_ERROR_INJECTION_POINT_NOT_FOUND;
	public static String CDI_RENAME_PROCESSOR_ERROR_FILE_NOT_FOUND;
	public static String CDI_RENAME_PROCESSOR_QUESTION_DIALOG_TITLE;
	public static String CDI_RENAME_PROCESSOR_QUESTION_DIALOG_MESSAGE;

	public static String RENAME_NAMED_BEAN_PROCESSOR_TITLE;
	public static String RENAME_NAMED_BEAN_PROCESSOR_ERROR;
	public static String CDI_RENAME_PROCESSOR_BEAN_HAS_NO_FILE;
	public static String CDI_RENAME_PROCESSOR_BEAN_HAS_NO_NAME_LOCATION;
	public static String CDI_UTIL_BUILD_CDI_MODEL;
	
	public static String CDI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL;
	public static String CDI_IMAGESIMAGE_NAME_CANNOT_BE_NULL;

}