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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.SourceColumnElements;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.SourceDataTableElements;
import org.jboss.tools.vpe.editor.template.VpeDataTableElements.VisualDataTableElements;
import org.jboss.tools.vpe.editor.util.MozillaSupports;

public class VpeOtrixGridViewTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		SourceDataTableElements sourceElements = new SourceDataTableElements(sourceNode);
		VisualDataTableElements visualElements = new VisualDataTableElements();

		Element visualTable = visualDocument.createElement("table");
		visualTable.setAttribute("border", "1");
		VpeCreationData creatorInfo = new VpeCreationData(visualTable);

		Element section = null, row = null, cell = null;
		if (true || sourceElements.hasHeaderSection()) {
			section = visualDocument.createElement("thead");
			if (true || sourceElements.hasTableHeader()) {
				row = visualDocument.createElement("tr");
				section.appendChild(row);
				visualElements.setTableHeaderRow(row);
				if (sourceElements.getTableHeader() != null) {
					VpeChildrenInfo info = new VpeChildrenInfo(row);
					info.addSourceChild(sourceElements.getTableHeader());
					creatorInfo.addChildrenInfo(info);
				}
			}
			if (true || sourceElements.hasColumnsHeader()) {
				row = visualDocument.createElement("tr");
				section.appendChild(row);
				visualElements.setColumnsHeaderRow(row);
			}
			visualTable.appendChild(section);
			MozillaSupports.release(section);
			visualElements.setHeader(section);
		}

		if (true || sourceElements.hasFooterSection()) {
			section = visualDocument.createElement("tfoot");
			if (true || sourceElements.hasColumnsFooter()) {
				row = visualDocument.createElement("tr");
				section.appendChild(row);
				visualElements.setColumnsFooterRow(row);
			}
			if (true || sourceElements.hasTableFooter()) {
				row = visualDocument.createElement("tr");
				section.appendChild(row);
				visualElements.setTableFooterRow(row);
				if (sourceElements.getTableFooter() != null) {
					VpeChildrenInfo info = new VpeChildrenInfo(row);
					info.addSourceChild(sourceElements.getTableFooter());
					creatorInfo.addChildrenInfo(info);
				}
			}
			visualTable.appendChild(section);
			MozillaSupports.release(section);
			visualElements.setFooter(section);
		}

		if (true || sourceElements.hasBodySection()) {
			section = visualDocument.createElement("tbody");
			row = visualDocument.createElement("tr");
			section.appendChild(row);
			visualTable.appendChild(section);
			MozillaSupports.release(section);
			visualElements.setBodyRow(row);
			visualElements.setBody(section);
		}

		VpeChildrenInfo info = null;
		if (sourceElements.getColumnCount() > 0) {
			Element group = visualDocument.createElement("colgroup");
			visualTable.appendChild(group);
			info = new VpeChildrenInfo(group);
			creatorInfo.addChildrenInfo(info);
		}

		for (int i = 0; i < sourceElements.getColumnCount(); i++) {
			SourceColumnElements column = sourceElements.getColumn(i);
			info.addSourceChild(column.getColumn());
		}

		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		
		Object[] elements = new Object[2];
		elements[0] = visualElements;
		elements[1] = sourceElements;
		visualNodeMap.put(this, elements);

