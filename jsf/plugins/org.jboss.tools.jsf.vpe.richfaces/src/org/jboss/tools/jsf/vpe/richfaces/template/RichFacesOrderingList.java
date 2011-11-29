/**
 * 
 */
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.Messages;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
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
	
	private static final String TOP_CONTROL_FACET = "topControl"; //$NON-NLS-1$
	private static final String UP_CONTROL_FACET = "upControl"; //$NON-NLS-1$
	private static final String DOWN_CONTROL_FACET = "downControl"; //$NON-NLS-1$
	private static final String BOTTOM_CONTROL_FACET = "bottomControl"; //$NON-NLS-1$
	
	private static final String HEADER = "header"; //$NON-NLS-1$
	private static final String HEADER_CLASS = "headerClass"; //$NON-NLS-1$

	private static final String ARROW_TOP = "\u21D1";  //$NON-NLS-1$
	private static final String ARROW_UP = "\u2191";  //$NON-NLS-1$
	private static final String ARROW_DOWN = "\u2193"; //$NON-NLS-1$
	private static final String ARROW_BOTTOM = "\u21D3"; //$NON-NLS-1$
	
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

	private static final String CSS_LIST_BODY_CLASS = "rich-ordering-list-body"; //$NON-NLS-1$
	private static final String CSS_LIST_OUTPUT_CLASS = "rich-ordering-list-output"; //$NON-NLS-1$
	private static final String CSS_LIST_CONTENT_CLASS = "rich-ordering-list-content"; //$NON-NLS-1$
	private static final String CSS_LIST_ITEMS_CLASS = "rich-ordering-list-items"; //$NON-NLS-1$
	private static final String CSS_LIST_ROW_CLASS = "rich-ordering-list-row"; //$NON-NLS-1$
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		/*
		 * Setting up ordering list css file
		 */
		ComponentUtil.setCSSLink(pageContext, "orderingList/orderingList.css", //$NON-NLS-1$
		"richFacesOrderingList"); //$NON-NLS-1$
		
		/*
		 * Getting source node attributes
		 */
		Element sourceElement = (Element) sourceNode;

		String width = sourceElement.hasAttribute(WIDTH) ? sourceElement.getAttribute(WIDTH) : DEFAULT_WIDTH;
		String height = sourceElement.hasAttribute(HEIGHT) ? sourceElement.getAttribute(HEIGHT) : DEFAULT_HEIGHT;
