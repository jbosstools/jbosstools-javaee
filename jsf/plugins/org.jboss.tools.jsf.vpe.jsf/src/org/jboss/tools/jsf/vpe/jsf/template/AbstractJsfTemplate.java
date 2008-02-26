/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.vpe.editor.template.EditableTemplateAdapter;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;

/**
 * general class for jsf templates
 * 
 * @author Sergey Dzmitrovich
 * 
 */
public abstract class AbstractJsfTemplate extends EditableTemplateAdapter {

	// general jsf attributes
	static private Map<String, String> attributes = new HashMap<String, String>();

	static {
		attributes.put("style", HTML.ATTR_STYLE);
		attributes.put("styleClass", HTML.ATTR_CLASS);
	}

	/**
	 * copy general
	 * 
	 * @param visualElement
	 * @param sourceElement
	 */
	protected void copyGeneralJsfAttributes(nsIDOMElement visualElement,
			Element sourceElement) {

		Set<String> jsfAttributes = attributes.keySet();

		for (String key : jsfAttributes) {
			if (sourceElement.hasAttribute(key))
				visualElement.setAttribute(attributes.get(key), sourceElement
						.getAttribute(key));
		}

	}

}
