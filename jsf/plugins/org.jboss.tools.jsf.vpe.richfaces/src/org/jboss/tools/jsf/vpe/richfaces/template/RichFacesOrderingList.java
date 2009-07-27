/**
 * 
 */
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.Messages;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
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
 * @author dmaliarevich
 * 
 */
public class RichFacesOrderingList extends VpeAbstractTemplate {

	private static final String COLUMN = ':' + RichFaces.TAG_COLUMN;
	private static final String COLUMNS = ':' + RichFaces.TAG_COLUMNS;
	private static final String DEFAULT_LIST_HEIGHT = "150px"; //$NON-NLS-1$
	private static final String DEFAULT_LIST_WIDTH = "300px"; //$NON-NLS-1$

	private static final String DEFAULT_HEIGHT = "200px"; //$NON-NLS-1$
	private static final String DEFAULT_WIDTH = "300px"; //$NON-NLS-1$
	
	private static final String CAPTION_FACET = "caption"; //$NON-NLS-1$
	private static final String TOP_CONTROL_FACET = "topControl"; //$NON-NLS-1$
	private static final String UP_CONTROL_FACET = "upControl"; //$NON-NLS-1$
	private static final String DOWN_CONTROL_FACET = "downControl"; //$NON-NLS-1$
	private static final String BOTTOM_CONTROL_FACET = "bottomControl"; //$NON-NLS-1$
	
	private static final String HEADER = "header"; //$NON-NLS-1$
	private static final String HEADER_CLASS = "headerClass"; //$NON-NLS-1$
	private static final String FOOTER = "footer"; //$NON-NLS-1$
	private static final String FOOTER_CLASS = "footerClass"; //$NON-NLS-1$
	private static final String CAPTION_CLASS = "captionClass"; //$NON-NLS-1$
	private static final String CAPTION_STYLE = "captionStyle"; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	private static final String STYLE_FOR_LOW_SCROLL = "overflow: scroll; width: 100%; height: 17px;"; //$NON-NLS-1$
	private static final String STYLE_FOR_RIGHT_SCROLL = "overflow: scroll; width: 17px; height: 100%;"; //$NON-NLS-1$

	private static final int NUM_ROW = 1;

	private static final String TOP_CONTROL_IMG = "orderingList/top.gif"; //$NON-NLS-1$
	private static final String UP_CONTROL_IMG = "orderingList/up.gif"; //$NON-NLS-1$
	private static final String DOWN_CONTROL_IMG = "orderingList/down.gif"; //$NON-NLS-1$
	private static final String BOTTOM_CONTROL_IMG = "orderingList/bottom.gif"; //$NON-NLS-1$

	private static final String BUTTON_BG = "orderingList/button_bg.gif"; //$NON-NLS-1$
	private static final String HEADER_CELL_BG = "orderingList/table_header_cell_bg.gif"; //$NON-NLS-1$

	private static final String WIDTH = "width"; //$NON-NLS-1$
	private static final String HEIGHT = "height"; //$NON-NLS-1$
	private static final String LIST_WIDTH = "listWidth"; //$NON-NLS-1$
	private static final String LIST_HEIGHT = "listHeight"; //$NON-NLS-1$

	private static final String TOP_CONTROL_LABEL = "topControlLabel"; //$NON-NLS-1$
	private static final String UP_CONTROL_LABEL = "upControlLabel"; //$NON-NLS-1$
	private static final String DOWN_CONTROL_LABEL = "downControlLabel"; //$NON-NLS-1$
	private static final String BOTTOM_CONTROL_LABEL = "bottomControlLabel"; //$NON-NLS-1$

	private static final String TOP_CONTROL_LABEL_DEFAULT = Messages.RichFacesOrderingList_FirstLabel;
	private static final String UP_CONTROL_LABEL_DEFAULT = Messages.RichFacesOrderingList_UpLabel;
	private static final String DOWN_CONTROL_LABEL_DEFAULT = Messages.RichFacesOrderingList_DownLabel;
	private static final String BOTTOM_CONTROL_LABEL_DEFAULT = Messages.RichFacesOrderingList_LastLabel;

