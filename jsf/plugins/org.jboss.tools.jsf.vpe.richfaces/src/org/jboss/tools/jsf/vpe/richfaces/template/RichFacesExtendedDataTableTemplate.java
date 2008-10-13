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

    private static final String DISPLAY_NONE = "display : none"; //$NON-NLS-1$
    private static final String FILTER_BY = "filterBy"; //$NON-NLS-1$
    private static final String DIV_STYLE = "padding : 4px"; //$NON-NLS-1$
    private static final String EXTENDED_TABLE_INPUT = "extendedTable-input"; //$NON-NLS-1$
    private static final String INPUT_TYPE_ATTR = "text"; //$NON-NLS-1$
    private static final String COLUMNS = "columns"; //$NON-NLS-1$
    private static final String FALSE = "false"; //$NON-NLS-1$
    private static final String SCOP = "scop"; //$NON-NLS-1$
    private static final String COL = "col"; //$NON-NLS-1$
    private static final String TABLE_CAPTION_RICH_TABLE_CAPTION = "dr-table-caption rich-table-caption"; //$NON-NLS-1$
    private static final String DR_TABLE_CAPTION_RICH_TABLE_CAPTION = "dr-table-caption rich-table-caption "; //$NON-NLS-1$
    private static final String SUB_TABLE = ":subTable"; //$NON-NLS-1$
    private static final String COLUMN_GROUP = ":columnGroup"; //$NON-NLS-1$
    private static final String DR_TABLE_ROW_RICH_TABLE_ROW = "dr-table-row rich-table-row"; //$NON-NLS-1$
    private static final String DR_TABLE_FIRSTROW_RICH_TABLE_FIRSTROW = "dr-table-firstrow rich-table-firstrow"; //$NON-NLS-1$
    private static final String TRUE = "true"; //$NON-NLS-1$
    private static final String BREAK_BEFORE = "breakBefore"; //$NON-NLS-1$
    private static final String DR_TABLE_FOOTERCELL_RICH_TABLE_FOOTERCELL = "dr-table-footercell rich-table-footercell"; //$NON-NLS-1$
    private static final String DR_TABLE_FOOTER_CONTINUE_RICH_TABLE_FOOTER_CONTINUE = "dr-table-footer-continue rich-table-footer-continue"; //$NON-NLS-1$
    private static final String DR_TABLE_FOOTER_RICH_TABLE_FOOTER = "dr-table-footer rich-table-footer"; //$NON-NLS-1$
    private static final String DR_TABLE_SUBFOOTERCELL_RICH_TABLE_SUBFOOTERCELL = "dr-table-subfootercell rich-table-subfootercell"; //$NON-NLS-1$
    private static final String DR_TABLE_SUBFOOTER_RICH_TABLE_SUBFOOTER = "dr-table-subfooter rich-table-subfooter"; //$NON-NLS-1$
    private static final String DR_TABLE_SUBHEADERCELL_RICH_TABLE_SUBHEADERCELL = "dr-table-subheadercell rich-table-subheadercell"; //$NON-NLS-1$
    private static final String DR_TABLE_SUBHEADER_RICH_TABLE_SUBHEADER = "dr-table-subheader rich-table-subheader"; //$NON-NLS-1$
    private static final String DR_TABLE_HEADERCELL_RICH_TABLE_HEADERCELL = "dr-table-headercell rich-table-headercell"; //$NON-NLS-1$
    private static final String DR_TABLE_HEADER_CONTINUE_RICH_TABLE_HEADER_CONTINUE = "dr-table-header-continue rich-table-header-continue"; //$NON-NLS-1$
    private static final String DR_TABLE_HEADER_RICH_TABLE_HEADER = "dr-table-header rich-table-header"; //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String DR_TABLE_RICH_TABLE = "dr-table rich-table "; //$NON-NLS-1$
    private static final String RICH_FACES_DATA_TABLE = "richFacesDataTable"; //$NON-NLS-1$
    private static final String EXTENDED_DATA_TABLE_CSS = "extendedDataTable/extendedDataTable.css"; //$NON-NLS-1$
    private static final String SEMICOLON = ";"; //$NON-NLS-1$
    private static final String COLON = " : "; //$NON-NLS-1$
    private static final String DR_TABLE_HIDDEN = "dr-table-hidden"; //$NON-NLS-1$
    private static final String COLUMN = ":column"; //$NON-NLS-1$
    final static String DEFAULT_HEIGHT = "500px"; //$NON-NLS-1$
    final static String DEFAULT_WIDTH = "100%"; //$NON-NLS-1$
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

    private static String STYLE_FOR_RIGHT_SCROLL = "height: 100%; overflow-y: scroll"; //$NON-NLS-1$
    private static String TD_STYLE_FOR_RIGHT_SCROLL = "height: 100%;width : 17px;"; //$NON-NLS-1$

    private static final int NUM_ROW = 5;

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

	String width = sourceElement.getAttribute(HTML.ATTR_WIDTH);
	String height = sourceElement.getAttribute(HTML.ATTR_HEIGHT);

	// -----------CommonTable
	nsIDOMElement tableCommon = visualDocument
		.createElement(HTML.TAG_TABLE);

	VpeCreationData creationData = new VpeCreationData(tableCommon);

	nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR);
	tableCommon.appendChild(tr1);

	// ---------------------tr1------------------------
	nsIDOMElement tr1_TD1 = visualDocument.createElement(HTML.TAG_TD);
	tr1.appendChild(tr1_TD1);

	nsIDOMElement tr1_TD2 = visualDocument.createElement(HTML.TAG_TD);
	tr1_TD2.setAttribute(HTML.ATTR_STYLE, TD_STYLE_FOR_RIGHT_SCROLL);
	tr1.appendChild(tr1_TD2);

	nsIDOMElement tr1_td2_DIV = visualDocument.createElement(HTML.TAG_DIV);
	tr1_td2_DIV.setAttribute(HTML.ATTR_STYLE, STYLE_FOR_RIGHT_SCROLL);
	tr1_TD2.appendChild(tr1_td2_DIV);

	// -------------------------------------------------------
	nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
	tr1_TD1.appendChild(div);
	div.setAttribute(HTML.ATTR_CLASS, DR_TABLE_HIDDEN);

	String divStyle = HTML.ATTR_WIDTH + COLON
		+ (width == null ? DEFAULT_WIDTH : width) + SEMICOLON
		+ HTML.ATTR_HEIGHT + COLON
		+ (height == null ? DEFAULT_HEIGHT : height) + SEMICOLON;

	div.setAttribute(HTML.ATTR_STYLE, divStyle);

	nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
	ComponentUtil.copyAttributes(sourceNode, table);
	table.removeAttribute(HTML.ATTR_HEIGHT);
	div.appendChild(table);

	ComponentUtil
		.setCSSLink(pageContext,
			EXTENDED_DATA_TABLE_CSS,
			RICH_FACES_DATA_TABLE);
	String tableClass = sourceElement
		.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	table.setAttribute(HTML.ATTR_CLASS, DR_TABLE_RICH_TABLE
		+ (tableClass == null ? EMPTY : tableClass));

	// Encode colgroup definition.
	ArrayList<Element> columns = getColumns(sourceElement);
	int columnsLength = getColumnsCount(sourceElement, columns);
	nsIDOMElement colgroup = visualDocument
		.createElement(HTML.TAG_COLGROUP);
	colgroup.setAttribute(HTML.TAG_SPAN, String.valueOf(columnsLength));
	table.appendChild(colgroup);

	// Encode Caption
	encodeCaption(creationData, sourceElement, visualDocument, table);

	// Encode Header
	Element header = ComponentUtil.getFacet(sourceElement, HEADER);
	ArrayList<Element> columnsHeaders = getColumnsWithFacet(columns, HEADER);
	if (header != null || !columnsHeaders.isEmpty()) {
	    nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
	    table.appendChild(thead);
	    String headerClass = (String) sourceElement
		    .getAttribute(HEADER_CLASS);
	    if (header != null) {
		encodeTableHeaderOrFooterFacet(creationData, thead,
			columnsLength, visualDocument, header,
			DR_TABLE_HEADER_RICH_TABLE_HEADER,
			DR_TABLE_HEADER_CONTINUE_RICH_TABLE_HEADER_CONTINUE,
			DR_TABLE_HEADERCELL_RICH_TABLE_HEADERCELL,
			headerClass, HTML.TAG_TD);
	    }
	    if (!columnsHeaders.isEmpty()) {
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement filterTR = visualDocument.createElement(HTML.TAG_TR);
		thead.appendChild(tr);
		thead.appendChild(filterTR);
		String styleClass = encodeStyleClass(null,
			DR_TABLE_SUBHEADER_RICH_TABLE_SUBHEADER, null,
			headerClass);
		if (styleClass != null) {
		    tr.setAttribute(HTML.ATTR_CLASS, styleClass);
		    filterTR.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		encodeHeaderFacets(creationData, tr, filterTR, visualDocument,
			columnsHeaders,
			DR_TABLE_SUBHEADERCELL_RICH_TABLE_SUBHEADERCELL,
			headerClass);
		
		
	    }
	}

	// Encode Footer
	Element footer = ComponentUtil.getFacet(sourceElement, FOOTER);
	ArrayList<Element> columnsFooters = getColumnsWithFacet(columns, FOOTER);
	if (footer != null || !columnsFooters.isEmpty()) {
	    nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
	    table.appendChild(tfoot);
	    String footerClass = (String) sourceElement
		    .getAttribute(FOOTER_CLASS);
	    if (!columnsFooters.isEmpty()) {
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		tfoot.appendChild(tr);
		String styleClass = encodeStyleClass(null,
			DR_TABLE_SUBFOOTER_RICH_TABLE_SUBFOOTER, null,
			footerClass);
		if (styleClass != null) {
		    tr.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		encodeFooterFacets(creationData, tr, visualDocument,
			columnsFooters,
			DR_TABLE_SUBFOOTERCELL_RICH_TABLE_SUBFOOTERCELL,
			footerClass);
	    }
	    if (footer != null) {
		encodeTableHeaderOrFooterFacet(creationData, tfoot,
			columnsLength, visualDocument, footer,
			DR_TABLE_FOOTER_RICH_TABLE_FOOTER,
			DR_TABLE_FOOTER_CONTINUE_RICH_TABLE_FOOTER_CONTINUE,
			DR_TABLE_FOOTERCELL_RICH_TABLE_FOOTERCELL,
			footerClass, HTML.TAG_TD);
	    }
	}

	nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
	table.appendChild(tbody);

	// Create mapping to Encode body
	for (int i = 0; i < NUM_ROW; i++) {
	    List<Node> children = ComponentUtil.getChildren(sourceElement);
	    boolean firstRow = true;
	    nsIDOMElement tr = null;
	    VpeChildrenInfo trInfo = null;
	    for (Node child : children) {
		if (child.getNodeName().endsWith(COLUMN)) {
		    String breakBefore = ((Element) child)
			    .getAttribute(BREAK_BEFORE);
		    if (breakBefore != null
			    && breakBefore.equalsIgnoreCase(TRUE)) {
			tr = null;
		    }
		    if (tr == null) {
			tr = visualDocument.createElement(HTML.TAG_TR);
			if (firstRow) {
			    tr.setAttribute(HTML.ATTR_CLASS,
				    DR_TABLE_FIRSTROW_RICH_TABLE_FIRSTROW);
			    firstRow = false;
			} else {
			    tr.setAttribute(HTML.ATTR_CLASS,
				    DR_TABLE_ROW_RICH_TABLE_ROW);
			}
			trInfo = new VpeChildrenInfo(tr);
			tbody.appendChild(tr);
			creationData.addChildrenInfo(trInfo);
		    }
		    trInfo.addSourceChild(child);
		} else if (child.getNodeName().endsWith(COLUMN_GROUP)) {
		    RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(
			    creationData, (Element) child, visualDocument,
			    tbody);
		    tr = null;
		} else if (child.getNodeName().endsWith(SUB_TABLE)) {
		    RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(
			    creationData, (Element) child, visualDocument,
			    tbody);
		    tr = null;
		} else {
		    VpeChildrenInfo childInfo = new VpeChildrenInfo(tbody);
		    childInfo.addSourceChild(child);
		    creationData.addChildrenInfo(childInfo);
		    tr = null;
		}
	    }
	}

	return creationData;
    }

    /**
     * 
     * @param creationData
     * @param sourceElement
     * @param visualDocument
     * @param table
     */
    protected void encodeCaption(VpeCreationData creationData,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement table) {
	// Encode caption
	Element captionFromFacet = ComponentUtil.getFacet(sourceElement,
		HTML.TAG_CAPTION);
	if (captionFromFacet != null) {
	    String captionClass = (String) table.getAttribute(CAPTION_CLASS);
	    String captionStyle = (String) table.getAttribute(CAPTION_STYLE);

	    nsIDOMElement caption = visualDocument
		    .createElement(HTML.TAG_CAPTION);
	    table.appendChild(caption);
	    if (captionClass != null && captionClass.length() > 0) {
		captionClass = DR_TABLE_CAPTION_RICH_TABLE_CAPTION
			+ captionClass;
	    } else {
		captionClass = TABLE_CAPTION_RICH_TABLE_CAPTION;
	    }
	    caption.setAttribute(HTML.ATTR_CLASS, captionClass);
	    if (captionStyle != null && captionStyle.length() > 0) {
		caption.setAttribute(HTML.ATTR_STYLE, captionStyle);
	    }

	    VpeChildrenInfo cap = new VpeChildrenInfo(caption);
	    cap.addSourceChild(captionFromFacet);
	    creationData.addChildrenInfo(cap);
	}

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
    public static void encodeHeaderFacets(VpeCreationData creationData,
	    nsIDOMElement parentTr, nsIDOMElement filterTR,
	    nsIDOMDocument visualDocument, ArrayList<Element> headers,
	    String skinCellClass, String headerClass) {
	String classAttribute = "headerClass"; //$NON-NLS-1$
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
	    nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

	    nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
	    td.appendChild(table);
	    table.setAttribute(HTML.ATTR_BORDER, ZERRO);
	    table.setAttribute(HTML.ATTR_CELLPADDING, ZERRO);
	    table.setAttribute(HTML.ATTR_CELLSPACING, ZERRO);
	    nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	    nsIDOMElement trTd1 = visualDocument.createElement(HTML.TAG_TD);
	    nsIDOMElement trTd2 = visualDocument.createElement(HTML.TAG_TD);
	    table.appendChild(tr);
	    tr.appendChild(trTd1);
	    tr.appendChild(trTd2);

	    parentTr.appendChild(td);
	    String styleClass = encodeStyleClass(null, skinCellClass,
		    headerClass, columnHeaderClass);
	    td.setAttribute(HTML.ATTR_CLASS, styleClass);
	    td.setAttribute(SCOP, COL);
	    String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
	    if (colspan != null && colspan.length() > 0) {
		td.setAttribute(HTML.ATTR_COLSPAN, colspan);
	    }
	    Element facetBody = ComponentUtil.getFacet(column, HEADER);

	    VpeChildrenInfo child = new VpeChildrenInfo(trTd1);
	    child.addSourceChild(facetBody);
	    creationData.addChildrenInfo(child);
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
		if(ComponentUtil.getAttribute(column, FILTER_BY).length() > 0) {
		    // Add input
		    nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		    div.setAttribute(HTML.ATTR_STYLE, DIV_STYLE);
		    filterTD.appendChild(div);
		    nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		    div.appendChild(input);
		    input.setAttribute(HTML.ATTR_TYPE, INPUT_TYPE_ATTR);
		    input.setAttribute(HTML.ATTR_CLASS, EXTENDED_TABLE_INPUT);
		}
		
		
	    }
	    // Add sortable attribute
	    String sortable = ComponentUtil.getAttribute(column, ATTR_SORTABLE);
	    if (sortable.equalsIgnoreCase(FALSE)) {
		continue;
	    }
	    nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
	    ComponentUtil.setImg(img, SORTABLE_PATH);
	    trTd2.appendChild(img);
	}
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
    public static void encodeFooterFacets(VpeCreationData creationData,
	    nsIDOMElement parentTr, nsIDOMDocument visualDocument,
	    ArrayList<Element> footers, String skinCellClass, String footerClass) {
	String classAttribute = "footerClass"; //$NON-NLS-1$
	for (Element column : footers) {

	    String columnHeaderClass = column.getAttribute(classAttribute);
	    nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
	    parentTr.appendChild(td);
	    String styleClass = encodeStyleClass(null, skinCellClass,
		    footerClass, columnHeaderClass);
	    td.setAttribute(HTML.ATTR_CLASS, styleClass);
	    td.setAttribute(SCOP, COL);
	    String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
	    if (colspan != null && colspan.length() > 0) {
		td.setAttribute(HTML.ATTR_COLSPAN, colspan);
	    }
	    Element facetBody = ComponentUtil.getFacet(column, FOOTER);

	    VpeChildrenInfo child = new VpeChildrenInfo(td);
	    child.addSourceChild(facetBody);
	    creationData.addChildrenInfo(child);
	}
    }

    /**
     * 
     * @param creationData
     * @param parentTheadOrTfood
     * @param columns
     * @param visualDocument
     * @param facetBody
     * @param skinFirstRowClass
     * @param skinRowClass
     * @param skinCellClass
     * @param facetBodyClass
     * @param element
     */
    protected void encodeTableHeaderOrFooterFacet(VpeCreationData creationData,
	    nsIDOMElement parentTheadOrTfood, int columns,
	    nsIDOMDocument visualDocument, Element facetBody,
	    String skinFirstRowClass, String skinRowClass,
	    String skinCellClass, String facetBodyClass, String element) {
	boolean isColumnGroup = facetBody.getNodeName()
		.endsWith(COLUMN_GROUP);
	boolean isSubTable = facetBody.getNodeName().endsWith(SUB_TABLE);
	if (isColumnGroup) {
	    RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData,
		    facetBody, visualDocument, parentTheadOrTfood);
	} else if (isSubTable) {
	    RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(creationData,
		    facetBody, visualDocument, parentTheadOrTfood);
	} else {
	    nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	    parentTheadOrTfood.appendChild(tr);

	    String styleClass = encodeStyleClass(null, skinFirstRowClass,
		    facetBodyClass, null);
	    if (styleClass != null) {
		tr.setAttribute(HTML.ATTR_CLASS, styleClass);
	    }
	    String style = ComponentUtil.getHeaderBackgoundImgStyle();
	    tr.setAttribute(HTML.ATTR_STYLE, style);

	    nsIDOMElement td = visualDocument.createElement(element);
	    tr.appendChild(td);

	    styleClass = encodeStyleClass(null, skinCellClass, facetBodyClass,
		    null);
	    if (styleClass != null) {
		td.setAttribute(HTML.ATTR_CLASS, styleClass);
	    }

	    if (columns > 0) {
		td.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(columns));
	    }
	    td.setAttribute(HTML.ATTR_SCOPE, HTML.TAG_COLGROUP);

	    VpeChildrenInfo child = new VpeChildrenInfo(td);
	    child.addSourceChild(facetBody);
	    creationData.addChildrenInfo(child);
	}
    }

    /**
     * 
     * @param parentSourceElement
     * @return list of columns
     */
    public static ArrayList<Element> getColumns(Element parentSourceElement) {
	ArrayList<Element> columns = new ArrayList<Element>();
	NodeList children = parentSourceElement.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if ((child instanceof Element)
		    && child.getNodeName().endsWith(COLUMN)) {
		columns.add((Element) child);
	    }
	}
	return columns;
    }

    /**
     * 
     * @param columns
     * @param facetName
     * @return list of columns with facet
     */
    public static ArrayList<Element> getColumnsWithFacet(
	    ArrayList<Element> columns, String facetName) {
	ArrayList<Element> columnsWithFacet = new ArrayList<Element>();
	for (Element column : columns) {
	    Element body = ComponentUtil.getFacet(column, facetName);
	    if (body != null) {
		columnsWithFacet.add(column);
	    }
	}
	return columnsWithFacet;
    }

    /**
     * 
     * @param parentPredefined
     * @param predefined
     * @param parent
     * @param custom
     * @return
     */
    public static String encodeStyleClass(Object parentPredefined,
	    Object predefined, Object parent, Object custom) {
	StringBuffer styleClass = new StringBuffer();
	// Construct predefined classes
	if (null != parentPredefined) {
	    styleClass.append(parentPredefined).append(SPACE);
	} else if (null != predefined) {
	    styleClass.append(predefined).append(SPACE);
	}
	// Append class from parent component.
	if (null != parent) {
	    styleClass.append(parent).append(SPACE);
	}
	if (null != custom) {
	    styleClass.append(custom);
	}
	if (styleClass.length() > 0) {
	    return styleClass.toString();
	}
	return null;
    }

    /**
     * 
     * @param sourceElement
     * @param columns
     * @return
     */
    protected int getColumnsCount(Element sourceElement,
	    ArrayList<Element> columns) {
	int count = 0;
	// check for exact value in component
	try {
	    int span = Integer.parseInt(sourceElement.getAttribute(COLUMNS));
	    count = count > 0 ? span : calculateRowColumns(sourceElement,
		    columns);
	} catch (NumberFormatException e) {
	    count = calculateRowColumns(sourceElement, columns);
	}
	return count;
    }

    /*
     * Calculate max number of columns per row. For rows, recursive calculate
     * max length.
     */
    private int calculateRowColumns(Element sourceElement,
	    ArrayList<Element> columns) {
	int count = 0;
	int currentLength = 0;
	for (Element column : columns) {
	    if (ComponentUtil.isRendered(column)) {
		if (column.getNodeName().endsWith(COLUMN_GROUP)) {
		    // Store max calculated value of previsous rows.
		    if (currentLength > count) {
			count = currentLength;
		    }
		    // Calculate number of columns in row.
		    currentLength = calculateRowColumns(sourceElement,
			    getColumns(column));
		    // Store max calculated value
		    if (currentLength > count) {
			count = currentLength;
		    }
		    currentLength = 0;
		} else if (column.getNodeName().equals(
			sourceElement.getPrefix() + COLUMN)) {
		    String breakBeforeStr = column.getAttribute(BREAK_BEFORE);
		    // For new row, save length of previsous.
		    if (Boolean.getBoolean(breakBeforeStr)) {
			if (currentLength > count) {
			    count = currentLength;
			}
			currentLength = 0;
		    }
		    String colspanStr = column.getAttribute(HTML.ATTR_COLSPAN);
		    try {
			int colspan = Integer.parseInt(colspanStr);
			currentLength += colspan > 0 ? colspan : 1;
		    } catch (NumberFormatException e) {
			currentLength++;
		    }
		} else if (column.getNodeName().endsWith(COLUMN)) {
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

    /**
     * Checks, whether it is necessary to re-create an element at change of
     * attribute
     * 
     * @param pageContext
     *            Contains the information on edited page.
     * @param sourceElement
     *            The current element of the source tree.
     * @param visualDocument
     *            The document of the visual tree.
     * @param visualNode
     *            The current node of the visual tree.
     * @param data
     *            The arbitrary data, built by a method <code>create</code>
     * @param name
     *            Attribute name
     * @param value
     *            Attribute value
     * @return <code>true</code> if it is required to re-create an element at a
     *         modification of attribute, <code>false</code> otherwise.
     */
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }
}
