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
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Displays template for extendedDataTable
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesExtendedDataTableTemplate extends VpeAbstractTemplate {

	private static final String PADDING_0PX = "padding: 0px;"; //$NON-NLS-1$
	private static final String RIGHT = "right"; //$NON-NLS-1$
	private static final String TOP = "top"; //$NON-NLS-1$
	private static final String VALUE = "100"; //$NON-NLS-1$
	private static final String _100 = "100%"; //$NON-NLS-1$
	private static final String _17PX = "17px"; //$NON-NLS-1$
	private static final String SCROLL_STYLE = "width: 17px; overflow : scroll; height : 100%;"; //$NON-NLS-1$
	private static final String VERTICAL_ALIGN_MIDDLE = "vertical-align:middle;"; //$NON-NLS-1$
	private static final String DISPLAY_NONE = "display : none"; //$NON-NLS-1$
	private static final String FILTER_BY = "filterBy"; //$NON-NLS-1$
	private static final String DIV_STYLE = "padding : 4px"; //$NON-NLS-1$
	private static final String EXTENDED_TABLE_INPUT = "extendedTable-input"; //$NON-NLS-1$
	private static final String INPUT_TYPE_ATTR = "text"; //$NON-NLS-1$
	private static final String FALSE = "false"; //$NON-NLS-1$
	private static final String SCOP = "scop"; //$NON-NLS-1$
	private static final String COL = "col"; //$NON-NLS-1$
	private static final String DR_TABLE_SUBFOOTERCELL_RICH_TABLE_SUBFOOTERCELL = "dr-table-subfootercell rich-table-subfootercell"; //$NON-NLS-1$
	private static final String DR_TABLE_SUBFOOTER_RICH_TABLE_SUBFOOTER = "dr-table-subfooter rich-table-subfooter"; //$NON-NLS-1$
	private static final String DR_TABLE_SUBHEADERCELL_RICH_TABLE_SUBHEADERCELL = "dr-table-subheadercell rich-table-subheadercell"; //$NON-NLS-1$
	private static final String DR_TABLE_SUBHEADER_RICH_TABLE_SUBHEADER = "dr-table-subheader rich-table-subheader"; //$NON-NLS-1$
	private static final String DR_BODY_TABLE_TR = "dr-body-table-tr"; //$NON-NLS-1$ 
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String RICH_FACES_DATA_TABLE = "richFacesDataTable"; //$NON-NLS-1$
	private static final String EXTENDED_DATA_TABLE_CSS = "extendedDataTable/extendedDataTable.css"; //$NON-NLS-1$
	private static final String SEMICOLON = ";"; //$NON-NLS-1$
	private static final String COLON = " : "; //$NON-NLS-1$
	private static final String COLUMN = "rich:column"; //$NON-NLS-1$
	final static String DEFAULT_HEIGHT = "500px"; //$NON-NLS-1$
	final static String HEADER = "header"; //$NON-NLS-1$
	final static String HEADER_CLASS = "headerClass"; //$NON-NLS-1$
	final static String FOOTER = "footer"; //$NON-NLS-1$
	final static String FOOTER_CLASS = "footerClass"; //$NON-NLS-1$
	final static String CAPTION_CLASS = "captionClass"; //$NON-NLS-1$
	final static String CAPTION_STYLE = "captionStyle"; //$NON-NLS-1$
	final static String ATTR_SORTABLE = "sortable"; //$NON-NLS-1$
	final static String SPACE = " "; //$NON-NLS-1$
	final static String ZERRO = "0"; //$NON-NLS-1$
	final static String SORTABLE_PATH = "extendedDataTable/sortable.gif"; //$NON-NLS-1$
	final static String SORT_BY_ATTR = "sortBy"; //$NON-NLS-1$
	final static String COMMA = ","; //$NON-NLS-1$

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have
	 * child nodes.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String width = ComponentUtil.getAttribute(sourceElement,
				HTML.ATTR_WIDTH);
		String height = ComponentUtil.getAttribute(sourceElement,
				HTML.ATTR_HEIGHT);
		String style = ComponentUtil.getAttribute(sourceElement,
				HTML.ATTR_STYLE);
		String styleCommonClass = ComponentUtil.getAttribute(sourceElement,
				RichFaces.ATTR_STYLE_CLASS);

		ComponentUtil.setCSSLink(pageContext, EXTENDED_DATA_TABLE_CSS,
				RICH_FACES_DATA_TABLE);

		final String commonTableHeight = height.length() > 0 ? height : DEFAULT_HEIGHT;
		final String commonTableWidth = (width.length() > 0 ? width : _100);

		// -----------CommonTable
		nsIDOMElement tableCommon = visualDocument
				.createElement(HTML.TAG_TABLE);
		tableCommon.setAttribute(HTML.ATTR_BORDER, ZERRO);
		tableCommon.setAttribute(HTML.ATTR_CELLPADDING, ZERRO);
		tableCommon.setAttribute(HTML.ATTR_CELLSPACING, ZERRO);
		tableCommon.setAttribute(HTML.ATTR_WIDTH, commonTableWidth);
		tableCommon.setAttribute(HTML.ATTR_HEIGHT, commonTableHeight);

		VpeCreationData vpeCreationData = new VpeCreationData(tableCommon);
		ArrayList<Element> columns = getColumns(sourceNode);
		int columnsLength = columns.size();

		// Add colgroup
		nsIDOMElement colgroup = visualDocument
				.createElement(HTML.TAG_COLGROUP);
		tableCommon.appendChild(colgroup);

		for (int i = 0; i < columnsLength; i++) {
			nsIDOMElement col = visualDocument.createElement(HTML.TAG_COL);
			col.setAttribute(HTML.ATTR_WIDTH, VALUE);
			colgroup.appendChild(col);
		}

		// Add Head
		String headerClass = (String) sourceElement.getAttribute(HEADER_CLASS);
		nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
		tableCommon.appendChild(thead);
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement filterTR = visualDocument.createElement(HTML.TAG_TR);
		thead.appendChild(tr);
		thead.appendChild(filterTR);
		String styleClass = ComponentUtil.encodeStyleClass(null,
				DR_TABLE_SUBHEADER_RICH_TABLE_SUBHEADER, null, headerClass);
		if (styleClass != null) {
			tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			filterTR.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		encodeHeaderFacets(pageContext, vpeCreationData, tr, filterTR,
				visualDocument, columns,
				DR_TABLE_SUBHEADERCELL_RICH_TABLE_SUBHEADERCELL, headerClass);

		// Add footer
		nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
		tableCommon.appendChild(tfoot);
		String footerClass = (String) sourceElement.getAttribute(FOOTER_CLASS);
		nsIDOMElement tfootTR = visualDocument.createElement(HTML.TAG_TR);
		tfoot.appendChild(tfootTR);
		String styleFooterClass = ComponentUtil.encodeStyleClass(
				null, DR_TABLE_SUBFOOTER_RICH_TABLE_SUBFOOTER, null,
				footerClass);
		if (styleFooterClass != null) {
			tfootTR.setAttribute(HTML.ATTR_CLASS, styleFooterClass);
		}
		encodeFooterFacets(pageContext, vpeCreationData, tfootTR,
				visualDocument, columns,
				DR_TABLE_SUBFOOTERCELL_RICH_TABLE_SUBFOOTERCELL,
				styleFooterClass);

		// Add tbody
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		tableCommon.appendChild(tbody);

		nsIDOMElement bodyTR = visualDocument.createElement(HTML.TAG_TR);
		bodyTR.setAttribute(HTML.ATTR_HEIGHT, _100);
		tbody.appendChild(bodyTR);

		nsIDOMElement bodyTD = visualDocument.createElement(HTML.TAG_TD);
		bodyTD.setAttribute(HTML.ATTR_COLSPAN, Integer.toString(columnsLength)
				.toString());
		bodyTD.setAttribute(HTML.ATTR_VALIGN, TOP);
		bodyTD.setAttribute(HTML.ATTR_STYLE, PADDING_0PX);

		bodyTR.appendChild(bodyTD);

		// Add body Table
		String border = ComponentUtil.getAttribute(sourceElement,
				HTML.ATTR_BORDER);

		nsIDOMElement bodyTable = visualDocument.createElement(HTML.TAG_TABLE);
		if (border.length() > 0)
			bodyTable.setAttribute(HTML.ATTR_BORDER, border);
		if (style.length() > 0) {
			bodyTable.setAttribute(HTML.ATTR_STYLE, style);
		}
		if (styleCommonClass.length() > 0) {
			tableCommon.setAttribute(HTML.ATTR_CLASS, styleCommonClass);
			bodyTable.setAttribute(HTML.ATTR_CLASS, styleCommonClass);
		}
		bodyTable.setAttribute(HTML.ATTR_WIDTH, _100);
		bodyTD.appendChild(bodyTable);

		nsIDOMElement bodyColgroup = visualDocument
				.createElement(HTML.TAG_COLGROUP);
		bodyTable.appendChild(bodyColgroup);

		for (int i = 0; i < columnsLength; i++) {
			nsIDOMElement col = visualDocument.createElement(HTML.TAG_COL);
			col.setAttribute(HTML.ATTR_WIDTH, VALUE);
			bodyColgroup.appendChild(col);
		}
		nsIDOMElement col = visualDocument.createElement(HTML.TAG_COL);
		bodyColgroup.appendChild(col);

		nsIDOMElement tableTbody = visualDocument.createElement(HTML.TAG_TBODY);
		bodyTable.appendChild(tableTbody);

		VisualDomUtil.setSubAttribute(tableCommon, HTML.TAG_STYLE, HTML.STYLE_PARAMETER_TABLE_LAYOUT, HTML.STYLE_VALUE_FIXED);
		VisualDomUtil.setSubAttribute(bodyTable, HTML.TAG_STYLE, HTML.STYLE_PARAMETER_TABLE_LAYOUT, HTML.STYLE_VALUE_FIXED);

		nsIDOMElement tbodyTR = visualDocument.createElement(HTML.TAG_TR);
		// Add rowClasses attr
		String rowClasses = ComponentUtil.getAttribute(sourceElement,
				RichFaces.ATTR_ROW_CLASSES);
		String tbodyTRClass = DR_BODY_TABLE_TR;
		if (rowClasses.length() > 0) {
			String[] rowsClass = rowClasses.split(COMMA);
			if (rowsClass != null && rowsClass.length > 0) {
				// Add first rowClass
				tbodyTRClass+= rowsClass[0];
			}
		}
		
		tbodyTR.setAttribute(HTML.ATTR_CLASS, tbodyTRClass);

		tableTbody.appendChild(tbodyTR);
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		VpeChildrenInfo trInfo = new VpeChildrenInfo(tbodyTR);
		vpeCreationData.addChildrenInfo(trInfo);

		for (Node child : children) {
			if (child.getNodeName().equals(COLUMN)) {
				trInfo.addSourceChild(child);
			}
		}

		// Add scroll
		nsIDOMElement scrollTD = visualDocument.createElement(HTML.TAG_TD);
		scrollTD.setAttribute(HTML.ATTR_ALIGN, RIGHT);
		scrollTD.setAttribute(HTML.ATTR_WIDTH, _17PX);
		bodyTR.appendChild(scrollTD);

		nsIDOMElement scrollDiv = visualDocument.createElement(HTML.TAG_DIV);
		scrollDiv.setAttribute(HTML.ATTR_STYLE, SCROLL_STYLE);
		scrollTD.appendChild(scrollDiv);

		return vpeCreationData;
	}

	/**
	 * 
	 * @param sourceNode
	 * @return
	 */
	public ArrayList<Element> getColumns(Node sourceNode) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList list = sourceNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element && node.getNodeName().equals(COLUMN)) {
				columns.add((Element) node);
			}
		}
		return columns;
	}

	/**
	 * 
	 * @param creationData
	 * @param parentTr
	 * @param visualDocument
	 * @param footers
	 * @param skinCellClass
	 * @param footerClass
	 */
	public void encodeFooterFacets(VpePageContext pageContext,
			VpeCreationData creationData, nsIDOMElement parentTr,
			nsIDOMDocument visualDocument, ArrayList<Element> footers,
			String skinCellClass, String footerClass) {
		String classAttribute = "footerClass"; //$NON-NLS-1$
		String styleClass = EMPTY;
		for (Element column : footers) {

			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
			parentTr.appendChild(td);
			styleClass = ComponentUtil.encodeStyleClass(null,
					skinCellClass, footerClass, columnHeaderClass);
			td.setAttribute(HTML.ATTR_CLASS, styleClass);
			td.setAttribute(SCOP, COL);
			String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
			if (colspan != null && colspan.length() > 0) {
				td.setAttribute(HTML.ATTR_COLSPAN, colspan);
			}
			/*
			 * Get all facet's children. And display only the first one JSF tag.
			 */
			Node facetBody = ComponentUtil.getFacetBody(pageContext, column,
					RichFaces.NAME_FACET_FOOTER);
			/*
			 * Add suitable facet child if there is any. 
			 */
			if (null != facetBody) {
				VpeChildrenInfo child = new VpeChildrenInfo(td);
				child.addSourceChild(facetBody);
				creationData.addChildrenInfo(child);
			}
		}

		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		td.setAttribute(HTML.ATTR_CLASS, styleClass);
		td.appendChild(visualDocument.createTextNode(SPACE));
		parentTr.appendChild(td);
	}

	/**
	 * 
	 * @param creationData
	 * @param parentTr
	 * @param filterTR
	 * @param visualDocument
	 * @param headers
	 * @param skinCellClass
	 * @param headerClass
	 */
	public void encodeHeaderFacets(VpePageContext pageContext,
			VpeCreationData creationData, nsIDOMElement parentTr,
			nsIDOMElement filterTR, nsIDOMDocument visualDocument,
			ArrayList<Element> headers, String skinCellClass, String headerClass) {
		String classAttribute = "headerClass"; //$NON-NLS-1$
		String styleClass = EMPTY;
		// Check filter
		boolean existFilters = false;
		for (Element column : headers) {
			if (ComponentUtil.getAttribute(column, FILTER_BY).length() > 0) {
				existFilters = true;
				break;
			}
		}

		// Not filters
		if (!existFilters) {
			filterTR.setAttribute(HTML.ATTR_STYLE, DISPLAY_NONE);
		}

		for (Element column : headers) {
			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TH);

			nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
			td.appendChild(span);

			parentTr.appendChild(td);
			styleClass = ComponentUtil.encodeStyleClass(null,
					skinCellClass, headerClass, columnHeaderClass);
			td.setAttribute(HTML.ATTR_CLASS, styleClass);
			td.setAttribute(SCOP, COL);
			String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
			if (colspan != null && colspan.length() > 0) {
				td.setAttribute(HTML.ATTR_COLSPAN, colspan);
			}
			/*
			 * Get all facet's children. And display only the first one JSF tag.
			 */
			Node facetBody = ComponentUtil.getFacetBody(pageContext, column,
					RichFaces.NAME_FACET_HEADER);
			/*
			 * Add suitable facet child if there is any. 
			 */
			if (null != facetBody) {
				VpeChildrenInfo child = new VpeChildrenInfo(span);
				child.addSourceChild(facetBody);
				creationData.addChildrenInfo(child);
			}
			// Add filter
			if (existFilters) {
				nsIDOMElement filterTD = visualDocument
						.createElement(HTML.TAG_TD);

				filterTR.appendChild(filterTD);
				filterTD.setAttribute(HTML.ATTR_CLASS, styleClass);
				filterTD.setAttribute(SCOP, COL);
				if (colspan != null && colspan.length() > 0) {
					filterTD.setAttribute(HTML.ATTR_COLSPAN, colspan);
				}
				// Check current filter
				if (ComponentUtil.getAttribute(column, FILTER_BY).length() > 0) {
					// Add input
					nsIDOMElement div = visualDocument
							.createElement(HTML.TAG_DIV);
					div.setAttribute(HTML.ATTR_STYLE, DIV_STYLE);
					filterTD.appendChild(div);
					nsIDOMElement input = visualDocument
							.createElement(HTML.TAG_INPUT);
					div.appendChild(input);
					input.setAttribute(HTML.ATTR_TYPE, INPUT_TYPE_ATTR);
					input.setAttribute(HTML.ATTR_CLASS, EXTENDED_TABLE_INPUT);
				}

			}
			// Add sortable attribute
			String sortable = ComponentUtil.getAttribute(column, ATTR_SORTABLE);
			String sortBy = ComponentUtil.getAttribute(column, SORT_BY_ATTR);
			if (sortBy.length() != 0 || !sortable.equalsIgnoreCase(FALSE)) {
				nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
				img.setAttribute(HTML.ATTR_STYLE, VERTICAL_ALIGN_MIDDLE);
				ComponentUtil.setImg(img, SORTABLE_PATH);
				td.appendChild(img);
			} 
		}

		nsIDOMElement th = visualDocument.createElement(HTML.TAG_TH);
		th.setAttribute(HTML.ATTR_CLASS, styleClass);
		th.appendChild(visualDocument.createTextNode(SPACE));
		parentTr.appendChild(th);

		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		td.setAttribute(HTML.ATTR_CLASS, styleClass);
		td.appendChild(visualDocument.createTextNode(SPACE));
		filterTR.appendChild(td);
	}
	
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