	private static final String CAPTION_LABEL = "captionLabel"; //$NON-NLS-1$
	
	private static final String CONTROLS_TYPE = "controlsType"; //$NON-NLS-1$
	private static final String CONTROLS_VERTICAL_ALIGN = "controlsVerticalAlign"; //$NON-NLS-1$
	private static final String CONTROLS_HORIZONTAL_ALIGN = "controlsHorizontalAlign"; //$NON-NLS-1$
	private static final String SHOW_BUTTON_LABELS = "showButtonLabels"; //$NON-NLS-1$
	private static final String FAST_ORDER_CONTROL_VISIBLE = "fastOrderControlsVisible"; //$NON-NLS-1$
	private static final String ORDER_CONTROL_VISIBLE = "orderControlsVisible"; //$NON-NLS-1$
	
	private static final String LIST_CLASS = "listClass"; //$NON-NLS-1$
	private static final String CONTROLS_CLASS = "controlsClass"; //$NON-NLS-1$
	private static final String TOP_CONTROL_CLASS = "topControlClass"; //$NON-NLS-1$
	private static final String UP_CONTROL_CLASS = "upControlClass"; //$NON-NLS-1$
	private static final String DOWN_CONTROL_CLASS = "downControlClass"; //$NON-NLS-1$
	private static final String BOTTOM_CONTROL_CLASS = "bottomControlClass"; //$NON-NLS-1$
	private static final String ROW_CLASSES = "rowClasses"; //$NON-NLS-1$
	
	private static final String CSS_CAPTION_CLASS = "rich-ordering-list-caption"; //$NON-NLS-1$
	
	private static final String CSS_CONTROLS_CLASS = "rich-ordering-controls"; //$NON-NLS-1$
	private static final String CSS_TOP_CONTROL_CLASS = "rich-ordering-control-top"; //$NON-NLS-1$
	private static final String CSS_BUTTON_LAYOUT_CLASS = "rich-ordering-list-button-layout"; //$NON-NLS-1$
	private static final String CSS_UP_CONTROL_CLASS = "rich-ordering-control-up"; //$NON-NLS-1$
	private static final String CSS_DOWN_CONTROL_CLASS = "rich-ordering-control-down"; //$NON-NLS-1$
	private static final String CSS_BOTTOM_CONTROL_CLASS = "rich-ordering-control-bottom"; //$NON-NLS-1$
	private static final String CSS_BUTTON_CLASS = "rich-ordering-list-button"; //$NON-NLS-1$
	private static final String CSS_BUTTON_SELECTION_CLASS = "rich-ordering-list-button-selection"; //$NON-NLS-1$
	private static final String CSS_BUTTON_CONTENT_CLASS = "rich-ordering-list-button-content"; //$NON-NLS-1$
	private static final String CSS_BUTTON_VALIGN_CLASS = "rich-ordering-list-button-valign"; //$NON-NLS-1$

	private static final String CSS_HEADER_CLASS = "rich-ordering-list-header"; //$NON-NLS-1$
	private static final String CSS_TABLE_HEADER_CLASS = "rich-ordering-list-table-header"; //$NON-NLS-1$
	private static final String CSS_TABLE_HEADER_CELL_CLASS = "rich-ordering-list-table-header-cell"; //$NON-NLS-1$
	private static final String CSS_FOOTER_CLASS = "rich-ordering-list-footer"; //$NON-NLS-1$
	private static final String CSS_TABLE_FOOTER_CLASS = "rich-ordering-list-table-footer"; //$NON-NLS-1$
	private static final String CSS_TABLE_FOOTER_CELL_CLASS = "rich-ordering-list-table-footer-cell"; //$NON-NLS-1$

