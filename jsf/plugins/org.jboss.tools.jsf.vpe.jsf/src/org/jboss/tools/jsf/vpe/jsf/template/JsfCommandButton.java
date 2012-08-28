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
import org.jboss.tools.vpe.editor.util.Constants;
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
			/*
			 * CommandButton is Disabled
			 */
			String disabled = sourceElement.getAttribute(JSF.ATTR_DISABLED);
			if (Constants.TRUE.equalsIgnoreCase(disabled)) {
				input.setAttribute(JSF.ATTR_DISABLED, JSF.ATTR_DISABLED);
			}
		}
		if (sourceElement.hasAttribute(JSF.ATTR_DIR)) {
			input.setAttribute(JSF.ATTR_DIR, sourceElement.getAttribute(JSF.ATTR_DIR));
		} 
		if (sourceElement.hasAttribute(JSF.ATTR_IMAGE)) {
			/*
			 * TYPE is IMAGE
			 */
			input.setAttribute(JSF.ATTR_TYPE,JSF.ATTR_IMAGE);
			input.setAttribute(JSF.ATTR_SRC, 
					VpeStyleUtil.addFullPathToImgSrc(
							sourceElement.getAttribute(JSF.ATTR_IMAGE), pageContext, true));
		} else if (sourceElement.hasAttribute(JSF.ATTR_TYPE)) {
			/*
			 * TYPE attribute presents
			 */
			String type = sourceElement.getAttribute(JSF.ATTR_TYPE);
			if (Constants.EMPTY.equalsIgnoreCase(type)) {
				type = HTML.VALUE_TYPE_SUBMIT;
			}
			input.setAttribute(JSF.ATTR_TYPE, type);
			if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {
				input.setAttribute(JSF.ATTR_VALUE, sourceElement.getAttribute(JSF.ATTR_VALUE));
			}
		} else {
			/*
			 * No TYPE attribute is specified
			 */
			input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_SUBMIT);
			if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {
				input.setAttribute(JSF.ATTR_VALUE, sourceElement.getAttribute(JSF.ATTR_VALUE));
			}
		}

		/*
		 * https://issues.jboss.org/browse/JBIDE-3225
		 * Components should render usual text inside
		 */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, input, HTML.TAG_SPAN, visualDocument);
		
		return creationData;
	}

}
