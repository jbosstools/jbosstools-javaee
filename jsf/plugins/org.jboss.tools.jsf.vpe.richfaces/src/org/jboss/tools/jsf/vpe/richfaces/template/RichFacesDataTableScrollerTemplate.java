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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces DataTableScroller
 */
public class RichFacesDataTableScrollerTemplate extends VpeAbstractTemplate {


    private static final String COMPONENT_NAME = "richFacesDataTableScroller"; //$NON-NLS-1$
    private static final String STYLE_PATH = "dataTableScroller/dataTableScroller.css"; //$NON-NLS-1$

    private static final String RIGHT_DOUBLE_SCROLL_SYMBOL = "\u00BB\u00BB"; //$NON-NLS-1$
    private static final String RIGHT_SINGLE_SCROLL_SYMBOL = "\u00BB"; //$NON-NLS-1$
    private static final String LEFT_DOUBLE_SCROLL_SYMBOL = "\u00AB\u00AB"; //$NON-NLS-1$
    private static final String LEFT_SINGLE_SCROLL_SYMBOL = "\u00AB"; //$NON-NLS-1$

    /*
     * Default cells number in datascroller.
     */
    private static final int DEFAULT_CELLS_NUMBER = 10;
    /*
     * Default active datascroller page number.
     */
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private static final String CSS_RICH_DATASCR = "rich-datascr"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCROLLER_TABLE = "rich-dtascroller-table"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCR_BUTTON = "rich-datascr-button"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCR_CTRLS_SEPARATOR = "rich-datascr-ctrls-separator"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCR_ACT = "rich-datascr-act"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCR_INACT = "rich-datascr-inact"; //$NON-NLS-1$
    private static final String CSS_RICH_DATASCR_BUTTON_DSBLD = "rich-datascr-button-dsbld"; //$NON-NLS-1$

    private static final String ATTR_BOUNDARY_CONTROLS = "boundaryControls"; //$NON-NLS-1$
    private static final String ATTR_FAST_CONTROLS = "fastControls"; //$NON-NLS-1$
    private static final String ATTR_MAX_PAGES = "maxPages"; //$NON-NLS-1$
    private static final String ATTR_PAGE = "page"; //$NON-NLS-1$
    private static final String ATTR_STEP_CONTROLS = "stepControls"; //$NON-NLS-1$

    private static final String ATTR_INACTIVE_STYLE = "inactiveStyle"; //$NON-NLS-1$
    private static final String ATTR_INACTIVE_STYLE_CLASS = "inactiveStyleClass"; //$NON-NLS-1$
    private static final String ATTR_SELECTED_STYLE = "selectedStyle"; //$NON-NLS-1$
    private static final String ATTR_SELECTED_STYLE_CLASS = "selectedStyleClass"; //$NON-NLS-1$
    private static final String ATTR_TABLE_STYLE = "tableStyle"; //$NON-NLS-1$
    private static final String ATTR_TABLE_STYLE_CLASS = "tableStyleClass"; //$NON-NLS-1$

    private static final String ATTR_VALUE_SHOW = "show"; //$NON-NLS-1$

    private static final String FACET_FIRST = "first"; //$NON-NLS-1$
    private static final String FACET_LAST = "last"; //$NON-NLS-1$
    private static final String FACET_FAST_FORWARD = "fastforward"; //$NON-NLS-1$
    private static final String FACET_FAST_REWIND = "fastrewind"; //$NON-NLS-1$
    private static final String FACET_NEXT = "next"; //$NON-NLS-1$
    private static final String FACET_PREVIOUS = "previous"; //$NON-NLS-1$

    private boolean showBoundaryControls;
    private boolean showFastControls;
    private int maxPages;
    private int page;
    private boolean showStepControls;
    private String inactiveStyle;
    private String inactiveStyleClass;
    private String selectedStyle;
    private String selectedStyleClass;
    private String tableStyle;
    private String tableStyleClass;
    private String style;
    private String styleClass;

    /**
     * 
     * Constructor.
     */
    public RichFacesDataTableScrollerTemplate() {
	super();
    }

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
	readAttributes(sourceNode);
	Element sourceElement = (Element) sourceNode;
	ComponentUtil.setCSSLink(pageContext, STYLE_PATH,
		COMPONENT_NAME);
	nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
	VpeCreationData creationData = new VpeCreationData(div);
	/*
	 * Adding fake children info to avoid creating pseudo element.
	 */
	creationData.addChildrenInfo(new VpeChildrenInfo(div));	
	
	String align = HTML.VALUE_ALIGN_CENTER;
	if (sourceElement.hasAttribute(RichFaces.ATTR_ALIGN)) {
		align = sourceElement.getAttribute(RichFaces.ATTR_ALIGN);		
	}
	div.setAttribute(HTML.ATTR_ALIGN, align);

	div.setAttribute(HTML.ATTR_CLASS, styleClass);
	if (ComponentUtil.isNotBlank(style)) {
	    div.setAttribute(HTML.ATTR_STYLE, style);
	}

	nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
	nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
	nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	tbody.appendChild(tr);
	table.appendChild(tbody);
	div.appendChild(table);

	table.setAttribute(HTML.ATTR_CLASS, tableStyleClass);
	if (ComponentUtil.isNotBlank(tableStyle)) {
	    table.setAttribute(HTML.ATTR_STYLE, tableStyle);
	}
	table.setAttribute(HTML.ATTR_CELLSPACING, "1"); //$NON-NLS-1$
	table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
	table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$

