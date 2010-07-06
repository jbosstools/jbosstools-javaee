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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;

import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RichFacesPanelBarTemplate extends VpeAbstractTemplate implements VpeToggableTemplate,
    VpeTemplate {
    private static final String PERCENT_100 = "100%"; //$NON-NLS-1$
    private static final String PANEL_BAR_ITEM = ":panelBarItem"; //$NON-NLS-1$
    private static final String DR_PNLBAR_RICH_PANELBAR_DR_PNLBAR_B = "dr-pnlbar rich-panelbar dr-pnlbar-b"; //$NON-NLS-1$
    private static final String PANEL_BAR_PANEL_BAR_CSS = "panelBar/panelBar.css"; //$NON-NLS-1$
    private static Map<Node, String> toggleMap = new HashMap<Node, String>();

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
        nsIDOMDocument visualDocument) {
        Element sourceElement = (Element) sourceNode;
        nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
        nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
        div.appendChild(table);
        VpeCreationData creationData = new VpeCreationData(div);

        ComponentUtil.setCSSLink(pageContext, PANEL_BAR_PANEL_BAR_CSS, "richFacesPanelBar"); //$NON-NLS-1$

        String styleClass = DR_PNLBAR_RICH_PANELBAR_DR_PNLBAR_B;
        if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
        	styleClass += " " + sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS); //$NON-NLS-1$
        }
        table.setAttribute(HTML.ATTR_CLASS, styleClass);

        // Set style attribute
        StringBuffer styleValue = new StringBuffer("padding: 0px; "); //$NON-NLS-1$
        styleValue.append(height(sourceElement)).append(Constants.WHITE_SPACE)
                  .append(width(sourceElement)).append(Constants.WHITE_SPACE)
                  .append(ComponentUtil.getAttribute(sourceElement, HTML.ATTR_STYLE));

        // Encode Body
        List<Node> children = ComponentUtil.getChildren(sourceElement);
        int activeId = getActiveId(sourceElement, children);
        int i = 0;

        String style = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_STYLE);

        String contentClass = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.CONTENT_CLASS);
        String contentStyle = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.CONTENT_STYLE);
        String headerClass = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.HEADER_CLASS);
        String headerStyle = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.HEADER_STYLE);
        String headerActiveStyle = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.HEADER_ACTIVE_STYLE);
        String headerActiveClass = ComponentUtil.getAttribute(sourceElement,
                RichFacesPanelItemTemplate.HEADER_ACTIVE_CLASS);

        for (Node child : children) {
            boolean active = (i == activeId);

            if (child.getNodeName().endsWith(PANEL_BAR_ITEM)) {
                RichFacesPanelItemTemplate.encode(creationData, pageContext, (Element) child,
                    visualDocument, table, active,
                    ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS),
                    style, headerClass, headerStyle, headerActiveClass, headerActiveStyle, contentClass,
                    contentStyle, String.valueOf(i));

                i++;
            }
        }

        table.setAttribute(HTML.ATTR_STYLE, styleValue.toString());
        return creationData;
    }

    /**
     *
     * @param sourceElement
     * @return
     */
    private String height(Element sourceElement) {
        String height = PERCENT_100;

        if (sourceElement.hasAttribute(HTML.ATTR_HEIGHT)) {
            height = sourceElement.getAttribute(HTML.ATTR_HEIGHT);
        }

        // added by estherbin fix not worked junit JBIDE1713Test
        Integer iHeight = null;

        //Added by estherbin
        //fix http://jira.jboss.com/jira/browse/JBIDE-2366
        try {
            iHeight = ComponentUtil.parseWidthHeightValue(height);
        } catch (NumberFormatException e) {
            height = PERCENT_100;
        }

        return "height: " + getValue(height, iHeight); //$NON-NLS-1$
    }

    /**
     *
     * @param sourceElement
     * @return
     */
    public String width(Element sourceElement) {
        String width = PERCENT_100;

        if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
            width = sourceElement.getAttribute(HTML.ATTR_WIDTH);
        }

        // added by estherbin fix not worked junit JBIDE1713Test
        Integer iWidth = null;

        //Added by estherbin
        //http://jira.jboss.com/jira/browse/JBIDE-2366
        try {
            iWidth = ComponentUtil.parseWidthHeightValue(width);
        } catch (NumberFormatException e) {
            width = PERCENT_100;
        }

        return "width: " + getValue(width, iWidth); //$NON-NLS-1$
    }

    private String getValue(String width, Integer iWidth) {
        String rst = Constants.EMPTY;
        if (width.equals(PERCENT_100)) {
            rst = width + Constants.SEMICOLON;
        } else {
            rst = String.valueOf(iWidth) + Constants.PIXEL + Constants.SEMICOLON;
        }

        return rst;
    }

    /**
     *
     */
    public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
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

        if (activeId == -1) {
            activeId = 0;
        }

        int count = getChildrenCount(children);

        if ((count - 1) < activeId) {
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
            if (child.getNodeName().endsWith(PANEL_BAR_ITEM)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext, Element sourceElement,
        nsIDOMDocument visualDocument, nsIDOMElement visualNode, Object data, String name, String value) {
//        if (name.equalsIgnoreCase(HTML.WIDTH_ATTR) ||
//                name.equalsIgnoreCase(HTML.ATTR_HEIGHT) ||
//                name.equalsIgnoreCase(HTML.ATTR_STYLE)) {
//            return true;
//        }
//
//        return false;
        return true;
    }
}
