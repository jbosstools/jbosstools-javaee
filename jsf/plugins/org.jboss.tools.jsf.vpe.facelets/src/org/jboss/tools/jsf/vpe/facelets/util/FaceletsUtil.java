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
package org.jboss.tools.jsf.vpe.facelets.util;

import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;

/**
 * Contains various utility methods used by facelets templates.  
 * 
 * @author yradtsevich
 */
public class FaceletsUtil {
	private static final String ANY_TAG_CAPTION_CLASS
			= "__any__tag__caption";			//$NON-NLS-1$
	private static final String MESSAGE_STYLE
			= "color:red;font-style:italic;";	//$NON-NLS-1$

	/**
	 * Creates and returns a DIV element composed of name of JSP tag
	 * and the errorMessage.
	 * <P>
	 * Should be used to show messages in the VPE like:
	 * <pre>ui:composition Template is not found.</pre>
	 */
	public static nsIDOMElement createErrorMessageElement(
			nsIDOMDocument visualDocument,
			String tagName, String errorMessage) {
		nsIDOMElement element = visualDocument.createElement(HTML.TAG_DIV);

		nsIDOMElement nameTag = visualDocument.createElement(HTML.TAG_SPAN);
		nameTag.setAttribute(HTML.ATTR_CLASS, ANY_TAG_CAPTION_CLASS);
		nameTag.appendChild(visualDocument.createTextNode(tagName));
		element.appendChild(nameTag);

		nsIDOMElement messageTag = visualDocument.createElement(HTML.TAG_SPAN);
		messageTag.setAttribute(HTML.ATTR_CLASS, ANY_TAG_CAPTION_CLASS);
		messageTag.setAttribute(HTML.ATTR_STYLE, MESSAGE_STYLE);
		messageTag.appendChild(visualDocument.createTextNode(
				" " + errorMessage));//$NON-NLS-1$
		element.appendChild(messageTag);

		return element;
	}
}
