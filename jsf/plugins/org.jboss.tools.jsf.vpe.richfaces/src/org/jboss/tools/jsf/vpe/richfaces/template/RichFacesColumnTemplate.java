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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesColumnTemplate extends VpeAbstractTemplate {

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		Element sourceElement = (Element)sourceNode;

		Element td = visualDocument.createElement("td");
		if(isHeader(sourceElement)) {
			td.setAttribute("class", "dr-table-headercell rich-table-headercell");
		} else if(isFooter(sourceElement)) {
			td.setAttribute("class", "dr-table-footercell rich-table-footercell");			
		} else {
			td.setAttribute("class", "dr-table-cell rich-table-cell");			
		}
		ComponentUtil.copyAttributes(sourceNode, td);
		VpeCreationData creationData = new VpeCreationData(td);

		// Create mapping to Encode body
		VpeChildrenInfo tdInfo = new VpeChildrenInfo(td);
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (Node child : children) {
			tdInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(tdInfo);

		return creationData;
	}

	private boolean isHeader(Element sourceElement) {
		return icludedInFacet(sourceElement, "header");
	}

	private boolean isFooter(Element sourceElement) {
		return icludedInFacet(sourceElement, "footer");
	}

	private boolean icludedInFacet(Element sourceElement, String facetName) {
		Node parent = sourceElement.getParentNode();
		if(parent!=null) {
			if(ComponentUtil.isFacet(parent, facetName)) {
				return true;
			} else if (parent.getNodeName().endsWith(":columnGroup")) {
				return ComponentUtil.isFacet(parent.getParentNode(), facetName);
			}
		}
		return false;
	}

	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name) {
		((Element)visualNode).removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		((Element)visualNode).setAttribute(name, value);
	}
}