	private static final String CSS_LIST_BODY_CLASS = "rich-ordering-list-body"; //$NON-NLS-1$
	private static final String CSS_LIST_OUTPUT_CLASS = "rich-ordering-list-output"; //$NON-NLS-1$
	private static final String CSS_LIST_CONTENT_CLASS = "rich-ordering-list-content"; //$NON-NLS-1$
	private static final String CSS_LIST_ITEMS_CLASS = "rich-ordering-list-items"; //$NON-NLS-1$
	private static final String CSS_LIST_ROW_CLASS = "rich-ordering-list-row"; //$NON-NLS-1$
	private static final String CSS_LIST_CELL_CLASS = "rich-ordering-list-cell"; //$NON-NLS-1$
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String width = sourceElement.getAttribute(WIDTH);
		String height = sourceElement.getAttribute(HEIGHT);
		String listWidth = sourceElement.getAttribute(LIST_WIDTH);
		String listHeight = sourceElement.getAttribute(LIST_HEIGHT);

		String controlsType = sourceElement.getAttribute(CONTROLS_TYPE);
		String controlsHorizontalAlign = sourceElement.getAttribute(CONTROLS_HORIZONTAL_ALIGN);
		String controlsVerticalAlign = sourceElement.getAttribute(CONTROLS_VERTICAL_ALIGN);
		String captionLabel = sourceElement.getAttribute(CAPTION_LABEL);

		// --------------------- COMMON TABLE ------------------------
		
		ComponentUtil.setCSSLink(pageContext, "orderingList/orderingList.css", //$NON-NLS-1$
		"richFacesOrderingList"); //$NON-NLS-1$
		
		nsIDOMElement tableCommon = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		VpeCreationData creationData = new VpeCreationData(tableCommon);

