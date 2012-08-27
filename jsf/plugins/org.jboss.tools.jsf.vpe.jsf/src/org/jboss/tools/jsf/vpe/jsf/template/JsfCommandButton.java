/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfCommandButton extends VpeAbstractTemplate {

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE)) {
			input.setAttribute(JSF.ATTR_STYLE, sourceElement.getAttribute(JSF.ATTR_STYLE));
		}
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE_CLASS)) {
			input.setAttribute(HTML.ATTR_CLASS, sourceElement.getAttribute(JSF.ATTR_STYLE_CLASS));
		}
		if (sourceElement.hasAttribute(JSF.ATTR_DISABLED)) {
			String disabled = sourceElement.getAttribute(JSF.ATTR_DISABLED);
			if ("true".equalsIgnoreCase(disabled)) { //$NON-NLS-1$
				input.setAttribute(JSF.ATTR_DISABLED, JSF.ATTR_DISABLED);
			}
		}
		if (sourceElement.hasAttribute(JSF.ATTR_DIR)) {
			input.setAttribute(JSF.ATTR_DIR, sourceElement.getAttribute(JSF.ATTR_DIR));
		} 
		if (sourceElement.hasAttribute(JSF.ATTR_IMAGE)) {
//		1) attr: +image -> type=image
			input.setAttribute(JSF.ATTR_TYPE,JSF.ATTR_IMAGE);
			input.setAttribute(JSF.ATTR_SRC, 
					VpeStyleUtil.addFullPathToImgSrc(
							sourceElement.getAttribute(JSF.ATTR_IMAGE), pageContext, true));
		} else if (sourceElement.hasAttribute(JSF.ATTR_TYPE)) { 
//		2) attr: +type -> type=type
			input.setAttribute(JSF.ATTR_TYPE, sourceElement.getAttribute(JSF.ATTR_TYPE));
			if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {
				input.setAttribute(JSF.ATTR_VALUE, sourceElement.getAttribute(JSF.ATTR_VALUE));
			}
		} else {
//		3) attr: -type -> type=button
			input.setAttribute(JSF.ATTR_TYPE, JSF.VALUE_BUTTON);
			if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {
				input.setAttribute(JSF.ATTR_VALUE, sourceElement.getAttribute(JSF.ATTR_VALUE));
			}
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
