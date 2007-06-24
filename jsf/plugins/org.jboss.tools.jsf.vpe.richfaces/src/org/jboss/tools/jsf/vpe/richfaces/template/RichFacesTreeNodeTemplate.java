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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates VPE content for rich:treeNode
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesTreeNodeTemplate extends VpeAbstractTemplate {

	public static final String DEFAULT_ICON_PARAM_VALUE = "DEFAULT_ICON_PARAM";

	public static final String ICON_PARAM_NAME = "richFacesTreeNodeParam";

	public static final String DEFAULT_ICON_EXPANDED_PARAM_VALUE = "DEFAULT_ICON_EXPANDED_PARAM";

	public static final String UNDEFINED_ICON = "/tree/unresolved.gif";

	public static final String NODE_ICON_LEAF_ATTR_NAME = "iconLeaf";

	public static final String ICON_NODE_WITH_LINES = "/tree/iconNodeWithLines.gif";

	public static final String ICON_EXPANDED_WITH_LINES = "/tree/iconCollapsedWithLines.gif";

	public static final String ICON_NODE_WITHOUT_LINES = "/tree/iconNode.gif";

	public static final String ICON_EXPANDED_WITHOUT_LINES = "/tree/iconCollapsed.gif";

	public static final String ICON_LEAF_WITH_LINES = "/tree/iconLeaf.gif";

	public static final String ICON_LEAF_WITHOUT_LINES = "/tree/iconLeafWithLines.gif";

	private static final String TREE_TABLE_PICTURE_STYLE_CLASS_NAME = "treePictureStyle";

	private static final String STYLE_CLASS_FOR_NODE_TITLE = "treeNodeNameStyle";

	private static final String NODE_TITLE_STYLE_CLASS_ATTR_NAME = "nodeClass";

	private static final String NODE_ICON_EXPANDED_ATTR_NAME = "iconExpanded";

	private static final String NODE_ICON_ATTR_NAME = "icon";

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
		Element tableRow = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		addAttributeToTableNode((Element) sourceNode, tableRow);
		VpeCreationData vpeCreationData = new VpeCreationData(tableRow);
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
	private void addAttributeToTableNode(Element sourceNode, Element tableRow) {

		String attrValue = sourceNode
				.getAttribute(NODE_TITLE_STYLE_CLASS_ATTR_NAME);
		if ((attrValue != null) && (attrValue.length() > 0)) {
			tableRow.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					attrValue);
		}
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
			Document visualDocument, Element sourceNode, Element iconCell,
			String nodeAttrName, String defaultImage) {
		Element img = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		addBasicAttributesToPicture(img);
		iconCell.appendChild(img);
		// get image from treeNode
		String imgName = sourceNode.getAttribute(nodeAttrName);
		// if in tree node image doesn't exist we get image attr from tree
		if (imgName == null || imgName.length() == 0) {
			imgName = ((Element) sourceNode.getParentNode())
					.getAttribute(nodeAttrName);
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
			Document visualDocument, Element treeRow, Node sourceNode,
			VpeCreationData vpeCreationData) {
		// creates icon node
		Element iconNode = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		String showLinesParam = ((Element) sourceNode.getParentNode())
				.getAttribute(RichFacesTreeTemplate.SHOW_LINES_ATTR_NAME);
		boolean showLinesValue = true;
		if (showLinesParam != null && showLinesParam.equalsIgnoreCase("false")) {
			showLinesValue = false;
		}
		// creates icon with status of node(collapsed or not) node
		Element td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// sets attribute for icon expanded picture or not
		setAttributeForPictureNode(pageContext, visualDocument,
				(Element) sourceNode, td1, NODE_ICON_EXPANDED_ATTR_NAME,
				showLinesValue == true ? ICON_EXPANDED_WITH_LINES
						: ICON_EXPANDED_WITHOUT_LINES);
		// sets icon node
		if (!isLastElement(sourceNode.getParentNode(), sourceNode)) {
			setAttributeForPictureNode(pageContext, visualDocument,
					(Element) sourceNode, iconNode, NODE_ICON_ATTR_NAME,
					showLinesValue == true ? ICON_NODE_WITH_LINES
							: ICON_NODE_WITHOUT_LINES);
		} else {
			setAttributeForPictureNode(pageContext, visualDocument,
					(Element) sourceNode, iconNode, NODE_ICON_LEAF_ATTR_NAME,
					showLinesValue == true ? ICON_LEAF_WITH_LINES
							: ICON_LEAF_WITHOUT_LINES);
		}

		treeRow.appendChild(td1);
		treeRow.appendChild(iconNode);
		// creates Tree Node Name Message
		Element nodeTitle = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		addBasicNadeTitleAttributes(nodeTitle);
		// Create mapping to Encode body
		VpeChildrenInfo tdInfo = new VpeChildrenInfo(nodeTitle);

		// Create mapping to Encode body
		List<Node> children = ComponentUtil.getChildren((Element) sourceNode,
				false);
		for (Node child : children) {
			tdInfo.addSourceChild(child);
		}
		vpeCreationData.addChildrenInfo(tdInfo);
		treeRow.appendChild(nodeTitle);
	}

	/**
	 * Sets attributes for no node title name
	 * 
	 * @param nodeTitle
	 */
	private void addBasicNadeTitleAttributes(Element nodeTitle) {
		nodeTitle.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				STYLE_CLASS_FOR_NODE_TITLE);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		/*
		 * processed only next attributes iconExpanded and icon, becouse tree
		 * allways shows as expanded and information is it leaf or not contains
		 * in model
		 */
		if (NODE_ICON_EXPANDED_ATTR_NAME.equalsIgnoreCase(name)) {
			Element expandedIconCell = (Element) visualNode.getChildNodes()
					.item(0);
			Element img = (Element) expandedIconCell.getChildNodes().item(0);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, "");
		} else if (NODE_ICON_ATTR_NAME.equals(name)
				&& !isLastElement(visualNode.getParentNode(), visualNode)) {
			Element iconCell = (Element) visualNode.getChildNodes().item(1);
			Element img = (Element) iconCell.getChildNodes().item(0);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, "");
		} else if (NODE_ICON_LEAF_ATTR_NAME.equals(name)
				&& isLastElement(sourceElement.getParentNode(), sourceElement)) {
			Element iconCell = (Element) visualNode.getChildNodes().item(1);
			Element img = (Element) iconCell.getChildNodes().item(0);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, "");
		}
	}

	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		/*
		 * processed only next attributes iconExpanded and icon, becouse tree
		 * allways shows as expanded and information is it leaf or not contains
		 * in model
		 */
		String showLinesParam = ((Element) sourceElement.getParentNode())
				.getAttribute(RichFacesTreeTemplate.SHOW_LINES_ATTR_NAME);
		boolean showLinesValue = true;
		if (showLinesParam != null && showLinesParam.equalsIgnoreCase("false")) {
			showLinesValue = false;
		}
		if (NODE_ICON_EXPANDED_ATTR_NAME.equalsIgnoreCase(name)) {
			Element expandedIconCell = (Element) visualNode.getChildNodes()
					.item(0);
			Element img = (Element) expandedIconCell.getChildNodes().item(0);
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
				&& !isLastElement(sourceElement.getParentNode(), sourceElement)) {
			Element iconCell = (Element) visualNode.getChildNodes().item(1);
			Element img = (Element) iconCell.getChildNodes().item(0);
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
				&& isLastElement(sourceElement.getParentNode(), sourceElement)) {
			Element iconCell = (Element) visualNode.getChildNodes().item(1);
			Element img = (Element) iconCell.getChildNodes().item(0);
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
	 * @param currentNode
	 * @return
	 */
	private boolean isLastElement(Node parentTree, Node currentNode) {
		NodeList childs = parentTree.getChildNodes();
		String treeNodeName = parentTree.getPrefix() + ":"
				+ RichFacesTreeTemplate.TREE_NODE_NAME;
		Node lastElement = null;
		Node el = null;
		for (int i = 0; i < childs.getLength(); i++) {
			el = childs.item(i);
			if (el.getNodeName().equals(treeNodeName)) {
				lastElement = el;
			}
		}
		return currentNode.equals(lastElement);
	}

	/**
	 * Adds basic attributes to tree
	 * 
	 * @param img
	 */

	private void addBasicAttributesToPicture(Element img) {
		img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				TREE_TABLE_PICTURE_STYLE_CLASS_NAME);
	}
}
