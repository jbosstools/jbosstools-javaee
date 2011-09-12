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
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Create template for rich:recursiveTreeNodesAdaptor element
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesRecursiveTreeNodesAdaptorTemplate extends RichFacesAbstractTreeTemplate {

	private static final String STYLE_PATH = "/tree/tree.css"; //$NON-NLS-1$
	public static final String ICON_DIV_LINE = "/tree/divLine.gif"; //$NON-NLS-1$
	private static final String ADAPTER_LINES_STYLE = "background-position: left center; background-repeat: repeat-y;"; //$NON-NLS-1$
	public static final String ID_ATTR_NAME = "ID"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData vpeCreationData = null;
		if (isEmptyNode(sourceNode)) {
			nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
			nsIDOMElement visualElement = visualDocument.createElement(HTML.TAG_TABLE);
			addBasicTreeNodeAttributes(visualElement);
			div.appendChild(visualElement);
			nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

			nsIDOMElement tableRow = visualDocument.createElement(HTML.TAG_TR);
			addAttributeToTableNode((Element) sourceNode, tableRow);
			visualElement.appendChild(tbody);
			tbody.appendChild(tableRow);

			vpeCreationData = new VpeCreationData(div);
			createBasicTree(pageContext, visualDocument, tableRow, sourceNode,vpeCreationData);
		} else {
			ComponentUtil.setCSSLink(pageContext, STYLE_PATH, TREE_NODES_ADAPTOR);
			nsIDOMElement visualElement = visualDocument.createElement(HTML.TAG_DIV);
			visualElement.setAttribute(ID_ATTR_NAME, TREE_NODES_ADAPTOR);
			if (isHasParentAdapter(sourceNode)) {
				visualElement.setAttribute(HTML.ATTR_CLASS, "dr-tree-h-ic-div"); //$NON-NLS-1$
				if (getShowLinesAttr(sourceNode)
						&& (isAdapterBetweenNodes(sourceNode) || isHasNextParentAdaptorElement(sourceNode))) {
					String path = RichFacesTemplatesActivator.getPluginResourcePath() + ICON_DIV_LINE;
					visualElement.setAttribute(HTML.ATTR_STYLE,
							"background-image: url(file://" + path + "); " //$NON-NLS-1$ //$NON-NLS-2$
							+ ADAPTER_LINES_STYLE);
				}
			}
			vpeCreationData = new VpeCreationData(visualElement);
			parseTree(pageContext, sourceNode, visualDocument, vpeCreationData,visualElement);
		}
		return vpeCreationData;
	}

	/**
	 * 
	 * @param sourceElement
	 * @return
	 */
	private boolean isEmptyNode(Node sourceNode) {
		NodeList childs = sourceNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node el = childs.item(i);
			if (!(el instanceof Element)) {
				continue;
			}
			return false;
		}
		return true;
	}

	@Override
	public void setPseudoContent(VpePageContext pageContext,
			Node sourceContainer, nsIDOMNode visualContainer,
			nsIDOMDocument visualDocument) {
		// Empty
	}
}