	/*
	 * Create left side controls
	 */
	Object object = null;
	Element facetElement = null;
	if (showBoundaryControls) {
	    object = LEFT_DOUBLE_SCROLL_SYMBOL;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_FIRST);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON + Constants.WHITE_SPACE
			    + CSS_RICH_DATASCR_BUTTON_DSBLD, Constants.EMPTY);
	}
	if (showFastControls) {
	    object = LEFT_SINGLE_SCROLL_SYMBOL;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_FAST_REWIND);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON + Constants.WHITE_SPACE
			    + CSS_RICH_DATASCR_BUTTON_DSBLD, Constants.EMPTY);
	}
	if (showStepControls) {
	    object = Constants.EMPTY;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_PREVIOUS);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON + Constants.WHITE_SPACE
			    + CSS_RICH_DATASCR_BUTTON_DSBLD, Constants.EMPTY);
	}

	/*
	 * Create page numbers controls
	 */
	for (int i = 1; i <= maxPages; i++) {
	    createCell(visualDocument, creationData, tr, String.valueOf(i),
		    (i == 1 ? selectedStyleClass : inactiveStyleClass),
		    (i == 1 ? selectedStyle : inactiveStyle));
	}

	/*
	 * Create right side controls
	 */
	if (showStepControls) {
	    object = Constants.EMPTY;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_NEXT);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON, Constants.EMPTY);
	}
	if (showFastControls) {
	    object = RIGHT_SINGLE_SCROLL_SYMBOL;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_FAST_FORWARD);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON, Constants.EMPTY);
	}
	if (showBoundaryControls) {
	    object = RIGHT_DOUBLE_SCROLL_SYMBOL;
	    facetElement = ComponentUtil.getFacetElement(sourceElement, FACET_LAST);
	    if (null != facetElement) {
		object = facetElement;
	    }
	    createCell(visualDocument, creationData, tr, object,
		    CSS_RICH_DATASCR_BUTTON, Constants.EMPTY);
	}

	return creationData;
    }

    
    private void createCell(nsIDOMDocument visualDocument,
	    VpeCreationData creationData, nsIDOMElement tr, Object element,
	    String styleClass, String style) {
	nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
	td.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_ALIGN_CENTER);

	if (ComponentUtil.isNotBlank(styleClass)) {
	    td.setAttribute(HTML.ATTR_CLASS, styleClass);
	}
	if (ComponentUtil.isNotBlank(style)) {
	    td.setAttribute(HTML.ATTR_STYLE, style);
	}

	if (element instanceof String) {
	    nsIDOMText cellText = visualDocument.createTextNode((String)element);
	    td.appendChild(cellText);
	} else if (element instanceof Element) {
	    VpeChildrenInfo facetInfo = new VpeChildrenInfo(td);
	    facetInfo.addSourceChild((Element) element);
	    creationData.addChildrenInfo(facetInfo);
	}
	tr.appendChild(td);

    }

    /**
     * Read attributes from the source element.
     * 
     * @param sourceNode
     *            the source node
     */
    private void readAttributes(Node sourceNode) {

	Element sourceElement = (Element) sourceNode;

	showBoundaryControls = (!sourceElement
		.hasAttribute(ATTR_BOUNDARY_CONTROLS) || ATTR_VALUE_SHOW
		.equalsIgnoreCase(sourceElement
			.getAttribute(ATTR_BOUNDARY_CONTROLS)));

	showFastControls = (!sourceElement.hasAttribute(ATTR_FAST_CONTROLS) || ATTR_VALUE_SHOW
		.equalsIgnoreCase(sourceElement
			.getAttribute(ATTR_FAST_CONTROLS)));

	maxPages = ComponentUtil.parseNumberAttribute(sourceElement,
		ATTR_MAX_PAGES, DEFAULT_CELLS_NUMBER);

	page = ComponentUtil.parseNumberAttribute(sourceElement, ATTR_PAGE, DEFAULT_PAGE_NUMBER);

	showStepControls = (!sourceElement.hasAttribute(ATTR_STEP_CONTROLS) || ATTR_VALUE_SHOW
		.equalsIgnoreCase(sourceElement
			.getAttribute(ATTR_STEP_CONTROLS)));

	inactiveStyle = sourceElement.getAttribute(ATTR_INACTIVE_STYLE);

	inactiveStyleClass = CSS_RICH_DATASCR_INACT;
	if (sourceElement.hasAttribute(ATTR_INACTIVE_STYLE_CLASS)) {
	    inactiveStyleClass += Constants.WHITE_SPACE + sourceElement.getAttribute(ATTR_INACTIVE_STYLE_CLASS);
	}

	selectedStyle = sourceElement.getAttribute(ATTR_SELECTED_STYLE);

	selectedStyleClass = CSS_RICH_DATASCR_ACT;
	if (sourceElement.hasAttribute(ATTR_SELECTED_STYLE_CLASS)) {
	    selectedStyleClass += Constants.WHITE_SPACE + sourceElement.getAttribute(ATTR_SELECTED_STYLE_CLASS);
	}

	tableStyle = sourceElement.getAttribute(ATTR_TABLE_STYLE);

	tableStyleClass = CSS_RICH_DATASCROLLER_TABLE;
	if (sourceElement.hasAttribute(ATTR_TABLE_STYLE_CLASS)) {
	    tableStyleClass += Constants.WHITE_SPACE + sourceElement.getAttribute(ATTR_TABLE_STYLE_CLASS);
	}

	style = sourceElement.getAttribute(HTML.ATTR_STYLE);

	styleClass = CSS_RICH_DATASCR;
	if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
	    styleClass += Constants.WHITE_SPACE + sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	}

    }

    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

}