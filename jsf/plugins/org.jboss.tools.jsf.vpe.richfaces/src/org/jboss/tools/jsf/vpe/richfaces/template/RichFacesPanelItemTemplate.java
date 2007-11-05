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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPanelItemTemplate extends VpeAbstractTemplate {

    /**
     * 
     * @param creationData
     * @param sourceElement
     * @param visualDocument
     * @param parentVisualElement
     * @param active
     * @param barStyleClass
     * @param barStyle
     * @param barHeaderStyleClass
     * @param barHeaderStyle
     * @param barHeaderActiveStyleClass
     * @param barHeaderActiveStyle
     * @param barContentStyleClass
     * @param barContentStyle
     * @param toggleId
     * @return
     */
    public static VpeCreationData encode(VpeCreationData creationData,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement parentVisualElement, boolean active,
	    String barStyleClass, String barStyle, String barHeaderStyleClass,
	    String barHeaderStyle, String barHeaderActiveStyleClass,
	    String barHeaderActiveStyle, String barContentStyleClass,
	    String barContentStyle, String toggleId) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);

	if (creationData == null) {
	    creationData = new VpeCreationData(div);
	} else {
	    parentVisualElement.appendChild(div);
	}

	div.setAttribute("class", "dr-pnlbar rich-panelbar dr-pnlbar-ext "
		+ barStyleClass);
	div.setAttribute("style", barStyle);
	div.setAttribute("vpe-user-toggle-id", toggleId);

	// Encode Header
	String headerActivetStyleClass = "dr-pnlbar-h-act rich-panelbar-header-act "
		+ barHeaderActiveStyleClass
		+ " "
		+ ComponentUtil
			.getAttribute(sourceElement, "headerClassActive");
	String headerActivetStyle = barHeaderStyle
		+ " "
		+ ComponentUtil.getAttribute(sourceElement, "headerStyle")
		+ " "
		+ barHeaderActiveStyle
		+ " "
		+ ComponentUtil
			.getAttribute(sourceElement, "headerStyleActive") + " "
		+ ComponentUtil.getHeaderBackgoundImgStyle();
	String headerStyleClass = "dr-pnlbar-h rich-panelbar-header "
		+ barHeaderStyleClass + " "
		+ ComponentUtil.getAttribute(sourceElement, "headerClass");
	String headerStyle = barHeaderStyle + " "
		+ ComponentUtil.getAttribute(sourceElement, "headerStyle")
		+ " " + ComponentUtil.getHeaderBackgoundImgStyle();
	if (active) {
	    encodeHeader(sourceElement, visualDocument, div,
		    headerActivetStyleClass, headerActivetStyle, toggleId);
	} else {
	    encodeHeader(sourceElement, visualDocument, div, headerStyleClass,
		    headerStyle, toggleId);
	}

	// Encode Body
	if (active) {

	    nsIDOMElement tr2 = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TR);
	    nsIDOMElement td2 = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TD);
	    tr2.appendChild(td2);
	    tr2.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
	    tr2.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%");
	    if (creationData == null) {
		creationData = new VpeCreationData(tr2);
	    } else {
		parentVisualElement.appendChild(tr2);
	    }

	    nsIDOMElement table = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	    td2.appendChild(table);
	    table.setAttribute("cellpadding", "0");
	    table.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
	    table.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%");

	    nsIDOMElement tbody = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TBODY);
	    table.appendChild(tbody);

	    nsIDOMElement tr = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TR);
	    tbody.appendChild(tr);

	    nsIDOMElement td = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TD);
	    tr.appendChild(td);

	    String tdClass = "dr-pnlbar-c rich-panelbar-content "
		    + barContentStyleClass + " "
		    + ComponentUtil.getAttribute(sourceElement, "contentClass");
	    String tdStyle = barContentStyle + " "
		    + ComponentUtil.getAttribute(sourceElement, "contentStyle");

	    td.setAttribute("class", tdClass);
	    td.setAttribute("style", tdStyle);

	    List<Node> children = ComponentUtil
		    .getChildren(sourceElement, true);
	    VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
	    for (Node child : children) {
		bodyInfo.addSourceChild(child);
	    }
	    creationData.addChildrenInfo(bodyInfo);
	}
	return creationData;
    }

    @Override
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	return encode(null, (Element) sourceNode, visualDocument, null, false,
		"", "", "", "", "", "", "", "", "0");
    }

    /**
     * 
     * @param sourceElement
     * @param visualDocument
     * @param parentDiv
     * @param styleClass
     * @param style
     * @param toggleId
     */
    private static void encodeHeader(Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMElement parentDiv,
	    String styleClass, String style, String toggleId) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentDiv.appendChild(div);
	div.setAttribute("class", styleClass);
	div.setAttribute("style", style);
	div.setAttribute("vpe-user-toggle-id", toggleId);

	String label = sourceElement.getAttribute("label");
	if (label != null) {
	    div.appendChild(visualDocument.createTextNode(label));
	}

    }
}