		nsIDOMElement dataRow = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		
		tableCommon.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, (width == null ? DEFAULT_WIDTH : width));
		tableCommon.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT, (height == null ? DEFAULT_HEIGHT : height));
		
		tableCommon.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_LIST_BODY_CLASS);
		tableCommon.appendChild(dataRow);

		// ---------------------caption td------------------------

		nsIDOMElement captionRow_TD_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		captionRow_TD_DIV.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_CAPTION_CLASS);
		captionRow_TD_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
						"width: " + (listWidth == null ? DEFAULT_LIST_WIDTH : listWidth) + "px;"  //$NON-NLS-1$ //$NON-NLS-2$
						+"height: " + (listHeight == null ? DEFAULT_LIST_WIDTH : listHeight) + "px;"); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element captionFacet = ComponentUtil.getFacet(sourceElement, CAPTION_FACET);
		if (null != captionFacet) {
			// Creating table caption with facet content
			nsIDOMElement fecetDiv = encodeFacetsToDiv(pageContext, captionFacet, false, CSS_CAPTION_CLASS, "", creationData, visualDocument); //$NON-NLS-1$
			captionRow_TD_DIV.appendChild(fecetDiv);
		} else {
			captionRow_TD_DIV.appendChild(visualDocument.createTextNode(captionLabel));
		}
		
		// ---------------------row with list table and buttons------------------------
		nsIDOMElement dataRow_leftTD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		dataRow.appendChild(dataRow_leftTD);

		nsIDOMElement dataRow_rightTD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		dataRow.appendChild(dataRow_rightTD);
		
		nsIDOMElement tableListTD;
		nsIDOMElement buttonsTD;
		
		if ("left".equalsIgnoreCase(controlsHorizontalAlign)) { //$NON-NLS-1$
			buttonsTD = dataRow_leftTD;
			tableListTD = dataRow_rightTD;
			buttonsTD.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 1%;"); //$NON-NLS-1$
		} else {
			tableListTD = dataRow_leftTD;
			buttonsTD = dataRow_rightTD;
		}

		// ---------------------buttons------------------------
		if (!"none".equalsIgnoreCase(controlsType)) { //$NON-NLS-1$
			nsIDOMElement controlsDiv = createControlsDiv(pageContext, creationData, visualDocument, sourceElement);
			buttonsTD.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					CSS_BUTTON_VALIGN_CLASS);
			buttonsTD.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, "center"); //$NON-NLS-1$
			
			if ((null != controlsVerticalAlign) && ("".equals(controlsVerticalAlign))){ //$NON-NLS-1$
				buttonsTD.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN, ("center" //$NON-NLS-1$
						.equalsIgnoreCase(controlsVerticalAlign) ? "middle" //$NON-NLS-1$
								: controlsVerticalAlign));
			}
			
			buttonsTD.appendChild(controlsDiv);
		}
		// --------------------------------------------

		// ---------------------listTable------------------------
		nsIDOMElement listDiv = createListTableDiv(visualDocument, sourceElement, creationData, pageContext);
		tableListTD.appendChild(captionRow_TD_DIV);
		tableListTD.appendChild(listDiv);
		// --------------------------------------------
		
		return creationData;
	}
	
	/**
	 * Creates the list table div.
	 * 
	 * @param visualDocument the visual document
	 * @param sourceElement the source element
	 * @param creationData the creation data
	 * @param pageContext the page context
	 * 
	 * @return the element
	 */
	private nsIDOMElement createListTableDiv(nsIDOMDocument visualDocument, 
			Element sourceElement, VpeCreationData creationData, VpePageContext pageContext) {
		
		nsIDOMElement listOutputDiv = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		
		nsIDOMElement listTable = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		nsIDOMElement tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		nsIDOMElement tr2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		
		listOutputDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "overflow:hidden;width:100%;"); //$NON-NLS-1$
		listTable.appendChild(tr1);
		listTable.appendChild(tr2);
		listOutputDiv.appendChild(listTable);
		
		// ---------------------tr1------------------------
		nsIDOMElement tr1_TD1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD1);

		nsIDOMElement tr1_TD2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD2);

		nsIDOMElement tr1_TD2_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr1_TD2_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_RIGHT_SCROLL);
		tr1_TD2.appendChild(tr1_TD2_DIV);

		// -------------------------------------------------------

		// ---------------------tr2------------------------
		nsIDOMElement tr2_TD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr2.appendChild(tr2_TD);

		nsIDOMElement tr2_TD_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr2_TD_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_LOW_SCROLL);
		tr2_TD.appendChild(tr2_TD_DIV);

		// --------------------------------------------

		nsIDOMElement contentDiv = createResultList(pageContext, creationData, visualDocument,
				sourceElement);
		tr1_TD1.appendChild(contentDiv);

		return listOutputDiv;
	}
	
	/**
	 * Creates the controls div.
	 * 
	 * @param creationData the creation data
	 * @param visualDocument the visual document
	 * @param sourceElement the source element
	 * 
	 * @return the element
	 */
	private nsIDOMElement createControlsDiv(final VpePageContext pageContext, VpeCreationData creationData, nsIDOMDocument visualDocument, 
			Element sourceElement) {
		
		String topControlClass = sourceElement.getAttribute(TOP_CONTROL_CLASS);
		String upControlClass = sourceElement.getAttribute(UP_CONTROL_CLASS);
		String downControlClass = sourceElement.getAttribute(DOWN_CONTROL_CLASS);
		String bottomControlClass = sourceElement.getAttribute(BOTTOM_CONTROL_CLASS);
		
		String topControlLabel = sourceElement.getAttribute(TOP_CONTROL_LABEL);
		String upControlLabel = sourceElement.getAttribute(UP_CONTROL_LABEL);
		String downControlLabel = sourceElement.getAttribute(DOWN_CONTROL_LABEL);
		String bottomControlLabel = sourceElement.getAttribute(BOTTOM_CONTROL_LABEL);

		String showButtonLabelsStr = sourceElement.getAttribute(SHOW_BUTTON_LABELS);
		String fastOrderControlsVisibleStr = sourceElement.getAttribute(FAST_ORDER_CONTROL_VISIBLE);
		String orderControlsVisibleStr = sourceElement.getAttribute(ORDER_CONTROL_VISIBLE);
		boolean showButtonLabels = ComponentUtil.string2boolean(showButtonLabelsStr);
		boolean fastOrderControlsVisible = ComponentUtil.string2boolean(fastOrderControlsVisibleStr);
		boolean orderControlsVisible = ComponentUtil.string2boolean(orderControlsVisibleStr);
		String controlsClass = sourceElement.getAttribute(CONTROLS_CLASS);
		
		nsIDOMElement buttonsDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		buttonsDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_CONTROLS_CLASS + " " + controlsClass + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ CSS_BUTTON_LAYOUT_CLASS);

		Element top_control_facet = ComponentUtil.getFacet(sourceElement, TOP_CONTROL_FACET);
		Element up_control_facet = ComponentUtil.getFacet(sourceElement, UP_CONTROL_FACET);
		Element down_control_facet = ComponentUtil.getFacet(sourceElement, DOWN_CONTROL_FACET);
		Element bottom_control_facet = ComponentUtil.getFacet(sourceElement, BOTTOM_CONTROL_FACET);
		
		if (fastOrderControlsVisible) {
			nsIDOMElement btnTopDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					(null == topControlLabel ? TOP_CONTROL_LABEL_DEFAULT
							: topControlLabel), TOP_CONTROL_IMG, 
							showButtonLabels, top_control_facet, CSS_TOP_CONTROL_CLASS, topControlClass);
			buttonsDiv.appendChild(btnTopDiv);
		}

		if (orderControlsVisible) {
			nsIDOMElement btnUpDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					(null == upControlLabel ? UP_CONTROL_LABEL_DEFAULT
							: upControlLabel), UP_CONTROL_IMG,
							showButtonLabels, up_control_facet, CSS_UP_CONTROL_CLASS, upControlClass);
			nsIDOMElement btnDownDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					(null == downControlLabel ? DOWN_CONTROL_LABEL_DEFAULT
							: downControlLabel), DOWN_CONTROL_IMG, 
							showButtonLabels, down_control_facet, CSS_DOWN_CONTROL_CLASS, downControlClass);
			buttonsDiv.appendChild(btnUpDiv);
			buttonsDiv.appendChild(btnDownDiv);
		}

		if (fastOrderControlsVisible) {
			nsIDOMElement btnBottomDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					(null == bottomControlLabel ? BOTTOM_CONTROL_LABEL_DEFAULT
							: bottomControlLabel), BOTTOM_CONTROL_IMG,
					showButtonLabels, bottom_control_facet, CSS_BOTTOM_CONTROL_CLASS, bottomControlClass);
			buttonsDiv.appendChild(btnBottomDiv);

		}
		return buttonsDiv;
	}


	/**
	 * Creates the single button div.
	 * 
	 * @param creationData the creation data
	 * @param visualDocument the visual document
	 * @param btnName the btn name
	 * @param imgName the img name
	 * @param showButtonLabels the show button labels
	 * @param buttonFacet the button facet
	 * @param cssStyleName the css style name
	 * @param customStyleClass the custom style class
	 * 
	 * @return the ns idom element
	 */
	private nsIDOMElement createSingleButtonDiv(final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMDocument visualDocument, String btnName, String imgName,
			boolean showButtonLabels, Element buttonFacet, String cssStyleName,
			String customStyleClass) {
		
		nsIDOMElement div1 = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement div2 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement a = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_A);
		nsIDOMElement div3 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement img = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		
		div1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-buttons-border" + " " + cssStyleName + " " + customStyleClass); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		div2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_BUTTON_CLASS);
		
		String  resourceFolder = RichFacesTemplatesActivator.getPluginResourcePath();
		String divStyle = "width: 100%;background-image: url(file://" + resourceFolder + BUTTON_BG + ");"; //$NON-NLS-1$ //$NON-NLS-2$
		
		div2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, divStyle);
		div1.appendChild(div2);
		
		if (null != buttonFacet) {
			// Creating button with facet content
			nsIDOMElement fecetDiv = encodeFacetsToDiv(pageContext, buttonFacet, true, cssStyleName, customStyleClass, creationData, visualDocument);
			div2.appendChild(fecetDiv);
		} else {
			a.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_BUTTON_SELECTION_CLASS);
			div3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_BUTTON_CONTENT_CLASS);
			div2.appendChild(a);
			a.appendChild(div3);
			// Creating button with image and label
			ComponentUtil.setImg(img, imgName);
			img.setAttribute(HTML.ATTR_WIDTH, "15"); //$NON-NLS-1$
			img.setAttribute(HTML.ATTR_HEIGHT, "15"); //$NON-NLS-1$
			div3.appendChild(img);
			if (showButtonLabels) {
				div3.appendChild(visualDocument.createTextNode(btnName));
			}
		}
		return div1;
	}

	
	/**
	 * Creates the result list.
	 * 
	 * @param creationData the creation data
	 * @param visualDocument the visual document
	 * @param sourceElement the source element
	 * 
	 * @return the  element
	 */
	private nsIDOMElement createResultList(final VpePageContext pageContext, VpeCreationData creationData, nsIDOMDocument visualDocument,
			Element sourceElement) {
		nsIDOMElement contentDiv = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);

		nsIDOMElement contentTable = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		nsIDOMElement thead = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_THEAD);
		nsIDOMElement tfoot = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TFOOT);
		
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		
		// ---------- HEADER -----------
		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, HEADER);
		ArrayList<Element> columnsHeaders = ComponentUtil.getColumnsWithFacet(columns, HEADER);
		if (header != null || !columnsHeaders.isEmpty()) {
			String headerClass = (String) sourceElement
					.getAttribute(HEADER_CLASS);
			/*
			if (header != null) {
				encodeTableHeaderOrFooterFacet(creationData, thead,
						columnsLength, visualDocument, header,
						CSS_HEADER_CLASS,
						CSS_HEADER_CLASS,
						headerClass, HtmlComponentUtil.HTML_TAG_TD);
			}
			*/
			if (!columnsHeaders.isEmpty()) {
				nsIDOMElement tr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
				thead.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null,
						CSS_HEADER_CLASS + " " + CSS_TABLE_HEADER_CLASS, null, //$NON-NLS-1$
						headerClass);
				if (styleClass != null) {
					tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsHeaders,
						CSS_TABLE_HEADER_CELL_CLASS,
						headerClass, HEADER, HtmlComponentUtil.HTML_TAG_TD);
			}
		}

		// ---------- FOOTER -----------
		// Encode Footer
		/*
		Element footer = ComponentUtil.getFacet(sourceElement, FOOTER);
		ArrayList<Element> columnsFooters = ComponentUtil.getColumnsWithFacet(columns, FOOTER);
		if (footer != null || !columnsFooters.isEmpty()) {
			String footerClass = (String) sourceElement
					.getAttribute(FOOTER_CLASS);
			if (!columnsFooters.isEmpty()) {
				nsIDOMElement tr = visualDocument
						.createElement(HtmlComponentUtil.HTML_TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null,
						CSS_TABLE_HEADER_CLASS + " " + CSS_TABLE_HEADER_CELL_CLASS, null,
						footerClass);
				if (styleClass != null) {
					tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsFooters,
						CSS_TABLE_FOOTER_CLASS + " " + CSS_TABLE_FOOTER_CELL_CLASS,
						footerClass, FOOTER, HtmlComponentUtil.HTML_TAG_TD);
			}
			
			if (footer != null) {
				encodeTableHeaderOrFooterFacet(creationData, tfoot,
						columnsLength, visualDocument, footer,
						CSS_FOOTER_CLASS,
						CSS_HEADER_CLASS,
						footerClass, HtmlComponentUtil.HTML_TAG_TD);
			}
			
		}
		*/
		// ---------- CONTENT -----------
		
		String listWidth = sourceElement.getAttribute(LIST_WIDTH);
		String listHeight = sourceElement.getAttribute(LIST_HEIGHT);
		String listClass = sourceElement.getAttribute(LIST_CLASS);
		
		// TODO: implement support of rowClasses
		// following line commented by yradtsevich because the variable rowClasses was not used
		//String rowClasses = sourceElement.getAttribute(ROW_CLASSES);
		
		String divStyle = HtmlComponentUtil.HTML_WIDTH_ATTR + " : " //$NON-NLS-1$
		+ (listWidth == null ? DEFAULT_LIST_WIDTH : listWidth) + ";" //$NON-NLS-1$
		+ HtmlComponentUtil.HTML_HEIGHT_ATTR + " : " //$NON-NLS-1$
		+ (listHeight == null ? DEFAULT_LIST_HEIGHT : listHeight) + ";"; //$NON-NLS-1$

		contentDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, divStyle);
		
		contentDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_LIST_OUTPUT_CLASS + " " + CSS_LIST_CONTENT_CLASS); //$NON-NLS-1$
		contentTable.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_LIST_ITEMS_CLASS + " " + (null == listClass ? "" : listClass)); //$NON-NLS-1$ //$NON-NLS-2$

		contentTable.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "1"); //$NON-NLS-1$
		
		VisualDomUtil.copyAttributes(sourceElement, contentTable);
		contentTable.removeAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);
		contentTable.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 100%;"); //$NON-NLS-1$
		
		// ---------- FINALIZING and children encoding -----------
		contentTable.appendChild(thead);
		RichFacesDataTableChildrenEncoder childrenEncoder = new RichFacesDataTableChildrenEncoder(creationData, visualDocument,
				sourceElement, contentTable);
		childrenEncoder.setRowClasses(CSS_LIST_ROW_CLASS, CSS_LIST_ROW_CLASS);
		childrenEncoder.encodeChildren();
		//contentTable.appendChild(tfoot);
		contentDiv.appendChild(contentTable);
		//outputDiv.appendChild(contentDiv);
		
		return contentDiv;
		//return outputDiv;
	}
	
	/**
	 * Encodes facets to div.
	 * 
	 * @param facetBody the facet body
	 * @param isControlFacet the is control facet
	 * @param cssStyleName the css style name
	 * @param customStyleClass the custom style class
	 * @param creationData the creation data
	 * @param visualDocument the visual document
	 * 
	 * @return the element
	 */
	private nsIDOMElement encodeFacetsToDiv(final VpePageContext pageContext, Element facetBody,
			boolean isControlFacet, String cssStyleName,
			String customStyleClass, VpeCreationData creationData,
			nsIDOMDocument visualDocument) {
		nsIDOMElement fecetDiv = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement table = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		nsIDOMElement tbody = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		
		boolean isColumnGroup = facetBody.getNodeName()
				.endsWith(":columnGroup"); //$NON-NLS-1$
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable"); //$NON-NLS-1$
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, tbody);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, tbody);
		} else {
			nsIDOMElement tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			tbody.appendChild(tr);

			nsIDOMElement td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(td);
			
			td.setAttribute(HtmlComponentUtil.HTML_SCOPE_ATTR,
					HtmlComponentUtil.HTML_TAG_COLGROUP);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
			
			if (isControlFacet) {
				
				tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						CSS_BUTTON_CLASS);
				td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						CSS_BUTTON_CONTENT_CLASS + " " + cssStyleName + " " //$NON-NLS-1$ //$NON-NLS-2$
								+ customStyleClass);
				
				fecetDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						CSS_BUTTON_CLASS + " " + CSS_BUTTON_CONTENT_CLASS + " " //$NON-NLS-1$ //$NON-NLS-2$
								+ cssStyleName + " " + customStyleClass); //$NON-NLS-1$
				
			} 
		}
		
		if (isControlFacet) {
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					CSS_BUTTON_CONTENT_CLASS);
		} else {
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					CSS_CAPTION_CLASS);
		}
		
		table.appendChild(tbody);
		fecetDiv.appendChild(table);
		return fecetDiv;
		//return table;
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
	private void encodeTableHeaderOrFooterFacet(final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMElement parentTheadOrTfood, int columns,
			nsIDOMDocument visualDocument, Element facetBody,
			String skinFirstRowClass, String skinCellClass,
			String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName()
				.endsWith(":columnGroup"); //$NON-NLS-1$
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable"); //$NON-NLS-1$
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = ComponentUtil.encodeStyleClass(null, skinFirstRowClass,
					facetBodyClass, null);
			if (styleClass != null) {
				tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);

			nsIDOMElement td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, facetBodyClass,
					null);
			if (styleClass != null) {
				td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			}

			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			td.setAttribute(HtmlComponentUtil.HTML_SCOPE_ATTR,
					HtmlComponentUtil.HTML_TAG_COLGROUP);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}
	
	/**
	 * 
	 * @param creationData
	/**
	 * 
	 * @param creationData
	 * @param parentTr
	 * @param visualDocument
	 * @param headersOrFooters
	 * @param skinCellClass
	 * @param headerClass
	 * @param facetName
	 * @param element
	 */
	private static void encodeHeaderOrFooterFacets(VpeCreationData creationData,
			nsIDOMElement parentTr, nsIDOMDocument visualDocument,
			ArrayList<Element> headersOrFooters, String skinCellClass,
			String headerClass, String facetName, String element) {
		for (Element column : headersOrFooters) {
			String classAttribute = facetName + "Class"; //$NON-NLS-1$
			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(element);
			parentTr.appendChild(td);
	
			td.setAttribute(HtmlComponentUtil.HTML_ATTR_BACKGROUND, "file:///" //$NON-NLS-1$
					+ ComponentUtil.getAbsoluteResourcePath(HEADER_CELL_BG).replace('\\', '/'));
			
			String styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass,
					headerClass, columnHeaderClass);
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			td.setAttribute("scop", "col"); //$NON-NLS-1$ //$NON-NLS-2$
			
			String colspan = column
					.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
			if (colspan != null && colspan.length() > 0) {
				td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, colspan);
			}
			Element facetBody = ComponentUtil.getFacet(column, facetName);

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
	private static ArrayList<Element> getColumns(Element parentSourceElement) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList children = parentSourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			if ((child instanceof Element)
					&& (nodeName.endsWith(COLUMN) || nodeName.endsWith(COLUMNS))) {
				columns.add((Element) child);
			}
		}
		return columns;
	}

	/**
	 * 
	 * @param sourceElement
	 * @param columns
	 * @return
	 */
	private int getColumnsCount(Element sourceElement,
			ArrayList<Element> columns) {
		int count = 0;
		// check for exact value in component
		try {
			count = Integer.parseInt(sourceElement.getAttribute("columns")); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			// calculate max html columns count for all columns/rows children.
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
				String nodeName = column.getNodeName();
				if (nodeName.endsWith(":columnGroup")) { //$NON-NLS-1$
					// Store max calculated value of previous rows.
					count = Math.max(currentLength, count);
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement,
							getColumns(column));
					// Store max calculated value
					count = Math.max(currentLength, count);
					currentLength = 0;						
					String colspanStr = column
							.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);

				} else if (nodeName.equals(sourceElement.getPrefix() + COLUMN) ||
						nodeName.equals(sourceElement.getPrefix() + COLUMNS)) {
					String breakBeforeStr = column.getAttribute("breakBefore"); //$NON-NLS-1$
					boolean breakBefore = Boolean.getBoolean(breakBeforeStr);
					
					// For new row, save length of previous.
					if (breakBefore) {
						count = Math.max (currentLength, count);
						currentLength = 0;
					}
					
					try {
						String colspanStr = column
								.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
						currentLength += Integer.parseInt(colspanStr);
					} catch (NumberFormatException e) {
						currentLength++;
					}
				} else if (nodeName.endsWith(COLUMN)) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}

			}
		}
		count = Math.max (currentLength, count);
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
	 * @return <code>true</code> if it is required to re-create an element at
	 *         a modification of attribute, <code>false</code> otherwise.
	 */
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#validate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument, org.jboss.tools.vpe.editor.template.VpeCreationData) */
	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		RichFacesDataTableChildrenEncoder.validateChildren(pageContext, sourceNode, visualDocument, data);
	}
}
