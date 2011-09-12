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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Create template for rich:treeNodes element
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesTreeNodeTemplate extends RichFacesAbstractTreeTemplate {

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
			nsIDOMDocument visualDocument) {

		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement visualElement = visualDocument
				.createElement(HTML.TAG_TABLE);
		addBasicTreeNodeAttributes(visualElement);
		div.appendChild(visualElement);
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement tableRow = visualDocument.createElement(HTML.TAG_TR);
		addAttributeToTableNode((Element) sourceNode, tableRow);
		visualElement.appendChild(tbody);
		tbody.appendChild(tableRow);

		VpeCreationData vpeCreationData = new VpeCreationData(div);
		createBasicTree(pageContext, visualDocument, tableRow, sourceNode,
				vpeCreationData);
		return vpeCreationData;
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
			nsIDOMElement expandedIconCell = queryInterface(visualNode
					.getChildNodes().item(0), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(expandedIconCell.getChildNodes()
					.item(0), nsIDOMElement.class);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, Constants.EMPTY);
		} else if (NODE_ICON_ATTR_NAME.equals(name)
				&& !isLastElement(visualNode)) {
			nsIDOMElement iconCell = queryInterface(visualNode.getChildNodes()
					.item(1), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(
					iconCell.getChildNodes().item(0), nsIDOMElement.class);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, Constants.EMPTY);
		} else if (NODE_ICON_LEAF_ATTR_NAME.equals(name)
				&& isLastElement(sourceElement)) {
			nsIDOMElement iconCell = queryInterface(visualNode.getChildNodes()
					.item(1), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(
					iconCell.getChildNodes().item(0), nsIDOMElement.class);
			ComponentUtil.setImgFromResources(pageContext, img, value,
					UNDEFINED_ICON);
			img.setAttribute(ICON_PARAM_NAME, Constants.EMPTY);
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

		Element parentElement = (Element) sourceElement.getParentNode();
		boolean showLinesValue = getShowLinesAttr(sourceElement);
		if (NODE_ICON_EXPANDED_ATTR_NAME.equalsIgnoreCase(name)) {

			nsIDOMElement expandedIconCell = queryInterface(visualNode
					.getChildNodes().item(0), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(expandedIconCell.getChildNodes()
					.item(0), nsIDOMElement.class);

			if (parentElement.hasAttribute(NODE_ICON_EXPANDED_ATTR_NAME)) {
				String parentAttrName = parentElement
						.getAttribute(NODE_ICON_EXPANDED_ATTR_NAME);
				ComponentUtil.setImgFromResources(pageContext, img,
						parentAttrName, UNDEFINED_ICON);
			} else {
				ComponentUtil.setImg(img,
						showLinesValue == true ? ICON_EXPANDED_WITH_LINES
								: ICON_EXPANDED_WITHOUT_LINES);
			}
			img.setAttribute(ICON_PARAM_NAME, DEFAULT_ICON_EXPANDED_PARAM_VALUE);
		} else if (NODE_ICON_ATTR_NAME.equalsIgnoreCase(name)
				&& !isLastElement(sourceElement)) {

			nsIDOMElement iconCell = queryInterface(visualNode.getChildNodes()
					.item(1), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(
					iconCell.getChildNodes().item(0), nsIDOMElement.class);

			if (parentElement.hasAttribute(NODE_ICON_ATTR_NAME)) {
				String parentAttrName = parentElement
						.getAttribute(NODE_ICON_ATTR_NAME);
				ComponentUtil.setImgFromResources(pageContext, img,
						parentAttrName, UNDEFINED_ICON);
			} else {
				ComponentUtil.setImg(img,
						showLinesValue == true ? ICON_NODE_WITH_LINES
								: ICON_NODE_WITHOUT_LINES);
			}
			img.setAttribute(ICON_PARAM_NAME, DEFAULT_ICON_PARAM_VALUE);

		} else if (NODE_ICON_LEAF_ATTR_NAME.equalsIgnoreCase(name)
				&& isLastElement(sourceElement)) {

			nsIDOMElement iconCell = queryInterface(visualNode.getChildNodes()
					.item(1), nsIDOMElement.class);
			nsIDOMElement img = queryInterface(
					iconCell.getChildNodes().item(0), nsIDOMElement.class);

			if (parentElement.hasAttribute(NODE_ICON_LEAF_ATTR_NAME)) {
				String parentAttrName = parentElement
						.getAttribute(NODE_ICON_LEAF_ATTR_NAME);
				ComponentUtil.setImgFromResources(pageContext, img,
						parentAttrName, UNDEFINED_ICON);
			} else {
				ComponentUtil.setImg(img,
						showLinesValue == true ? ICON_LEAF_WITH_LINES
								: ICON_LEAF_WITHOUT_LINES);
			}
			img.setAttribute(ICON_PARAM_NAME, NODE_ICON_LEAF_ATTR_NAME);
		}
	}
}