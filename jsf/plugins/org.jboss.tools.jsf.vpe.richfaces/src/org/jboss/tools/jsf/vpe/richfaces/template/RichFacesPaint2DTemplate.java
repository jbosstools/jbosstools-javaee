/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class for creating Paint2D content
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesPaint2DTemplate extends VpeAbstractTemplate {

	private String IMAGE_NAME = "/paint2D/paint2D.gif";

	private String PAINT2D_CSS_FILE = "/paint2D/paint2D.css";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#removeAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String)
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
		Element img = (Element) visualNode;
		if (name.equals("styleClass")
				&& sourceElement
						.getAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR) == null
				&& sourceElement
						.getAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR) == null) {
			img
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							"imgStyleClass");
		} else
			img.removeAttribute(name);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		Element img = (Element) visualNode;
		if (name.equals("styleClass")) {
			img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, value);
		} else
			img.setAttribute(name, value);
	}

	/**
	 * Create html instead rich:faces component.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		Element img = visualDocument.createElement("img");
		ComponentUtil.setImg(img, IMAGE_NAME);
		ComponentUtil.setCSSLink(pageContext, PAINT2D_CSS_FILE, "paint2d");
		String attrValue = ((Element) sourceNode).getAttribute("styleClass");
		if (attrValue != null && attrValue.length() != 0) {
			img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, attrValue);
		} else if (((Element) sourceNode)
				.getAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR) == null
				&& ((Element) sourceNode)
						.getAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR) == null) {
			img
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							"imgStyleClass");
		}
		copyAtrributes(sourceNode, img, visualDocument);
		VpeCreationData creationData = new VpeCreationData(img);
		return creationData;
	}

	/**
	 * Copy attributes from one node to another node.
	 * 
	 * @param sourceNode
	 * @param distinitionNode
	 */
	private void copyAtrributes(Node sourceNode, Element distinitionNode,
			Document visualDocument) {
		NamedNodeMap sourceAttrbutes = sourceNode.getAttributes();
		for (int i = 0; i < sourceAttrbutes.getLength(); i++) {
			Attr attr = visualDocument.createAttribute(sourceAttrbutes.item(i)
					.getNodeName());
			attr.setNodeValue(sourceAttrbutes.item(i).getNodeValue());
			distinitionNode.setAttributeNode(attr);
		}
	}
}
