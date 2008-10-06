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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataTableTemplate extends VpeAbstractTemplate {

	private static final String ATTR_BREAK_BEFORE = "breakBefore"; //$NON-NLS-1$
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		ComponentUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataTable"); //$NON-NLS-1$ //$NON-NLS-2$
		String tableClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		table.setAttribute(HTML.ATTR_CLASS, "dr-table rich-table " + (tableClass==null?Constants.EMPTY:tableClass)); //$NON-NLS-1$

		// Encode colgroup definition.
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		nsIDOMElement colgroup = visualDocument.createElement(HTML.TAG_COLGROUP);
		colgroup.setAttribute(HTML.TAG_SPAN, String.valueOf(columnsLength));
		table.appendChild(colgroup);

		String columnsWidth = sourceElement.getAttribute(RichFaces.ATTR_COLUMNS_WIDTH);
		if (null != columnsWidth) {
			String[] widths = columnsWidth.split(Constants.COMMA);
			for (int i = 0; i < widths.length; i++) {
				nsIDOMElement col = visualDocument.createElement(HTML.TAG_COL);
				col.setAttribute(HTML.ATTR_WIDTH, widths[i]);
				colgroup.appendChild(col);
			}
		}

		//Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, table);

		// Encode Header
		Node header = ComponentUtil.getFacet((Element)sourceElement, RichFaces.NAME_FACET_HEADER,true);
		ArrayList<Element> columnsHeaders = getColumnsWithFacet(columns, RichFaces.NAME_FACET_HEADER);
		if(header!=null || !columnsHeaders.isEmpty()) {
			nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
			table.appendChild(thead);
			String headerClass = (String) sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS);
			if(header != null) {
				encodeTableHeaderOrFooterFacet(creationData, thead, columnsLength, visualDocument, header,
						"dr-table-header rich-table-header", //$NON-NLS-1$
						"dr-table-header-continue rich-table-header-continue", //$NON-NLS-1$
						"dr-table-headercell rich-table-headercell", //$NON-NLS-1$
						headerClass, HTML.TAG_TD);
			}
			if(!columnsHeaders.isEmpty()) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				thead.appendChild(tr);
				String styleClass = encodeStyleClass(null, "dr-table-subheader rich-table-subheader", null, headerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsHeaders,
						"dr-table-subheadercell rich-table-subheadercell", //$NON-NLS-1$
						headerClass, RichFaces.NAME_FACET_HEADER, HTML.TAG_TD);
			}
		}

		// Encode Footer
		Element footer = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
		ArrayList<Element> columnsFooters = getColumnsWithFacet(columns, RichFaces.NAME_FACET_FOOTER);
		if (footer != null || !columnsFooters.isEmpty()) {
			nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
			table.appendChild(tfoot);
			String footerClass = (String) sourceElement.getAttribute(RichFaces.ATTR_FOOTER_CLASS);
			if(!columnsFooters.isEmpty()) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = encodeStyleClass(null, "dr-table-subfooter rich-table-subfooter", null, footerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsFooters,
						"dr-table-subfootercell rich-table-subfootercell", //$NON-NLS-1$
						footerClass, RichFaces.NAME_FACET_FOOTER, HTML.TAG_TD);
			}
			if (footer != null) {
				encodeTableHeaderOrFooterFacet(creationData, tfoot, columnsLength, visualDocument, footer,
						"dr-table-footer rich-table-footer", //$NON-NLS-1$
						"dr-table-footer-continue rich-table-footer-continue", //$NON-NLS-1$
						"dr-table-footercell rich-table-footercell", //$NON-NLS-1$
						footerClass,HTML.TAG_TD);
			}
		}

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		table.appendChild(tbody);

		// Create mapping to Encode body
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		boolean firstRow = true;
		nsIDOMElement tr = null;
		VpeChildrenInfo trInfo = null;
		for (Node child : children) {
			if(child.getNodeName().endsWith(RichFaces.TAG_COLUMN)) {
				String breakBefore = ((Element)child).getAttribute(ATTR_BREAK_BEFORE);
				if(breakBefore!=null && breakBefore.equalsIgnoreCase(Constants.TRUE)) {
					tr = null;
				}
				if(tr==null) {
					tr = visualDocument.createElement(HTML.TAG_TR);
					if(firstRow) {
						tr.setAttribute(HTML.ATTR_CLASS, "dr-table-firstrow rich-table-firstrow"); //$NON-NLS-1$
						firstRow = false;
					} else {
						tr.setAttribute(HTML.ATTR_CLASS, "dr-table-row rich-table-row"); //$NON-NLS-1$
					}
					trInfo = new VpeChildrenInfo(tr);
					tbody.appendChild(tr);
					creationData.addChildrenInfo(trInfo);
				}
					trInfo.addSourceChild(child);

			} else if(child.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData, (Element)child, visualDocument, tbody);
				tr = null;
			} else if(child.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE)) {
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

	protected void encodeCaption(VpeCreationData creationData, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMElement table) {
		//Encode caption
		Element captionFromFacet = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_CAPTION);
		if (captionFromFacet != null) {
			String captionClass = (String) table.getAttribute(RichFaces.ATTR_CAPTION_CLASS);
			String captionStyle = (String) table.getAttribute(RichFaces.ATTR_CAPTION_STYLE);

			nsIDOMElement caption = visualDocument.createElement(HTML.TAG_CAPTION);
			table.appendChild(caption);
			if (captionClass != null && captionClass.length()>0) {
				captionClass = "dr-table-caption rich-table-caption " + captionClass; //$NON-NLS-1$
			} else {
				captionClass = "dr-table-caption rich-table-caption"; //$NON-NLS-1$
			}
			caption.setAttribute(HTML.ATTR_CLASS, captionClass);
			if (captionStyle != null && captionStyle.length()>0) {
				caption.setAttribute(HTML.ATTR_STYLE, captionStyle);
			}
			
			VpeChildrenInfo cap = new VpeChildrenInfo(caption);
			cap.addSourceChild(captionFromFacet);
			creationData.addChildrenInfo(cap);
		}

	}

	public static void encodeHeaderOrFooterFacets(VpeCreationData creationData, nsIDOMElement parentTr, nsIDOMDocument visualDocument, ArrayList<Element> headersOrFooters, String skinCellClass, String headerClass, String facetName, String element) {
		for (Element column : headersOrFooters) {
			String classAttribute = facetName + "Class"; //$NON-NLS-1$
			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(element);
			parentTr.appendChild(td);
			String styleClass = encodeStyleClass(null, skinCellClass, headerClass, columnHeaderClass);
			td.setAttribute(HTML.ATTR_CLASS, styleClass);
			td.setAttribute(HTML.ATTR_SCOPE, "col"); //$NON-NLS-1$
			String colspan = column.getAttribute("colspan"); //$NON-NLS-1$
			if(colspan!=null && colspan.length()>0) {
				td.setAttribute(HTML.ATTR_COLSPAN, colspan);
			}
			Node facetBody = ComponentUtil.getFacet(column, facetName,true);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	protected void encodeTableHeaderOrFooterFacet(VpeCreationData creationData, nsIDOMElement parentTheadOrTfood, int columns, nsIDOMDocument visualDocument, Node facetBody, String skinFirstRowClass, String skinRowClass, String skinCellClass, String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP);
		boolean isSubTable = facetBody.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE);
		if(isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else if(isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = encodeStyleClass(null, skinFirstRowClass, facetBodyClass, null);
			if(styleClass!=null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HTML.ATTR_STYLE, style);

			nsIDOMElement td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = encodeStyleClass(null, skinCellClass, facetBodyClass, null);
			if(styleClass!=null) {
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
			}

			if (columns>0) {
				td.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(columns));
			}
			td.setAttribute(HTML.ATTR_SCOPE, "colgroup"); //$NON-NLS-1$

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	public static ArrayList<Element> getColumns(Node parentSourceElement) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList children = parentSourceElement.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if((child instanceof Element) && child.getNodeName().endsWith(RichFaces.TAG_COLUMN)) {
				columns.add((Element)child);
			}
		}
		return columns;
	}

	public static ArrayList<Element> getColumnsWithFacet(ArrayList<Element> columns, String facetName) {
		ArrayList<Element> columnsWithFacet = new ArrayList<Element>();
		for (Element column : columns) {
			Node body = ComponentUtil.getFacet(column, facetName,true);
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
			styleClass.append(parentPredefined).append(Constants.WHITE_SPACE);			
		} else if (null != predefined) {
			styleClass.append(predefined).append(Constants.WHITE_SPACE);
		}
		// Append class from parent component.
		if (null != parent) {
			styleClass.append(parent).append(Constants.WHITE_SPACE);
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
		try {
			count = Integer.parseInt(sourceElement.getAttribute(RichFaces.ATTR_COLUMNS));
		} catch (NumberFormatException e) {
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
				if (column.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP)) {
					// Store max calculated value of previous rows.
					count = Math.max(currentLength,count);
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement, getColumns(column));
					// Store max calculated value
					count = Math.max(currentLength,count);
					currentLength = 0;
				} else if (column.getNodeName().equals(sourceElement.getPrefix() + Constants.COLON + RichFaces.TAG_COLUMN)) {
					// For new row, save length of previous.
					if (Boolean.getBoolean(column.getAttribute(ATTR_BREAK_BEFORE))) {
						count = Math.max(currentLength,count);
						currentLength = 0;
					}
					String colspanStr = column.getAttribute("colspan"); //$NON-NLS-1$
					Integer colspan = null;
					try {
						currentLength += Integer.parseInt(colspanStr);
					} catch (NumberFormatException e) {
						currentLength++;
					}
				} else if (column.getNodeName().endsWith(RichFaces.TAG_COLUMN)) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}
			}
		}
		return Math.max(currentLength,count);
	}
	
	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		nsIDOMElement visualElement = (nsIDOMElement)visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		visualElement.removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		nsIDOMElement visualElement = (nsIDOMElement)visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		visualElement.setAttribute(name, value);
	}

}