/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.JsfTemplatePlugin;
import org.jboss.tools.vpe.editor.bundle.BundleMap;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Utilities for jsf templates
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public class ComponentUtil {

	/**
	 * Returns value of attribute.
	 * 
	 * @param sourceElement
	 * @param attributeName
	 * @return
	 */
	public static String getAttribute(Element sourceElement,
			String attributeName) {
		String attribute = sourceElement.getAttribute(attributeName);
		if (attribute == null) {
			attribute = "";
		}
		return attribute;
	}

	/**
	 * Returns value of attribute.
	 * 
	 * @param sourceElement
	 * @param attributeName
	 * @return
	 */
	public static String getAttribute(nsIDOMElement sourceElement,
			String attributeName) {
		String attribute = sourceElement.getAttribute(attributeName);
		if (attribute == null) {
			attribute = "";
		}
		return attribute;
	}

	/**
	 * Parses string value retrieved from sourceElement.getAttribure(..) method
	 * to its boolean value.
	 * <p>
	 * <code>false</code> is returned only if it specified explicitly,
	 * otherwise <code>true</code> is returned.
	 * 
	 * @param str
	 *            the string to parse
	 * @return boolean value from string
	 */
	public static boolean string2boolean(String str) {
		if ((str == null) || ("".equals(str))) {
			return false;
		} else if (("true".equalsIgnoreCase(str))
				|| ("false".equalsIgnoreCase(str))) {
			return new Boolean(str).booleanValue();
		}
		return false;
	}

	/**
	 * get bundle
	 * 
	 * @param pageContext
	 * @param attr
	 * @return
	 */
	public static String getBundleValue(VpePageContext pageContext, Attr attr) {

		return getBundleValue(pageContext, attr.getNodeValue(),
				((IDOMAttr) attr).getValueRegionStartOffset());

	}

	/**
	 * 
	 * @param pageContext
	 * @param value
	 * @param offfset
	 * @return
	 */
	public static String getBundleValue(VpePageContext pageContext,
			String value, int offfset) {

		BundleMap bundle = pageContext.getBundle();

		return bundle.getBundleValue(value);

	}
}
