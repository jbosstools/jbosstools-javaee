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
import org.jboss.tools.vpe.editor.util.ResourceUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPanelItemTemplate extends VpeAbstractTemplate {

    public final static String CONTENT_CLASS = "contentClass"; //$NON-NLS-1$
    public final static String CONTENT_STYLE = "contentStyle"; //$NON-NLS-1$
    public final static String HEADER_CLASS = "headerClass"; //$NON-NLS-1$
    public final static String HEADER_STYLE = "headerStyle"; //$NON-NLS-1$
    public final static String HEADER_ACTIVE_CLASS = "headerClassActive"; //$NON-NLS-1$
    public final static String HEADER_ACTIVE_STYLE = "headerStyleActive"; //$NON-NLS-1$

    private static final String PERCENT_100 = "100%"; //$NON-NLS-1$
    private static final String DR_PNLBAR_H_RICH_PANELBAR_HEADER = "dr-pnlbar-h rich-panelbar-header"; //$NON-NLS-1$
    private static final String DR_PNLBAR_RICH_PANELBAR_DR_PNLBAR_EXT = " dr-pnlbar rich-panelbar dr-pnlbar-ext "; //$NON-NLS-1$
    private static final String DR_PNLBAR_C_RICH_PANELBAR_CONTENT = "dr-pnlbar-c rich-panelbar-content "; //$NON-NLS-1$
    private static final String ZERO = "0"; //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String VPE_USER_TOGGLE_ID = "vpe-user-toggle-id"; //$NON-NLS-1$
    private static final String LABEL = "label"; //$NON-NLS-1$
    private static final String DEFAULT_LABEL = "auto generated label"; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$

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
	    VpePageContext pageContext, Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMElement parentVisualElement,
	    boolean active, String barStyleClass, String barStyle,
	    String barHeaderStyleClass, String barHeaderStyle,
	    String barHeaderActiveStyleClass, String barHeaderActiveStyle,
	    String barContentStyleClass, String barContentStyle, String toggleId) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);

	if (creationData == null) {
	    creationData = new VpeCreationData(div);
	} else {
	    parentVisualElement.appendChild(div);
	}

	div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, barStyleClass
		+ DR_PNLBAR_RICH_PANELBAR_DR_PNLBAR_EXT);
	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, barStyle);
	div.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

	// Encode Header
	String headerActiveStyle = (barHeaderStyle
		+ SPACE
		+ ComponentUtil.getAttribute(sourceElement, HEADER_STYLE)
		+ SPACE
		+ barHeaderActiveStyle
		+ SPACE
		+ ComponentUtil
			.getAttribute(sourceElement, HEADER_ACTIVE_STYLE)).trim();
	String headerStyle = (barHeaderStyle + SPACE
		+ ComponentUtil.getAttribute(sourceElement, HEADER_STYLE)).trim();

	String internContentClass = ComponentUtil.getAttribute(sourceElement,
		CONTENT_CLASS);
	String internContentStyle = ComponentUtil.getAttribute(sourceElement,
		CONTENT_STYLE);
	String internHeaderClass = ComponentUtil.getAttribute(sourceElement,
		HEADER_CLASS);
	String internHeaderActiveClass = ComponentUtil.getAttribute(
		sourceElement, HEADER_ACTIVE_CLASS);

	if (active) {
	    String headerClass = (DR_PNLBAR_H_RICH_PANELBAR_HEADER + SPACE
		    + barHeaderStyleClass + SPACE + internHeaderClass + SPACE
		    + barHeaderActiveStyleClass + SPACE
		    + internHeaderActiveClass).trim();
	    encodeHeader(creationData, pageContext, sourceElement,
		    visualDocument, div, headerClass, headerActiveStyle,
		    toggleId);
	} else {
	    String headerClass = (DR_PNLBAR_H_RICH_PANELBAR_HEADER + SPACE
		    + barHeaderStyleClass + SPACE + internHeaderClass).trim();
	    encodeHeader(creationData, pageContext, sourceElement,
		    visualDocument, div, headerClass, headerStyle, toggleId);
	}

	// Encode Body
	if (active) {

	    nsIDOMElement tr2 = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TR);
	    nsIDOMElement td2 = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TD);
	    tr2.appendChild(td2);
	    tr2.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, PERCENT_100);
	    tr2.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, PERCENT_100);
	    if (creationData == null) {
		creationData = new VpeCreationData(tr2);
	    } else {
		parentVisualElement.appendChild(tr2);
	    }

	    nsIDOMElement contentTable = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TABLE);

	    td2.appendChild(contentTable);
	    contentTable.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR,
		    ZERO);
	    contentTable.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR,
		    PERCENT_100);
	    contentTable.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR,
		    PERCENT_100);

	    nsIDOMElement tbody = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TBODY);
	    contentTable.appendChild(tbody);

	    nsIDOMElement tr = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TR);
	    tbody.appendChild(tr);

	    nsIDOMElement td = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_TD);
	    tr.appendChild(td);

	    String tdClass = DR_PNLBAR_C_RICH_PANELBAR_CONTENT
		    + barContentStyleClass + SPACE + internContentClass;
	    String tdStyle = barContentStyle + SPACE + internContentStyle;

	    td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, tdClass);
	    td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, tdStyle);

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
	return encode(null, pageContext, (Element) sourceNode, visualDocument,
		null, false, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
		EMPTY, ZERO);
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
	    VpePageContext pageContext, Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMElement parentDiv,
	    String styleClass, String style, String toggleId) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentDiv.appendChild(div);
	div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
	div.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

	Element facet = ComponentUtil.getFacet(sourceElement, LABEL);

	if (facet == null) {
	    Attr attr = null;
	    if (sourceElement.hasAttribute(LABEL)) {
		attr = sourceElement.getAttributeNode(LABEL);
	    }
	    if (attr != null) {
		String itemLabel = attr.getNodeValue();
		String bundleValue = ResourceUtil.getBundleValue(pageContext,
			attr.getValue());
		nsIDOMText text;
		// if bundleValue differ from value then will be represent
		// bundleValue, but text will be not edit
		if (!itemLabel.equals(bundleValue)) {
		    text = visualDocument.createTextNode(bundleValue);

		} else {
		    text = visualDocument.createTextNode(itemLabel);
		}
		div.appendChild(text);
	    } else {
		div.appendChild(visualDocument.createTextNode(DEFAULT_LABEL));
	    }

	} else {
	    VpeChildrenInfo facetInfo = new VpeChildrenInfo(div);
	    facetInfo.addSourceChild(facet);
	    vpeCreationData.addChildrenInfo(facetInfo);
	}

    }

}