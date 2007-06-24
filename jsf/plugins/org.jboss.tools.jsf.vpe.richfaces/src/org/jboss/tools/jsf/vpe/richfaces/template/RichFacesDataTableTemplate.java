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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataTableTemplate extends VpeAbstractTemplate {
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		Element sourceElement = (Element)sourceNode;

		Element table = visualDocument.createElement("table");
		ComponentUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataTable");
		String tableClass = sourceElement.getAttribute("styleClass");
		table.setAttribute("class", "dr-table rich-table " + (tableClass==null?"":tableClass));

		// Encode colgroup definition.
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		Element colgroup = visualDocument.createElement("colgroup");
		colgroup.setAttribute("span", String.valueOf(columnsLength));
		table.appendChild(colgroup);

		String columnsWidth = sourceElement.getAttribute("columnsWidth");
		if (null != columnsWidth) {
			String[] widths = columnsWidth.split(",");
			for (int i = 0; i < widths.length; i++) {
				Element col = visualDocument.createElement("col");
				col.setAttribute("width", widths[i]);
				colgroup.appendChild(col);
			}
		}

		//Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, table);

		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, "header");
		ArrayList<Element> columnsHeaders = getColumnsWithFacet(columns, "header");
		if(header!=null || !columnsHeaders.isEmpty()) {
			Element thead = visualDocument.createElement("thead");
			table.appendChild(thead);
			String headerClass = (String) sourceElement.getAttribute("headerClass");
			if(header != null) {
				encodeTableHeaderOrFooterFacet(creationData, thead, columnsLength, visualDocument, header,
						"dr-table-header rich-table-header",
						"dr-table-header-continue rich-table-header-continue",
						"dr-table-headercell rich-table-headercell",
						headerClass, "td");
			}
			if(!columnsHeaders.isEmpty()) {
				Element tr = visualDocument.createElement("tr");
				thead.appendChild(tr);
				String styleClass = encodeStyleClass(null, "dr-table-subheader rich-table-subheader", null, headerClass);
				if(styleClass!=null) {
					tr.setAttribute("class", styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsHeaders,
						"dr-table-subheadercell rich-table-subheadercell",
						headerClass, "header", "td");
			}
		}

		// Encode Footer
		Element footer = ComponentUtil.getFacet(sourceElement, "footer");
		ArrayList<Element> columnsFooters = getColumnsWithFacet(columns, "footer");
		if (footer != null || !columnsFooters.isEmpty()) {
			Element tfoot = visualDocument.createElement("tfoot");
			table.appendChild(tfoot);
			String footerClass = (String) sourceElement.getAttribute("footerClass");
			if(!columnsFooters.isEmpty()) {
				Element tr = visualDocument.createElement("tr");
				tfoot.appendChild(tr);
				String styleClass = encodeStyleClass(null, "dr-table-subfooter rich-table-subfooter", null, footerClass);
				if(styleClass!=null) {
					tr.setAttribute("class", styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsFooters,
						"dr-table-subfootercell rich-table-subfootercell",
						footerClass, "footer", "td");
			}
			if (footer != null) {
				encodeTableHeaderOrFooterFacet(creationData, tfoot, columnsLength, visualDocument, footer,
						"dr-table-footer rich-table-footer",
						"dr-table-footer-continue rich-table-footer-continue",
						"dr-table-footercell rich-table-footercell",
						footerClass, "td");
			}
		}

		Element tbody = visualDocument.createElement("tbody");
		table.appendChild(tbody);

		// Create mapping to Encode body
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		boolean firstRow = true;
		Element tr = null;
		VpeChildrenInfo trInfo = null;
		for (Node child : children) {
			if(child.getNodeName().endsWith(":column")) {
				String breakBefore = ((Element)child).getAttribute("breakBefore");
				if(breakBefore!=null && breakBefore.equalsIgnoreCase("true")) {
					tr = null;
				}
				if(tr==null) {
					tr = visualDocument.createElement("tr");
					if(firstRow) {
						tr.setAttribute("class", "dr-table-firstrow rich-table-firstrow");
						firstRow = false;
					} else {
						tr.setAttribute("class", "dr-table-row rich-table-row");
					}
					trInfo = new VpeChildrenInfo(tr);
					tbody.appendChild(tr);
					creationData.addChildrenInfo(trInfo);
				}
				trInfo.addSourceChild(child);
			} else if(child.getNodeName().endsWith(":columnGroup")) {
				RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData, (Element)child, visualDocument, tbody);
				tr = null;
			} else if(child.getNodeName().endsWith(":subTable")) {
				RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(creationData, (Element)child, visualDocument, tbody);
				tr = null;
			} else {
				VpeChildrenInfo childInfo = new VpeChildrenInfo(tbody);
				childInfo.addSourceChild(child);
				creationData.addChildrenInfo(childInfo);
				tr = null;
			}
		}

		return creationData;
	}

	protected void encodeCaption(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element table) {
		//Encode caption
		Element captionFromFacet = ComponentUtil.getFacet(sourceElement, "caption");
		if (captionFromFacet != null) {
			String captionClass = (String) table.getAttribute("captionClass");
			String captionStyle = (String) table.getAttribute("captionStyle");

			Element caption = visualDocument.createElement("caption");
			table.appendChild(caption);
			if (captionClass != null && captionClass.length()>0) {
				captionClass = "dr-table-caption rich-table-caption " + captionClass;
			} else {
				captionClass = "dr-table-caption rich-table-caption";
			}
			caption.setAttribute("class", captionClass);
			if (captionStyle != null && captionStyle.length()>0) {
				caption.setAttribute("style", captionStyle);
			}
			VpeChildrenInfo cap = new VpeChildrenInfo(caption);
			cap.addSourceChild(captionFromFacet);
			creationData.addChildrenInfo(cap);
		}

	}

	public static void encodeHeaderOrFooterFacets(VpeCreationData creationData, Element parentTr, Document visualDocument, ArrayList<Element> headersOrFooters, String skinCellClass, String headerClass, String facetName, String element) {
		for (Element column : headersOrFooters) {
			String classAttribute = facetName + "Class";
			String columnHeaderClass = column.getAttribute(classAttribute);
			Element td = visualDocument.createElement(element);
			parentTr.appendChild(td);
			String styleClass = encodeStyleClass(null, skinCellClass, headerClass, columnHeaderClass);
			td.setAttribute("class", styleClass);
			td.setAttribute("scop", "col");
			String colspan = column.getAttribute("colspan");
			if(colspan!=null && colspan.length()>0) {
				td.setAttribute("colspan", colspan);
			}
			Element facetBody = ComponentUtil.getFacet(column, facetName);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	protected void encodeTableHeaderOrFooterFacet(VpeCreationData creationData, Element parentTheadOrTfood, int columns, Document visualDocument, Element facetBody, String skinFirstRowClass, String skinRowClass, String skinCellClass, String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName().endsWith(":columnGroup");
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable");
		if(isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData, facetBody, visualDocument, parentTheadOrTfood);
		} else if(isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(creationData, facetBody, visualDocument, parentTheadOrTfood);
		} else {
			Element tr = visualDocument.createElement("tr");
			parentTheadOrTfood.appendChild(tr);

			String styleClass = encodeStyleClass(null, skinFirstRowClass, facetBodyClass, null);
			if(styleClass!=null) {
				tr.setAttribute("class", styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute("style", style);

			Element td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = encodeStyleClass(null, skinCellClass, facetBodyClass, null);
			if(styleClass!=null) {
				td.setAttribute("class", styleClass);
			}

			if (columns>0) {
				td.setAttribute("colspan", String.valueOf(columns));
			}
			td.setAttribute("scope", "colgroup");

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	public static ArrayList<Element> getColumns(Element parentSourceElement) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList children = parentSourceElement.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if((child instanceof Element) && child.getNodeName().endsWith(":column")) {
				columns.add((Element)child);
			}
		}
		return columns;
	}

	public static ArrayList<Element> getColumnsWithFacet(ArrayList<Element> columns, String facetName) {
		ArrayList<Element> columnsWithFacet = new ArrayList<Element>();
		for (Element column : columns) {
			Element body = ComponentUtil.getFacet(column, facetName);
			if(body!=null) {
				columnsWithFacet.add(column);
			}
		}
		return columnsWithFacet;
	}

	public static String encodeStyleClass(Object parentPredefined, Object predefined, Object parent, Object custom) {
		StringBuffer styleClass = new StringBuffer();
		// Construct predefined classes
		if (null != parentPredefined) {
			styleClass.append(parentPredefined).append(" ");			
		} else if (null != predefined) {
			styleClass.append(predefined).append(" ");
		}
		// Append class from parent component.
		if (null != parent) {
			styleClass.append(parent).append(" ");
		}
		if (null != custom) {
			styleClass.append(custom);
		}
		if (styleClass.length() > 0) {
			return styleClass.toString();
		}
		return null;
	}

	protected int getColumnsCount(Element sourceElement, ArrayList<Element> columns) {
		int count = 0;
		// check for exact value in component
		Integer span = null;
		try {
			span = Integer.valueOf(sourceElement.getAttribute("columns"));			
		} catch (Exception e) {
			// Ignore bad attribute
		}
		if (null != span && span.intValue() != Integer.MIN_VALUE) {
			count = span.intValue();
		} else {
			// calculate max html columns count for all columns/rows children.
			count = calculateRowColumns(sourceElement, columns);
		}
		return count;
	}

	/*
	 * Calculate max number of columns per row. For rows, recursive calculate
	 * max length.
	 */
	private int calculateRowColumns(Element sourceElement, ArrayList<Element> columns) {
		int count = 0;
		int currentLength = 0;
		for (Element column : columns) {
			if (ComponentUtil.isRendered(column)) {
				if (column.getNodeName().endsWith(":columnGroup")) {
					// Store max calculated value of previsous rows.
					if (currentLength > count) {
						count = currentLength;
					}
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement, getColumns(column));
					// Store max calculated value
					if (currentLength > count) {
						count = currentLength;
					}
					currentLength = 0;
				} else if (column.getNodeName().equals(sourceElement.getPrefix() + ":column")) {
					String breakBeforeStr = column.getAttribute("breakBefore");
					boolean breakBefore = false;
					if(breakBeforeStr!=null) {
						try {
							breakBefore = Boolean.getBoolean(breakBeforeStr);
						} catch (Exception e) {
							// Ignore bad attribute
						}
					}
					// For new row, save length of previsous.
					if (breakBefore) {
						if (currentLength > count) {
							count = currentLength;
						}
						currentLength = 0;
					}
					String colspanStr = column.getAttribute("colspan");
					Integer colspan = null;
					try {
						colspan = Integer.valueOf(colspanStr);
					} catch (Exception e) {
						// Ignore
					}
					// Append colspan of this column
					if (null != colspan
							&& colspan.intValue() != Integer.MIN_VALUE) {
						currentLength += colspan.intValue();
					} else {
						currentLength++;
					}
				} else if (column.getNodeName().endsWith(":column")) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}

			}
		}
		if (currentLength > count) {
			count = currentLength;
		}
		return count;
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