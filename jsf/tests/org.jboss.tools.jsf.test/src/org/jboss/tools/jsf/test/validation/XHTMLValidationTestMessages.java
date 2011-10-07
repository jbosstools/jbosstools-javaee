/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import org.eclipse.osgi.util.NLS;

public class XHTMLValidationTestMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.test.validation.messages"; //$NON-NLS-1$

	public static String XHTML_CONTENT_TEMPLATE;
	public static String XHTML_BROKEN_CONTENT_TEMPLATE;
	public static String XHTML_LARGE_CONTENT_TEMPLATE;
	public static String XHTML_GOOD_PUBLIC_ID;
	public static String XHTML_WRONG_PUBLIC_ID;
	public static String XHTML_GOOD_URI;
	public static String XHTML_WRONG_URI;
	public static String XHTML_GOOD_TAGNAME;
	public static String XHTML_WRONG_TAGNAME;
	public static String XHTML_LARGE_GOOD_TAGNAME;
	public static String XHTML_LARGE_WRONG_TAGNAME;
	public static String XHTML_MARKUP_IS_BROKEN_ERROR;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, XHTMLValidationTestMessages.class);
	}

}
