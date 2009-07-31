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
package org.jboss.tools.jsf.text.ext;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jeremy
 *
 */

public class JSFTextExtMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.text.ext.JSFTextExtMessages"; //$NON-NLS-1$
	
	private JSFTextExtMessages() { }
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, JSFTextExtMessages.class);
	}
	
	public static String Converter;
	public static String JSFExtensionsPlugin_NoMessage;
	public static String OpenConverterForId;
	public static String OpenTagLibraryForATag;
	public static String OpenTagLibraryForTagName;
	public static String OpenTagLibraryForAnAttribute;
	public static String OpenTagLibraryForAttributeName;
	public static String NavigationRule;
	public static String OpenBeanProperty;
	public static String BeanProperty;
	public static String RenderKit;
	public static String OpenRenderKit;
	public static String Validator;
	public static String OpenValidatorForId;
}
