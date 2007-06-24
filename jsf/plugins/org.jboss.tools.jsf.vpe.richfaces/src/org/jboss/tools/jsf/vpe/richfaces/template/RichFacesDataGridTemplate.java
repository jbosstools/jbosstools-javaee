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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesDataGridTemplate extends RichFacesDataTableTemplate {

	private int defaultRows = 3;
	private String[] rowClasses;
	private String[] columnClasses;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		Element sourceElement = (Element)sourceNode;

		initStyleClasses(sourceElement);

		Element table = visualDocument.createElement("table");
		ComponentUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataGrid");
		String tableClass = sourceElement.getAttribute("styleClass");
		table.setAttribute("class", "dr-table rich-table " + (tableClass==null?"":tableClass));

		// Encode colgroup definition.
		int columnsLength = getColumnsCount(sourceElement);
		Element colgroup = visualDocument.createElement("colgroup");
		colgroup.setAttribute("span", String.valueOf(columnsLength));
		table.appendChild(colgroup);

		//Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, table);

		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, "header");
		if(header!=null) {
			Element thead = visualDocument.createElement("thead");
			table.appendChild(thead);
			String headerClass = (String) sourceElement.getAttribute("headerClass");
			encodeTableHeaderOrFooterFacet(creationData, thead, columnsLength, visualDocument, header,
					"dr-table-header rich-table-header",
					"dr-table-header-continue rich-table-header-continue",
					"dr-table-headercell rich-table-headercell",
					headerClass, "td");
		}

		// Encode Footer
		Element footer = ComponentUtil.getFacet(sourceElement, "footer");
		if (footer != null) {
			Element tfoot = visualDocument.createElement("tfoot");
			table.appendChild(tfoot);
			String footerClass = (String) sourceElement.getAttribute("footerClass");
			encodeTableHeaderOrFooterFacet(creationData, tfoot, columnsLength, visualDocument, footer,
					"dr-table-footer rich-table-footer",
					"dr-table-footer-continue rich-table-footer-continue",
					"dr-table-footercell rich-table-footercell",
					footerClass, "td");
		}

		Element tbody = visualDocument.createElement("tbody");
		table.appendChild(tbody);

		// Create mapping to Encode body
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		sourceElement.getAttribute("elements");

		int elementsCount = getElementsCount(sourceElement, columnsLength);
		if(columnsLength>0) {
			int rowIndex = 0;
			for(int elementIndex = 0; elementIndex<elementsCount; rowIndex++) {
				Element tr = visualDocument.createElement("tr");
				tbody.appendChild(tr);
				tr.setAttribute("class", "dr-table-row rich-table-row " + getRowClass(rowIndex));
				for(int columnIndex = 0; columnIndex<columnsLength && elementIndex<elementsCount; columnIndex++) {
					Element td = visualDocument.createElement("td");
					tr.appendChild(td);
					td.setAttribute("class", "dr-table-cell rich-table-cell " + getColumnClass(columnIndex));
					if(!children.isEmpty()) {
						VpeChildrenInfo childInfo = new VpeChildrenInfo(td);
						for (Node child : children) {
							childInfo.addSourceChild(child);
						}
						creationData.addChildrenInfo(childInfo);
					}
					elementIndex++;
				}
			}
		}

		return creationData;
	}

	private void initStyleClasses(Element sourceElement) {
		String columnClassesString = sourceElement.getAttribute("columnClasses");
		String rowClassesString = sourceElement.getAttribute("rowClasses");
		columnClasses = parceClasses(columnClassesString);
		rowClasses = parceClasses(rowClassesString);
	}

	private String[] parceClasses(String classes) {
		if(classes==null) {
			return new String[]{""};
		}
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(classes, ",", false);
		while(st.hasMoreElements()) {
			list.add((String)st.nextElement());
		}
		if(list.isEmpty()) {
			return new String[]{""};
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	private String getColumnClass(int column) {
		return columnClasses[column%columnClasses.length];
	}

	private String getRowClass(int row) {
		return rowClasses[row%rowClasses.length];
	}

	protected int getColumnsCount(Element sourceElement) {
		int count = 0;
		// check for exact value in component
		Integer span = null;
		try {
			span = Integer.valueOf(sourceElement.getAttribute("columns"));			
		} catch (Exception e) {
			// Ignore bad attribute
		}
		if (null != span && span.intValue() >0) {
			count = span.intValue();
		}
		return count;
	}

	protected int getElementsCount(Element sourceElement, int columnCount) {
		int elements = 0;
		// check for exact value in component
		Integer span = null;
		try {
			span = Integer.valueOf(sourceElement.getAttribute("elements"));			
		} catch (Exception e) {
			// Ignore bad attribute
		}
		if (null != span && span.intValue()>0) {
			elements = span.intValue();
		} else {
			// default rows = 3;
			elements = columnCount * defaultRows;
		}

		return elements;
	}

	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name) {
		((Element)visualNode).removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		((Element)visualNode).setAttribute(name, value);
	}
}