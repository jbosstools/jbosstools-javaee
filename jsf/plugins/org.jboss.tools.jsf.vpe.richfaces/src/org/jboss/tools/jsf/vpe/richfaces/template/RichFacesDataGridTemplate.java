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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesDataGridTemplate extends RichFacesDataTableTemplate {

    	/*
    	 * https://jira.jboss.org/jira/browse/JBIDE-3491
    	 * Set default table size when no attributes are specified.
    	 */
	private int defaultRows = 3;
	private int defaultColumns = 1;
	private String[] rowClasses;
	private String[] columnClasses;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		initStyleClasses(sourceElement);

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		VisualDomUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);
		creationData.addChildrenInfo(new VpeChildrenInfo(null));

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataGrid"); //$NON-NLS-1$ //$NON-NLS-2$
		String tableClass = "dr-table rich-table"; //$NON-NLS-1$
		if(sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			tableClass += " " + sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS); //$NON-NLS-1$
		}
		table.setAttribute(HTML.ATTR_CLASS, tableClass);

		/*
		 * Encode colgroup definition.
		 */
		int columnsLength = getColumnsCount(sourceElement);
		nsIDOMElement colgroup = visualDocument.createElement(HTML.TAG_COLGROUP);
		colgroup.setAttribute(HTML.ATTR_SPAN, String.valueOf(columnsLength));
		table.appendChild(colgroup);
		
		/*
		 * Encode Caption
		 */
		Element caption = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_CAPTION);
		Map<String, List<Node>> captionFacetChildren = VisualDomUtil.findFacetElements(caption, pageContext);
		Node captionBody = ComponentUtil.getFacetBody(captionFacetChildren);
		encodeCaption(pageContext, creationData, sourceElement, visualDocument, table, captionBody);

		/*
		 * Encode Header
		 */
		Element header = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_HEADER);
		Map<String, List<Node>> headerFacetChildren = VisualDomUtil.findFacetElements(header, pageContext);
		Node headerBody = ComponentUtil.getFacetBody(headerFacetChildren);
		if (headerBody != null) {
			nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
			table.appendChild(thead);
			String headerClass = sourceElement.hasAttribute(RichFaces.ATTR_HEADER_CLASS) ? sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS) : null;
			encodeTableHeaderOrFooterFacet(pageContext, creationData, thead, columnsLength, visualDocument, headerBody,
					"dr-table-header rich-table-header", //$NON-NLS-1$
					"dr-table-header-continue rich-table-header-continue", //$NON-NLS-1$
					"dr-table-headercell rich-table-headercell", //$NON-NLS-1$
					headerClass, HTML.TAG_TD, false);
		}

		/*
		 * Encode Footer
		 */
		Element footer = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_FOOTER);
		Map<String, List<Node>> footerFacetChildren = VisualDomUtil.findFacetElements(footer, pageContext);
		Node footerBody = ComponentUtil.getFacetBody(footerFacetChildren);
		if (footerBody != null) {
			nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
			table.appendChild(tfoot);
			String footerClass = sourceElement.hasAttribute(RichFaces.ATTR_FOOTER_CLASS) ? sourceElement.getAttribute(RichFaces.ATTR_FOOTER_CLASS) : null;
			encodeTableHeaderOrFooterFacet(pageContext, creationData, tfoot, columnsLength, visualDocument, footerBody,
					"dr-table-footer rich-table-footer", //$NON-NLS-1$
					"dr-table-footer-continue rich-table-footer-continue", //$NON-NLS-1$
					"dr-table-footercell rich-table-footercell", //$NON-NLS-1$
					footerClass, HTML.TAG_TD, false);
		}

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		table.appendChild(tbody);

		/*
		 * https://jira.jboss.org/jira/browse/JBIDE-3491
		 * Encode body.
		 * Add text nodes to children list too.
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		sourceElement.getAttribute(RichFaces.ATTR_ELEMENTS);

		int elementsCount = getElementsCount(sourceElement, columnsLength);
		
		if(columnsLength>0) {
			int rowIndex = 0;
			for(int elementIndex = 0; elementIndex<elementsCount; rowIndex++) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				tbody.appendChild(tr);
				tr.setAttribute(HTML.ATTR_CLASS, "dr-table-row rich-table-row " + getRowClass(rowIndex)); //$NON-NLS-1$
				for(int columnIndex = 0; columnIndex<columnsLength && elementIndex<elementsCount; columnIndex++) {
					nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
					tr.appendChild(td);
					td.setAttribute(HTML.ATTR_CLASS, "dr-table-cell rich-table-cell " + getColumnClass(columnIndex)); //$NON-NLS-1$
					/*
					 * Add HTML elements from caption, header and footer. 
					 */
					VpeChildrenInfo childInfo = null;
					if (captionFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0) {
						childInfo = new VpeChildrenInfo(td);
						for (Node child : captionFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
						    childInfo.addSourceChild(child);
						}
						creationData.addChildrenInfo(childInfo);
					}
					if (headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0) {
						childInfo = new VpeChildrenInfo(td);
						for (Node child : headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
							childInfo.addSourceChild(child);
						}
						creationData.addChildrenInfo(childInfo);
					}
					if (footerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0) {
						childInfo = new VpeChildrenInfo(td);
						for (Node child : footerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
							childInfo.addSourceChild(child);
						}
						creationData.addChildrenInfo(childInfo);
					}
					if(!children.isEmpty()) {
						childInfo = new VpeChildrenInfo(td);
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
		String columnClassesString = sourceElement.getAttribute(RichFaces.ATTR_COLUMN_CLASSES);
		String rowClassesString = sourceElement.getAttribute(RichFaces.ATTR_ROW_CLASSES);
		columnClasses = parceClasses(columnClassesString);
		rowClasses = parceClasses(rowClassesString);
	}

	private String[] parceClasses(String classes) {
		if(classes==null) {
			return new String[]{Constants.EMPTY};
		}
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(classes, Constants.COMMA, false);
		while(st.hasMoreElements()) {
			list.add((String)st.nextElement());
		}
		if(list.isEmpty()) {
			return new String[]{Constants.EMPTY};
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
		int count = defaultColumns;
		// check for exact value in component
		try {
			int span = Integer.parseInt(sourceElement.getAttribute(RichFaces.ATTR_COLUMNS));
			count = span > 0 ? span : defaultColumns;
		} catch (NumberFormatException e) {
			// Ignore wrong formatted attribute 
		}
		return count;
	}

	protected int getElementsCount(Element sourceElement, int columnCount) {
		int elements = columnCount * defaultRows;
		// check for exact value in component
		try {
			int span = Integer.parseInt(sourceElement.getAttribute(RichFaces.ATTR_ELEMENTS));
			elements = span>0 ? span : columnCount * defaultRows;
		} catch (NumberFormatException e) {
			elements = columnCount * defaultRows;
		}
		return elements;
	}
	
	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		nsIDOMElement visualElement = queryInterface(visualNode, nsIDOMElement.class); 
		visualElement.removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		nsIDOMElement visualElement = queryInterface(visualNode, nsIDOMElement.class); 
		visualElement.setAttribute(name, value);
	}

}