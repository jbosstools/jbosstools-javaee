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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 * template for <h:outputLabel .../> jsf tag
 * 
 */
public class JsfOutputLabelTemplate extends AbstractOutputJsfTemplate {

	/**
	 * name of "for" attribute
	 */
	protected static final String FOR_ATTR_NAME = "for";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element element = (Element) sourceNode;

		List<VpeNodeMapping> attributesMapping = new ArrayList<VpeNodeMapping>();

		// create label element
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);

		// copy attributes
		copyOutputJsfAttributes(label, element);
		copyAttribute(label, element, FOR_ATTR_NAME, HTML.ATTR_FOR);

		// creation data
		VpeCreationData creationData = new VpeCreationData(label);

		// get attribute to represent
		Attr attr = getOutputAttributeNode(element);

		if (attr != null) {

			// if escape then contents of value (or other attribute) is only
			// text
			if (!element.hasAttribute(ESCAPE_ATTR_NAME)
					|| ("true".equalsIgnoreCase(element
							.getAttribute(ESCAPE_ATTR_NAME)))) {

				String value = attr.getNodeValue();

				// get bundle value
				String bundleValue = getBundleValue(pageContext, attr);

				nsIDOMText text;
				// if bundleValue differ from value then will be represent
				// bundleValue, but text will be not edit
				if (!value.equals(bundleValue)) {

					text = visualDocument.createTextNode(bundleValue);

				} else {

					text = visualDocument.createTextNode(value);
					// add attribute for ability of editing
					attributesMapping.add(new VpeNodeMapping(attr, text));
				}
				label.appendChild(text);
			}
			// then text can be html code
			else {

				// create info
				VpeChildrenInfo spanInfo = new VpeChildrenInfo(label);

				// reparse attribute's value
				NodeList list = NodeProxyUtil.reparseAttributeValue(attr);

				// add children to info
				for (int i = 0; i < list.getLength(); i++) {

					Node child = list.item(i);

					spanInfo.addSourceChild(child);
				}

				// add info to creation data
				creationData.addChildrenInfo(spanInfo);

			}

		}

		creationData.setData(attributesMapping);

		return creationData;

	}

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {

		return true;
	}

}
