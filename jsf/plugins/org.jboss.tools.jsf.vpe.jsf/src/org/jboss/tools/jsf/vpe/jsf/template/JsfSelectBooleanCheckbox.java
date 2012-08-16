/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfSelectBooleanCheckbox extends VpeAbstractTemplate {

	private static final String TRUE = "true"; //$NON-NLS-1$
	
	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_CHECKBOX);
		
		if (sourceElement.hasAttribute(HTML.ATTR_DISABLED)) {
			String disabled = sourceElement.getAttribute(HTML.ATTR_DISABLED);
			if (TRUE.equalsIgnoreCase(disabled)) {
				input.setAttribute(HTML.ATTR_DISABLED, disabled);
			}
		}
		if (sourceElement.hasAttribute(HTML.ATTR_BORDER)) {
			input.setAttribute(HTML.ATTR_BORDER, sourceElement.getAttribute(HTML.ATTR_BORDER));
		}
		if (sourceElement.hasAttribute(HTML.ATTR_VALUE)) {
			input.setAttribute(HTML.ATTR_CHECKED, sourceElement.getAttribute(HTML.ATTR_VALUE));
		}
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE)) {
			input.setAttribute(JSF.ATTR_STYLE, sourceElement.getAttribute(JSF.ATTR_STYLE));
		}
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE_CLASS)) {
			input.setAttribute(JSF.ATTR_STYLE_CLASS, sourceElement.getAttribute(JSF.ATTR_STYLE_CLASS));
		}
		/*
		 * https://issues.jboss.org/browse/JBIDE-3225
		 * Components should render usual text inside
		 */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, input, HTML.TAG_DIV, visualDocument);
		
		return creationData;
	}

}
