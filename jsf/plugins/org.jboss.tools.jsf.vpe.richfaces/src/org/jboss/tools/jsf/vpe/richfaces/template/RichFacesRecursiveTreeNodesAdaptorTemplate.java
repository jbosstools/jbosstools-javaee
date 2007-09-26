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
 * Create template for rich:recursiveTreeNodesAdaptor element
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesRecursiveTreeNodesAdaptorTemplate extends
	VpeAbstractTemplate {
    private final static String TREE_NODE_NAME = "treeNode";

    private static final String TREE_TABLE_ATR_CELLSPACING_VALUE = "0px";

    private static final String TREE_TABLE_ATR_CELLPADDING_VALUE = "0px";

    private static final String TREE_TABLE_ATR_BORDER_VALUE = "0px";

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    Document visualDocument) {
	Element visualElement = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	addBasicTreeNodeAttributes(visualElement);
	VpeCreationData vpeCreationData = new VpeCreationData(visualElement);
	parseTree(pageContext, sourceNode, visualDocument, vpeCreationData,
		visualElement);
	return vpeCreationData;
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
	    Element parentElement) {
	NodeList nodeList = sourceNode.getChildNodes();
	Element element = null;
	int lenght = nodeList.getLength();
	String treeNodeName = sourceNode.getPrefix() + ":" + TREE_NODE_NAME;
	VpeChildrenInfo vpeChildrenInfo = null;
	for (int i = 0; i < lenght; i++) {
	    if (!(nodeList.item(i) instanceof Element)) {
		continue;
	    }
	    element = (Element) nodeList.item(i);
	    if (element.getNodeName().equals(treeNodeName)) {
		Element tr = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_TR);
		Element td = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr.appendChild(td);
		vpeChildrenInfo = new VpeChildrenInfo(td);
		vpeCreationData.addChildrenInfo(vpeChildrenInfo);
		vpeChildrenInfo.addSourceChild(element);
		parentElement.appendChild(tr);
	    }
	}
    }

    /**
     * Set attributes for treeNode
     * 
     * @param table
     */
    private void addBasicTreeNodeAttributes(Element table) {
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

}