//		for (int i = 0; i < propertyCreators.size(); i++) {
//			VpeCreator creator = (VpeCreator)propertyCreators.get(i);
//			if (creator != null) {
//				VpeCreatorInfo info1 = creator.create(pageContext, (Element) sourceNode, visualDocument, visualTable, visualNodeMap);
//				if (info1 != null && info1.getVisualNode() != null) {
//					Attr attr = (Attr)info1.getVisualNode();
//					visualTable.setAttributeNode(attr);
//					attr.Release();
//				}
//			}
//		}
		return creatorInfo;
	}

	public void setAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		VisualDataTableElements visualElements = getVisualDataTableElements(visualNodeMap);
		if (visualElements != null) {
			if ("headerStyleClass".equals(name)) {
				setCellsClass(visualElements.getTableHeaderRow(), "class", value);
			} else if ("headerStyle".equals(name)) {
					setCellsClass(visualElements.getTableHeaderRow(), "style", value);
			} else if ("columnHeaderStyleClass".equals(name)) {
				setCellsClass(visualElements.getColumnsHeaderRow(), "class", value);
			} else if ("columnHeaderStyle".equals(name)) {
				setCellsClass(visualElements.getColumnsHeaderRow(), "style", value);
			} else if ("columnFooterStyleClass".equals(name)) {
				setCellsClass(visualElements.getColumnsFooterRow(), "class", value);
			} else if ("columnFooterStyle".equals(name)) {
				setCellsClass(visualElements.getColumnsFooterRow(), "style", value);
			} else if ("footerStyleClass".equals(name)) {
				setCellsClass(visualElements.getTableFooterRow(), "class", value);
			} else if ("footerStyle".equals(name)) {
				setCellsClass(visualElements.getTableFooterRow(), "style", value);
			} else if ("rowStyleClass".equals(name)) {
				setRowClass(visualElements.getBodyRow(), "class", value);
			} else if ("rowStyle".equals(name)) {
				setRowClass(visualElements.getBodyRow(), "style", value);
			} else if ("cloumnStyleClass".equalsIgnoreCase(name)) {
				setCellsClass(visualElements.getBodyRow(), "class", value);
			} else if ("cloumnStyle".equalsIgnoreCase(name)) {
				setCellsClass(visualElements.getBodyRow(), "style", value);
			}
		}
	}

	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name) {
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		VisualDataTableElements visualElements = getVisualDataTableElements(visualNodeMap);
		if (visualElements != null) {
			if ("headerStyleClass".equals(name)) {
				removeCellsClass(visualElements.getTableHeaderRow(), "class");
			} else if ("headerStyle".equals(name)) {
					removeCellsClass(visualElements.getTableHeaderRow(), "style");
			} else if ("columnHeaderStyleClass".equals(name)) {
				removeCellsClass(visualElements.getColumnsHeaderRow(), "class");
			} else if ("columnHeaderStyle".equals(name)) {
				removeCellsClass(visualElements.getColumnsHeaderRow(), "style");
			} else if ("columnFooterStyleClass".equals(name)) {
				removeCellsClass(visualElements.getColumnsFooterRow(), "class");
			} else if ("columnFooterStyle".equals(name)) {
				removeCellsClass(visualElements.getColumnsFooterRow(), "style");
			} else if ("footerStyleClass".equals(name)) {
				removeCellsClass(visualElements.getTableFooterRow(), "class");
			} else if ("footerStyle".equals(name)) {
				removeCellsClass(visualElements.getTableFooterRow(), "style");
			} else if ("rowStyleClass".equals(name)) {
				removeRowClass(visualElements.getBodyRow(), "class");
			} else if ("rowStyle".equals(name)) {
				removeRowClass(visualElements.getBodyRow(), "style");
			} else if ("columnStyleClass".equals(name)) {
				removeCellsClass(visualElements.getBodyRow(), "class");
			} else if ("columnStyle".equals(name)) {
				removeCellsClass(visualElements.getBodyRow(), "style");
			}
		}
	}

	private void setCellsClass(Element row, String name, String value) {
		if (row != null && value != null) {
			String[] classes = getClasses(value);
			int ind = 0;

			NodeList children = row.getChildNodes();
			int count = children != null ? children.getLength() : 0;
			for (int i = 0; i < count; i++) {
				Node child = children.item(i);
				if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
					((Element)child).setAttribute(name, classes[ind]);
					ind = ind < (classes.length - 1) ? ind + 1 : 0;
				}
				MozillaSupports.release(child);
			}
			MozillaSupports.release(children);
		}
	}

	private String[] getClasses(String value) {
		if (value != null) {
			return value.split(",");
		}
		return null;
	}

	private VisualDataTableElements getVisualDataTableElements(Map visualNodeMap) {
		if (visualNodeMap != null) {
			Object o = visualNodeMap.get(this);
			try {
				if (o != null && o instanceof Object[] && ((Object[])o)[0] instanceof VisualDataTableElements) {
					return (VisualDataTableElements)((Object[])o)[0];
				}
			} catch (Exception e) {
				//ignore
			}
		}
		return null;
	}

	private void setRowClass(Element row, String name, String value) {
		if (row != null && value != null) {
			String[] rowClasses = getClasses(value);
			String rowClass = (rowClasses != null && rowClasses.length > 0) ? rowClasses[0] : null;
			if (rowClass.trim().length() > 0) {
				row.setAttribute(name, rowClass);
			} else {
				row.removeAttribute(name);
			}
		}
	}

	private void removeCellsClass(Element row, String name) {
		if (row != null) {
			NodeList children = row.getChildNodes();
			int count = children != null ? children.getLength() : 0;
			for (int i = 0; i < count; i++) {
				Node child = children.item(i);
				if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
					((Element)child).removeAttribute(name);
				}
				MozillaSupports.release(child);
			}
			MozillaSupports.release(children);
		}
	}

	private void removeRowClass(Element row, String name) {
		if (row != null) {
			row.removeAttribute(name);
		}
	}

	public void validate(VpePageContext pageContext, Node sourceNode, Document visualDocument, VpeCreationData data) {
		VisualDataTableElements visualElements = null;
		SourceDataTableElements sourceElements = null;
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		if (visualNodeMap != null) {
			visualElements = getVisualDataTableElements(visualNodeMap);
			sourceElements = getSourceDataTableElements(visualNodeMap);
		} else if (sourceNode != null) {
			sourceElements = new SourceDataTableElements(sourceNode);
			VpeDomMapping domMapping = pageContext.getDomMapping();
			Node visualNode = domMapping.getVisualNode(sourceNode);
			if (visualNode != null) {
				visualElements = VpeDataTableElements.getVisualDataTableElements(visualNode);
			}
		}
		if (visualElements != null) {
			NamedNodeMap attrs = sourceNode.getAttributes();
			setCellsClass(visualElements.getTableHeaderRow(), "class", attrs.getNamedItem("headerStyleClass").getNodeValue());
			setCellsClass(visualElements.getTableHeaderRow(), "style", attrs.getNamedItem("headerStyle").getNodeValue());

			setCellsClass(visualElements.getColumnsHeaderRow(), "class", attrs.getNamedItem("columnHeaderStyleClass").getNodeValue());
			setCellsClass(visualElements.getColumnsHeaderRow(), "style", attrs.getNamedItem("columnHeaderStyle").getNodeValue());

			setCellsClass(visualElements.getColumnsFooterRow(), "class", attrs.getNamedItem("columnFooterStyleClass").getNodeValue());
			setCellsClass(visualElements.getColumnsFooterRow(), "style", attrs.getNamedItem("columnFooterStyle").getNodeValue());

			setCellsClass(visualElements.getTableFooterRow(), "class", attrs.getNamedItem("footerStyleClass").getNodeValue());
			setCellsClass(visualElements.getTableFooterRow(), "style", attrs.getNamedItem("footerStyle").getNodeValue());

			setRowClass(visualElements.getBodyRow(), "class", attrs.getNamedItem("rowStyleClass").getNodeValue());
			setRowClass(visualElements.getBodyRow(), "style", attrs.getNamedItem("rowStyle").getNodeValue());

			setCellsClass(visualElements.getBodyRow(), "class", attrs.getNamedItem("columnStyleClass").getNodeValue());
			setCellsClass(visualElements.getBodyRow(), "style", attrs.getNamedItem("columnStyle").getNodeValue());
		}
		if (sourceElements != null && visualElements != null) {
			setRowDisplayStyle(visualElements.getTableHeaderRow(), sourceElements.hasTableHeader());
			setRowDisplayStyle(visualElements.getColumnsHeaderRow(), sourceElements.hasColumnsHeader());
			setRowDisplayStyle(visualElements.getBodyRow(), sourceElements.hasBodySection());
			setRowDisplayStyle(visualElements.getColumnsFooterRow(), sourceElements.hasColumnsFooter());
			setRowDisplayStyle(visualElements.getTableFooterRow(), sourceElements.hasTableFooter());
		}
	}

	private void setRowDisplayStyle(Element row, boolean visible) {
		if (row != null) {
			row.setAttribute("style", "display:" + (visible ? "" : "none"));
		}
	}

	private SourceDataTableElements getSourceDataTableElements(Map visualNodeMap) {
		if (visualNodeMap != null) {
			Object o = visualNodeMap.get(this);
			try {
				if (o != null && o instanceof Object[] && ((Object[])o)[1] instanceof VisualDataTableElements) {
					return (SourceDataTableElements)o;
				}
			} catch (Exception e) {
				//ignore
			}
		}
		return null;
	}

	public String[] getOutputAtributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getOutputTextNode(VpePageContext pageContext, Element sourceElement, Object data) {
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
}
