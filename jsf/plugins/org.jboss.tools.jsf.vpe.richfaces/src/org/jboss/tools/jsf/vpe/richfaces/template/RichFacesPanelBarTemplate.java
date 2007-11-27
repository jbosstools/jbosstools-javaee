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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPanelBarTemplate extends VpeAbstractTemplate implements
	VpeToggableTemplate, VpeTemplate {

    private static Map toggleMap = new HashMap();

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element sourceElement = (Element) sourceNode;

	nsIDOMElement div = visualDocument.createElement("table");

	VpeCreationData creationData = new VpeCreationData(div);

	ComponentUtil.setCSSLink(pageContext, "panelBar/panelBar.css",
		"richFacesPanelBar");
	String styleClass = sourceElement.getAttribute("styleClass");
	div.setAttribute("class", "dr-pnlbar rich-panelbar dr-pnlbar-b "
		+ (styleClass == null ? "" : styleClass));

	// Set style attribute
	StringBuffer styleValue = new StringBuffer("padding: 0px; ");
	styleValue.append(height(sourceElement)).append(" ").append(
		width(sourceElement)).append(" ").append(
		ComponentUtil.getAttribute(sourceElement, "style"));

	// Encode Body
	List<Node> children = ComponentUtil.getChildren(sourceElement);
	int activeId = getActiveId(sourceElement, children);
	int i = 0;
	for (Node child : children) {
	    boolean active = (i == activeId);

	    if (child.getNodeName().endsWith(":panelBarItem")) {

		RichFacesPanelItemTemplate.encode(creationData,
			(Element) child, visualDocument, div, active,
			ComponentUtil.getAttribute((Element) child,
				"styleClass"), ComponentUtil.getAttribute(
				(Element) child, "style"), ComponentUtil
				.getAttribute((Element) child, "headerClass"),
			ComponentUtil.getAttribute((Element) child,
				"headerStyle"), ComponentUtil.getAttribute(
				(Element) child, "headerClassActive"),
			ComponentUtil.getAttribute((Element) child,
				"headerStyleActive"), ComponentUtil
				.getAttribute((Element) child, "contentClass"),
			ComponentUtil.getAttribute((Element) child,
				"contentStyle"), String.valueOf(i));
		i++;
	    }
	}

	div.setAttribute("style", styleValue.toString());
	return creationData;
    }

    /**
     * 
     * @param sourceElement
     * @return
     */
    private String height(Element sourceElement) {
	String height = sourceElement.getAttribute("height");
	if (height == null || height.length() == 0 || height.equals("100%")) {
	    height = "100%";
	}
	return "height: " + height + ";";
    }

    /**
     * 
     * @param sourceElement
     * @return
     */
    public String width(Element sourceElement) {
	String width = sourceElement.getAttribute("width");
	if (width == null || width.length() == 0) {
	    width = "100%";
	}
	return "width: " + width + ";";
    }

    /**
     * 
     */
    public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
	    String toggleId) {
	toggleMap.put(sourceNode, toggleId);
    }

    /**
     * 
     */
    public void stopToggling(Node sourceNode) {
	toggleMap.remove(sourceNode);
    }

    /**
     * 
     * @param sourceElement
     * @param children
     * @return
     */
    private int getActiveId(Element sourceElement, List<Node> children) {
	int activeId = -1;
	try {
	    activeId = Integer.valueOf((String) toggleMap.get(sourceElement));
	} catch (NumberFormatException nfe) {
	    activeId = -1;
	}

	if (activeId == -1)
	    activeId = 0;

	int count = getChildrenCount(children);
	if (count - 1 < activeId) {
	    activeId = count - 1;
	}

	return activeId;
    }

    /**
     * 
     * @param children
     * @return
     */
    private int getChildrenCount(List<Node> children) {
	int count = 0;
	for (Node child : children) {
	    if (child.getNodeName().endsWith(":panelBarItem")) {
		count++;
	    }
	}
	return count;
    }

    @Override
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_WIDTH_ATTR)
		|| name.equalsIgnoreCase(HtmlComponentUtil.HTML_HEIGHT_ATTR)
		|| name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR))
	    return true;
	return false;
    }
}