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
import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataTableTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		VisualDomUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataTable"); //$NON-NLS-1$ //$NON-NLS-2$
		String tableClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		table.setAttribute(HTML.ATTR_CLASS, "dr-table rich-table " + (tableClass==null?Constants.EMPTY:tableClass)); //$NON-NLS-1$

		// Encode colgroup definition.
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		nsIDOMElement colgroup = visualDocument.createElement(HTML.TAG_COLGROUP);
		colgroup.setAttribute(HTML.ATTR_SPAN, String.valueOf(columnsLength));
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
		final boolean hasColumnWithHeader = hasColumnWithFacet(columns, RichFaces.NAME_FACET_HEADER);
		if(header!=null || hasColumnWithHeader) {
			nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
			table.appendChild(thead);
			String headerClass = (String) sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS);
			if(header != null) {
				encodeTableHeaderOrFooterFacet(pageContext, creationData, thead, columnsLength, visualDocument, header,
						"dr-table-header rich-table-header", //$NON-NLS-1$
						"dr-table-header-continue rich-table-header-continue", //$NON-NLS-1$
						"dr-table-headercell rich-table-headercell", //$NON-NLS-1$
						headerClass, HTML.TAG_TD);
			}
			if(hasColumnWithHeader) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				thead.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null, "dr-table-subheader rich-table-subheader", null, headerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeHeaderOrFooterFacets(pageContext, creationData, tr, visualDocument, columns,
						"dr-table-subheadercell rich-table-subheadercell", //$NON-NLS-1$
						headerClass, RichFaces.NAME_FACET_HEADER, HTML.TAG_TD);
			}
		}

		// Encode Footer
		Node footer = ComponentUtil.getFacet((Element)sourceElement, RichFaces.NAME_FACET_FOOTER,true);
		final boolean hasColumnWithFooter = hasColumnWithFacet(columns, RichFaces.NAME_FACET_FOOTER);
		if (footer != null || hasColumnWithFooter) {
			nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
			table.appendChild(tfoot);
			String footerClass = (String) sourceElement.getAttribute(RichFaces.ATTR_FOOTER_CLASS);
			if(hasColumnWithFooter) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null, "dr-table-subfooter rich-table-subfooter", null, footerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeHeaderOrFooterFacets(pageContext, creationData, tr, visualDocument, columns,
						"dr-table-subfootercell rich-table-subfootercell", //$NON-NLS-1$
						footerClass, RichFaces.NAME_FACET_FOOTER, HTML.TAG_TD);
			}
			if (footer != null) {
				encodeTableHeaderOrFooterFacet(pageContext, creationData, tfoot, columnsLength, visualDocument, footer,
						"dr-table-footer rich-table-footer", //$NON-NLS-1$
						"dr-table-footer-continue rich-table-footer-continue", //$NON-NLS-1$
						"dr-table-footercell rich-table-footercell", //$NON-NLS-1$
						footerClass,HTML.TAG_TD);
			}
		}

		new RichFacesDataTableChildrenEncoder(creationData, visualDocument,
				sourceElement, table).encodeChildren();

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

	public static void encodeHeaderOrFooterFacets(VpePageContext pageContext, VpeCreationData creationData, 
			nsIDOMElement parentTr, nsIDOMDocument visualDocument, ArrayList<Element> headersOrFooters, 
			String skinCellClass, String headerClass, String facetName, String element) {
		for (Element column : headersOrFooters) {
			String classAttribute = facetName + "Class"; //$NON-NLS-1$

			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(element);
			parentTr.appendChild(td);
			String styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, headerClass, columnHeaderClass);
			if (!RichFacesColumnTemplate.isVisible(column)) {
				VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
						HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
			}
			td.setAttribute(HTML.ATTR_CLASS, styleClass);
			td.setAttribute(HTML.ATTR_SCOPE, "col"); //$NON-NLS-1$
			String colspan = column.getAttribute("colspan"); //$NON-NLS-1$
			if(colspan!=null && colspan.length()>0) {
				td.setAttribute(HTML.ATTR_COLSPAN, colspan);
			}
			Node facetBody = ComponentUtil.getFacet(column, facetName,true);

			nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
		    td.appendChild(span);
		    if (RichFaces.NAME_FACET_HEADER.equals(facetName)) {
		    	nsIDOMElement icon = RichFacesColumnTemplate.getHeaderIcon(pageContext, column, visualDocument);
		    	if (icon != null) {
		    		td.appendChild(icon);
		    	}
		    }

			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(span);
			childrenInfo.addSourceChild(facetBody);
			creationData.addChildrenInfo(childrenInfo);
			
		}
	}

	protected void encodeTableHeaderOrFooterFacet(final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMElement parentTheadOrTfood, int columns, nsIDOMDocument visualDocument, Node facetBody, 
			String skinFirstRowClass, String skinRowClass, String skinCellClass, String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP);
		boolean isSubTable = facetBody.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE);
		if(isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else if(isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = ComponentUtil.encodeStyleClass(null, skinFirstRowClass, facetBodyClass, null);
			if(styleClass!=null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HTML.ATTR_STYLE, style);

			nsIDOMElement td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, facetBodyClass, null);
			if(styleClass!=null) {
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
			}

			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			
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
			String nodeName = child.getNodeName();
			if((child instanceof Element) && (
					nodeName.endsWith(RichFaces.TAG_COLUMN) ||
					nodeName.endsWith(RichFaces.TAG_COLUMNS)
					)) {
				columns.add((Element)child);
			}
		}
		return columns;
	}

	/**
	 * Returns true if and only if {@code columns} contains at least one column that have facet 
	 * with given {@code facetName}.
	 */
	public static boolean hasColumnWithFacet(ArrayList<Element> columns, String facetName) {
		for (Element column : columns) {
			Node body = ComponentUtil.getFacet(column, facetName, true);
			if(body!=null) {
				return true;
			}
		}
		
		return false;
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
				String nodeName = column.getNodeName();
				if (nodeName.endsWith(RichFaces.TAG_COLUMN_GROUP)) {
					// Store max calculated value of previous rows.
					count = Math.max(currentLength,count);
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement, getColumns(column));
					// Store max calculated value
					count = Math.max(currentLength,count);
					currentLength = 0;
				} else if (nodeName.equals(sourceElement.getPrefix() + Constants.COLON + RichFaces.TAG_COLUMN) ||
						nodeName.equals(sourceElement.getPrefix() + Constants.COLON + RichFaces.TAG_COLUMNS)) {
					// For new row, save length of previous.
					if (Boolean.getBoolean(column.getAttribute(RichFaces.ATTR_BREAK_BEFORE))) {
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
				} else if (nodeName.endsWith(RichFaces.TAG_COLUMN)) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}
			}
		}
		return Math.max(currentLength,count);
	}
	
	
	
	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#validate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument, org.jboss.tools.vpe.editor.template.VpeCreationData)
	 */
	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		RichFacesDataTableChildrenEncoder.validateChildren(pageContext, sourceNode, visualDocument, data);
		
		final RichFacesDataTableStyleClassesApplier styleClassesApplier = 
			new RichFacesDataTableStyleClassesApplier(visualDocument, 
					pageContext, sourceNode);
		styleClassesApplier.applyClasses((nsIDOMElement) data.getNode());
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
