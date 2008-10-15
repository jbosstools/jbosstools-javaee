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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesColumnTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument  visualDocument) {
		Element sourceElement = (Element)sourceNode;

		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		if(isHeader(sourceElement)) {
			td.setAttribute(HTML.ATTR_CLASS, "dr-table-headercell rich-table-headercell"); //$NON-NLS-1$
		} else if(isFooter(sourceElement)) {
			td.setAttribute(HTML.ATTR_CLASS, "dr-table-footercell rich-table-footercell");			 //$NON-NLS-1$
		} else {
			td.setAttribute(HTML.ATTR_CLASS, "dr-table-cell rich-table-cell");			 //$NON-NLS-1$
		}
		ComponentUtil.copyAttributes(sourceNode, td);
		VpeCreationData creationData = new VpeCreationData(td);

		// Create mapping to Encode body
		VpeChildrenInfo tdInfo = new VpeChildrenInfo(td);
		List<Node> children = ComponentUtil.getChildren(sourceElement,true);
		for (Node child : children) {
			tdInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(tdInfo);

		return creationData;
	}

	private boolean isHeader(Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
	}

	private boolean isFooter(Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
	}

	private boolean icludedInFacet(Element sourceElement, String facetName) {
		Node parent = sourceElement.getParentNode();
		if(parent!=null) {
			if(ComponentUtil.isFacet(parent, facetName)) {
				return true;
			} else if (parent.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				return ComponentUtil.isFacet(parent.getParentNode(), facetName);
			}
		}
		return false;
	}

	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		nsIDOMElement visualElement = (nsIDOMElement)visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		visualElement.removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		nsIDOMElement visualElement = (nsIDOMElement)visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		visualElement.setAttribute(name, value);
	}
}