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

public class RichFacesPanelTemplate extends VpeAbstractTemplate {

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		Element sourceElement = (Element)sourceNode;

		Element div = visualDocument.createElement("div");

		VpeCreationData creationData = new VpeCreationData(div);

		ComponentUtil.setCSSLink(pageContext, "panel/panel.css", "richFacesPanel");
		String styleClass = sourceElement.getAttribute("styleClass");
		div.setAttribute("class", "dr-pnl rich-panel " + (styleClass==null?"":styleClass));
		String style = sourceElement.getAttribute("style");
		if(style!=null && style.length()>0) {
			div.setAttribute("style", style);
		}

		// Encode Header
		Node header = ComponentUtil.getFacet(sourceElement, "header", true);
		if(header!=null) {
			Element headerDiv = visualDocument.createElement("div");
			div.appendChild(headerDiv);
			String headerClass = sourceElement.getAttribute("headerClass");
			headerDiv.setAttribute("class", "dr-pnl-h rich-panel-header " + (headerClass==null?"":headerClass));
			headerDiv.setAttribute("style", ComponentUtil.getHeaderBackgoundImgStyle());

			VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
			headerInfo.addSourceChild(header);
			creationData.addChildrenInfo(headerInfo);
		}

		// Encode Body
		Element bodyDiv = visualDocument.createElement("div");
		div.appendChild(bodyDiv);
		String bodyClass = sourceElement.getAttribute("bodyClass");
		bodyDiv.setAttribute("class", "dr-pnl-b rich-panel-body " + (bodyClass==null?"":bodyClass));

		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(bodyDiv);
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);

		return creationData;
	}
}