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
    
    private static final String PANEL_MENU_GROUP_ATTR_DISABLED_STYLE = "disabledStyle";
    
    private static final String PANEL_MENU_GROUP_ATTR_DISABLED_CLASS = "disabledClass";
    
    private static final String PANEL_MENU_GROUP_ATTR_DISABLED = "disabled";
    
    private static final String PANEL_MENU_GROUP_ATTR_ICON_STYLE = "iconStyle";
    
    private static final String PANEL_MENU_GROUP_ATTR_ICON_CLASS = "iconClass";
    
    private static final String PANEL_MENU_GROUP_ATTR_ICON_EXPANDED = "iconExpanded";
    
    private static final String PANEL_MENU_GROUP_ATTR_ICON_DISABLED = "iconDisabled";
    
    private static final String PANEL_MENU_GROUP_ATTR_ICON_LABEL = "label";
    
    private static final String PANEL_MENU_GROUP_ATTR_EXPANDED = "expanded";
    
    private static final String COMPONENT_ATTR_VPE_SUPPORT = "vpeSupport";
    
    private static final String COMPONENT_ATTR_VPE_USER_TOGGLE_ID = "vpe-user-toggle-id";
    
    private static final String DEFAULT_PANEL_MENU_GROUP_SPACER = "/panelMenuGroup/spacer.gif";

    private static final String DEFAULT_PANEL_MENU_GROUP_POINTER = "/panelMenuGroup/pointer.gif";

    private static final String DEFAULT_PANEL_MENU_GROUP_POINT = "/panelMenuGroup/point.gif";
    
    private static final String DEFAULT_PANEL_MENU_GROUP_COLLAPSED = "/panelMenuGroup/collapsed.gif";

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

    public static VpeCreationData encode(VpePageContext pageContext, VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualElement, boolean expanded, int activeChildId) {
	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, NAME_COMPONENT);

	Element div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentVisualElement.appendChild(div);

	div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-group-div");
	div.setAttribute(COMPONENT_ATTR_VPE_SUPPORT, NAME_COMPONENT);
	div.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));
	buildTable(sourceElement, visualDocument, div, expanded, expanded, activeChildId);

	List<Node> children = ComponentUtil.getChildren(sourceElement);

	if (!children.isEmpty()) {
	    Element childSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
	    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childSpan);
	    for (Node child : children) {
		if (!child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG) && !child.getNodeName().endsWith( PANEL_MENU_ITEM_END_TAG)) {
		    if (childrenInfo.getSourceChildren() == null || childrenInfo.getSourceChildren().size() == 0) {
			div.appendChild(childSpan);
		    }
		    childrenInfo.addSourceChild(child);
		} else {
		    if (expanded) {
			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG)) {
			    RichFacesPanelMenuGroupTemplate.encode(pageContext, creationData, (Element) child, visualDocument, div, true, -1);
			} else {
			    RichFacesPanelMenuItemTemplate.encode(pageContext, creationData, (Element) child, visualDocument, div, false);
			}
		    }

		    if (childrenInfo.getSourceChildren() != null && childrenInfo.getSourceChildren().size() > 0) {
			creationData.addChildrenInfo(childrenInfo);
			childSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
			childrenInfo = new VpeChildrenInfo(childSpan);
		    }
		}
	    }
	    
	    if(childrenInfo.getSourceChildren() != null && childrenInfo.getSourceChildren().size() > 0) {
		creationData.addChildrenInfo(childrenInfo);
	    }
	}

	return creationData;
    }

    private static final void buildTable(Element sourceElement, Document visualDocument, Element div, boolean active, boolean expanded,  int activeChildId) {
	Node parent = getRichPanelParent(sourceElement);

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

	Element column2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column2);
	column2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 100%;");

	Text name = visualDocument.createTextNode(sourceElement.getAttribute("label"));
	column2.appendChild(name);
	column2.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));
	if(active) {
	    div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-selected-item");
	}
	

	Element column3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	column3.setAttribute(COMPONENT_ATTR_VPE_USER_TOGGLE_ID, String.valueOf(activeChildId));
	tableBody.appendChild(column3);

	Element img2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column3.appendChild(img2);
	img2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-pmenu-group-icon");

	if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG)) {
	    ComponentUtil.setImg(img1, DEFAULT_PANEL_MENU_GROUP_SPACER);
	    if(expanded) {
		ComponentUtil.setImg(img2, DEFAULT_PANEL_MENU_GROUP_COLLAPSED);
	    } else {
		ComponentUtil.setImg(img2, DEFAULT_PANEL_MENU_GROUP_POINTER);
	    }
	    column2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group-self-label dr-pmenu-selected-item");
	    table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-group");
	} else {
	    div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-self-div");
	    ComponentUtil.setImg(img1, DEFAULT_PANEL_MENU_GROUP_POINT);
	    img1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "vertical-align: middle");
	    ComponentUtil.setImg(img2, DEFAULT_PANEL_MENU_GROUP_SPACER);
	    column2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group rich-pmenu-group");
	    table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group-self-label rich-pmenu-group-self-label");
	}
    }

    private static final Node getRichPanelParent(Element sourceElement) {
	Node parent = sourceElement.getParentNode();

	while (true) {
	    if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG) || parent.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG)) {
		break;
	    } else {
		parent = parent.getParentNode();
	    }
	}

	return parent;
    }
}
