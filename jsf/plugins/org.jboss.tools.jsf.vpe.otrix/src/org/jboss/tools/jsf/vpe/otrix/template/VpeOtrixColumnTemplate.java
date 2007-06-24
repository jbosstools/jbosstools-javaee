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
package org.jboss.tools.jsf.vpe.otrix.template;

import java.util.Map;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.SourceColumnElements;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.VisualColumnElements;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.VisualDataTableElements;

public class VpeOtrixColumnTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {

		int index = getColumnIndex(sourceNode);

		VpeCreationData creatorInfo = null;

		Node sourceParent = sourceNode.getParentNode();
		Node visualParent = null;
		VpeDomMapping domMapping = pageContext.getDomMapping();
		if (sourceParent != null && domMapping != null) {
			visualParent = pageContext.getDomMapping().getVisualNode(sourceParent);
		}

		SourceColumnElements columnElements = new SourceColumnElements(sourceNode);
		if (visualParent != null && visualParent.getNodeName().equalsIgnoreCase("table") && columnElements != null) {
			VisualDataTableElements visualDataTableElements = VpeDataTableElements.getVisualDataTableElements(visualParent);
			Element col = visualDocument.createElement("col");
			Element colgroup = VpeDataTableElements.getNamedChild(visualParent, "colgroup", 0);
			creatorInfo = new VpeCreationData(col);
			VisualColumnElements visualColumnElements = new VisualColumnElements();
			if (colgroup != null) {
				colgroup.appendChild(col);
				VpeChildrenInfo info = null;
				Element cell = VpeDataTableElements.makeCell(visualDataTableElements.getColumnsHeaderRow(), index, "th", visualDocument);
				info = new VpeChildrenInfo(cell);
				if (columnElements.hasHeader()) {
					info.addSourceChild(columnElements.getHeader());
				}
				creatorInfo.addChildrenInfo(info);
				visualColumnElements.setHeaderCell(cell);

				cell = VpeDataTableElements.makeCell(visualDataTableElements.getColumnsFooterRow(), index, "td", visualDocument);
				info = new VpeChildrenInfo(cell);
				if (columnElements.hasFooter()) {
					info.addSourceChild(columnElements.getFooter());
				}
				creatorInfo.addChildrenInfo(info);
				visualColumnElements.setFooterCell(cell);
				
				cell = VpeDataTableElements.makeCell(visualDataTableElements.getBodyRow(), index, "TD", visualDocument);
				NodeList list = sourceNode.getChildNodes();
				int cnt = list != null ? list.getLength() : 0;
				if (cnt > 0) {
					info = new VpeChildrenInfo(cell);
					for (int i = 0; i < cnt; i++) {
						Node node = list.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							boolean isFacet = namesIsEquals("facet", node.getNodeName());
							Node attrName = node.getAttributes().getNamedItem("name");
							if (!isFacet || (attrName != null && !"header".equals(attrName.getNodeValue()) && !"footer".equals(attrName.getNodeValue()))) {
								info.addSourceChild(node);
							}
						}
					}
					creatorInfo.addChildrenInfo(info);
					visualColumnElements.setBodyCell(cell);
				}
				Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
				visualNodeMap.put(this, visualColumnElements);
			}
		}

		return creatorInfo;
	}

	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data,
			String name, String value) {
		// TODO Auto-generated method stub

	}

	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument,
			Node visualNode, Object data, String name) {
		// TODO Auto-generated method stub

	}

	public String[] getOutputAtributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getOutputTextNode(VpePageContext pageContext,
			Element sourceElement, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOutputAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public VpeAnyData getAnyData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validate(VpePageContext pageContext, Node sourceNode, Document visualDocument, VpeCreationData data) {
		Node parent = sourceNode.getParentNode();
		while (parent != null && parent.getNodeName().indexOf("gridView") < 0) {
			parent = parent.getParentNode();
		}
		if (parent != null) {
			VpeDomMapping mapping = pageContext.getDomMapping();
			VpeNodeMapping nodeMapping = mapping.getNodeMapping(parent);
			if (nodeMapping != null && nodeMapping instanceof VpeElementMapping) {
				VpeTemplate template = ((VpeElementMapping)nodeMapping).getTemplate();
				if (template != null) {
					template.validate(pageContext, parent, visualDocument, new VpeCreationData(null));
				}
			}
		}
	}

	private int getColumnIndex(Node sourceNode) {
		int index = 0;
		Node prevNode = sourceNode.getPreviousSibling();
		while (prevNode != null) {
			if (prevNode.getNodeName().equals(sourceNode.getNodeName())) {
				index++;
			}
			prevNode = prevNode.getPreviousSibling();
		}
		return index;
	}

	private static boolean namesIsEquals(String name1, String name2) {
		int ind = name2.indexOf(":");
		return ind < name2.length() && name1.equals(name2.substring(ind >= 0 ? ind + 1 : 0));
	}

	public void beforeRemove(VpePageContext pageContext, Node sourceNode, Node visualNode, Object data) {
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		Object elements = visualNodeMap.get(this);
		if (elements != null && elements instanceof VisualColumnElements) {
			removeChild(((VisualColumnElements)elements).getHeaderCell());
			removeChild(((VisualColumnElements)elements).getBodyCell());
			removeChild(((VisualColumnElements)elements).getFooterCell());
		}
	}

	private static void removeChild(Element child) {
		if (child != null && child.getParentNode() != null) {
			child.getParentNode().removeChild(child);
		}
	}
}
