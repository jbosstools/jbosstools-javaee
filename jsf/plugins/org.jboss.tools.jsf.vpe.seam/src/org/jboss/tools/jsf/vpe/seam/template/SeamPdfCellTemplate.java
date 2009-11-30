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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfCellTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement visualElement;
	private Element sourceElement;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		sourceElement = (Element) sourceNode;
		nsIDOMNode visualNode = visualDocument.createElement(HTML.TAG_TD);
		visualElement = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		copyAttrs(visualElement, sourceElement);
		visualElement.setAttribute(HTML.ATTR_STYLE, "border-width: 2px; border-color: black; border-style: solid"); //$NON-NLS-1$
		return new VpeCreationData(visualElement);
	}

	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode,
			nsIDOMNode visualNode, Object data) {
		Node nodeForUpdate = super.getNodeForUpdate(pageContext, sourceNode,
				visualNode, data);
		if (nodeForUpdate == null) {
			nodeForUpdate = SeamUtil.getParentByName(pageContext, sourceNode,
					"p:table"); //$NON-NLS-1$
		}
		return nodeForUpdate;
	}

	private void copyAttrs(nsIDOMElement visualElement, Element sourceElement) {
		String attr = sourceElement.getAttribute(HTML.ATTR_COLSPAN);
		if (attr != null) {
			visualElement.setAttribute(HTML.ATTR_COLSPAN, attr);
		}
		attr = sourceElement
				.getAttribute(SeamUtil.SEAM_ATTR_HORIZONAL_ALIGNMENT);
		if (attr != null) {
			visualElement.setAttribute(HTML.ATTR_ALIGN, attr);
		}
		attr = sourceElement
				.getAttribute(SeamUtil.SEAM_ATTR_VERTICAL_ALIGNMENT);
		if (attr != null) {
			visualElement.setAttribute(HTML.ATTR_VALIGN, attr);
		}
		attr = sourceElement.getAttribute("noWrap"); //$NON-NLS-1$
		if (attr != null) {
			visualElement.setAttribute("nowrap", attr); //$NON-NLS-1$
		}
	}

}
