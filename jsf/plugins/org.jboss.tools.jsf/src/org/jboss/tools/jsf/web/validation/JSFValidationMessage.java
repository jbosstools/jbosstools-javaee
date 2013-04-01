/*******************************************************************************
 * Copyright (c) 2011-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation;

import org.eclipse.osgi.util.NLS;

public class JSFValidationMessage {

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages"; //$NON-NLS-1$

	public static String UNKNOWN_COMPOSITE_COMPONENT_NAME;
	public static String UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE;

	public static String UNKNOWN_TAGLIB_COMPONENT_NAME;
	public static String UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE;

	public static String SEARCHING_RESOURCES;
	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_PROJECT;

	public static String XHTML_VALIDATION;
	public static String XHTML_VALIDATION_NO_START_TAG;
	public static String XHTML_VALIDATION_NO_END_TAG;
	public static String XHTML_VALIDATION_BAD_ENTITY;

	public static String VIEW_ID_NO_SLASH;
	public static String TO_VIEW_ID_EMPTY;
	public static String TO_VIEW_ID_STAR;
	public static String VIEW_NOT_EXISTS;
	public static String INVALID_FACES_CONFIG_REFERENCE;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFValidationMessage.class);
	}
}