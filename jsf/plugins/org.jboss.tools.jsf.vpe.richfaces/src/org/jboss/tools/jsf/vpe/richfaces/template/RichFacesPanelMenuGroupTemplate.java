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

    private static String PANEL_MENU_GROUP_SPACER = "/panelMenuGroup/spacer.gif";

    private static String PANEL_MENU_GROUP_POINTER = "/panelMenuGroup/pointer.gif";

    private static String PANEL_MENU_GROUP_POINT = "/panelMenuGroup/point.gif";

    private static String STYLE_PATH = "/panelMenuGroup/style.css";

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "panelMenuGroup");

	Element div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	VpeCreationData creationData = new VpeCreationData(div);

	if (checkPanelMenuParent(sourceNode)) {
	    div.setAttribute("class", "dr-pmenu-top-group-div");
	    div.setAttribute("vpeSupport", "panelMenuGroup");

	    buildTable(sourceNode, visualDocument, div);

	    Element sourceElement = (Element) sourceNode;

	    List<Node> children = ComponentUtil.getChildren(sourceElement);

	    if (!children.isEmpty()) {
		VpeChildrenInfo childInfo = new VpeChildrenInfo(div);
		for (Node child : children) {
		    childInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(childInfo);
	    }
	} else {
	    div.setAttribute("style", "display:none;");
	}

	return creationData;
    }
    
    public static VpeCreationData encode(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualElement, boolean active) {
	return null;
    }

    private void buildTable(Node sourceNode, Document visualDocument, Element div) {
	Node parent = getRichPanelParent(sourceNode);

	Element table = visualDocument.createElement("table");
	div.appendChild(table);
	table.setAttribute("cellspacing", "0");
	table.setAttribute("cellpadding", "0");
	table.setAttribute("border", "0");

	Element tableBody = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
	table.appendChild(tableBody);

	Element column1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column1);

	Element img1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column1.appendChild(img1);
	img1.setAttribute("width", "16");
	img1.setAttribute("vspace", "0");
	img1.setAttribute("hspace", "0");
	img1.setAttribute("height", "16");

	Element column2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column2);
	column2.setAttribute("style", "width: 100%;");

	Text name = visualDocument.createTextNode(sourceNode.getAttributes().getNamedItem("label").getNodeValue());
	column2.appendChild(name);

	Element column3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
	tableBody.appendChild(column3);

	Element img2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	column3.appendChild(img2);
	img2.setAttribute("width", "16");
	img2.setAttribute("vspace", "0");
	img2.setAttribute("hspace", "0");
	img2.setAttribute("height", "16");

	if (parent.getNodeName().endsWith(":panelMenu")) {
	    ComponentUtil.setImg(img1, PANEL_MENU_GROUP_SPACER);
	    ComponentUtil.setImg(img2, PANEL_MENU_GROUP_POINTER);
	    column2.setAttribute("class", "dr-pmenu-group-self-label dr-pmenu-selected-item");
	    table.setAttribute("class", "dr-pmenu-top-group");
	} else {
	    div.setAttribute("style", "display:none;");
	    ComponentUtil.setImg(img1, PANEL_MENU_GROUP_POINT);
	    img1.setAttribute("style", "vertical-align: middle");
	    ComponentUtil.setImg(img2, PANEL_MENU_GROUP_SPACER);
	    column2.setAttribute("class", "dr-pmenu-group rich-pmenu-group");
	    table.setAttribute("class", "dr-pmenu-group-self-label rich-pmenu-group-self-label");
	}
    }

    private boolean checkPanelMenuParent(Node checkNode) {
	boolean result = false;

	Node parent = checkNode.getParentNode();

	while (parent != null) {
	    if (parent.getNodeName().endsWith(":panelMenu")) {
		result = true;
		break;
	    }

	    parent = parent.getParentNode();
	}

	return result;
    }

    private Node getRichPanelParent(Node sourceNode) {
	Node parent = sourceNode.getParentNode();

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
