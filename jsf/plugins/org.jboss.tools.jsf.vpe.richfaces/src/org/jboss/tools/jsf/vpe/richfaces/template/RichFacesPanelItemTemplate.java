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

    private static final String DEFAULT_LABEL = "auto generated label";

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

	div.setAttribute("class", barStyleClass
		+ " dr-pnlbar rich-panelbar dr-pnlbar-ext ");
	div.setAttribute("style", barStyle);
	div.setAttribute("vpe-user-toggle-id", toggleId);

	// Encode Header
	String headerActivetStyleClass = "dr-pnlbar-h rich-panelbar-header "
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
			.getAttribute(sourceElement, "headerStyleActive");
	String headerStyleClass = "dr-pnlbar-h rich-panelbar-header "
		+ ComponentUtil.getAttribute(sourceElement, "headerClass")
		+ barHeaderStyleClass;
	String headerStyle = barHeaderStyle + " "
		+ ComponentUtil.getAttribute(sourceElement, "headerStyle");
	if (active) {
	    encodeHeader(creationData, sourceElement, visualDocument, div,
		    headerActivetStyleClass, headerActivetStyle, toggleId);
	} else {
	    encodeHeader(creationData, sourceElement, visualDocument, div,
		    headerStyleClass, headerStyle, toggleId);
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

	    nsIDOMElement contentTable = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TABLE);

	    td2.appendChild(contentTable);
	    contentTable.setAttribute("cellpadding", "0");
	    contentTable.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
	    contentTable.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%");

	    nsIDOMElement tbody = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TBODY);
	    contentTable.appendChild(tbody);

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
    private static void encodeHeader(VpeCreationData vpeCreationData,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement parentDiv, String styleClass, String style,
	    String toggleId) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentDiv.appendChild(div);
	div.setAttribute("class", styleClass);
	div.setAttribute("style", style);
	div.setAttribute("vpe-user-toggle-id", toggleId);

	String label = sourceElement.getAttribute("label");
	Element facet = ComponentUtil.getFacet(sourceElement, "label");
	if (facet == null) {
	    div.appendChild(visualDocument
		    .createTextNode((label == null) ? DEFAULT_LABEL : label));
	} else {
	    VpeChildrenInfo facetInfo = new VpeChildrenInfo(div);
	    facetInfo.addSourceChild(facet);
	    vpeCreationData.addChildrenInfo(facetInfo);
	}
    }

}