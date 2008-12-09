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
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Create template for rich:treeNodes element
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesTreeNodeTemplate extends VpeAbstractTemplate {

    public static final String DEFAULT_ICON_PARAM_VALUE = "DEFAULT_ICON_PARAM";

    public static final String ICON_PARAM_NAME = "richFacesTreeNodeParam";

    public static final String DEFAULT_ICON_EXPANDED_PARAM_VALUE = "DEFAULT_ICON_EXPANDED_PARAM";

    public static final String UNDEFINED_ICON = "/tree/unresolved.gif";

    public static final String NODE_ICON_LEAF_ATTR_NAME = "iconLeaf";

    public static final String ICON_NODE_WITH_LINES = "/tree/iconNodeWithLines.gif";

    public static final String ICON_NODE_WITH_LINE = "/tree/iconNodeWithLine.gif";

    public static final String ICON_EXPANDED_WITH_LINES = "/tree/iconCollapsedWithLines.gif";

    public static final String ICON_NODE_WITHOUT_LINES = "/tree/iconNode.gif";

    public static final String ICON_EXPANDED_WITHOUT_LINES = "/tree/iconCollapsed.gif";

    public static final String ICON_NOT_EXPANDED_WITH_LINES = "/tree/iconNotCollapsedWithLines.gif";

    public static final String ICON_NOT_EXPANDED_WITH_ALL_LINES = "/tree/iconNotCollapsedWithAllLines.gif";

    public static final String ICON_NOT_EXPANDED_WITHOUT_LINES = "/tree/iconNotCollapsed.gif";

    public static final String ICON_EXPANDED_ADAPTER_WITH_LINES = "/tree/iconClosedNodeWithLines.gif";

    public static final String ICON_EXPANDED_ADAPTER_WITHOUT_LINES = "/tree/iconClosedNode.gif";

    public static final String ICON_LEAF_WITH_LINES = "/tree/iconLeafWithLines.gif";

    public static final String ICON_RIGHT_LINE = "/tree/rightLine.gif";

    public static final String ICON_LEFT_LINE = "/tree/leftLine.gif";

    public static final String ICON_LINE = "/tree/line.gif";

    public static final String ICON_LEAF_WITHOUT_LINES = "/tree/iconLeaf.gif";

    private static final String TREE_TABLE_PICTURE_STYLE_CLASS_NAME = "treePictureStyle";

    private static final String STYLE_CLASS_FOR_NODE_TITLE = "treeNodeNameStyle";

    private static final String NODE_TITLE_STYLE_CLASS_ATTR_NAME = "nodeClass";

    private static final String NODE_ICON_EXPANDED_ATTR_NAME = "iconExpanded";

    private static final String NODE_ICON_ATTR_NAME = "icon";

    private static final String TREE_TABLE_ATR_CELLSPACING_VALUE = "0px";

    private static final String TREE_TABLE_ATR_CELLPADDING_VALUE = "0px";

    private static final String TREE_TABLE_ATR_BORDER_VALUE = "0px";

    private static final String TREE_NAME = "tree";

    private static final String NODE_LINES_STYLE = "background-position: center; background-repeat: repeat-y;";

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

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	nsIDOMElement visualElement = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	addBasicTreeNodeAttributes(visualElement);
	div.appendChild(visualElement);
	nsIDOMElement tbody = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TBODY);

	nsIDOMElement tableRow = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TR);
	addAttributeToTableNode((Element) sourceNode, tableRow);
	visualElement.appendChild(tbody);
	tbody.appendChild(tableRow);

	VpeCreationData vpeCreationData = new VpeCreationData(div);
	createBasicTree(pageContext, visualDocument, tableRow, sourceNode,
		vpeCreationData);
	return vpeCreationData;
    }

    /**
     * Checks for attributes for node and if such exist convert it's to html
     * atributes.
     * 
     * @param sourceNode
     * @param tableRow
     */
    private void addAttributeToTableNode(Element sourceNode,
	    nsIDOMElement tableRow) {

	String attrValue = sourceNode
		.getAttribute(NODE_TITLE_STYLE_CLASS_ATTR_NAME);
	if ((attrValue != null) && (attrValue.length() > 0)) {
	    tableRow.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, attrValue);
	}
    }

    /**
     * Used for setting images into tree nodes
     * 
     * @param pageContext
     *                page context
     * @param visualDocument
     *                visual document
     * @param sourceNode
     *                treeNode element
     * @param iconCell
     *                cell were image should be setted
     * @param nodeAttrName
     *                image attr name( icon, iconExpanded, ...)
     * @param defaultImage
     *                (image by default)
     */
    private void setAttributeForPictureNode(VpePageContext pageContext,
	    nsIDOMDocument visualDocument, Element sourceNode,
	    nsIDOMElement iconCell, String nodeAttrName, String defaultImage) {
	if (RichFacesTemplatesActivator.getDefault().isDebugging()) {
	    System.out.println("call setAttributeForPictureNode");
	}
	nsIDOMElement img = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_IMG);
	addBasicAttributesToPicture(img);
	iconCell.appendChild(img);
	// get image from treeNode
	String imgName = sourceNode.getAttribute(nodeAttrName);
	// if in tree node image doesn't exist we get image attr from tree
	if (imgName == null || imgName.length() == 0) {
	    Node parentElement = sourceNode.getParentNode();
	    if (parentElement instanceof Element) {
		imgName = ((Element) parentElement).getAttribute(nodeAttrName);
	    }
	} else {
	    img.setAttribute(ICON_PARAM_NAME, "");
	}
	// if we can't get attribute from parent we use default attribute
	addBasicAttributesToPicture(img);
	iconCell.appendChild(img);
	if (imgName == null || imgName.length() == 0) {
	    ComponentUtil.setImg(img, defaultImage);
	} else {
	    ComponentUtil.setImgFromResources(pageContext, img, imgName,
		    UNDEFINED_ICON);
	}
	if (nodeAttrName.equals(NODE_ICON_EXPANDED_ATTR_NAME)) {
	    img
		    .setAttribute(ICON_PARAM_NAME,
			    DEFAULT_ICON_EXPANDED_PARAM_VALUE);
	} else if (nodeAttrName.equals(NODE_ICON_ATTR_NAME)) {
	    img.setAttribute(ICON_PARAM_NAME, DEFAULT_ICON_PARAM_VALUE);
	} else if (nodeAttrName.equals(NODE_ICON_LEAF_ATTR_NAME)) {
	    img.setAttribute(ICON_PARAM_NAME, NODE_ICON_LEAF_ATTR_NAME);
	}
    }

    /**
     * Create simple tree node attribute.Used for creating more complex trees.
     * 
     * @param treeNodeTitle
     * @param visualDocument
     * @return tree
     */
    private void createBasicTree(VpePageContext pageContext,
	    nsIDOMDocument visualDocument, nsIDOMElement treeRow,
	    Node sourceNode, VpeCreationData vpeCreationData) {
	// creates icon node
	String backgroundLinePath = null;

	boolean showLinesValue = getShowLinesAttr((Element) sourceNode);
	nsIDOMElement iconNode = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);
	// creates icon with status of node(collapsed or not) node
	nsIDOMElement td1 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);
	// sets icon node
	if (!isLastElement(sourceNode) && isAdaptorChild(sourceNode)
		&& !isHasNextAdaptorElement(sourceNode)) {
	    backgroundLinePath = RichFacesTemplatesActivator
		    .getPluginResourcePath()
		    + ICON_LINE;
	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, td1, NODE_ICON_EXPANDED_ATTR_NAME,
		    showLinesValue == true ? ICON_EXPANDED_ADAPTER_WITH_LINES
			    : ICON_EXPANDED_ADAPTER_WITHOUT_LINES);
	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, iconNode, NODE_ICON_ATTR_NAME,
		    showLinesValue == true ? ICON_NODE_WITH_LINE
			    : ICON_NODE_WITHOUT_LINES);
	} else if (!isLastElement(sourceNode) && isAdaptorChild(sourceNode)
		&& isHasNextAdaptorElement(sourceNode)) {
	    backgroundLinePath = RichFacesTemplatesActivator
		    .getPluginResourcePath()
		    + ICON_LINE;
	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, td1, NODE_ICON_EXPANDED_ATTR_NAME,
		    showLinesValue == true ? ICON_EXPANDED_ADAPTER_WITH_LINES
			    : ICON_EXPANDED_ADAPTER_WITHOUT_LINES);
	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, iconNode, NODE_ICON_ATTR_NAME,
		    showLinesValue == true ? ICON_NODE_WITH_LINES
			    : ICON_NODE_WITHOUT_LINES);
	    if (showLinesValue) {
		String path = RichFacesTemplatesActivator
			.getPluginResourcePath()
			+ ICON_LEFT_LINE;
		iconNode.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
			"background-image: url(file://" + path + "); "
				+ NODE_LINES_STYLE);
	    }
	} else if ((isAdaptorChild(sourceNode) && isLastElement(sourceNode) && (isLastElementAfterAdaptor(sourceNode) == isAdaptorInTree(sourceNode)))
		|| (!isAdaptorChild(sourceNode) && isLastElement(sourceNode))
		|| (isAdaptorChild(sourceNode) && isOnlyOneNodeInAdaptor(sourceNode))) {

	    if (isAdaptorChild(sourceNode)
		    && isOnlyOneNodeInAdaptor(sourceNode)
		    && !isLastElementAfterAdaptor(sourceNode)
		    && isHasNextParentAdaptorElement(sourceNode)) {
		backgroundLinePath = RichFacesTemplatesActivator
			.getPluginResourcePath()
			+ ICON_LINE;
		setAttributeForPictureNode(
			pageContext,
			visualDocument,
			(Element) sourceNode,
			td1,
			NODE_ICON_EXPANDED_ATTR_NAME,
			showLinesValue == true ? ICON_NOT_EXPANDED_WITH_ALL_LINES
				: ICON_NOT_EXPANDED_WITHOUT_LINES);
	    } else {
		backgroundLinePath = RichFacesTemplatesActivator
			.getPluginResourcePath()
			+ ICON_RIGHT_LINE;
		setAttributeForPictureNode(pageContext, visualDocument,
			(Element) sourceNode, td1,
			NODE_ICON_EXPANDED_ATTR_NAME,
			showLinesValue == true ? ICON_NOT_EXPANDED_WITH_LINES
				: ICON_NOT_EXPANDED_WITHOUT_LINES);
	    }

	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, iconNode, NODE_ICON_LEAF_ATTR_NAME,
		    showLinesValue == true ? ICON_LEAF_WITH_LINES
			    : ICON_LEAF_WITHOUT_LINES);
	} else {
	    backgroundLinePath = RichFacesTemplatesActivator
		    .getPluginResourcePath()
		    + ICON_RIGHT_LINE;
	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, td1, NODE_ICON_EXPANDED_ATTR_NAME,
		    showLinesValue == true ? ICON_EXPANDED_WITH_LINES
			    : ICON_EXPANDED_WITHOUT_LINES);

	    if (showLinesValue) {
		String path = RichFacesTemplatesActivator
			.getPluginResourcePath()
			+ ICON_LEFT_LINE;
		iconNode.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
			"background-image: url(file://" + path + "); "
				+ NODE_LINES_STYLE);
	    }

	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, iconNode, NODE_ICON_ATTR_NAME,
		    showLinesValue == true ? ICON_NODE_WITH_LINES
			    : ICON_NODE_WITHOUT_LINES);
	}

	if (showLinesValue) {
	    td1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
		    "background-image: url(file://" + backgroundLinePath
			    + "); " + NODE_LINES_STYLE);
	}
	treeRow.appendChild(td1);
	treeRow.appendChild(iconNode);
	// creates Tree Node Name Message
	nsIDOMElement nodeTitle = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);
	addBasicNodeTitleAttributes(nodeTitle);
	// Create mapping to Encode body
	String treeRecursiveNodesAdaptorName = sourceNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	if (sourceNode.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
	    Element sourceElement = (Element) sourceNode;
	    String nodes = sourceElement
		    .getAttribute(RichFacesRecursiveTreeNodesAdaptorTemplate.NODES_NAME);
	    nsIDOMElement span = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_SPAN);
	    nsIDOMText text = visualDocument
		    .createTextNode((nodes == null) ? "" : nodes);
	    span.appendChild(text);
	    nodeTitle.appendChild(span);
	} else {
	    VpeChildrenInfo tdInfo = new VpeChildrenInfo(nodeTitle);

	    // Create mapping to Encode body
	    List<Node> children = ComponentUtil.getChildren(
		    (Element) sourceNode, false);
	    for (Node child : children) {
		tdInfo.addSourceChild(child);
	    }
	    vpeCreationData.addChildrenInfo(tdInfo);
	}
	treeRow.appendChild(nodeTitle);

    }

    /**
     * Sets attributes for no node title name
     * 
     * @param nodeTitle
     */
    private void addBasicNodeTitleAttributes(nsIDOMElement nodeTitle) {
	nodeTitle.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		STYLE_CLASS_FOR_NODE_TITLE);
    }

    @Override
    public void setAttribute(VpePageContext pageContext, Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
	    String name, String value) {
	/*
	 * processed only next attributes iconExpanded and icon, because tree
	 * always shows as expanded and information is it leaf or not contains
	 * in model
	 */
	if (NODE_ICON_EXPANDED_ATTR_NAME.equalsIgnoreCase(name)) {
	    nsIDOMElement expandedIconCell = (nsIDOMElement) visualNode
		    .getChildNodes().item(0).queryInterface(
			    nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMElement img = (nsIDOMElement) expandedIconCell
		    .getChildNodes().item(0).queryInterface(
			    nsIDOMElement.NS_IDOMELEMENT_IID);
	    ComponentUtil.setImgFromResources(pageContext, img, value,
		    UNDEFINED_ICON);
	    img.setAttribute(ICON_PARAM_NAME, "");
	} else if (NODE_ICON_ATTR_NAME.equals(name)
		&& !isLastElement(visualNode)) {
	    nsIDOMElement iconCell = (nsIDOMElement) visualNode.getChildNodes()
		    .item(1).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMElement img = (nsIDOMElement) iconCell.getChildNodes()
		    .item(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    ComponentUtil.setImgFromResources(pageContext, img, value,
		    UNDEFINED_ICON);
	    img.setAttribute(ICON_PARAM_NAME, "");
	} else if (NODE_ICON_LEAF_ATTR_NAME.equals(name)
		&& isLastElement(sourceElement)) {
	    nsIDOMElement iconCell = (nsIDOMElement) visualNode.getChildNodes()
		    .item(1).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMElement img = (nsIDOMElement) iconCell.getChildNodes()
		    .item(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    ComponentUtil.setImgFromResources(pageContext, img, value,
		    UNDEFINED_ICON);
	    img.setAttribute(ICON_PARAM_NAME, "");
	}
    }

    @Override
    public void removeAttribute(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMNode visualNode, Object data, String name) {
	/*
	 * processed only next attributes iconExpanded and icon, because tree
	 * always shows as expanded and information is it leaf or not contains
	 * in model
	 */

	boolean showLinesValue = getShowLinesAttr(sourceElement);
	if (NODE_ICON_EXPANDED_ATTR_NAME.equalsIgnoreCase(name)) {
	    nsIDOMElement expandedIconCell = (nsIDOMElement) visualNode
		    .getChildNodes().item(0).queryInterface(
			    nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMElement img = (nsIDOMElement) expandedIconCell
		    .getChildNodes().item(0).queryInterface(
			    nsIDOMElement.NS_IDOMELEMENT_IID);
	    String parentAttrName = ((Element) sourceElement.getParentNode())
		    .getAttribute(NODE_ICON_EXPANDED_ATTR_NAME);
	    if (parentAttrName == null || parentAttrName.length() == 0) {
		ComponentUtil.setImg(img,
			showLinesValue == true ? ICON_EXPANDED_WITH_LINES
				: ICON_EXPANDED_WITHOUT_LINES);
	    } else {
		ComponentUtil.setImgFromResources(pageContext, img,
			parentAttrName, UNDEFINED_ICON);
	    }
	    img
		    .setAttribute(ICON_PARAM_NAME,
			    DEFAULT_ICON_EXPANDED_PARAM_VALUE);
	} else if (NODE_ICON_ATTR_NAME.equalsIgnoreCase(name)
		&& !isLastElement(sourceElement)) {
	    nsIDOMElement iconCell = (nsIDOMElement) visualNode.getChildNodes()
		    .item(1).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	    nsIDOMElement img = (nsIDOMElement) iconCell.getChildNodes()
		    .item(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    String parentAttrName = ((Element) sourceElement.getParentNode())
		    .getAttribute(NODE_ICON_ATTR_NAME);
	    if (parentAttrName == null || parentAttrName.length() == 0) {
		ComponentUtil.setImg(img,
			showLinesValue == true ? ICON_NODE_WITH_LINES
				: ICON_NODE_WITHOUT_LINES);
	    } else {
		ComponentUtil.setImgFromResources(pageContext, img,
			parentAttrName, UNDEFINED_ICON);
	    }
	    img.setAttribute(ICON_PARAM_NAME, DEFAULT_ICON_PARAM_VALUE);

	} else if (NODE_ICON_LEAF_ATTR_NAME.equalsIgnoreCase(name)
		&& isLastElement(sourceElement)) {
	    nsIDOMElement iconCell = (nsIDOMElement) visualNode.getChildNodes()
		    .item(1).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMElement img = (nsIDOMElement) iconCell.getChildNodes()
		    .item(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    String parentAttrName = ((Element) sourceElement.getParentNode())
		    .getAttribute(NODE_ICON_LEAF_ATTR_NAME);
	    if (parentAttrName == null || parentAttrName.length() == 0) {
		ComponentUtil.setImg(img,
			showLinesValue == true ? ICON_LEAF_WITH_LINES
				: ICON_LEAF_WITHOUT_LINES);
	    } else {
		ComponentUtil.setImgFromResources(pageContext, img,
			parentAttrName, UNDEFINED_ICON);
	    }
	    img.setAttribute(ICON_PARAM_NAME, NODE_ICON_LEAF_ATTR_NAME);
	}
    }

    /**
     * 
     * @param parentTree
     * @param sourceNode
     * @return
     */
    private boolean isLastElement(nsIDOMNode sourceNode) {
	nsIDOMNode parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	nsIDOMNodeList childs = parentTree.getChildNodes();
	String treeNodeName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODE_NAME;
	String treeNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	nsIDOMNode lastElement = null;
	nsIDOMNode el = null;
	for (int i = 0; i < childs.getLength(); i++) {
	    el = childs.item(i);
	    if (el.getNodeName().equals(treeNodeName)
		    || el.getNodeName().equals(treeNodesAdaptorName)
		    || el.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
		lastElement = el;
	    }
	}
	return sourceNode.equals(lastElement);
    }

    /**
     * Node is Adaptor child
     * 
     * @param sourceNode
     * @return
     */
    private boolean isAdaptorChild(Node sourceNode) {
	Node parentNode = sourceNode.getParentNode();
	if (!(parentNode instanceof Element)) {
	    return true;
	}

	String treeNodesAdaptorName = sourceNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = sourceNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	if (parentNode.getNodeName().equals(treeNodesAdaptorName)
		|| parentNode.getNodeName().equals(
			treeRecursiveNodesAdaptorName)) {
	    return true;
	}
	return false;
    }

    /**
     * Node is last element
     * 
     * @param parentTree
     * @param sourceNode
     * @return
     */
    private boolean isLastElement(Node sourceNode) {
	Node parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodeName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODE_NAME;
	String treeNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	Node lastElement = null;
	Node el = null;
	for (int i = 0; i < childs.getLength(); i++) {
	    el = childs.item(i);
	    if (el.getNodeName().equals(treeNodeName)
		    || el.getNodeName().equals(treeNodesAdaptorName)
		    || el.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
		lastElement = el;
	    }
	}
	return sourceNode.equals(lastElement);
    }

    /**
     * Next element is Adaptor
     * 
     * @param sourceNode
     * @return
     */
    private boolean isHasNextAdaptorElement(Node sourceNode) {
	Node parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	Node lastElement = null;
	Node el = null;

	for (int i = 0; i < childs.getLength(); i++) {
	    el = childs.item(i);
	    if (!(el instanceof Element)) {
		continue;
	    }

	    if (lastElement != null) {
		break;
	    }
	  
	    if (sourceNode.equals(el)) {
		lastElement = el;
	    }
	}
	if (el.getNodeName().equals(treeNodesAdaptorName)
		|| el.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
	    return true;
	}
	return false;
    }

    /**
     * Adds basic attributes to tree
     * 
     * @param img
     */

    private void addBasicAttributesToPicture(nsIDOMElement img) {
	img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		TREE_TABLE_PICTURE_STYLE_CLASS_NAME);
    }

    /**
     * Set attributes for treeNode
     * 
     * @param table
     */
    private void addBasicTreeNodeAttributes(nsIDOMElement table) {
	if (table == null) {
	    return;
	}
	table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR,
		TREE_TABLE_ATR_CELLPADDING_VALUE);
	table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR,
		TREE_TABLE_ATR_CELLSPACING_VALUE);
	table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR,
		TREE_TABLE_ATR_BORDER_VALUE);
	table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		"dr-tree-full-width");
    }

    /**
     * Get showConnectingLines attribute
     * 
     * @param sourceNode
     * @return
     */
    private boolean getShowLinesAttr(Node sourceNode) {
	String treeName = sourceNode.getPrefix() + ":" + TREE_NAME;
	do {
	    sourceNode = sourceNode.getParentNode();
	    if (!(sourceNode instanceof Element)) {
		return true;
	    }
	} while (!sourceNode.getNodeName().equals(treeName));

	String showLinesParam = ((Element) sourceNode)
		.getAttribute(RichFacesTreeTemplate.SHOW_LINES_ATTR_NAME);

	boolean showLinesValue = true;
	if (showLinesParam != null && showLinesParam.equalsIgnoreCase("false")) {
	    showLinesValue = false;
	}
	return showLinesValue;
    }

    /**
     * Node has element after adaptor
     * 
     * @param sourceNode
     * @return
     */
    private boolean isLastElementAfterAdaptor(Node sourceNode) {
	Node nodeAdaptor = sourceNode.getParentNode();
	if (!(nodeAdaptor instanceof Element)) {
	    return true;
	}
	return isLastElement(nodeAdaptor);
    }

    /**
     * 
     * @param sourceNode
     * @return
     */
    private boolean isAdaptorInTree(Node sourceNode) {
	Node adaptorNode = sourceNode.getParentNode();
	if (!(adaptorNode instanceof Element)) {
	    return true;
	}
	String treeNodesAdaptorName = adaptorNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = adaptorNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	if (adaptorNode.getNodeName().equals(treeNodesAdaptorName)
		|| adaptorNode.getNodeName().equals(
			treeRecursiveNodesAdaptorName)) {
	    Node treeNode = adaptorNode.getParentNode();
	    String treeName = treeNode.getPrefix() + ":" + TREE_NAME;
	    if (treeNode.getNodeName().equals(treeName)) {
		return true;
	    }
	}
	return false;
    }

    private boolean isOnlyOneNodeInAdaptor(Node sourceNode) {
	Node parent = sourceNode.getParentNode();
	NodeList list = parent.getChildNodes();
	Node currentNode = null;
	String treeNodeName = sourceNode.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODE_NAME;
	for (int i = 0; i < list.getLength(); i++) {
	    Node el = list.item(i);
	    if (!(el instanceof Element)) {
		continue;
	    }
	    if (el.getNodeName().equals(treeNodeName)) {
		if (currentNode == null) {
		    currentNode = el;
		} else {
		    return false;
		}
	    } else {
		return false;
	    }
	}
	return true;
    }

    /**
     * Has Next element
     * 
     * @param sourceNode
     * @return
     */
    private boolean isHasNextParentAdaptorElement(Node sourceNode) {
	Node tree = sourceNode.getParentNode();
	if (!(tree instanceof Element)) {
	    return true;
	}
	Node parentTree = tree.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix() + ":"
		+ RichFacesTreeTemplate.TREE_RECURSIVE_NODES_ADAPTOR;
	Node lastElement = null;
	Node el = null;

	for (int i = 0; i < childs.getLength(); i++) {
	    el = childs.item(i);
	    if (!(el instanceof Element)) {
		continue;
	    }

	    if (lastElement != null) {
		break;
	    }
	    if (el.equals(tree)) {
		lastElement = el;
	    }
	}

	if (el.getNodeName().equals(treeNodesAdaptorName)
		|| el.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
	    return true;
	}
	return false;
    }

}
