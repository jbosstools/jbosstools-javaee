/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class RichFacesAbstractTreeTemplate extends VpeAbstractTemplate {

    public static final String TREE = "tree"; //$NON-NLS-1$
    public static final String TREE_NODE = "treeNode"; //$NON-NLS-1$
    public static final String TREE_NODES_ADAPTOR = "treeNodesAdaptor"; //$NON-NLS-1$
    public static final String RECURSIVE_TREE_NODES_ADAPTOR = "recursiveTreeNodesAdaptor"; //$NON-NLS-1$
    public static final String SHOW_LINES_ATTR_NAME = "showConnectingLines"; //$NON-NLS-1$

    public static final String DEFAULT_ICON_PARAM_VALUE = "DEFAULT_ICON_PARAM"; //$NON-NLS-1$
    public static final String ICON_PARAM_NAME = "richFacesTreeNodeParam"; //$NON-NLS-1$
    public static final String DEFAULT_ICON_EXPANDED_PARAM_VALUE = "DEFAULT_ICON_EXPANDED_PARAM"; //$NON-NLS-1$
    public static final String UNDEFINED_ICON = "/tree/unresolved.gif"; //$NON-NLS-1$
    public static final String NODE_ICON_LEAF_ATTR_NAME = "iconLeaf"; //$NON-NLS-1$
    public static final String ICON_NODE_WITH_LINES = "/tree/iconNodeWithLines.gif"; //$NON-NLS-1$
    public static final String ICON_NODE_WITH_LINE = "/tree/iconNodeWithLine.gif"; //$NON-NLS-1$
    public static final String ICON_EXPANDED_WITH_LINES = "/tree/iconCollapsedWithLines.gif"; //$NON-NLS-1$
    public static final String ICON_NODE_WITHOUT_LINES = "/tree/iconNode.gif"; //$NON-NLS-1$
    public static final String ICON_EXPANDED_WITHOUT_LINES = "/tree/iconCollapsed.gif"; //$NON-NLS-1$
    public static final String ICON_NOT_EXPANDED_WITH_LINES = "/tree/iconNotCollapsedWithLines.gif"; //$NON-NLS-1$
    public static final String ICON_NOT_EXPANDED_WITH_ALL_LINES = "/tree/iconNotCollapsedWithAllLines.gif"; //$NON-NLS-1$
    public static final String ICON_NOT_EXPANDED_WITHOUT_LINES = "/tree/iconNotCollapsed.gif"; //$NON-NLS-1$
    public static final String ICON_EXPANDED_ADAPTER_WITH_LINES = "/tree/iconClosedNodeWithLines.gif"; //$NON-NLS-1$
    public static final String ICON_EXPANDED_ADAPTER_WITHOUT_LINES = "/tree/iconClosedNode.gif"; //$NON-NLS-1$
    public static final String ICON_LEAF_WITH_LINES = "/tree/iconLeafWithLines.gif"; //$NON-NLS-1$
    public static final String ICON_RIGHT_LINE = "/tree/rightLine.gif"; //$NON-NLS-1$
    public static final String ICON_LEFT_LINE = "/tree/leftLine.gif"; //$NON-NLS-1$
    public static final String ICON_LINE = "/tree/line.gif"; //$NON-NLS-1$
    public static final String ICON_LEAF_WITHOUT_LINES = "/tree/iconLeaf.gif"; //$NON-NLS-1$
    protected static final String TREE_TABLE_PICTURE_STYLE_CLASS_NAME = "treePictureStyle"; //$NON-NLS-1$
    protected static final String STYLE_CLASS_FOR_NODE_TITLE = "treeNodeNameStyle"; //$NON-NLS-1$
    protected static final String NODE_TITLE_STYLE_CLASS_ATTR_NAME = "nodeClass"; //$NON-NLS-1$
    protected static final String NODE_ICON_EXPANDED_ATTR_NAME = "iconExpanded"; //$NON-NLS-1$
    protected static final String NODE_ICON_ATTR_NAME = "icon"; //$NON-NLS-1$
    protected static final String TREE_TABLE_ATR_CELLSPACING_VALUE = "0px"; //$NON-NLS-1$
    protected static final String TREE_TABLE_ATR_CELLPADDING_VALUE = "0px"; //$NON-NLS-1$
    protected static final String TREE_TABLE_ATR_BORDER_VALUE = "0px"; //$NON-NLS-1$
    protected static final String NODE_LINES_STYLE = "background-position: center; background-repeat: repeat-y;"; //$NON-NLS-1$

    /**
     * Get showConnectingLines attribute
     * 
     * @param sourceNode
     * @return
     */
    protected boolean getShowLinesAttr(Node sourceNode) {
	String treeName = sourceNode.getPrefix() + Constants.COLON + TREE;
	do {
	    sourceNode = sourceNode.getParentNode();
	    if (!(sourceNode instanceof Element)) {
		return true;
	    }
	} while (!sourceNode.getNodeName().equals(treeName));

	String showLinesParam = ((Element) sourceNode)
		.getAttribute(SHOW_LINES_ATTR_NAME);

	boolean showLinesValue = true;
	if (showLinesParam != null
		&& Constants.FALSE.equalsIgnoreCase(showLinesParam)) {
	    showLinesValue = false;
	}
	return showLinesValue;
    }

    /**
     * Is adapter between treeNodes
     * 
     * @param sourceNode
     * @return
     */
    protected boolean isAdapterBetweenNodes(Node sourceNode) {
	Node parentNode = sourceNode.getParentNode();
	NodeList childs = parentNode.getChildNodes();
	Node beforeAdapterNode = null;
	Node afterAdapterNode = null;
	Node adapterNode = null;
	String treeNodeName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODE;
	for (int i = 0; i < childs.getLength(); i++) {
	    Node el = childs.item(i);
	    if (!(el instanceof Element)) {
		continue;
	    }
	    if (el.equals(sourceNode)) {
		adapterNode = el;
	    } else {
		if (el.getNodeName().equals(treeNodeName)) {
		    if (adapterNode == null) {
			beforeAdapterNode = el;
		    } else {
			afterAdapterNode = el;
		    }
		}

	    }

	}

	if (beforeAdapterNode != null && afterAdapterNode != null) {
	    return true;
	}
	return false;
    }

    /**
     * Next element is Adaptor
     * 
     * @param sourceNode
     * @return
     */
    protected boolean isHasNextAdaptorElement(Node sourceNode) {
	Node parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodesAdaptorName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
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
     * Has Next element
     * 
     * @param sourceNode
     * @return
     */
    protected boolean isHasNextParentAdaptorElement(Node sourceNode) {
	Node tree = sourceNode.getParentNode();
	if (!(tree instanceof Element)) {
	    return true;
	}
	Node parentTree = tree.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodeName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODE;
	String treeNodesAdaptorName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
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

	if (el.getNodeName().equals(treeNodeName)
		|| el.getNodeName().equals(treeNodesAdaptorName)
		|| el.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @param sourceNode
     * @return
     */
    protected boolean isHasParentAdapter(Node sourceNode) {
	String treeNodesAdaptorName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String recursiveTreeNodesAdaptorName = sourceNode.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
	Node node = sourceNode.getParentNode();
	if (node.getNodeName().equals(treeNodesAdaptorName)
		|| node.getNodeName().equals(recursiveTreeNodesAdaptorName)) {
	    return true;
	}
	return false;
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
    protected void parseTree(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument, VpeCreationData vpeCreationData,
	    nsIDOMElement parentElement) {
	NodeList nodeList = sourceNode.getChildNodes();
	Element element = null;
	int lenght = nodeList.getLength();
	String treeNodeName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODE;
	String treeNodesAdaptorName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String recursiveTreeNodesAdaptorName = sourceNode.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
	VpeChildrenInfo vpeChildrenInfo = null;
	for (int i = 0; i < lenght; i++) {
	    if (!(nodeList.item(i) instanceof Element)) {
		continue;
	    }
	    element = (Element) nodeList.item(i);
	    if (element.getNodeName().equals(treeNodeName)
		    || element.getNodeName().equals(
			    recursiveTreeNodesAdaptorName)) {
		vpeChildrenInfo = new VpeChildrenInfo(parentElement);
		vpeCreationData.addChildrenInfo(vpeChildrenInfo);
		vpeChildrenInfo.addSourceChild(element);
	    } else if (element.getNodeName().equals(treeNodesAdaptorName)) {
		vpeChildrenInfo = new VpeChildrenInfo(parentElement);
		vpeCreationData.addChildrenInfo(vpeChildrenInfo);
		vpeChildrenInfo.addSourceChild(element);
	    }
	}
    }

    /**
     * Create simple tree node attribute.Used for creating more complex trees.
     * 
     * @param treeNodeTitle
     * @param visualDocument
     * @return tree
     */
    protected void createBasicTree(VpePageContext pageContext,
	    nsIDOMDocument visualDocument, nsIDOMElement treeRow,
	    Node sourceNode, VpeCreationData vpeCreationData) {
	// creates icon node
	String backgroundLinePath = null;

	boolean showLinesValue = getShowLinesAttr((Element) sourceNode);
	nsIDOMElement iconNode = visualDocument.createElement(HTML.TAG_TD);
	// creates icon with status of node(collapsed or not) node
	nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD);
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
		iconNode.setAttribute(HTML.ATTR_STYLE,
			"background-image: url(file://" + path + "); " //$NON-NLS-1$ //$NON-NLS-2$
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
		iconNode.setAttribute(HTML.ATTR_STYLE,
			"background-image: url(file://" + path + "); " //$NON-NLS-1$ //$NON-NLS-2$
				+ NODE_LINES_STYLE);
	    }

	    setAttributeForPictureNode(pageContext, visualDocument,
		    (Element) sourceNode, iconNode, NODE_ICON_ATTR_NAME,
		    showLinesValue == true ? ICON_NODE_WITH_LINES
			    : ICON_NODE_WITHOUT_LINES);
	}

	if (showLinesValue) {
	    td1.setAttribute(HTML.ATTR_STYLE,
		    "background-image: url(file://" + backgroundLinePath //$NON-NLS-1$
			    + "); " + NODE_LINES_STYLE); //$NON-NLS-1$
	}
	treeRow.appendChild(td1);
	treeRow.appendChild(iconNode);
	// creates Tree Node Name Message
	nsIDOMElement nodeTitle = visualDocument.createElement(HTML.TAG_TD);
	addBasicNodeTitleAttributes(nodeTitle);
	// Create mapping to Encode body
	String treeRecursiveNodesAdaptorName = sourceNode.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
	if (sourceNode.getNodeName().equals(treeRecursiveNodesAdaptorName)) {
	    Element sourceElement = (Element) sourceNode;
	    String nodes = sourceElement.hasAttribute(RichFacesRecursiveTreeNodesAdaptorTemplate.NODES_NAME) ? 
	    		sourceElement.getAttribute(RichFacesRecursiveTreeNodesAdaptorTemplate.NODES_NAME) : Constants.EMPTY;
	    nsIDOMElement textContainer = VisualDomUtil
	    		.createBorderlessContainer(visualDocument);
	    nsIDOMText text = visualDocument.createTextNode(nodes);
	    textContainer.appendChild(text);
	    nodeTitle.appendChild(textContainer);
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
     * 
     * @param parentTree
     * @param sourceNode
     * @return
     */
    protected boolean isLastElement(nsIDOMNode sourceNode) {
	nsIDOMNode parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	nsIDOMNodeList childs = parentTree.getChildNodes();
	String treeNodeName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODE;
	String treeNodesAdaptorName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
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

	String treeNodesAdaptorName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = sourceNode.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
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
    protected boolean isLastElement(Node sourceNode) {
	Node parentTree = sourceNode.getParentNode();
	if (!(parentTree instanceof Element)) {
	    return true;
	}
	NodeList childs = parentTree.getChildNodes();
	String treeNodeName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODE;
	String treeNodesAdaptorName = parentTree.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = parentTree.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
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
     * Adds basic attributes to tree
     * 
     * @param img
     */

    private void addBasicAttributesToPicture(nsIDOMElement img) {
	img.setAttribute(HTML.ATTR_CLASS, TREE_TABLE_PICTURE_STYLE_CLASS_NAME);
    }

    /**
     * Sets attributes for no node title name
     * 
     * @param nodeTitle
     */
    private void addBasicNodeTitleAttributes(nsIDOMElement nodeTitle) {
	nodeTitle.setAttribute(HTML.ATTR_CLASS, STYLE_CLASS_FOR_NODE_TITLE);
    }

    /**
     * Used for setting images into tree nodes
     * 
     * @param pageContext
     *            page context
     * @param visualDocument
     *            visual document
     * @param sourceNode
     *            treeNode element
     * @param iconCell
     *            cell were image should be setted
     * @param nodeAttrName
     *            image attr name( icon, iconExpanded, ...)
     * @param defaultImage
     *            (image by default)
     */
    private void setAttributeForPictureNode(VpePageContext pageContext,
	    nsIDOMDocument visualDocument, Element sourceNode,
	    nsIDOMElement iconCell, String nodeAttrName, String defaultImage) {
	if (RichFacesTemplatesActivator.getDefault().isDebugging()) {
	    System.out.println("call setAttributeForPictureNode"); //$NON-NLS-1$
	}
	nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
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
	    img.setAttribute(ICON_PARAM_NAME, Constants.EMPTY);
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

    private boolean isOnlyOneNodeInAdaptor(Node sourceNode) {
	Node parent = sourceNode.getParentNode();
	NodeList list = parent.getChildNodes();
	Node currentNode = null;
	String treeNodeName = sourceNode.getPrefix() + Constants.COLON
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
	String treeNodesAdaptorName = adaptorNode.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String treeRecursiveNodesAdaptorName = adaptorNode.getPrefix()
		+ Constants.COLON + RECURSIVE_TREE_NODES_ADAPTOR;
	if (adaptorNode.getNodeName().equals(treeNodesAdaptorName)
		|| adaptorNode.getNodeName().equals(
			treeRecursiveNodesAdaptorName)) {
	    Node treeNode = adaptorNode.getParentNode();
	    String treeName = treeNode.getPrefix() + Constants.COLON + TREE;
	    if (treeNode.getNodeName().equals(treeName)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Set attributes for treeNode
     * 
     * @param table
     */
    protected void addBasicTreeNodeAttributes(nsIDOMElement table) {
	if (table == null) {
	    return;
	}
	table.setAttribute(HTML.ATTR_CELLPADDING,
		TREE_TABLE_ATR_CELLPADDING_VALUE);
	table.setAttribute(HTML.ATTR_CELLSPACING,
		TREE_TABLE_ATR_CELLSPACING_VALUE);
	table.setAttribute(HTML.ATTR_BORDER, TREE_TABLE_ATR_BORDER_VALUE);
	table.setAttribute(HTML.ATTR_CLASS, "dr-tree-full-width"); //$NON-NLS-1$
    }

    /**
     * Checks for attributes for node and if such exist convert it's to html
     * atributes.
     * 
     * @param sourceNode
     * @param tableRow
     */
    protected void addAttributeToTableNode(Element sourceNode,
	    nsIDOMElement tableRow) {

    	if (sourceNode.hasAttribute(NODE_TITLE_STYLE_CLASS_ATTR_NAME)) {
    		tableRow.setAttribute(HTML.ATTR_CLASS, sourceNode.getAttribute(NODE_TITLE_STYLE_CLASS_ATTR_NAME));
    	}
    }

}
