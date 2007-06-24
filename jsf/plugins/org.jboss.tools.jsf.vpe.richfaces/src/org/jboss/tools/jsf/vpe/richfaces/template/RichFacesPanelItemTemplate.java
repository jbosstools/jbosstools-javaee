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

public class RichFacesPanelItemTemplate extends VpeAbstractTemplate {
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public static VpeCreationData encode(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualElement, boolean active,
			String barStyleClass, String barStyle,
			String barHeaderStyleClass, String barHeaderStyle,
			String barHeaderActiveStyleClass, String barHeaderActiveStyle,
			String barContentStyleClass,
			String barContentStyle,
			String toggleId) {
		Element div = visualDocument.createElement("div");

		if(creationData==null) {
			creationData = new VpeCreationData(div);
		} else {
			parentVisualElement.appendChild(div);
		}

		div.setAttribute("class", "dr-pnlbar rich-panelbar dr-pnlbar-ext " + barStyleClass);
		div.setAttribute("style", barStyle);
		div.setAttribute("vpe-user-toggle-id", toggleId);

		// Encode Header
		String headerActivetStyleClass = "dr-pnlbar-h-act rich-panelbar-header-act " + barHeaderActiveStyleClass + " " + ComponentUtil.getAttribute(sourceElement, "headerClassActive");
		String headerActivetStyle = barHeaderStyle + " " + ComponentUtil.getAttribute(sourceElement, "headerStyle") + " " + barHeaderActiveStyle + " " + ComponentUtil.getAttribute(sourceElement, "headerStyleActive") + " " + ComponentUtil.getHeaderBackgoundImgStyle();
		String headerStyleClass = "dr-pnlbar-h rich-panelbar-header " + barHeaderStyleClass + " " + ComponentUtil.getAttribute(sourceElement, "headerClass");
		String headerStyle = barHeaderStyle + " " + ComponentUtil.getAttribute(sourceElement, "headerStyle") + " " + ComponentUtil.getHeaderBackgoundImgStyle();
		if(active) {
			encodeHeader(sourceElement, visualDocument, div, headerActivetStyleClass, headerActivetStyle, toggleId);
		} else {
			encodeHeader(sourceElement, visualDocument, div, headerStyleClass, headerStyle, toggleId);
		}

		// Encode Body
		if(active) {
			Element bodyDiv = visualDocument.createElement("div");
			div.appendChild(bodyDiv);
			bodyDiv.setAttribute("style", "width: 100%;");

			Element table = visualDocument.createElement("table");
			bodyDiv.appendChild(table);
			table.setAttribute("cellpadding", "0");
			table.setAttribute("width", "100%");
			table.setAttribute("style", "height: 100%;");

			Element tbody = visualDocument.createElement("tbody");
			table.appendChild(tbody);
			
			Element tr = visualDocument.createElement("tr");
			tbody.appendChild(tr);
			
			Element td = visualDocument.createElement("td");
			tr.appendChild(td);

			String tdClass = "dr-pnlbar-c rich-panelbar-content " + barContentStyleClass + " " + ComponentUtil.getAttribute(sourceElement, "contentClass");
			String tdStyle = barContentStyle + " " + ComponentUtil.getAttribute(sourceElement, "contentStyle");

			td.setAttribute("class", tdClass);
			td.setAttribute("style", tdStyle);

			List<Node> children = ComponentUtil.getChildren(sourceElement, true);
			VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
			for (Node child : children) {
				bodyInfo.addSourceChild(child);
			}
			creationData.addChildrenInfo(bodyInfo);
		}

		return creationData;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		return encode(null, (Element)sourceNode, visualDocument, null, false, "", "", "", "", "", "", "", "", "0");
	}

	private static void encodeHeader(Element sourceElement, Document visualDocument, Element parentDiv, String styleClass, String style, String toggleId) {
		Element div = visualDocument.createElement("div");
		parentDiv.appendChild(div);
		div.setAttribute("class", styleClass);
		div.setAttribute("style", style);
		div.setAttribute("vpe-user-toggle-id", toggleId);

		String label = sourceElement.getAttribute("label");
		if(label!=null) {
			div.appendChild(visualDocument.createTextNode(label));
		}
	}
}