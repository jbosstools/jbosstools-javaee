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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Create template for rich:tree element.
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesTreeTemplate extends VpeAbstractTemplate {

    /** Resurces */

    public static final String TREE_NODE_NAME = "treeNode";

    public static final String TREE_NODES_ADAPTOR = "treeNodesAdaptor";

    public static final String TREE_RECURSIVE_NODES_ADAPTOR = "recursiveTreeNodesAdaptor";

    public static final String SHOW_LINES_ATTR_NAME = "showConnectingLines";

    private static final String STYLE_PATH = "/tree/tree.css";

    private static final String ICON_ATTR = "icon";

    private static final String TREE_STYLE_CLASS_ATR_NAME = "styleClass";

    private static final String ICON_COLLAPSED_ATTR_NAME = "iconExpanded";

    /**
     * Creates a node of the visual tree on the node of the source tree. This
     * visual node should not have the parent node This visual node can have
     * child nodes.
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceNode
     *                The current node of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @return The information on the created node of the visual tree.
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	// sets css for tree on page
	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "tree");
	nsIDOMElement visualElement = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	Element sourceElement = (Element) sourceNode;
	String style = sourceElement
		.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	if (style != null) {
	    visualElement
		    .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
	}
	String styleClass = sourceElement
		.getAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
	if (styleClass != null) {
	    visualElement.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		    styleClass);
	}
	VpeCreationData vpeCreationData = new VpeCreationData(visualElement);
	parseTree(pageContext, sourceNode, visualDocument, vpeCreationData,
		visualElement);
	setStylesAttributesToTree(visualElement, (Element) sourceNode);
	return vpeCreationData;
    }

    /**
     * Sets to tree basic style attributes
     * 
     * @param tree
     * @param sourceNode
     */
    private void setStylesAttributesToTree(nsIDOMElement treeTable,
	    Element sourceNode) {
	String styleAttr = sourceNode
		.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	if (styleAttr != null && styleAttr.length() != 0) {
	    setAttributeToTree(treeTable, HtmlComponentUtil.HTML_STYLE_ATTR,
		    removeFromStyleWithAndHeight(styleAttr));
	    treeTable
		    .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, styleAttr);
	}
	String styleClassAttr = sourceNode
		.getAttribute(TREE_STYLE_CLASS_ATR_NAME);
	if ((styleClassAttr != null) && (styleClassAttr.length() != 0)) {
	    setAttributeToTree(treeTable, HtmlComponentUtil.HTML_CLASS_ATTR,
		    styleClassAttr);
	}
    }

    /**
     * 
     * @param styleArgs
     * @return
     */
    private String removeFromStyleWithAndHeight(String styleArgs) {
	StringBuffer result = new StringBuffer();
	String[] mas = styleArgs.split(";");
	for (String styleAttr : mas) {
	    if ((styleAttr.indexOf(HtmlComponentUtil.HTML_ATR_WIDTH) != -1)
		    || (styleAttr.indexOf(HtmlComponentUtil.HTML_ATR_HEIGHT) != -1)) {
		continue;
	    }
	    result.append(styleAttr + ";");
	}
	return result.toString();
    }

    /**
     * Sets to tree tables attributes
     * 
     * @param node
     * @param sourceNode
     * @param attrValue
     */
    private void setAttributeToTree(nsIDOMNode node, String attrName,
	    String attrValue) {
	try {
	    nsIDOMElement element = (nsIDOMElement) node
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    if (node.getNodeName().equalsIgnoreCase(
		    HtmlComponentUtil.HTML_TAG_TABLE)) {
		element.setAttribute(attrName, attrValue);
	    }
	    nsIDOMNodeList list2 = node.getChildNodes();
	    for (int i = 0; i < list2.getLength(); i++) {
		setAttributeToTree(list2.item(i), attrName, attrValue);
	    }
	} catch (XPCOMException exception) {
	    // Ignore 
	    return;
	}
    }

    /**
     * Is invoked after construction of all child nodes of the current visual
     * node.
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceNode
     *                The current node of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @param data
     *                Object <code>VpeCreationData</code>, built by a method
     *                <code>create</code>
     */

    @Override
    public void validate(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument, VpeCreationData data) {
	super.validate(pageContext, sourceNode, visualDocument, data);
	revertTableRows(data.getNode());
    }

    /**
     * Revert tree elements in right order.
     * 
     * @param node
     */
    private void revertTableRows(nsIDOMNode node) {

	try {
	    nsIDOMNodeList list = node.getChildNodes();
	    nsIDOMElement element = (nsIDOMElement) node
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    String id = element
		    .getAttribute(RichFacesTreeNodesAdaptorTemplate.ID_ATTR_NAME);
	    if (id == null)
		id = "";
	    if (node.getNodeName().equalsIgnoreCase(
		    HtmlComponentUtil.HTML_TAG_DIV)
		    && list.getLength() == 2
		    && !(id
			    .equalsIgnoreCase(RichFacesTreeNodesAdaptorTemplate.TREE_NODES_ADAPTOR_NAME) || id
			    .equalsIgnoreCase(RichFacesTreeNodesAdaptorTemplate.RECURSIVE_TREE_NODES_ADAPTOR_NAME))) {
		nsIDOMNode table1 = list.item(0);
		nsIDOMNode table2 = list.item(1);
		node.removeChild(table1);
		node.removeChild(table2);
		node.appendChild(table2);
		node.appendChild(table1);
	    }
	    nsIDOMNodeList list2 = node.getChildNodes();
	    for (int i = 0; i < list2.getLength(); i++) {
		revertTableRows(list2.item(i));
	    }
	} catch (XPCOMException e) {
	    //Ignore
	    return;
	}
    }

    /**
     * 
     * Function for parsing tree by tree nodes;
     * 
     * @param pageContext
     * @param sourceNode
     * @param visualDocument
     * @return
     */
    public void parseTree(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument, VpeCreationData vpeCreationData,
	    nsIDOMElement parentDiv) {
	NodeList nodeList = sourceNode.getChildNodes();
	Element element = null;
	nsIDOMElement div = null;
	nsIDOMElement childTree = null;
	nsIDOMElement childLast = null;
	int lenght = nodeList.getLength();
	String treeNodeName = sourceNode.getPrefix() + ":" + TREE_NODE_NAME;
	String treeNodesAdaptorName = sourceNode.getPrefix() + ":"
		+ TREE_NODES_ADAPTOR;
	String recursiveTreeNodesAdaptorName = sourceNode.getPrefix() + ":"
		+ TREE_RECURSIVE_NODES_ADAPTOR;
	VpeChildrenInfo vpeChildrenInfo = null;
	for (int i = 0; i < lenght; i++) {
	    if (!(nodeList.item(i) instanceof Element)) {
		continue;
	    }
	    element = (Element) nodeList.item(i);
	    childTree = null;
	    if (element.getNodeName().equals(treeNodeName)
		    || element.getNodeName().equals(treeNodesAdaptorName)
		    || element.getNodeName().equals(
			    recursiveTreeNodesAdaptorName)) {
		if (div == null) {

		    vpeChildrenInfo = new VpeChildrenInfo(parentDiv);
		    vpeCreationData.addChildrenInfo(vpeChildrenInfo);
		    vpeChildrenInfo.addSourceChild(element);
		    div = createBasicTree(visualDocument);
		    childLast = parentDiv;
		    continue;
		} else if (childTree == null) {

		    vpeChildrenInfo = new VpeChildrenInfo(div);
		    vpeCreationData.addChildrenInfo(vpeChildrenInfo);
		    vpeChildrenInfo.addSourceChild(element);
		    childLast.appendChild(div);
		    childLast = div;
		    div = createBasicTree(visualDocument);
		    continue;
		}
	    }
	}
    }

    @Override
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	if (ICON_COLLAPSED_ATTR_NAME.equals(name)
		|| SHOW_LINES_ATTR_NAME.equals(name)
		|| ICON_ATTR.equals(name)
		|| RichFacesTreeNodeTemplate.NODE_ICON_LEAF_ATTR_NAME
			.equals(name)) {
	    return true;
	}
	return false;
    }

    @Override
    public void setAttribute(VpePageContext pageContext, Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
	    String name, String value) {
	if (TREE_STYLE_CLASS_ATR_NAME.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HtmlComponentUtil.HTML_CLASS_ATTR,
		    value);
	} else if (HtmlComponentUtil.HTML_STYLE_ATTR.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HtmlComponentUtil.HTML_STYLE_ATTR,
		    removeFromStyleWithAndHeight(value));
	    nsIDOMElement visualElement = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    visualElement.setAttribute(
		    HtmlComponentUtil.HTML_STYLE_ATTR, value);
	}
    }

    @Override
    public void removeAttribute(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMNode visualNode, Object data, String name) {
	if (TREE_STYLE_CLASS_ATR_NAME.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HtmlComponentUtil.HTML_CLASS_ATTR,
		    "");
	} else if (HtmlComponentUtil.HTML_STYLE_ATTR.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HtmlComponentUtil.HTML_STYLE_ATTR,
		    "");
	}
    }

    /**
     * Create simple tree node attribute.Used for creating more complex trees.
     * 
     * @param treeNodeTitle
     * @param visualDocument
     * @return tree
     */
    private nsIDOMElement createBasicTree(nsIDOMDocument visualDocument) {

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-tree-h-ic-div");
	return div;
    }

}
