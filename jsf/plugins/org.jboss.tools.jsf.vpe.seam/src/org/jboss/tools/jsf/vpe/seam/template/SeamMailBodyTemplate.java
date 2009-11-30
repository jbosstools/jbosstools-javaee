/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamMailBodyTemplate extends VpeAbstractTemplate {

	private static final String ATTR_TYPE = "type";
	private static final String ATTR_VALUE_PLAIN = "plain";
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMNode visualNode = null;
		if (sourceElement.hasChildNodes()) {
			visualNode = visualDocument.createElement(HTML.TAG_DIV);
		}
		return new VpeCreationData(visualNode);
	}

	private nsIDOMNode clearAllStyleAttrs(nsIDOMNode visualNode) {
		if (visualNode.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
			nsIDOMElement visualElement = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			visualElement.removeAttribute(HTML.ATTR_CLASS);
			visualElement.removeAttribute(HTML.ATTR_STYLE);
		}
		if (visualNode != null) {
			nsIDOMNodeList children = visualNode.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				clearAllStyleAttrs(children.item(i));
			}
		}
		return visualNode;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		String attrValue =((Element)sourceNode).getAttribute(ATTR_TYPE);
		if (attrValue!=null && ATTR_VALUE_PLAIN.equals(attrValue.trim())) {
			nsIDOMNode parentNode = data.getNode();
			if (parentNode!=null) {
				nsIDOMNodeList nodeList = parentNode.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					clearAllStyleAttrs(nodeList.item(i));
				}
			}
		}
	}
	
}
