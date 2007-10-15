/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class RichFacesPanelMenuGroupTemplate extends VpeAbstractTemplate {

    private static final String NAME_COMPONENT = "panelMenuGroup";

    private static final String PANEL_MENU_END_TAG = ":panelMenu";

    private static final String PANEL_MENU_GROUP_END_TAG = ":panelMenuGroup";

    private static final String PANEL_MENU_ITEM_END_TAG = ":panelMenuItem";

    // *******************************************************************************
    // * Panel menu attribytes.
    // ******************************************************************************/
    private static final String PANEL_MENU_ATTR_ICON_GROUP_POSITION = "iconGroupPosition";

    private static final String PANEL_MENU_ATTR_ICON_TOP_GROUP_POSITION = "iconGroupTopPosition";

    private static final String PANEL_MENU_ATTR_ICON_COLLAPSED_GROUP = "iconCollapsedGroup";

    private static final String PANEL_MENU_ATTR_ICON_COLLAPSED_TOP_GROUP = "iconCollapsedTopGroup";

    private static final String PANEL_MENU_ATTR_ICON_EXPANDED_GROUP = "iconExpandedGroup";

    private static final String PANEL_MENU_ATTR_ICON_EXPANDED_TOP_GROUP = "iconExpandedTopGroup";

    private static final String PANEL_MENU_ATTR_ICON_DISABLE_GROUP = "iconDisableGroup";

    private static final String PANEL_MENU_ATTR_ICON_TOP_DISABLE_GROUP = "iconTopDisableGroup";

    private static final String PANEL_MENU_ATTR_DISABLED_GROUP_CLASS = "disabledGroupClass";

    private static final String PANEL_MENU_ATTR_DISABLED_GROUP_STYLE = "disabledGroupStyle";

    private static final String PANEL_MENU_ATTR_TOP_GROUP_CLASS = "topGroupClass";

    private static final String PANEL_MENU_ATTR_GROUP_CLASS = "groupClass";

    private static final String PANEL_MENU_ATTR_TOP_GROUP_STYLE = "topGroupStyle";

    private static final String PANEL_MENU_ATTR_GROUP_STYLE = "groupStyle";

    // *******************************************************************************
    // * Panel menu group attribytes.
    // ******************************************************************************/
    private static final String PANEL_MENU_GROUP_ATTR_DISABLED_STYLE = "disabledStyle";

    private static final String PANEL_MENU_GROUP_ATTR_DISABLED_CLASS = "disabledClass";

    private static final String PANEL_MENU_GROUP_ATTR_DISABLED = "disabled";

    private static final String PANEL_MENU_GROUP_ATTR_ICON_EXPANDED = "iconExpanded";

    private static final String PANEL_MENU_GROUP_ATTR_ICON_COLLAPSED = "iconCollapsed";

    private static final String PANEL_MENU_GROUP_ATTR_ICON_DISABLED = "iconDisabled";

    private static final String PANEL_MENU_GROUP_ATTR_ICON_LABEL = "label";

    // private static final String PANEL_MENU_GROUP_ATTR_EXPANDED = "expanded";

    private static final String COMPONENT_ATTR_VPE_SUPPORT = "vpeSupport";

    private static final String COMPONENT_ATTR_VPE_USER_TOGGLE_ID = "vpe-user-toggle-id";

    private static final String DEFAULT_PANEL_MENU_GROUP_SPACER = "/panelMenuGroup/spacer.gif";

    private static final String STYLE_PATH = "/panelMenuGroup/style.css";

    private static final String EMPTY_DIV_STYLE = "display: none;";

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
	Element div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EMPTY_DIV_STYLE);
	return new VpeCreationData(div);
    }

    public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
	return true;
    }

    public static VpeCreationData encode(VpePageContext pageContext, VpeCreationData creationData, Element sourceParentElement, Element sourceElement, Document visualDocument,
	    Element parentVisualElement, boolean expanded, int activeChildId) {
	boolean disabled = false;
	Element parent = getRichPanelParent(sourceElement);

	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, NAME_COMPONENT);

	if (expanded == true) {
	    activeChildId = -1;
	}

	Element div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentVisualElement.appendChild(div);
	div.setAttribute(COMPONENT_ATTR_VPE_SUPPORT, NAME_COMPONENT);
	div.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));

	if ("true".equalsIgnoreCase(sourceParentElement.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED))) {
	    disabled = true;
	} else if ("true".equalsIgnoreCase(parent.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED))) {
	    disabled = true;
	} else if ("true".equalsIgnoreCase(sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED))) {
	    disabled = true;
	}

	buildTable(pageContext, sourceParentElement, parent, sourceElement, visualDocument, div, expanded, disabled, activeChildId);

	List<Node> children = ComponentUtil.getChildren(sourceElement);

	if (!children.isEmpty()) {
	    Element childSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
	    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childSpan);
	    for (Node child : children) {
		if (!child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG) && !child.getNodeName().endsWith(PANEL_MENU_ITEM_END_TAG)) {
		    if (childrenInfo.getSourceChildren() == null || childrenInfo.getSourceChildren().size() == 0) {
			div.appendChild(childSpan);
		    }
		    childrenInfo.addSourceChild(child);
		} else {
		    if (expanded && !disabled) {
			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG)) {
			    RichFacesPanelMenuGroupTemplate.encode(pageContext, creationData, sourceParentElement, (Element) child, visualDocument, div, true, -1);
			} else {
			    RichFacesPanelMenuItemTemplate.encode(pageContext, creationData, sourceParentElement, (Element) child, visualDocument, div, false);
			}
		    }

		    if (childrenInfo.getSourceChildren() != null && childrenInfo.getSourceChildren().size() > 0) {
			creationData.addChildrenInfo(childrenInfo);
			childSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
			childrenInfo = new VpeChildrenInfo(childSpan);
		    }
		}
	    }

	    if (childrenInfo.getSourceChildren() != null && childrenInfo.getSourceChildren().size() > 0) {
		creationData.addChildrenInfo(childrenInfo);
	    }
	}

	return creationData;
    }

    private static final void buildTable(VpePageContext pageContext, Element sourceParentElement, Element parent, Element sourceElement, Document visualDocument, Element div, boolean expanded,
	    boolean disabled, int activeChildId) {
	
	String disabledStyle = sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED_STYLE);
	String disableClass = null;
	String style = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	String styleClass = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
	
	if(disabledStyle == null) {
	    disabledStyle = sourceParentElement.getAttribute(PANEL_MENU_ATTR_DISABLED_GROUP_STYLE);
	}
	
	if(sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED_CLASS) != null) {
	    disableClass = sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_DISABLED_CLASS);
	}else if(sourceParentElement.getAttribute(PANEL_MENU_ATTR_DISABLED_GROUP_CLASS) != null) {
	    disableClass = sourceParentElement.getAttribute(PANEL_MENU_ATTR_DISABLED_GROUP_CLASS);
	} else {
	    disableClass = "rich-pmenu-group-disabled";
	}

	Element table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	div.appendChild(table);
	table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
	table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
	table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");

	Element tableBody = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
	table.appendChild(tableBody);

	Element column1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	column1.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));
	tableBody.appendChild(column1);

	Element img1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column1.appendChild(img1);
	img1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-pmenu-group-icon");
	ComponentUtil.setImg(img1, DEFAULT_PANEL_MENU_GROUP_SPACER);

	Element column2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column2);
	column2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 100%;");

	Text name = visualDocument.createTextNode(sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_ICON_LABEL));
	column2.appendChild(name);
	column2.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));

	Element column3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	column3.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));
	tableBody.appendChild(column3);

	Element img2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column3.appendChild(img2);
	img2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-pmenu-group-icon");
	ComponentUtil.setImg(img2, DEFAULT_PANEL_MENU_GROUP_SPACER);

	setIcon(pageContext, parent, sourceParentElement, sourceElement, img1, img2, expanded, disabled);
	
	if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG)) {
	    if(styleClass != null && sourceParentElement.getAttribute(PANEL_MENU_ATTR_TOP_GROUP_CLASS) != null) {
		styleClass = "dr-pmenu-group-self-label dr-pmenu-top-group" + " " + sourceParentElement.getAttribute(PANEL_MENU_ATTR_TOP_GROUP_CLASS);
	    }else {
		styleClass = "dr-pmenu-group-self-label dr-pmenu-top-group";
	    }
	    if(style != null && sourceParentElement.getAttribute(PANEL_MENU_ATTR_TOP_GROUP_STYLE) != null) {
		style = sourceParentElement.getAttribute(PANEL_MENU_ATTR_TOP_GROUP_STYLE);
	    }else {
		style = "";
	    }
	    div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-group-div");
	} else {
	    if(styleClass != null && sourceParentElement.getAttribute(PANEL_MENU_ATTR_GROUP_CLASS) != null) {
		styleClass = "dr-pmenu-group-self-label rich-pmenu-group-self-label" + " " + sourceParentElement.getAttribute(PANEL_MENU_ATTR_GROUP_CLASS);
	    }else {
		styleClass = "dr-pmenu-group-self-label rich-pmenu-group-self-label";
	    }
	    if(style != null && sourceParentElement.getAttribute(PANEL_MENU_ATTR_GROUP_STYLE) != null) {
		style = sourceParentElement.getAttribute(PANEL_MENU_ATTR_GROUP_STYLE);
	    }else {
		style = "";
	    }
	    div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-self-div");
	}
	
	if(disabled) {
	    styleClass = styleClass + " " + disableClass;
	    
	    if(disabledStyle != null) {
		style = style + " " + disabledStyle;
	    }
	}
	
	if(!"".equals(style)) {
	    table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
	}
	table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
    }

    private static final Element getRichPanelParent(Element sourceElement) {
	Element parent = (Element) sourceElement.getParentNode();

	while (true) {
	    if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG) || parent.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG)) {
		break;
	    } else {
		parent = (Element) parent.getParentNode();
	    }
	}

	return parent;
    }

    private static final void setIcon(VpePageContext pageContext, Node parent, Element sourceParentElement, Element sourceElement, Element img1, Element img2, boolean expanded, boolean disabled) {
	boolean needChangePosition = false;
	String pathIconExpanded = sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_ICON_EXPANDED);
	String pathIconCollapsed = sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_ICON_COLLAPSED);
	String pathIconDisabled = sourceElement.getAttribute(PANEL_MENU_GROUP_ATTR_ICON_DISABLED);

	if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG)) {
	    if (pathIconExpanded == null) {
		pathIconExpanded = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_EXPANDED_TOP_GROUP);
	    }
	    if (pathIconCollapsed == null) {
		pathIconCollapsed = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_COLLAPSED_TOP_GROUP);
	    }
	    if (pathIconDisabled == null) {
		pathIconDisabled = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_TOP_DISABLE_GROUP);
	    }

	    if ("right".equalsIgnoreCase(sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_TOP_GROUP_POSITION))) {
		needChangePosition = true;
	    }
	} else {
	    if (pathIconExpanded == null) {
		pathIconExpanded = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_EXPANDED_GROUP);
	    }
	    if (pathIconCollapsed == null) {
		pathIconCollapsed = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_COLLAPSED_GROUP);
	    }
	    if (pathIconDisabled == null) {
		pathIconDisabled = sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_DISABLE_GROUP);
	    }

	    if ("right".equalsIgnoreCase(sourceParentElement.getAttribute(PANEL_MENU_ATTR_ICON_GROUP_POSITION))) {
		needChangePosition = true;
	    }
	}

	if (needChangePosition) {
	    Element temp = img2;
	    img2 = img1;
	    img1 = temp;
	}

	if (disabled) {
	    ComponentUtil.setImgFromResources(pageContext, img1, pathIconDisabled, DEFAULT_PANEL_MENU_GROUP_SPACER);
	} else {
	    if (expanded) {
		ComponentUtil.setImgFromResources(pageContext, img1, pathIconExpanded, DEFAULT_PANEL_MENU_GROUP_SPACER);
	    } else {
		ComponentUtil.setImgFromResources(pageContext, img1, pathIconCollapsed, DEFAULT_PANEL_MENU_GROUP_SPACER);
	    }
	}
    }
}
