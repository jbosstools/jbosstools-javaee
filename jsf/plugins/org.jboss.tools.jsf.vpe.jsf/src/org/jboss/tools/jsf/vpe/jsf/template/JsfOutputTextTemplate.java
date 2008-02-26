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

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.vpe.editor.bundle.BundleMap;
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
 * template for <h:outputText .../> jsf tag
 * 
 */
public class JsfOutputTextTemplate extends AbstractJsfTemplate {

	/**
	 * name of "value" attribute
	 */
	private static final String VALUE_ATTR_NAME = "value";

	/**
	 * name of "binding" attribute
	 */
	private static final String BINDING_ATTR_NAME = "binding";

	/**
	 * name of "escape" attribute
	 */
	private static final String ESCAPE_ATTR_NAME = "escape";

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

		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

		VpeCreationData creationData = new VpeCreationData(span);

		copyGeneralJsfAttributes(span, element);

		Attr attr = getOutputAttributeNode(element);

		if (attr != null) {

			if (!element.hasAttribute(ESCAPE_ATTR_NAME) || "true".equalsIgnoreCase(element.getAttribute(ESCAPE_ATTR_NAME))) {

				String value = attr.getNodeValue();

				String bundleValue = getBundleValue(pageContext, attr);

				nsIDOMText text;
				if (!value.equals(bundleValue)) {

					text = visualDocument.createTextNode(bundleValue);

				} else {

					text = visualDocument.createTextNode(value);
					attributesMapping.add(new VpeNodeMapping(attr, text));
				}
				span.appendChild(text);
			} else {

				VpeChildrenInfo spanInfo = new VpeChildrenInfo(span);

				NodeList list = NodeProxyUtil.reparseAttributeValue(attr);

				for (int i = 0; i < list.getLength(); i++) {

					Node child = list.item(i);

					spanInfo.addSourceChild(child);
				}

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

	private Attr getOutputAttributeNode(Element element) {

		if (element.hasAttribute(VALUE_ATTR_NAME))
			return element.getAttributeNode(VALUE_ATTR_NAME);
		else if (element.hasAttribute(BINDING_ATTR_NAME))
			return element.getAttributeNode(BINDING_ATTR_NAME);

		return null;

	}

	private String getBundleValue(VpePageContext pageContext, Attr attr) {

		BundleMap bundle = pageContext.getBundle();

		return bundle.getBundleValue(attr.getNodeValue(), ((IDOMAttr) attr)
				.getValueRegionStartOffset());

	}

}
