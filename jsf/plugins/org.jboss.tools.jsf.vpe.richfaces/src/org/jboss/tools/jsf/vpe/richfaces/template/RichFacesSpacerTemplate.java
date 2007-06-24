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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for spacer control
 */
public class RichFacesSpacerTemplate extends VpeAbstractTemplate {

	final static private String IMAGE_NAME = "/spacer/spacer.gif";

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have
	 * child nodes.
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

		Element img = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		ComponentUtil.setImg(img, IMAGE_NAME);

		if(sourceNode instanceof Element) {
			img.setAttribute("width", getSize((Element)sourceNode, "width"));
			img.setAttribute("height", getSize((Element)sourceNode, "height"));
		}

		VpeCreationData creationData = new VpeCreationData(img);

		return creationData;
	}

	private String getSize(Element sourceElement, String attributeName) {
		String size = sourceElement.getAttribute(attributeName); 
		if (size==null || size.length()==0) {
			return "1px";
		} else {
			return size;
		}
	}

	/**
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

		img.setAttribute("width", getSize(sourceElement, "width"));
		img.setAttribute("height", getSize(sourceElement, "height"));
	}
}