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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Create template for rich:tree element.
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesTreeTemplate extends VpeAbstractTemplate {

	/** Resurces */

	public static final String TREE_NODE_NAME = "treeNode";

	public static final String SHOW_LINES_ATTR_NAME = "showConnectingLines";

	private static final String STYLE_PATH = "/tree/tree.css";

	private static final String ICON_ATTR = "icon";

	private static final String TREE_TABLE_ATR_CELLSPACING_VALUE = "0px";

	private static final String TREE_TABLE_ATR_CELLPADDING_VALUE = "0px";

	private static final String TREE_TABLE_ATR_BORDER_VALUE = "0px";

	private static final String TREE_STYLE_CLASS_ATR_NAME = "styleClass";

	private static final String ICON_COLLAPSED_ATTR_NAME = "iconExpanded";

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have
	 * child nodes.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		// sets css for tree on page
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "tree");
		Element visualElement = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		Element treeRow = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		Element treeCell = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		addBasicTreeAttributes(visualElement);
		treeRow.appendChild(treeCell);
		visualElement.appendChild(treeRow);
		VpeCreationData vpeCreationData = new VpeCreationData(visualElement);
		parseTree(pageContext, sourceNode, visualDocument, vpeCreationData,
				treeCell);
		setStylesAttributesToTree(visualElement, (Element) sourceNode);
		return vpeCreationData;
	}

	/**
	 * Sets to tree basic style attributes
	 * 
	 * @param tree
	 * @param sourceNode
	 */
	private void setStylesAttributesToTree(Element treeTable, Element sourceNode) {
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
	private void setAttributeToTree(Node node, String attrName, String attrValue) {
		if (!(node instanceof Element)) {
			return;
		}
		if (node.getNodeName().equalsIgnoreCase(
				HtmlComponentUtil.HTML_TAG_TABLE)) {
			((Element) node).setAttribute(attrName, attrValue);
		}
		NodeList list2 = node.getChildNodes();
		for (int i = 0; i < list2.getLength(); i++) {
			setAttributeToTree(list2.item(i), attrName, attrValue);
		}
	}

	/**
	 * Is invoked after construction of all child nodes of the current visual
	 * node.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param data
	 *            Object <code>VpeCreationData</code>, built by a method
	 *            <code>create</code>
	 */
	public void validate(VpePageContext pageContext, Node sourceNode,
			Document visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		revertTableRows(data.getNode());
	}

	/**
	 * Recursive go throw three and checks if icon was seted for node or not
	 * 
	 * @param node
	 */
	private void correctImage(VpePageContext pageContex, Element sourceNode,
			Node node) {
		if (!(node instanceof Element)) {
			return;
		}
		if (node.getNodeName().equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_IMG)) {
			String srcAttr = ((Element) node)
					.getAttribute(RichFacesTreeNodeTemplate.ICON_PARAM_NAME);
			if (RichFacesTreeNodeTemplate.DEFAULT_ICON_EXPANDED_PARAM_VALUE
					.equals(srcAttr)) {
				setImgForNode(pageContex, sourceNode, (Element) node,
						ICON_COLLAPSED_ATTR_NAME,
						RichFacesTreeNodeTemplate.ICON_EXPANDED_WITH_LINES,
						RichFacesTreeNodeTemplate.ICON_EXPANDED_WITHOUT_LINES);
			} else if (RichFacesTreeNodeTemplate.DEFAULT_ICON_PARAM_VALUE
					.equals(srcAttr)) {
				setImgForNode(pageContex, sourceNode, (Element) node,
						ICON_ATTR,
						RichFacesTreeNodeTemplate.ICON_NODE_WITH_LINES,
						RichFacesTreeNodeTemplate.ICON_NODE_WITHOUT_LINES);
			} else if (RichFacesTreeNodeTemplate.NODE_ICON_LEAF_ATTR_NAME
					.equals(srcAttr)) {
				setImgForNode(pageContex, sourceNode, (Element) node,
						RichFacesTreeNodeTemplate.NODE_ICON_LEAF_ATTR_NAME,
						RichFacesTreeNodeTemplate.ICON_LEAF_WITH_LINES,
						RichFacesTreeNodeTemplate.ICON_LEAF_WITHOUT_LINES);
			}
		}
		NodeList list2 = node.getChildNodes();
		for (int i = 0; i < list2.getLength(); i++) {
			correctImage(pageContex, sourceNode, list2.item(i));
		}
	}

	/**
	 * Sets icon fro node
	 * 
	 * @param img
	 * @param sourceNode
	 */
	private void setImgForNode(VpePageContext pageContext, Element sourceNode,
			Element img, String attrName, String iconWithLines,
			String iconWithoutLines) {
		String treeIconAttr = sourceNode.getAttribute(attrName);
		if (treeIconAttr != null && treeIconAttr.length() > 0) {
			ComponentUtil.setImgFromResources(pageContext, img, treeIconAttr,
					RichFacesTreeNodeTemplate.UNDEFINED_ICON);
			return;
		}
		String showConnectingLinesAttr = sourceNode
				.getAttribute(SHOW_LINES_ATTR_NAME);
		if (showConnectingLinesAttr != null
				&& showConnectingLinesAttr.length() > 0
				&& showConnectingLinesAttr.equalsIgnoreCase("false")) {
			ComponentUtil.setImg(img, iconWithoutLines);
		} else {
			ComponentUtil.setImg(img, iconWithLines);
		}
	}

	/**
	 * Revert tree elements in right order.
	 * 
	 * @param node
	 */
	private void revertTableRows(Node node) {
		if (!(node instanceof Element)) {
			return;
		}
		NodeList list = node.getChildNodes();
		if (node.getNodeName().equalsIgnoreCase(
				HtmlComponentUtil.HTML_TAG_TABLE)
				&& list.getLength() == 2) {
			Node tr1 = list.item(0);
			Node tr2 = list.item(1);
			node.removeChild(tr1);
			node.removeChild(tr2);
			node.appendChild(tr2);
			node.appendChild(tr1);
		}
		NodeList list2 = node.getChildNodes();
		for (int i = 0; i < list2.getLength(); i++) {
			revertTableRows(list2.item(i));
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
			Document visualDocument, VpeCreationData vpeCreationData,
			Element cell) {
		// work arroud, becouse getChielsByName returns always null;
		NodeList nodeList = sourceNode.getChildNodes();
		Element element = null;
		Element tree = null;
		Element childTree = null;
		Element childLast = null;
		int lenght = nodeList.getLength();
		String treeNodeName = sourceNode.getPrefix() + ":" + TREE_NODE_NAME;
		VpeChildrenInfo vpeChildrenInfo = null;
		for (int i = 0; i < lenght; i++) {
			if (!(nodeList.item(i) instanceof Element)) {
				continue;
			}
			element = (Element) nodeList.item(i);
			childTree = null;
			if (element.getNodeName().equals(treeNodeName)) {
				if (tree == null) {

					tree = createBasicTree(visualDocument);
					vpeChildrenInfo = new VpeChildrenInfo(tree);
					vpeCreationData.addChildrenInfo(vpeChildrenInfo);
					vpeChildrenInfo.addSourceChild(element);
					childLast = tree;
					continue;
				} else if (childTree == null) {

					childTree = createBasicTree(visualDocument);
					appendChildTree(childLast, childTree, visualDocument);
					vpeChildrenInfo = new VpeChildrenInfo(childTree);
					vpeCreationData.addChildrenInfo(vpeChildrenInfo);
					vpeChildrenInfo.addSourceChild(element);
					childLast = childTree;
					continue;
				}
			}
		}
		if (tree != null) {
			cell.appendChild(tree);
		}
	}

	/**
	 * Appends child node to tree
	 * 
	 * @param parentNode
	 * @param childNode
	 * @param visualDocument
	 */
	private void appendChildTree(Element parentNode, Element childNode,
			Document visualDocument) {
		Element tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		Element emptyCell = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		Element treeCell = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		treeCell.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, "2");
		tr.appendChild(emptyCell);
		tr.appendChild(treeCell);
		treeCell.appendChild(childNode);
		parentNode.appendChild(tr);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		if (TREE_STYLE_CLASS_ATR_NAME.equalsIgnoreCase(name)) {
			setAttributeToTree(visualNode, HtmlComponentUtil.HTML_CLASS_ATTR,
					value);
		} else if (HtmlComponentUtil.HTML_STYLE_ATTR.equalsIgnoreCase(name)) {
			setAttributeToTree(visualNode, HtmlComponentUtil.HTML_STYLE_ATTR,
					removeFromStyleWithAndHeight(value));
			((Element) visualNode).setAttribute(
					HtmlComponentUtil.HTML_STYLE_ATTR, value);
		} else if (ICON_COLLAPSED_ATTR_NAME.equals(name)
				|| SHOW_LINES_ATTR_NAME.equals(name)
				|| ICON_ATTR.equals(name)
				|| RichFacesTreeNodeTemplate.NODE_ICON_LEAF_ATTR_NAME
						.equals(name)) {
			correctImage(pageContext, sourceElement, visualNode);
		}
	}

	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		if (TREE_STYLE_CLASS_ATR_NAME.equalsIgnoreCase(name)) {
			setAttributeToTree(visualNode, HtmlComponentUtil.HTML_CLASS_ATTR,
					"");
		} else if (HtmlComponentUtil.HTML_STYLE_ATTR.equalsIgnoreCase(name)) {
			setAttributeToTree(visualNode, HtmlComponentUtil.HTML_STYLE_ATTR,
					"");
		} else if (ICON_COLLAPSED_ATTR_NAME.equals(name)
				|| SHOW_LINES_ATTR_NAME.equals(name)
				|| ICON_ATTR.equals(name)
				|| RichFacesTreeNodeTemplate.NODE_ICON_LEAF_ATTR_NAME
						.equals(name)) {
			correctImage(pageContext, sourceElement, visualNode);
		}
	}

	/**
	 * Create simple tree node attribute.Used for creating more complex trees.
	 * 
	 * @param treeNodeTitle
	 * @param visualDocument
	 * @return tree
	 */
	private Element createBasicTree(Document visualDocument) {
		// create table
		Element tree = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		addBasicTreeAttributes(tree);
		return tree;
	}

	/**
	 * Sets some attributes which necessary for displaying table as tree
	 * 
	 * @param tree
	 */
	private void addBasicTreeAttributes(Element tree) {
		if (tree == null) {
			return;
		}
		tree.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR,
				TREE_TABLE_ATR_CELLSPACING_VALUE);
		tree.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR,
				TREE_TABLE_ATR_CELLPADDING_VALUE);
		tree.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR,
				TREE_TABLE_ATR_BORDER_VALUE);
	}
}
