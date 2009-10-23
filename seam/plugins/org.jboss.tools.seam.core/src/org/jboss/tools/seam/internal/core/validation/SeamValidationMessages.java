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
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.jsf.web.validation.JSFValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class SeamValidationMessages {

	private static final String BUNDLE_NAME = "org.jboss.tools.seam.internal.core.validation.messages"; //$NON-NLS-1$

	public static String NONUNIQUE_COMPONENT_NAME_MESSAGE;
	public static String STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE;
	public static String STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY;
	public static String STATEFUL_COMPONENT_WRONG_SCOPE;
	public static String UNKNOWN_COMPONENT_CLASS_NAME;
	public static String UNKNOWN_COMPONENT_PROPERTY;

	public static String ENTITY_COMPONENT_WRONG_SCOPE;
	public static String DUPLICATE_REMOVE;

	public static String DUPLICATE_DESTROY;
	public static String DUPLICATE_CREATE;
	public static String DUPLICATE_UNWRAP;
	public static String DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN;
	public static String CREATE_DOESNT_BELONG_TO_COMPONENT;
	public static String UNWRAP_DOESNT_BELONG_TO_COMPONENT;
	public static String OBSERVER_DOESNT_BELONG_TO_COMPONENT;

	public static String UNKNOWN_FACTORY_NAME;
	public static String DUPLICATE_VARIABLE_NAME;

	public static String MULTIPLE_DATA_BINDER;
	public static String UNKNOWN_DATA_MODEL;

	public static String UNKNOWN_VARIABLE_NAME;

	public static String INVALID_PARENT_PROJECT;
	public static String INVALID_SEAM_RUNTIME; 
	public static String INVALID_WEBFOLDER;
	public static String INVALID_EJB_PROJECT;
	public static String INVALID_TEST_PROJECT;
	public static String INVALID_MODEL_SRC;
	public static String INVALID_ACTION_SRC;
	public static String INVALID_TEST_SRC;
	public static String INVALID_MODEL_PACKAGE_NAME;
	public static String INVALID_ACTION_PACKAGE_NAME;
	public static String INVALID_TEST_PACKAGE_NAME;
	public static String INVALID_CONNECTION_NAME;

	public static String INVALID_XML_VERSION;

	public static String INVALID_SEAM_JAR_MODULE_IN_APPLICATION_XML;
	public static String INVALID_JAR_MODULE_IN_APPLICATION_XML;

	public static String SEARCHING_RESOURCES;
	public static String VALIDATING_COMPONENT; 
	public static String VALIDATING_FACTORY;
	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_CLASS;
	public static String VALIDATING_PROJECT;

	static {
		NLS.initializeMessages(BUNDLE_NAME, SeamValidationMessages.class);
	}
}