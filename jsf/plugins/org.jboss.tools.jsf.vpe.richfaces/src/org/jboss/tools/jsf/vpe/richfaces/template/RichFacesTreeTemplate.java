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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
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

    public static final String TREE= "tree"; //$NON-NLS-1$
    public static final String TREE_NODE_NAME = "treeNode"; //$NON-NLS-1$
    public static final String TREE_NODES_ADAPTOR = "treeNodesAdaptor"; //$NON-NLS-1$
    public static final String RECURSIVE_TREE_NODES_ADAPTOR = "recursiveTreeNodesAdaptor"; //$NON-NLS-1$
    public static final String SHOW_LINES_ATTR_NAME = "showConnectingLines"; //$NON-NLS-1$
    private static final String STYLE_PATH = "/tree/tree.css"; //$NON-NLS-1$
    private static final String ICON_ATTR = "icon"; //$NON-NLS-1$
    private static final String TREE_STYLE_CLASS_ATR_NAME = "styleClass"; //$NON-NLS-1$
    private static final String ICON_COLLAPSED_ATTR_NAME = "iconExpanded"; //$NON-NLS-1$

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
	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, TREE);
	nsIDOMElement visualElement = visualDocument
		.createElement(HTML.TAG_DIV);
	Element sourceElement = (Element) sourceNode;
	
	if (sourceElement.hasAttribute(HTML.ATTR_STYLE)) {
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
	    visualElement.setAttribute(HTML.ATTR_STYLE, style);
	}
	
	if (sourceElement.hasAttribute(HTML.ATTR_CLASS)) {
		String styleClass = sourceElement.getAttribute(HTML.ATTR_CLASS);
	    visualElement.setAttribute(HTML.ATTR_CLASS, styleClass);
	}
	VpeCreationData vpeCreationData = new VpeCreationData(visualElement);
	vpeCreationData.addChildrenInfo(new VpeChildrenInfo(null));
	
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
	
		if (sourceNode.hasAttribute(HTML.ATTR_STYLE)) {
			String styleAttr = sourceNode.getAttribute(HTML.ATTR_STYLE);
		    setAttributeToTree(treeTable, HTML.ATTR_STYLE,
			    removeFromStyleWithAndHeight(styleAttr));
		    treeTable
			    .setAttribute(HTML.ATTR_STYLE, styleAttr);
		}
		
		if (sourceNode.hasAttribute(TREE_STYLE_CLASS_ATR_NAME)) {
			String styleClassAttr = sourceNode.getAttribute(TREE_STYLE_CLASS_ATR_NAME);
		    setAttributeToTree(treeTable, HTML.ATTR_CLASS,
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
	String[] mas = styleArgs.split(Constants.SEMICOLON);
	for (String styleAttr : mas) {
	    if ((styleAttr.indexOf(HTML.ATTR_WIDTH) != -1)
		    || (styleAttr.indexOf(HTML.ATTR_HEIGHT) != -1)) {
		continue;
	    }
	    result.append(styleAttr + Constants.SEMICOLON);
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
	    nsIDOMElement element = queryInterface(node, nsIDOMElement.class);
	    if (node.getNodeName().equalsIgnoreCase(
		    HTML.TAG_TABLE)) {
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
	    nsIDOMElement element = queryInterface(node, nsIDOMElement.class);
	    String id = element.hasAttribute(RichFacesTreeNodesAdaptorTemplate.ID_ATTR_NAME) ? 
	    		element.getAttribute(RichFacesTreeNodesAdaptorTemplate.ID_ATTR_NAME) : 
	    			Constants.EMPTY;
	    if (node.getNodeName().equalsIgnoreCase(
		    HTML.TAG_DIV)
		    && list.getLength() == 2
		    && !(id
			    .equalsIgnoreCase(TREE_NODES_ADAPTOR) || id
			    .equalsIgnoreCase(RECURSIVE_TREE_NODES_ADAPTOR))) {
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
	String treeNodeName = sourceNode.getPrefix() + Constants.COLON + TREE_NODE_NAME;
	String treeNodesAdaptorName = sourceNode.getPrefix() + Constants.COLON
		+ TREE_NODES_ADAPTOR;
	String recursiveTreeNodesAdaptorName = sourceNode.getPrefix() + Constants.COLON
		+ RECURSIVE_TREE_NODES_ADAPTOR;
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
    public boolean recreateAtAttrChange(VpePageContext pageContext,
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
	    setAttributeToTree(visualNode, HTML.ATTR_CLASS,
		    value);
	} else if (HTML.ATTR_STYLE.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HTML.ATTR_STYLE,
		    removeFromStyleWithAndHeight(value));
	    nsIDOMElement visualElement = queryInterface(visualNode, nsIDOMElement.class);
	    visualElement.setAttribute(
		    HTML.ATTR_STYLE, value);
	}
    }

    @Override
    public void removeAttribute(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMNode visualNode, Object data, String name) {
	if (TREE_STYLE_CLASS_ATR_NAME.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HTML.ATTR_CLASS,
		    Constants.EMPTY);
	} else if (HTML.ATTR_STYLE.equalsIgnoreCase(name)) {
	    setAttributeToTree(visualNode, HTML.ATTR_STYLE,
		    Constants.EMPTY);
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
		.createElement(HTML.TAG_DIV);
	div.setAttribute(HTML.ATTR_CLASS, "dr-tree-h-ic-div"); //$NON-NLS-1$
	return div;
    }

}
