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

/**
 * @author yzhishko
 */

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SeamPdfChapterTemplate extends SeamPdfAbstractTemplate {

	private static final String NUMBER = "number"; //$NON-NLS-1$

	private nsIDOMElement visualElement;
	private Element sourceElement;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		sourceElement = (Element) sourceNode;
		visualElement = VisualDomUtil.createBorderlessContainer(visualDocument);
		nsIDOMNode headNode = visualDocument.createElement(HTML.TAG_H1);
		String chapterNumber = sourceElement.getAttribute(NUMBER);
		if (chapterNumber != null) {
			try {
				Integer.parseInt(chapterNumber);
			} catch (NumberFormatException e) {
				chapterNumber = "1"; //$NON-NLS-1$
			}
		} else {
			chapterNumber = "1"; //$NON-NLS-1$
		}
		nsIDOMText chapterNumberNode = visualDocument
				.createTextNode(chapterNumber + ". "); //$NON-NLS-1$
		headNode.appendChild(chapterNumberNode);
		visualElement.appendChild(headNode);
		return new VpeCreationData(visualElement);
	}

	private void setTitle(VpePageContext pageContext, Element sourceElement,
			VpeCreationData data) {
		Node sourceTitleNode = null;
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				if (children.item(i).getNodeName().endsWith("title")) { //$NON-NLS-1$
					sourceTitleNode = children.item(i);
				}
			}
		}
		nsIDOMNode visualTitleNode = null;
		if (sourceTitleNode != null) {
			visualTitleNode = pageContext.getDomMapping().getVisualNode(
					sourceTitleNode);
		}
		if (visualTitleNode != null) {
			nsIDOMElement headElement = getHeadElement(data);
			nsIDOMNode parentNode = visualTitleNode.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(visualTitleNode);
				headElement.appendChild(visualTitleNode);
			}
		}
	}

	private nsIDOMElement getHeadElement(VpeCreationData data) {
		nsIDOMNode visualNode = data.getNode();
		nsIDOMNodeList children = visualNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == nsIDOMNode.ELEMENT_NODE) {
				if (children.item(i).getNodeName()
						.equalsIgnoreCase(HTML.TAG_H1)) {
					return (nsIDOMElement) children.item(i).queryInterface(
							nsIDOMElement.NS_IDOMELEMENT_IID);
				}
			}
		}
		return null;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
			setTitle(pageContext, (Element) sourceNode, data);
	}

}