//		String listWidth = sourceElement.getAttribute(LIST_WIDTH);
//		String listHeight = sourceElement.getAttribute(LIST_HEIGHT);

		/*
		 * Crating tags structure
		 */
		nsIDOMElement tableCommon = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement tableBody = visualDocument.createElement(HTML.TAG_TBODY);
		nsIDOMElement tableCaptionRow = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement tableCaptionCell = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement tableCaptionDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement tableListAndButtonsRow = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement leftCell = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement rightCell = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement tableListCell;
		nsIDOMElement tableButtonsCell;
		tableCaptionCell.appendChild(tableCaptionDiv);
		tableCaptionRow.appendChild(tableCaptionCell);
		tableBody.appendChild(tableCaptionRow);
		tableListAndButtonsRow.appendChild(leftCell);
		tableListAndButtonsRow.appendChild(rightCell);
		tableBody.appendChild(tableListAndButtonsRow);
		tableCommon.appendChild(tableBody);
		
		/*
		 * Set correct controls position
		 */
		String controlsHorizontalAlign = sourceElement.getAttribute(CONTROLS_HORIZONTAL_ALIGN);
		if ("left".equalsIgnoreCase(controlsHorizontalAlign)) { //$NON-NLS-1$
			tableButtonsCell = leftCell;
			tableListCell = rightCell;
			tableButtonsCell.setAttribute(HTML.ATTR_STYLE, "width: 1%;"); //$NON-NLS-1$
		} else {
			tableButtonsCell = rightCell;
			tableListCell = leftCell;
		}
		
		/*
		 * Creating template's VpeCreationData. 
		 */
		VpeCreationData creationData = new VpeCreationData(tableCommon);
		
		/*
		 * Setting required attributes 
		 */
		tableCommon.setAttribute(HTML.ATTR_WIDTH, width);
		tableCommon.setAttribute(HTML.ATTR_HEIGHT, height);
		tableCommon.setAttribute(HTML.ATTR_CLASS, CSS_LIST_BODY_CLASS);
		tableCaptionCell.setAttribute(HTML.ATTR_CLASS, CSS_CAPTION_CLASS);
		
		/*
		 * Encoding table caption
		 */
		Element captionFacet = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_CAPTION);
		if (null != captionFacet) {
			/*
			 * Encode caption facet
			 */
			VpeChildrenInfo captionInfo = new VpeChildrenInfo(tableCaptionDiv);
			captionInfo.addSourceChild(captionFacet);
			creationData.addChildrenInfo(captionInfo);
		} else {
			/*
			 * Get value from caption label
			 */
			String captionLabel = sourceElement.getAttribute(CAPTION_LABEL);
			tableCaptionCell.appendChild(visualDocument.createTextNode(captionLabel));
		}
		
		/*
		 * Encode controls
		 */
		String controlsType = sourceElement.getAttribute(CONTROLS_TYPE);
		if (!"none".equalsIgnoreCase(controlsType)) { //$NON-NLS-1$
			nsIDOMElement controlsDiv = createControlsDiv(pageContext, creationData, visualDocument, sourceElement);
			tableButtonsCell.setAttribute(HTML.ATTR_CLASS,
					CSS_BUTTON_VALIGN_CLASS);
			tableButtonsCell.setAttribute(HTML.ATTR_ALIGN, "center"); //$NON-NLS-1$			
			
			if (sourceElement.hasAttribute(CONTROLS_VERTICAL_ALIGN)){
				String controlsVerticalAlign = sourceElement.getAttribute(CONTROLS_VERTICAL_ALIGN);
				String valign = "center".equalsIgnoreCase(controlsVerticalAlign) ? "middle" : controlsVerticalAlign; //$NON-NLS-1$ //$NON-NLS-2$
				tableButtonsCell.setAttribute(HTML.ATTR_VALIGN, valign);
			}
			
			tableButtonsCell.appendChild(controlsDiv);
		}
		/*
		 * Encode ordering list itself
		 */
		tableListCell.appendChild(createResultList(pageContext, creationData, visualDocument,
				sourceElement));
		
		return creationData;
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
		
		String showButtonLabelsStr = sourceElement.getAttribute(SHOW_BUTTON_LABELS);
		String fastOrderControlsVisibleStr = sourceElement.getAttribute(FAST_ORDER_CONTROL_VISIBLE);
		String orderControlsVisibleStr = sourceElement.getAttribute(ORDER_CONTROL_VISIBLE);
		boolean showButtonLabels = ComponentUtil.string2boolean(showButtonLabelsStr);
		boolean fastOrderControlsVisible = ComponentUtil.string2boolean(fastOrderControlsVisibleStr);
		boolean orderControlsVisible = ComponentUtil.string2boolean(orderControlsVisibleStr);
		
		nsIDOMElement buttonsDiv = visualDocument.createElement(HTML.TAG_DIV);
		
		String controlsClass = CSS_CONTROLS_CLASS + " "; //$NON-NLS-1$
		if (sourceElement.hasAttribute(CONTROLS_CLASS)) {
			controlsClass += sourceElement.getAttribute(CONTROLS_CLASS) + " "; //$NON-NLS-1$
		}
		buttonsDiv.setAttribute(HTML.ATTR_CLASS, controlsClass + CSS_BUTTON_LAYOUT_CLASS);

		Element top_control_facet = ComponentUtil.getFacet(sourceElement, TOP_CONTROL_FACET);
		Element up_control_facet = ComponentUtil.getFacet(sourceElement, UP_CONTROL_FACET);
		Element down_control_facet = ComponentUtil.getFacet(sourceElement, DOWN_CONTROL_FACET);
		Element bottom_control_facet = ComponentUtil.getFacet(sourceElement, BOTTOM_CONTROL_FACET);
		
		if (fastOrderControlsVisible) {
			
			String topControlLabel = sourceElement.hasAttribute(TOP_CONTROL_LABEL) ? sourceElement.getAttribute(TOP_CONTROL_LABEL) : TOP_CONTROL_LABEL_DEFAULT;
			
			nsIDOMElement btnTopDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					topControlLabel, ARROW_TOP, showButtonLabels, top_control_facet, 
							CSS_TOP_CONTROL_CLASS, topControlClass);
			
			buttonsDiv.appendChild(btnTopDiv);
		}

		if (orderControlsVisible) {
			
			String upControlLabel = sourceElement.hasAttribute(UP_CONTROL_LABEL)? sourceElement.getAttribute(UP_CONTROL_LABEL) : UP_CONTROL_LABEL_DEFAULT;
			String downControlLabel = sourceElement.hasAttribute(DOWN_CONTROL_LABEL) ? sourceElement.getAttribute(DOWN_CONTROL_LABEL) : DOWN_CONTROL_LABEL_DEFAULT;
			
			nsIDOMElement btnUpDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					upControlLabel, ARROW_UP, showButtonLabels, up_control_facet,
							CSS_UP_CONTROL_CLASS, upControlClass);			
			nsIDOMElement btnDownDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					downControlLabel, ARROW_DOWN, showButtonLabels, down_control_facet, 
							CSS_DOWN_CONTROL_CLASS, downControlClass);
			
			buttonsDiv.appendChild(btnUpDiv);
			buttonsDiv.appendChild(btnDownDiv);
		}
		
		if (fastOrderControlsVisible) {
			
			String bottomControlLabel = sourceElement.hasAttribute(BOTTOM_CONTROL_LABEL) ? sourceElement.getAttribute(BOTTOM_CONTROL_LABEL) : BOTTOM_CONTROL_LABEL_DEFAULT;
						
			nsIDOMElement btnBottomDiv = createSingleButtonDiv(pageContext, creationData, visualDocument,
					bottomControlLabel, ARROW_BOTTOM, showButtonLabels, bottom_control_facet, 
							CSS_BOTTOM_CONTROL_CLASS, bottomControlClass);
			
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
	 * @param arrowName the img name
	 * @param showButtonLabels the show button labels
	 * @param buttonFacet the button facet
	 * @param cssStyleName the css style name
	 * @param customStyleClass the custom style class
	 * 
	 * @return the element
	 */
	private nsIDOMElement createSingleButtonDiv(final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMDocument visualDocument, String btnName, String arrowName,
			boolean showButtonLabels, Element buttonFacet, String cssStyleName,
			String customStyleClass) {
		
		nsIDOMElement div1 = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement div2 = visualDocument.createElement(HTML.TAG_DIV);
//		nsIDOMElement a = visualDocument.createElement(HTML.TAG_A);
		nsIDOMElement div3 = visualDocument.createElement(HTML.TAG_DIV);
//		nsIDOMElement arrowImg = visualDocument.createElement(HTML.TAG_IMG);
		
		div1.setAttribute(HTML.ATTR_CLASS,
						"dr-buttons-border" + " " + cssStyleName //$NON-NLS-1$ //$NON-NLS-2$
						+ (null != customStyleClass ? " " + customStyleClass : "")); //$NON-NLS-1$ //$NON-NLS-2$
		div2.setAttribute(HTML.ATTR_CLASS, CSS_BUTTON_CLASS);
		
		String  resourceFolder = RichFacesTemplatesActivator.getPluginResourcePath();
		String divStyle = "width: 100%;background-image: url(file://" + resourceFolder + BUTTON_BG + ");"; //$NON-NLS-1$ //$NON-NLS-2$
		
		div2.setAttribute(HTML.ATTR_STYLE, divStyle);
		div1.appendChild(div2);
		
		if (null != buttonFacet) {
			// Creating button with facet content
			nsIDOMElement fecetDiv = encodeControlsFacets(pageContext,
					buttonFacet, cssStyleName, customStyleClass,
					creationData, visualDocument);
			div2.appendChild(fecetDiv);
		} else {
			/*
			 * Creating button with image and label
			 */
//			ComponentUtil.setImg(arrowImg, arrowName);
//			arrowImg.setAttribute(HTML.ATTR_WIDTH, "15"); //$NON-NLS-1$
//			arrowImg.setAttribute(HTML.ATTR_HEIGHT, "15"); //$NON-NLS-1$
//			div3.appendChild(arrowImg);
			if (showButtonLabels) {
				div3.appendChild(visualDocument.createTextNode(arrowName
						+ Constants.WHITE_SPACE + btnName));
			} else {
				div3.appendChild(visualDocument.createTextNode(arrowName));
			}
			
//			a.setAttribute(HTML.ATTR_CLASS, CSS_BUTTON_SELECTION_CLASS);
//			a.appendChild(div3);
			div3.setAttribute(HTML.ATTR_CLASS, CSS_BUTTON_SELECTION_CLASS + " " + CSS_BUTTON_CONTENT_CLASS); //$NON-NLS-1$
			div2.appendChild(div3);
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

		/*
		 * Create list elements
		 */
		nsIDOMElement contentDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement contentTable = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
		
		/*
		 * Get list columns 
		 */
		ArrayList<Element> columns = getColumns(sourceElement);
		
		/*
		 * Encode Header
		 */
		ArrayList<Element> columnsHeaders = ComponentUtil.getColumnsWithFacet(columns, HEADER);
		if (!columnsHeaders.isEmpty()) {
			String headerClass = sourceElement.hasAttribute(HEADER_CLASS) ? sourceElement.getAttribute(HEADER_CLASS) : null;
			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			thead.appendChild(tr);
			String styleClass = ComponentUtil.encodeStyleClass(null,
					CSS_HEADER_CLASS + " " + CSS_TABLE_HEADER_CLASS, Constants.EMPTY, //$NON-NLS-1$
					headerClass);
			if (styleClass != null) {
				tr.setAttribute(HTML.ATTR_CLASS,styleClass);
			}
			
			/*
			 * Encoding columns headers
			 */
			for (Element column : columnsHeaders) {
				String columnHeaderClass = column.hasAttribute(RichFaces.ATTR_HEADER_CLASS) ? column.getAttribute(RichFaces.ATTR_HEADER_CLASS) : null;
				nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
				tr.appendChild(td);
				td.setAttribute(HTML.ATTR_BACKGROUND, "file:///" //$NON-NLS-1$
						+ ComponentUtil.getAbsoluteResourcePath(HEADER_CELL_BG).replace('\\', '/'));
				styleClass = ComponentUtil.encodeStyleClass(null, CSS_TABLE_HEADER_CELL_CLASS,
						headerClass, columnHeaderClass);
				td.setAttribute(HTML.ATTR_CLASS, styleClass);				
				
				if (column.hasAttribute(HTML.ATTR_COLSPAN)) {
					String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
					td.setAttribute(HTML.ATTR_COLSPAN, colspan);
				}
				Element facetBody = ComponentUtil.getFacet(column, RichFaces.NAME_FACET_HEADER);
				VpeChildrenInfo child = new VpeChildrenInfo(td);
				child.addSourceChild(facetBody);
				creationData.addChildrenInfo(child);
			}
		}
		
		/*
		 * Encode content 
		 */
		
		/*
		 * TODO: implement support of rowClasses
		 * following line commented by yradtsevich
		 * because the variable rowClasses was not used 
		 */
//		String rowClasses = sourceElement.getAttribute(ROW_CLASSES);
		
		String listWidth = sourceElement.hasAttribute(LIST_WIDTH) ? sourceElement.getAttribute(LIST_WIDTH) : DEFAULT_LIST_WIDTH;
		String listHeight = sourceElement.hasAttribute(LIST_HEIGHT) ? sourceElement.getAttribute(LIST_HEIGHT) : DEFAULT_LIST_HEIGHT;
		
		String divStyle = HTML.ATTR_WIDTH + " : " //$NON-NLS-1$
			+ listWidth + ";" //$NON-NLS-1$
				+ HTML.ATTR_HEIGHT + " : " //$NON-NLS-1$
					+ listHeight + ";" //$NON-NLS-1$
						+ "overflow: scroll;"; //$NON-NLS-1$
		contentDiv.setAttribute(HTML.ATTR_STYLE, divStyle);		
		contentDiv.setAttribute(HTML.ATTR_CLASS,
				CSS_LIST_OUTPUT_CLASS + " " + CSS_LIST_CONTENT_CLASS); //$NON-NLS-1$
		
		String listClass = CSS_LIST_ITEMS_CLASS;
		if (sourceElement.hasAttribute(LIST_CLASS)) {
			listClass += " " + sourceElement.getAttribute(LIST_CLASS); //$NON-NLS-1$
		}
		contentTable.setAttribute(HTML.ATTR_CLASS, listClass);
		contentTable.setAttribute(HTML.ATTR_CELLSPACING, "1"); //$NON-NLS-1$
		
		VisualDomUtil.copyAttributes(sourceElement, contentTable);
		contentTable.removeAttribute(HTML.ATTR_HEIGHT);
		contentTable.setAttribute(HTML.ATTR_STYLE, "width: 100%;"); //$NON-NLS-1$
		
		/*
		 * Encode children
		 */
		contentTable.appendChild(thead);
		RichFacesDataTableChildrenEncoder childrenEncoder = new RichFacesDataTableChildrenEncoder(
				creationData, visualDocument, sourceElement, contentTable);
		childrenEncoder.setRowClasses(CSS_LIST_ROW_CLASS, CSS_LIST_ROW_CLASS);
		childrenEncoder.encodeChildren();
		contentDiv.appendChild(contentTable);
		
		return contentDiv;
	}

	/**
	 * Encodes controls facets to DIV element with TABLE.
	 * 
	 * @param pageContext
	 * @param facetBody the facet body
	 * @param cssStyleName the css style name
	 * @param customStyleClass the custom style class
	 * @param creationData the creation data
	 * @param visualDocument the visual document
	 * @return the DIV element with facet
	 */
	private nsIDOMElement encodeControlsFacets(final VpePageContext pageContext,
			Element facetBody, String cssStyleName, String customStyleClass,
			VpeCreationData creationData, nsIDOMDocument visualDocument) {
		
		/*
		 * Create table for facet
		 */
		nsIDOMElement fecetDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		
		boolean isColumnGroup = facetBody.getNodeName()
				.endsWith(":columnGroup"); //$NON-NLS-1$
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable"); //$NON-NLS-1$
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData,
					facetBody, visualDocument, tbody);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData,
					facetBody, visualDocument, tbody);
		} else {
			nsIDOMElement tr = visualDocument
					.createElement(HTML.TAG_TR);
			tbody.appendChild(tr);

			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
			tr.appendChild(td);
			
			td.setAttribute(HTML.ATTR_SCOPE,
					HTML.TAG_COLGROUP);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);

			/*
			 * It is always controls facet
			 */
			tr.setAttribute(HTML.ATTR_CLASS,CSS_BUTTON_CLASS);
			td.setAttribute(HTML.ATTR_CLASS,
					CSS_BUTTON_CONTENT_CLASS + " " + cssStyleName + " " //$NON-NLS-1$ //$NON-NLS-2$
					+ customStyleClass);

			fecetDiv.setAttribute(HTML.ATTR_CLASS,
					CSS_BUTTON_CLASS + " " + CSS_BUTTON_CONTENT_CLASS + " " //$NON-NLS-1$ //$NON-NLS-2$
					+ cssStyleName + " " + customStyleClass); //$NON-NLS-1$
		}
	
		table.setAttribute(HTML.ATTR_CLASS,CSS_BUTTON_CONTENT_CLASS);
		table.appendChild(tbody);
		fecetDiv.appendChild(table);
		return fecetDiv;
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

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		RichFacesDataTableChildrenEncoder.validateChildren(pageContext, sourceNode, visualDocument, data);
	}
}
