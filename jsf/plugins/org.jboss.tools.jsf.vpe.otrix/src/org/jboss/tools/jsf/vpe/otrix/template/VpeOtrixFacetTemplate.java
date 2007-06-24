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
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.util.MozillaSupports;

public class VpeOtrixFacetTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		VpeCreationData creatorInfo = null;

		boolean isHeader = false, isFooter = false;
		Node nameAttr = sourceNode.getAttributes().getNamedItem("name");
		if (nameAttr != null) {
			String name = nameAttr.getNodeValue();
			isHeader = name.equals("header");
			isFooter = name.equals("footer");
		}

		if (isHeader || isFooter) {
			Node sourceParent = sourceNode.getParentNode();
			if (sourceParent != null) {
				Node visualParent = null;
				VpeDomMapping domMapping = pageContext.getDomMapping();
				if (sourceParent != null && domMapping != null) {
					visualParent = pageContext.getDomMapping().getVisualNode(sourceParent);
				}

				Node header = null, footer = null;
				if (visualParent != null && visualParent.getNodeName().equalsIgnoreCase("table")) {

					NodeList children = visualParent.getChildNodes();
					int count = children != null ? children.getLength() : 0;
					if (count > 0) {
						for (int i = 0; i < count; i++) {
							Node node = children.item(i);
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								if (isHeader && "THEAD".equalsIgnoreCase(node.getNodeName())) {
									header = node;
									MozillaSupports.release(node);
									break;
								} else if (isFooter && "TFOOT".equalsIgnoreCase(node.getNodeName())) {
									footer = node;
									MozillaSupports.release(node);
									break;
								}
							}
							MozillaSupports.release(node);
						}
						MozillaSupports.release(children);
					}
				}

				Node cell = null;
				int columnsCount = getColumnsCount(sourceParent); 
				if (isHeader) {
					cell = makeCell(header, columnsCount, "TH", visualDocument, 0);
				} else if (isFooter) {
					cell = makeCell(footer, columnsCount, "TD", visualDocument, 1);
				}
				if (cell != null) {
					if (isHeader) {
						setCellClass(cell, "class", getTableAttrValue(sourceParent, "columnHeaderStyleClass"));
					} else if (isFooter) {
						setCellClass(cell, "class", getTableAttrValue(sourceParent, "columnFooterStyleClass"));
					}
					creatorInfo = new VpeCreationData(cell);
				}
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

	private int getColumnsCount(Node dataTableNode) {
		int count = 0;
		NodeList childs = dataTableNode.getChildNodes();
		int length = childs != null ? childs.getLength() : 0;
		for (int i = 0; i < length; i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().indexOf(":column") > 0) {
				count++;
			}
		}
		return count;
	}

	private Node makeCell(Node section, int columnCount, String cellTag, Document visualDocument, int index) {
		Element visualCell = visualDocument.createElement(cellTag);
		if (columnCount > 1) {
			visualCell.setAttribute("colspan", "" + columnCount);
		}
		return visualCell;
	}

	private String getTableAttrValue(Node dataTableNode, String attrName) {
		if (dataTableNode != null) {
			Node attr = dataTableNode.getAttributes().getNamedItem(attrName);
			if (attr != null) {
				return attr.getNodeValue();
			}
		}
		return null;
	}

	private void setCellClass(Node cell, String name, String className) {
		if (cell != null) {
			if (className != null && className.trim().length() > 0) {
				((Element)cell).setAttribute(name, className);
			}
		}
	}
}
