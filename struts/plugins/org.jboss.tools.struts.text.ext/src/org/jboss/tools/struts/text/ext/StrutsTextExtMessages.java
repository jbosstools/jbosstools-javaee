/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.text.ext;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jeremy
 *
 */

public class StrutsTextExtMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.struts.text.ext.StrutsTextExtMessages"; //$NON-NLS-1$

	private StrutsTextExtMessages() { }
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, StrutsTextExtMessages.class);
	}

	public static String OpenTagLibraryForATag;
	public static String OpenTagLibraryForTagName;
	public static String OpenTagLibraryForAnAttribute;
	public static String OpenTagLibraryForAttributeName;
	public static String OpenAction;
	public static String Action;
	public static String OpenActionMapping;
	public static String ActionMapping;
	public static String OpenFormBean;
	public static String FormBean;
	public static String OpenFormProperty;
	public static String FormProperty;
	public static String OpenForwardPath;
	public static String ForwardPath;
	public static String OpenPage;
	public static String Page;
	public static String OpenValidationBundle;
	public static String ValidationBundle;
	public static String OpenValidationBundleProperty;
	public static String OpenValidationBundlePropertyForBundle;
	public static String ValidationBundleProperty;
	public static String OpenForward;
	public static String Forward;
	public static String OpenValidationProperty;
	public static String ValidationProperty;
	public static String OpenProperty;
	public static String Property;
	public static String OpenPropertyForFormAction;
	public static String OpenValidator;
	public static String Validator;
}
