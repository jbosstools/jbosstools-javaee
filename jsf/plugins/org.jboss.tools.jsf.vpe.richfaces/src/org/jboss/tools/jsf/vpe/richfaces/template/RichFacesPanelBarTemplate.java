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

    private final static String CONTENT_CLASS = "contentClass";
    private final static String CONTENT_STYLE = "contentStyle";
    private final static String HEADER_CLASS = "headerClass";
    private final static String HEADER_STYLE = "headerStyle";
    private final static String HEADER_ACTIVE_CLASS = "headerClassActive";
    private final static String HEADER_ACTIVE_STYLE = "headerStyleActive";
    
     
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	
	Element sourceElement = (Element) sourceNode;
	nsIDOMElement table = visualDocument.createElement("table");

	VpeCreationData creationData = new VpeCreationData(table);

	ComponentUtil.setCSSLink(pageContext, "panelBar/panelBar.css",
		"richFacesPanelBar");
	String styleClass = sourceElement.getAttribute("styleClass");
	table.setAttribute("class", "dr-pnlbar rich-panelbar dr-pnlbar-b "
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

	String style = ComponentUtil.getAttribute(sourceElement,
		HtmlComponentUtil.HTML_STYLE_ATTR);

	String contentClass = ComponentUtil.getAttribute(sourceElement,
		CONTENT_CLASS);
	String contentStyle = ComponentUtil.getAttribute(sourceElement,
		CONTENT_STYLE);
	String headerClass = ComponentUtil.getAttribute(sourceElement,
		HEADER_CLASS);
	String headerStyle = ComponentUtil.getAttribute(sourceElement,
		HEADER_STYLE);
	String headerActiveStyle = ComponentUtil.getAttribute(sourceElement,
		HEADER_ACTIVE_STYLE);
	String headerActiveClass = ComponentUtil.getAttribute(sourceElement,
		HEADER_ACTIVE_CLASS);

	for (Node child : children) {
	    boolean active = (i == activeId);

	    if (child.getNodeName().endsWith(":panelBarItem")) {

		String internContentClass = ComponentUtil.getAttribute(
			(Element) child, CONTENT_CLASS);
		String internContentStyle = ComponentUtil.getAttribute(
			(Element) child, CONTENT_STYLE);
		String internHeaderClass = ComponentUtil.getAttribute(
			(Element) child, HEADER_CLASS);
		String internHeaderStyle = ComponentUtil.getAttribute(
			(Element) child, HEADER_STYLE);
		String internHeaderActiveStyle = ComponentUtil.getAttribute(
			(Element) child, HEADER_ACTIVE_STYLE);
		String internHeaderActiveClass = ComponentUtil.getAttribute(
			(Element) child, HEADER_ACTIVE_CLASS);

		RichFacesPanelItemTemplate
			.encode(
				creationData,
				(Element) child,
				visualDocument,
				table,
				active,
				ComponentUtil.getAttribute(sourceElement,
					HtmlComponentUtil.HTML_STYLECLASS_ATTR),
				style,
				(internHeaderClass.length() == 0) ? headerClass
					: internHeaderClass,
				(internHeaderStyle.length() == 0) ? headerStyle
					: internHeaderStyle,
				(internHeaderActiveClass.length() == 0) ? headerActiveClass
					: internHeaderActiveClass,
				(internHeaderActiveStyle.length() == 0) ? headerActiveStyle
					: internHeaderActiveStyle,
				(internContentClass.length() == 0) ? contentClass
					: internContentClass,
				(internContentStyle.length() == 0) ? contentStyle
					: internContentStyle, String.valueOf(i));
		
		i++;
	    }
	}

	table.setAttribute("style", styleValue.toString());
	return creationData;
    }

    /**
     * 
     * @param sourceElement
     * @return
     */
    private String height(Element sourceElement) {
	String height = sourceElement.getAttribute("height");
	if (height == null || height.length() == 0 ) {
	   
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