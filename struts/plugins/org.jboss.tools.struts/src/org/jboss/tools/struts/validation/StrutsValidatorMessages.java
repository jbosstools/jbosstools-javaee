/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.validation;

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class StrutsValidatorMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.validation.messages"; //$NON-NLS-1$

	public static String WEB_XML_PLUGIN_NO_MESSAGE;

	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_PROJECT;

	public static String ACTION_TYPE_EMPTY;
	public static String ACTION_TYPE_EXISTS;
	public static String ACTION_TYPE_EXTENDS;
	public static String ACTION_TYPE_UPTODATE;
	public static String ACTION_NAME_EXISTS;
	public static String ACTION_NAME_EMPTY;
	public static String ACTION_INPUT;

	public static String ACTION_FORWARD;
	public static String ACTION_INCLUDE;
	public static String ACTION_FORWARD_PATH_EMPTY;
	public static String ACTION_FORWARD_PATH_EXISTS;
	public static String ACTION_FORWARD_CLASSNAME_EXISTS;
	public static String ACTION_FORWARD_CONTEXT_RELATIVE_CROSS;
	public static String ACTION_FORWARD_CONTEXT_RELATIVE_MONO;
	public static String GLOBAL_FORWARD_PATH_EMPTY;
	public static String GLOBAL_FORWARD_PATH_EXISTS;
	public static String GLOBAL_FORWARD_CLASSNAME_EXISTS;
	public static String GLOBAL_FORWARD_CONTEXT_RELATIVE_CROSS;
	public static String GLOBAL_FORWARD_CONTEXT_RELATIVE_MONO;
	public static String GLOBAL_EXCEPTION_PATH_EXISTS;
	public static String GLOBAL_EXCEPTION_CLASSNAME_EXISTS;
	public static String GLOBAL_EXCEPTION_HANDLER_EXISTS;
	public static String GLOBAL_EXCEPTION_MODULE_RELATIVE;
	public static String CONFIG_FILE_XML;
	public static String CONTROLLER_CLASSNAME_EXISTS;
	public static String CONTROLLER_MULTIPART_CLASS_EXISTS;
	public static String CONTROLLER_PROCESSOR_CLASS_EXISTS;
	public static String RESOURCE_EXISTS;
	public static String CONFIG_VALID;

	static {
		NLS.initializeMessages(BUNDLE_NAME, StrutsValidatorMessages.class);
	}
}
