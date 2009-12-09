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

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SeamPdfAbstractChapterTemplate extends
		SeamPdfAbstractTemplate {

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		setTitle(pageContext, (Element) sourceNode, data);
	}

	private void setTitle(VpePageContext pageContext, Element sourceElement,
			VpeCreationData data) {
		nsIDOMNode visualTitleNode = getTitleForVisualNode(data.getNode());
		if (visualTitleNode != null) {
			nsIDOMElement headElement = getHeadElement(data);
			nsIDOMNode parentNode = visualTitleNode.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(visualTitleNode);
				headElement.appendChild(visualTitleNode);
			}
		}
	}

	private nsIDOMNode getTitleForVisualNode(nsIDOMNode visualNode) {
		if (visualNode == null) {
			return null;
		}
		nsIDOMNodeList children = visualNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			nsIDOMNode child = children.item(i);
			if (HTML.TAG_SPAN.equalsIgnoreCase(child.getNodeName())) {
				nsIDOMElement childElement = (nsIDOMElement) child
						.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				String attrType = childElement
						.getAttribute(SeamUtil.SEAM_ATTR_TYPE_ID);
				if (attrType != null
						&& SeamUtil.SEAM_ATTR_TYPE_ID_VALUE_PDF_TITLE
								.equalsIgnoreCase(attrType)) {
					return child;
				}
			}
		}
		return null;
	}

	protected abstract nsIDOMElement getHeadElement(VpeCreationData data);

}
