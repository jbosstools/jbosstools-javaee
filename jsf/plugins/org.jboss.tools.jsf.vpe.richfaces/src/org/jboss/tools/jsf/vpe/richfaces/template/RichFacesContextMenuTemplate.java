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
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces contextMenu
 * 
 * @author dsakovich@exadel.com
 */
public class RichFacesContextMenuTemplate extends VpeAbstractTemplate {

    private final static String STYLE_PATH = "contextMenu/contextMenu.css";
    private final static String IMAGE_PATH = "contextMenu/spacer.gif";
    private final static String BACKGROUND_PATH = "contextMenu/background.gif";
    private final static String MENU_ITEM_NODE_NAME = "menuItem";
    private final static String VALUE = "value";
    private final static String DIV_STYLE = "border : 1px solid #C4C0B9; ";

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	ComponentUtil.setCSSLink(pageContext, STYLE_PATH,
		"richFacesContextMenu");
	Element sourceElement = (Element) sourceNode;

	nsIDOMElement parentDiv = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);

	parentDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		"dr-menu-list-border");
	nsIDOMElement table = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

	table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		"dr-menu-list-bg rich-menu-list-bg dr-menu-list-border");

	String path = RichFacesTemplatesActivator.getPluginResourcePath()
		+ BACKGROUND_PATH;

	table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
		"background-image: url(file://" + path + "); " + DIV_STYLE);

	parentDiv.appendChild(table);
	List<Node> listItems = ComponentUtil.getChildren(sourceElement, false);

	for (Node node : listItems) {
	    nsIDOMElement element = createMenuItem(visualDocument, node);
	    if (element != null)
		table.appendChild(element);
	}

	VpeCreationData creationData = new VpeCreationData(parentDiv);

	return creationData;

    }

    /**
     * Create menu item
     * 
     * @param document
     * @param sourceNode
     * @return
     */
    private nsIDOMElement createMenuItem(nsIDOMDocument document,
	    Node sourceNode) {

	Element sourceElement = (Element) sourceNode;

	String nodeName = sourceElement.getPrefix() + ":" + MENU_ITEM_NODE_NAME;
	if (!sourceElement.getNodeName().equalsIgnoreCase(nodeName)) {
	    return null;
	}
	String value = sourceElement.getAttribute(VALUE);
	nsIDOMElement div = document
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	div
		.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			"dr-menu-item rich-menu-item dr-menu-item-enabled rich-menu-item-enabled");

	nsIDOMElement span = document
		.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
	span.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		"dr-menu-icon rich-menu-item-icon");

	nsIDOMElement img = document
		.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	img.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, "16");
	img.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT, "16");
	ComponentUtil.setImg(img, IMAGE_PATH);

	nsIDOMElement textSpan = document
		.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
	textSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		"rich-menu-item-label");

	nsIDOMText text = document.createTextNode((value == null) ? "" : value);

	textSpan.appendChild(text);
	span.appendChild(img);
	div.appendChild(span);
	div.appendChild(textSpan);

	return div;
    }
}
