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
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesTabTemplate extends VpeAbstractTemplate {

	/**
	 * Encode body of tab
	 * @param creationData
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentVisualElement
	 * @param active
	 * @param tabClass
	 * @param activeTabClass
	 * @param inactiveTabClass
	 * @param disabledTabClass
	 * @param contentClass
	 * @param contentStyle
	 * @return
	 */
	public static VpeCreationData encodeBody(VpeCreationData creationData, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMElement parentVisualElement, boolean active,
			String tabClass,
			String activeTabClass,
			String inactiveTabClass,
			String disabledTabClass,
			String contentClass,
			String contentStyle) {
	    
	    	nsIDOMElement td = visualDocument.createElement("td");

		if(creationData==null) {
			
			creationData = new VpeCreationData(td);
		} else {
			parentVisualElement.appendChild(td);
		}
		if(!active) {
			return creationData;
		}
//		td.setAttribute("style", "position: relative;");
		td.setAttribute("height", "100%");
		

		nsIDOMElement table = visualDocument.createElement("table");
		td.appendChild(table);
		table.setAttribute("border", "0");
		table.setAttribute("cellpadding", "10");
		table.setAttribute("cellspacing", "0");
		table.setAttribute("width", "100%");
		table.setAttribute("class", "dr-tbpnl-cntnt-pstn rich-tabpanel-content-position");
		table.setAttribute("style", "position: relative; z-index: 1;");

		nsIDOMElement tr = visualDocument.createElement("tr");
		table.appendChild(tr);
		td = visualDocument.createElement("td");
		tr.appendChild(td);
		td.setAttribute("class", "dr-tbpnl-cntnt rich-tabpanel-content " + contentClass + " " + ComponentUtil.getAttribute(sourceElement, "styleClass"));
		td.setAttribute("style", ComponentUtil.getAttribute(sourceElement, "contentStyle") + "; " + ComponentUtil.getAttribute(sourceElement, "style"));

		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);

		return creationData;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		return encodeBody(null, (Element)sourceNode, visualDocument, null, true, "", "", "", "", "", "");
	}

	/**
	 * Encode Header of tab
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentDiv
	 * @param active
	 * @param activeTabClass
	 * @param inactiveTabClass
	 * @param disabledTabClass
	 */
	public static void encodeHeader(Element sourceElement, nsIDOMDocument visualDocument, nsIDOMElement parentDiv, boolean active,
			String activeTabClass,
			String inactiveTabClass,
			String disabledTabClass, String toggleId) {
	    
		nsIDOMElement td = visualDocument.createElement("td");
		parentDiv.appendChild(td);
		td.setAttribute("style", "height: 100%; vertical-align: bottom;");
		String styleClass = "dr-tbpnl-tbcell-dsbl rich-tabhdr-cell-dsbl";
		if(!"true".equalsIgnoreCase(sourceElement.getAttribute("disabled"))) {
			if(active) {
				styleClass = "dr-tbpnl-tbcell-act rich-tabhdr-cell-active";
			} else {
				styleClass = "dr-tbpnl-tbcell-inact rich-tabhdr-cell-inactive";
			}
		}
		td.setAttribute("class", styleClass);
		td.setAttribute("vpe-user-toggle-id", toggleId);

		nsIDOMElement table = visualDocument.createElement("table");
		td.appendChild(table);
		table.setAttribute("border", "0");
		table.setAttribute("cellpadding", "0");
		table.setAttribute("cellspacing", "0");
		table.setAttribute("style", "height : 100%; position : relative; z-index : 2;");
		table.setAttribute("vpe-user-toggle-id", toggleId);

		nsIDOMElement mainTr = visualDocument.createElement("tr");
		table.appendChild(mainTr);
		encodeSpacer(mainTr, visualDocument);

		td = visualDocument.createElement("td");
		mainTr.appendChild(td);
		td.setAttribute("class", "dr-tbpnl-tbtopbrdr rich-tabhdr-side-cell");
		td.setAttribute("style", "width: " + ComponentUtil.getAttribute(sourceElement, "labelWidth") + ";");
		td.setAttribute("vpe-user-toggle-id", toggleId);

		table = visualDocument.createElement("table");
		td.appendChild(table);
		table.setAttribute("style", "height: 100%; width: 100%;");
		table.setAttribute("border", "0");
		table.setAttribute("cellpadding", "0");
		table.setAttribute("cellspacing", "0");
		table.setAttribute("vpe-user-toggle-id", toggleId);

		nsIDOMElement tr = visualDocument.createElement("tr");
		table.appendChild(tr);
		td = visualDocument.createElement("td");
		tr.appendChild(td);

		styleClass = "dr-tbpnl-tb rich-tab-header dr-tbpnl-tb-dsbl rich-tab-disabled " + disabledTabClass;
		String bgImgPath = ComponentUtil.getAbsoluteResourcePath("tabPanel/inactiveBackground.gif");

		if(!"true".equalsIgnoreCase(sourceElement.getAttribute("disabled"))) {
			if(active) {
				styleClass = "dr-tbpnl-tb rich-tab-header dr-tbpnl-tb-act rich-tab-active " + activeTabClass;
				bgImgPath = ComponentUtil.getAbsoluteResourcePath("tabPanel/activeBackground.gif");
			} else {
				styleClass = "dr-tbpnl-tb rich-tab-header dr-tbpnl-tb-inact rich-tab-inactive " + inactiveTabClass;
			}
		}

		td.setAttribute("class", styleClass);
		String style = "background-image: url(file:///" + bgImgPath.replace('\\', '/') + ");";
		td.setAttribute("style", style);
		td.setAttribute("vpe-user-toggle-id", toggleId);
		String label = sourceElement.getAttribute("label");
		if(label==null) {
			char space = 160;
			label = "" + space;
		}
		td.appendChild(visualDocument.createTextNode(label));
		encodeSpacer(mainTr, visualDocument);
	}

	/*
	 * Add <td class="dr-tbpnl-tbbrdr rich-tabhdr-side-border"><img src="#{spacer}" width="1" height="1" alt="" border="0" /></td>
	 */
	private static void encodeSpacer(nsIDOMElement parentTr, nsIDOMDocument visualDocument) {
	    	nsIDOMElement td = visualDocument.createElement("td");
		parentTr.appendChild(td);
		td.setAttribute("class", "dr-tbpnl-tbbrdr rich-tabhdr-side-border");
		String borderImgPath = ComponentUtil.getAbsoluteResourcePath("tabPanel/border.gif");
		String style = "background-image: url(file:///" + borderImgPath.replace('\\', '/') + ");";
		td.setAttribute("style", style);
		nsIDOMElement img = visualDocument.createElement("img");
		td.appendChild(img);
		ComponentUtil.setImg(img, "common/spacer.gif");
		img.setAttribute("width", "1");
		img.setAttribute("height", "1");
		img.setAttribute("border", "0");
	}
}