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

    private static final String DEFAULT_PANEL_MENU_GROUP_SPACER = "/panelMenuGroup/spacer.gif";

    private static final String DEFAULT_PANEL_MENU_GROUP_POINTER = "/panelMenuGroup/pointer.gif";

    private static final String DEFAULT_PANEL_MENU_GROUP_POINT = "/panelMenuGroup/point.gif";
    
    private static final String STYLE_PATH = "/panelMenuGroup/style.css";
    
    private static final String ICON_WIDTH = "16";
    
    private static final String ICON_HEIGHT = "16";
    
    private static final String ICON_VSPACE = "0";
    
    private static final String ICON_HSPACE = "0";
    
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
	return new VpeCreationData(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV));
    }

    public static VpeCreationData encode(VpePageContext pageContext, VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualElement, boolean expanded) {
	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "panelMenuGroup");

	Element div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	parentVisualElement.appendChild(div);
	
	div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-group-div");
	div.setAttribute("vpeSupport", "panelMenuGroup");
	buildTable(sourceElement, visualDocument, div);
	
	List<Node> children = ComponentUtil.getChildren(sourceElement);

	if (!children.isEmpty()) {
	    VpeChildrenInfo childInfo = new VpeChildrenInfo(div);
	    for (Node child : children) {
		if(!expanded && !child.getNodeName().endsWith(":panelMenuGroup") && !child.getNodeName().endsWith(":panelMenuItem")) {
		    childInfo.addSourceChild(child);
		} else {
		    if(child.getNodeName().endsWith(":panelMenuGroup")) {
			RichFacesPanelMenuGroupTemplate.encode(pageContext, creationData, (Element)child, visualDocument, div, false);
		    }
		    if(child.getNodeName().endsWith(":panelMenuItem")){
			RichFacesPanelMenuItemTemplate.encode(creationData, (Element)child, visualDocument, div, false);
		    }
		    childInfo.addSourceChild(child);
		}
	    }
	    creationData.addChildrenInfo(childInfo);
	}

	return creationData;
    }

    private static final void buildTable(Element sourceElement, Document visualDocument, Element div) {
	Node parent = getRichPanelParent(sourceElement);

	Element table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	div.appendChild(table);
	table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
	table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
	table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");

	Element tableBody = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
	table.appendChild(tableBody);

	Element column1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column1);

	Element img1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column1.appendChild(img1);
	img1.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, ICON_WIDTH);
	img1.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ICON_HEIGHT);
	img1.setAttribute("vspace", ICON_VSPACE);
	img1.setAttribute("hspace", ICON_HSPACE);

	Element column2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column2);
	column2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 100%;");

	Text name = visualDocument.createTextNode(sourceElement.getAttribute("label"));
	column2.appendChild(name);

	Element column3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column3);

	Element img2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column3.appendChild(img2);
	img2.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, ICON_WIDTH);
	img2.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ICON_WIDTH);
	img2.setAttribute("vspace", ICON_VSPACE);
	img2.setAttribute("hspace", ICON_HSPACE);

	if (parent.getNodeName().endsWith(":panelMenu")) {
	    ComponentUtil.setImg(img1, DEFAULT_PANEL_MENU_GROUP_SPACER);
	    ComponentUtil.setImg(img2,  DEFAULT_PANEL_MENU_GROUP_POINTER);
	    column2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group-self-label dr-pmenu-selected-item");
	    table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-top-group");
	} else {
	    ComponentUtil.setImg(img1,  DEFAULT_PANEL_MENU_GROUP_POINT);
	    img1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "vertical-align: middle");
	    ComponentUtil.setImg(img2,  DEFAULT_PANEL_MENU_GROUP_SPACER);
	    column2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group rich-pmenu-group");
	    table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-pmenu-group-self-label rich-pmenu-group-self-label");
	}
    }

    private static final Node getRichPanelParent(Element sourceElement) {
	Node parent = sourceElement.getParentNode();

	while (true) {
	    if (parent.getNodeName().endsWith(":panelMenu") || parent.getNodeName().endsWith(":panelMenuGroup")) {
		break;
	    } else {
		parent = parent.getParentNode();
	    }
	}
	
	return parent;
    }
}
