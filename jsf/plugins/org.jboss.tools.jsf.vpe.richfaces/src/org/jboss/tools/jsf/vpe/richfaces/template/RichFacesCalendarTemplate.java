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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Displays template for calendar
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesCalendarTemplate extends VpeAbstractTemplate implements VpeToggableTemplate {

    static String[] HEADER_CONTENT = { "<<", "<", "", ">", ">>" };
    static String[] HEADER_CONTENT_ON_POPUP = { "<<", "<", "", ">", ">>", "X" };
    final static int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
			30, 31 };
	final static int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31,
			30, 31, 30, 31 }; 
    final static String STYLE_PATH = "calendar/calendar.css";
    final static String BUTTON_IMG = "calendar/calendar.gif";
    final static int COLUMN = 8;
    final static String FILL_WIDTH = "100%";
    final static int NUM_DAYS_IN_WEEK = 7;
    final static int NUM_WEEK_ON_PAGE = 6;
    final static String TODAY = "Today";
    final static String VERTICAL_SEPARATOR = "|";
    final static String APPLY = "Apply";
    final static int CALENDAR_WIDTH = 200;
    final static int CALENDAR_INPUT_WIDTH = CALENDAR_WIDTH - 20;
    final static int CALENDAR_IMAGE_WIDTH = 20;
    final static int CALENDAR_CUSTOM_IMAGE_WIDTH = 40;
    final static int CALENDAR_BUTTON_WIDTH = 80;
    final static String ATTRIBUTE_TEXT = "text";
    
    static final String DEFAULT_DATE_PATTERN = "MMMM, yyyy";
    static final String 	 HEADER = "header";
    static final String 	 FOOTER = "footer";
    static final String 	 VPE_USER_TOGGLE_ID_ATTR = "vpe-user-toggle-id";
    
    /*rich:calendar attributes*/
	static final String BUTTON_LABEL = "buttonLabel";
	static final String BUTTON_ICON = "buttonIcon";
	static final String DIRECTION = "direction";
	static final String ENABLE_MANUAL_INPUT = "enableManualInput";
	static final String CELL_HEIGHT = "cellHeight";
	static final String CELL_WIDTH = "cellWidth";
	static final String DATE_PATTERN = "datePattern";
	static final String VALUE = "value";
	static final String DISABLED = "disabled";
	static final String HORIZONTAL_OFFSET = "horizontalOffset";
	static final String VERTICAL_OFFSET = "verticalOffset";
	static final String LOCALE = "locale";
	static final String POPUP = "popup";
	static final String SHOW_APPLY_BUTTON = "showApplyButton";
	static final String SHOW_INPUT = "showInput";
	static final String SHOW_SHOW_WEEKS_DAY_BAR = "showWeekDaysBar";
	static final String SHOW_WEEKS_BAR = "showWeeksBar";
	static final String TODAY_CONTROL_MODE = "todayControlMode";
	static final String SHOW_HEADER = "showHeader";
	static final String SHOW_FOOTER= "showFooter";

	/*CSS  classes*/
	static final String CSS_R_C_INPUT = "rich-calendar-input";
	static final String CSS_R_C_BUTTON = "rich-calendar-button";
	static final String CSS_R_C_EXTERIOR = "rich-calendar-exterior";
	static final String CSS_R_C_HEADER = "rich-calendar-header";
	static final String CSS_R_C_TOOL = "rich-calendar-tool";
	static final String CSS_R_C_MONTH = "rich-calendar-month";
	static final String CSS_R_C_TOOL_CLOSE = "rich-calendar-tool-close";
	static final String CSS_R_C_DAYS = "rich-calendar-days";
	static final String CSS_R_C_WEEKENDS = "rich-calendar-weekends";
	static final String CSS_R_C_WEEK = "rich-calendar-week";
	static final String CSS_R_C_CELL = "rich-calendar-cell";
	static final String CSS_R_C_CELL_SIZE = "rich-calendar-cell-size";
	static final String CSS_R_C_HOLLY = "rich-calendar-holly";
	static final String CSS_R_C_BOUNDARY_DATES = "rich-calendar-boundary-dates";
	static final String CSS_R_C_BTN = "rich-calendar-btn";
	static final String CSS_R_C_TODAY = "rich-calendar-today";
	static final String CSS_R_C_SELECT = "rich-calendar-select";
	static final String CSS_R_C_TOOLFOOTER = "rich-calendar-toolfooter";
	static final String CSS_R_C_FOOTER = "rich-calendar-footer";
	static final String CSS_R_C_HEADER_OPTIONAL = "rich-calendar-header-optional";
	static final String CSS_R_C_FOOTER_OPTIONAL = "rich-calendar-footer-optional";
	
	private final static String WEEK_DAY_HTML_CLASS_ATTR = CSS_R_C_DAYS;
	private final static String HOL_WEEK_DAY_HTML_CLASS_ATTR = CSS_R_C_DAYS + " " + CSS_R_C_WEEKENDS;
	private final static String TODAY_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE + " "+ CSS_R_C_CELL + " "+ CSS_R_C_TODAY;
	private final static String CUR_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE + " "+ CSS_R_C_CELL;
	private final static String HOL_CUR_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE + " "+ CSS_R_C_CELL + " "+  CSS_R_C_HOLLY;
	private final static String OTHER_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE + " "+ CSS_R_C_CELL + " "+  CSS_R_C_BOUNDARY_DATES;
	private final static String HOL_OTHER_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE + " "+ CSS_R_C_CELL + " "+  CSS_R_C_HOLLY + " " + CSS_R_C_BOUNDARY_DATES ;
	
	/*Attribute values*/
	private static final String	DIRECTIONS_TOP_LEFT = "top-left";
	private static final String	DIRECTIONS_TOP_RIGHT = "top-right";
	private static final String	DIRECTIONS_BOTTOM_LEFT = "bottom-left";
	private static final String	DIRECTIONS_BOTTOM_RIGHT = "bottom-right";
	private static final int DIRECTIONS_TOP_LEFT_INT = 1;
	private static final int	DIRECTIONS_TOP_RIGHT_INT = 2;
	private static final int DIRECTIONS_BOTTOM_LEFT_INT = 3;
	private static final int DIRECTIONS_BOTTOM_RIGHT_INT = 4;
	
	/*styles for direction table cells*/
	private static final String STYLE_TOP_LEFT = "vertical-align: bottom; text-align: right;";
	private static final String STYLE_TOP_RIGHT = "vertical-align: bottom; text-align: left;";
	private static final String STYLE_BOTTOM_LEFT = "vertical-align: top; text-align: right;";
	private static final String STYLE_BOTTOM_RIGHT = "vertical-align: top; text-align: left;";
	
	/*rich:calendar attributes*/
	private String buttonLabel;
	private String buttonIcon;
	private String direction;
	private String cellHeight;
	private String cellWidth;
	private String datePattern;
	private String value;
	private String disabled;
	private String enableManualInput;
	private String horizontalOffset;
	private String verticalOffset;
	private String locale;
	private String popup;
	private String showApplyButton;
	private String showInput;
	private String showWeekDaysBar;
	private String showWeeksBar;
	private String todayControlMode;
	private String showHeader;
	private String showFooter;

    private List<String> holidays = new ArrayList<String>();
    private String[] weeks = new String[7];
    static Map<String, Integer> directions;
    private boolean showPopupCalendar = false;
	
    /**
     * Instantiates a new rich faces calendar template.
     */
    public RichFacesCalendarTemplate() {
		super();
		directions = new HashMap<String, Integer>(4);
		directions.put(DIRECTIONS_TOP_LEFT, DIRECTIONS_TOP_LEFT_INT);
		directions.put(DIRECTIONS_TOP_RIGHT,  DIRECTIONS_TOP_RIGHT_INT);
		directions.put(DIRECTIONS_BOTTOM_LEFT, DIRECTIONS_BOTTOM_LEFT_INT);
		directions.put(DIRECTIONS_BOTTOM_RIGHT, DIRECTIONS_BOTTOM_RIGHT_INT);
			
	}

    /**
     * Creates a node of the visual tree on the node of the source tree. This
     * visual node should not have the parent node This visual node can have
     * child nodes.
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceNode
     *                The current node of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @return The information on the created node of the visual tree.
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
    		nsIDOMDocument visualDocument) {
    	Element source = (Element) sourceNode;

    	readAttributes(sourceNode);
    	initCalendar();

    	ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "calendar");
    	nsIDOMElement div = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_DIV);
    	
    	nsIDOMElement resultTable = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
    	nsIDOMElement resultTD = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TD);
    	nsIDOMElement resultTR = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TR);
    	
    	resultTD.appendChild(div);
    	resultTR.appendChild(resultTD);
    	resultTable.appendChild(resultTR);
    	
    	VpeCreationData creationData = new VpeCreationData(resultTable);
    	
    	
    	nsIDOMElement calendar;
    	nsIDOMElement calendarWithPopup;
    	if ("false".equalsIgnoreCase(popup)) {
    		if ("true".equalsIgnoreCase(disabled)) {
    			calendar = visualDocument
    			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
    		} else {
    			calendar = createCalendar(visualDocument, creationData, source);
    		}
    		div.appendChild(calendar);
    	} else {
    		calendarWithPopup = createCalendarWithPopup(visualDocument, source);
    		calendar = createCalendar(visualDocument, creationData, source);

    		if (showPopupCalendar) {
    			if (attrPresents(direction) && directions.keySet().contains(direction)) {
    				nsIDOMElement table = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
    				nsIDOMElement top_tr = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TR);
    				nsIDOMElement bottom_tr = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TR);
    				nsIDOMElement top_left_td = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TD);
    				nsIDOMElement top_right_td = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TD);
    				nsIDOMElement bottom_left_td = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TD);
    				nsIDOMElement bottom_right_td = visualDocument
    				.createElement(HtmlComponentUtil.HTML_TAG_TD);

    				top_tr.appendChild(top_left_td);
    				top_tr.appendChild(top_right_td);
    				bottom_tr.appendChild(bottom_left_td);
    				bottom_tr.appendChild(bottom_right_td);
    				table.appendChild(top_tr);
    				table.appendChild(bottom_tr);
    				div.appendChild(table);

    				top_left_td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, STYLE_TOP_LEFT);
    				top_right_td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, STYLE_TOP_RIGHT);
    				bottom_left_td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, STYLE_BOTTOM_LEFT);
    				bottom_right_td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, STYLE_BOTTOM_RIGHT);

    				switch (directions.get(direction)) {
    				case DIRECTIONS_BOTTOM_RIGHT_INT:
    					top_right_td.appendChild(calendarWithPopup);
    					bottom_right_td.appendChild(calendar);
    					break;
    				case DIRECTIONS_BOTTOM_LEFT_INT:
    					top_right_td.appendChild(calendarWithPopup);
    					bottom_left_td.appendChild(calendar);
    					break;
    				case DIRECTIONS_TOP_RIGHT_INT:
    					top_right_td.appendChild(calendar);
    					top_right_td.appendChild(calendarWithPopup);
    					break;
    				case DIRECTIONS_TOP_LEFT_INT:
    					top_right_td.appendChild(calendarWithPopup);
    					top_left_td.appendChild(calendar);
    					break;
    				default:
    					break;
    				}

    			} else {
    				// no direction. simple display.
    				div.appendChild(calendarWithPopup);
    				div.appendChild(calendar);
    			}

    		} else {
    			// display input field with button
    			div.appendChild(calendarWithPopup);
    			// hide unparsed facets
    			nsIDOMElement hiddenDiv = visualDocument
    			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
    			hiddenDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
    					HtmlComponentUtil.HTML_ATR_WIDTH + " : " + CALENDAR_WIDTH
    					+ "px; overflow: hidden; display: none;");

    			nsIDOMElement hiddenTable = visualDocument
    			.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

    			Element headerFacet = ComponentUtil.getFacet(source, HEADER);
    			nsIDOMElement optionalHeader = null;
    			if (null != headerFacet) {
    				optionalHeader = createCalendarOptionalHeaderOrFooter(visualDocument, creationData, headerFacet, true);
    				hiddenTable.appendChild(optionalHeader);
    			}
    			Element footerFacet = ComponentUtil.getFacet(source, FOOTER);
    			nsIDOMElement optionalFooter = null;
    			if (null != footerFacet) {
    				optionalFooter = createCalendarOptionalHeaderOrFooter(visualDocument, creationData, footerFacet, false);
    				hiddenTable.appendChild(optionalFooter);
    			}
    			hiddenDiv.appendChild(hiddenTable);
    		}

    	}
    	return creationData;
    }

    /**
     * 
     * @param visualDocument
     * @return Node of the visual tree.
     */
    private nsIDOMElement createCalendarWithPopup(
    		nsIDOMDocument visualDocument, Element source) {
    	nsIDOMElement table = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

    	nsIDOMElement tr1 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TR);
    	nsIDOMElement td1_1 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TD);
    	nsIDOMElement td1_2 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TD);

    	nsIDOMElement tr2 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TR);
    	nsIDOMElement td2_1 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TD);

    	table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
    			HtmlComponentUtil.HTML_ATR_WIDTH + " : 100%");

    	tr1.appendChild(td1_1);
    	tr1.appendChild(td1_2);
    	table.appendChild(tr1);

    	tr2.appendChild(td2_1);
    	table.appendChild(tr2);

    	nsIDOMElement div = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_DIV);
    	int divWidth = CALENDAR_WIDTH + CALENDAR_IMAGE_WIDTH;
    	div.appendChild(table);

    	if (!"false".equalsIgnoreCase(showInput)) {
    		nsIDOMElement input = visualDocument
    		.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
    		input.setAttribute(HtmlComponentUtil.HTML_TYPE_ATTR, ATTRIBUTE_TEXT);
    		input.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_R_C_INPUT);
    		input.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
    				HtmlComponentUtil.HTML_ATR_WIDTH + " : "
    				+ CALENDAR_INPUT_WIDTH + "px;");

    		if ("true".equals(enableManualInput)) {
    			input.setAttribute(HtmlComponentUtil.HTML_READONLY_ATTR, "true");
    		}
    		if ("true".equals(disabled)) {
    			input.setAttribute(HtmlComponentUtil.HTML_ATTR_DISABLED, "true");
    		}
    		
    		if (attrPresents(value)) {
    			input.setAttribute(HtmlComponentUtil.HTML_VALUE_ATTR, value);
    		}
    		
    		td1_1.appendChild(input);
    	}


    	if (attrPresents(buttonLabel)) {
    		nsIDOMElement button = visualDocument
    		.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
    		button.setAttribute(HtmlComponentUtil.HTML_TYPE_ATTR,
    				HtmlComponentUtil.HTML_TAG_BUTTON);
    		button.setAttribute(HtmlComponentUtil.HTML_VALUE_ATTR, buttonLabel);
    		button.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_R_C_BUTTON);
    		if ("true".equalsIgnoreCase(disabled)) {
    			button.setAttribute(HtmlComponentUtil.HTML_ATTR_DISABLED, "true");
    		}
    		button.setAttribute(VPE_USER_TOGGLE_ID_ATTR, ""+showPopupCalendar);
    		td1_2.appendChild(button);
    		divWidth = CALENDAR_WIDTH + CALENDAR_BUTTON_WIDTH;
    	} else {
    		nsIDOMElement image = visualDocument
    		.createElement(HtmlComponentUtil.HTML_TAG_IMG);
    		image.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
    				CSS_R_C_BUTTON);
    		if (attrPresents(buttonIcon)) {
    			image.setAttribute(HtmlComponentUtil.HTML_ATR_SRC, buttonIcon);
    			divWidth = CALENDAR_WIDTH + CALENDAR_CUSTOM_IMAGE_WIDTH;
    		} else {
    			ComponentUtil.setImg(image, BUTTON_IMG);
    			divWidth = CALENDAR_WIDTH + CALENDAR_IMAGE_WIDTH;
    		}
    		image.setAttribute(VPE_USER_TOGGLE_ID_ATTR, ""+showPopupCalendar);
    		td1_2.appendChild(image);
    	}
    	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
    			HtmlComponentUtil.HTML_ATR_WIDTH + " : " 
    			+ divWidth + "px;  overflow: hidden;");
    	return div;
    }

    /**
     * Checks, whether it is necessary to re-create an element at change of
     * attribute
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceElement
     *                The current element of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @param visualNode
     *                The current node of the visual tree.
     * @param data
     *                The arbitrary data, built by a method <code>create</code>
     * @param name
     *                Atrribute name
     * @param value
     *                Attribute value
     * @return <code>true</code> if it is required to re-create an element at
     *         a modification of attribute, <code>false</code> otherwise.
     */
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

    /**
     * 
     * @param visualDocument
     * @return Node of the visual tree.
     */
    private nsIDOMElement createCalendar(nsIDOMDocument visualDocument, VpeCreationData creationData, Element sourceElement) {
    	nsIDOMElement div = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_DIV);
    	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
    			HtmlComponentUtil.HTML_ATR_WIDTH + " : " + CALENDAR_WIDTH
    			+ "px; overflow: hidden;");

    	nsIDOMElement table = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
    	table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
    			CSS_R_C_EXTERIOR);

    	nsIDOMElement tbody = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
    	Element headerFacet = ComponentUtil.getFacet(sourceElement, HEADER);
    	nsIDOMElement optionalHeader = null;
    	if (null != headerFacet) {
    		optionalHeader = createCalendarOptionalHeaderOrFooter(visualDocument, creationData, headerFacet, true);
    	}
    	Element footerFacet = ComponentUtil.getFacet(sourceElement, FOOTER);
    	nsIDOMElement optionalFooter = null;
    	if (null != footerFacet) {
    		optionalFooter = createCalendarOptionalHeaderOrFooter(visualDocument, creationData, footerFacet, false);
    	}
    	nsIDOMElement header = null;
    	nsIDOMElement calendarBody = createCalendarBody(visualDocument);
    	nsIDOMElement footer = null;

    	if (!"false".equals(showHeader)) {
    		header = createCalendarHeader(visualDocument);
    	}
    	if (!"false".equals(showFooter)) {
    		footer = createCalendarFooter(visualDocument);
    	}

    	if (null != optionalHeader) {
    		tbody.appendChild(optionalHeader);
    	}
    	if (null != header) {
    		tbody.appendChild(header);
    	}
    	tbody.appendChild(calendarBody);
    	if (null != footer) {
    		tbody.appendChild(footer);
    	}
    	if (null != optionalFooter) {
    		tbody.appendChild(optionalFooter);
    	}

    	table.appendChild(tbody);
    	div.appendChild(table);

    	return div;
    }

    /**
     * Creates the calendar optional header or footer.
     * 
     * @param visualDocument the visual document
     * @param creationData the creation data
     * @param facetBody the facet body
     * @param isHeader the is header
     * 
     * @return the element
     */
    private nsIDOMElement createCalendarOptionalHeaderOrFooter(
			nsIDOMDocument visualDocument, VpeCreationData creationData, Element facetBody, boolean isHeader) {
		nsIDOMElement tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		nsIDOMElement td = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, "" + COLUMN);
		tr.appendChild(td);
		
		if (isHeader) {
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_R_C_HEADER_OPTIONAL);
		} else {
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_R_C_FOOTER_OPTIONAL);
		}
		
		VpeChildrenInfo child = new VpeChildrenInfo(td);
		child.addSourceChild(facetBody);
		creationData.addChildrenInfo(child);
		return tr;
	}
    
    /**
     * 
     * @param visualDocument
     * @return Node of the visual tree.
     */
    private nsIDOMElement createCalendarHeader(nsIDOMDocument visualDocument) {
    	nsIDOMElement tr = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TR);
    	nsIDOMElement td = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TD);
    	td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, "" + COLUMN);

    	String[] array;
    	if ("false".equalsIgnoreCase(popup)) {
    		array = HEADER_CONTENT;
    	} else {
    		array = HEADER_CONTENT_ON_POPUP;
    	}

    	SimpleDateFormat sdf = new SimpleDateFormat();
    	Date date = getCalendarWithLocale().getTime();
    	String dateStr = "";
    	if (attrPresents(datePattern)) {
    		try {
    			sdf.applyPattern(datePattern);
    			dateStr = sdf.format(date);
    		} catch (Exception e) {
    			sdf.applyPattern(DEFAULT_DATE_PATTERN);
    			dateStr = sdf.format(date);
    		}
    	} else {
    		sdf.applyPattern(DEFAULT_DATE_PATTERN);
    		dateStr = sdf.format(date);
    	}
    	array[2] = dateStr;

    	nsIDOMElement table = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
    	table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
    	table.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, FILL_WIDTH);

    	nsIDOMElement tbody = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TBODY);

    	nsIDOMElement tr1 = visualDocument
    	.createElement(HtmlComponentUtil.HTML_TAG_TR);
    	tr1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
    			CSS_R_C_HEADER);

    	for (int i = 0; i < array.length; i++) {
    		nsIDOMElement td1 = visualDocument
    		.createElement(HtmlComponentUtil.HTML_TAG_TD);
    		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
    				i == 2 ? CSS_R_C_MONTH : CSS_R_C_TOOL);

    		// close tool
    		if (i == 5) {
    			td1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, "0");
    			td1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "cursor: pointer;");
    		}

    		nsIDOMText text1 = visualDocument.createTextNode(array[i]);
    		td1.appendChild(text1);
    		tr1.appendChild(td1);
    	}

    	tbody.appendChild(tr1);
    	table.appendChild(tbody);
    	td.appendChild(table);
    	tr.appendChild(td);
    	return tr;
    }
    
    /**
     * Creates the calendar footer.
     * 
     * @param visualDocument the visual document
     * 
     * @return the ns idom element
     */
    private nsIDOMElement createCalendarFooter(nsIDOMDocument visualDocument) {
		nsIDOMElement tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		nsIDOMElement td = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, "" + COLUMN);
		nsIDOMElement table = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, FILL_WIDTH);

		nsIDOMElement tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		tr1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_R_C_FOOTER);

		nsIDOMElement td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_R_C_TOOLFOOTER);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = getCalendarWithLocale();

		nsIDOMText text1 = visualDocument.createTextNode(sdf.format(cal
				.getTime()));
		td1.appendChild(text1);
		tr1.appendChild(td1);

		nsIDOMElement td2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td2.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, FILL_WIDTH);
		tr1.appendChild(td2);

		nsIDOMElement td3 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td3.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, FILL_WIDTH);
		td3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CSS_R_C_TOOLFOOTER);
		td3.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		String nodeText;
		if ("false".equalsIgnoreCase(showApplyButton)) {
			if ("hidden".equalsIgnoreCase(todayControlMode)) {
				nodeText = "";
			} else {
				nodeText = TODAY;
			}
		} else {
			if ("hidden".equalsIgnoreCase(todayControlMode)) {
				nodeText = APPLY;
			} else {
				nodeText = TODAY + VERTICAL_SEPARATOR + APPLY;
			}
		}
		nsIDOMText text3 = visualDocument.createTextNode(nodeText);
		
		td3.appendChild(text3);
		tr1.appendChild(td3);

		table.appendChild(tr1);
		td.appendChild(table);
		tr.appendChild(td);
		
		return tr;
    }

    /**
	 * 
	 * @param visualDocument
	 * @return Node of the visual tree.
	 */
	private nsIDOMElement createCalendarBody(nsIDOMDocument visualDocument) {

		nsIDOMElement tbody = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TBODY);

		nsIDOMElement bodyTR = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		Calendar cal1 = getCalendarWithLocale();

		SimpleDateFormat wdf = new SimpleDateFormat("EE");

		cal1.set(Calendar.WEEK_OF_MONTH, Calendar.SATURDAY);
		
		// Create week days row
		if (!"false".equalsIgnoreCase(showWeekDaysBar)) {
			for (int i = 0; i < COLUMN; i++) {
				nsIDOMElement td = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
				if ((i == 0) && (!"false".equalsIgnoreCase(showWeeksBar))) {
					td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							WEEK_DAY_HTML_CLASS_ATTR);
					nsIDOMElement br = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_BR);
					td.appendChild(br);
					bodyTR.appendChild(td);
				} else if ( i > 0) {
					if (holidays.contains(weeks[i - 1])) {
						td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
								HOL_WEEK_DAY_HTML_CLASS_ATTR);
					} else {
						td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
								WEEK_DAY_HTML_CLASS_ATTR);
					}
					nsIDOMText text = visualDocument.createTextNode(i == 0 ? ""
							: weeks[i - 1]);
					td.appendChild(text);
					bodyTR.appendChild(td);
				}
				//bodyTR.appendChild(td);
			}
			tbody.appendChild(bodyTR);
		} // showWeekDaysBar

		// Calendar body
		Calendar cal = getCalendarWithLocale();

		int month = cal.get(Calendar.MONTH);
		int dayN = cal.get(Calendar.DAY_OF_MONTH);

		// shift 'cal' to month's start
		cal.add(Calendar.DAY_OF_MONTH, -dayN);
		// shift 'cal' to week's start
		cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_WEEK) - cal
				.getFirstDayOfWeek()));

		//for number of week
		for (int i = NUM_WEEK_ON_PAGE; i > 0; i--) {

			nsIDOMElement tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);

			if (!"false".equalsIgnoreCase(showWeeksBar)) {
				// Week in year
				nsIDOMElement weekTD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
				weekTD.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						CSS_R_C_WEEK);
				nsIDOMText weekText = visualDocument.createTextNode(""
						+ cal.get(Calendar.WEEK_OF_YEAR));
				weekTD.appendChild(weekText);
				tr.appendChild(weekTD);
			}
			
			
			//for number of days in week
			for (int j = NUM_DAYS_IN_WEEK; j > 0; j--) {

				nsIDOMElement td = visualDocument
						.createElement(HtmlComponentUtil.HTML_TAG_TD);

				String currentAttr = "";

				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

				// if 'cal' is a member of month
				if (cal.get(Calendar.MONTH) == month) {

					// if this is current day
					if (cal.get(Calendar.DAY_OF_MONTH) == dayN
							&& cal.get(Calendar.MONTH) == month) {
						currentAttr = TODAY_HTML_CLASS_ATTR;

					}
					// if this is holiday
					else if (dayOfWeek == Calendar.SATURDAY
							|| dayOfWeek == Calendar.SUNDAY) {
						currentAttr = HOL_CUR_MONTH_HTML_CLASS_ATTR;
					} else {
						currentAttr = CUR_MONTH_HTML_CLASS_ATTR;
					}
				}
				// if 'cal' isn't a member of month
				else {
					// if this is holiday
					if (dayOfWeek == Calendar.SATURDAY
							|| dayOfWeek == Calendar.SUNDAY) {
						currentAttr = HOL_OTHER_MONTH_HTML_CLASS_ATTR;
					} else {
						currentAttr = OTHER_MONTH_HTML_CLASS_ATTR;
					}
				}

				td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, currentAttr);
				if (attrPresents(cellWidth) && attrPresents(cellHeight)) {
					td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
							HtmlComponentUtil.HTML_ATR_WIDTH + ":" + cellWidth
									+ "px;" + HtmlComponentUtil.HTML_ATR_HEIGHT
									+ ":" + cellHeight + "px;");
				} else if (attrPresents(cellWidth)) {
					td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, 
							HtmlComponentUtil.HTML_ATR_WIDTH +":" + cellWidth + "px;");
				} else if (attrPresents(cellHeight)) {
					td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, 
							HtmlComponentUtil.HTML_ATR_HEIGHT+":" + cellHeight + "px;");
				}
				
				nsIDOMText text = visualDocument.createTextNode(""
						+ cal.get(Calendar.DAY_OF_MONTH));
				td.appendChild(text);
				tr.appendChild(td);

				cal.add(Calendar.DAY_OF_MONTH, 1);

			}
			tbody.appendChild(tr);
		}
		return tbody;
	}


    /* (non-Javadoc)
     * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#setAttribute(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMNode, java.lang.Object, java.lang.String, java.lang.String)
     */
    public void setAttribute(VpePageContext pageContext, Element sourceElement,
	    nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
	    String name, String value) {
	super.setAttribute(pageContext, sourceElement, visualDocument,
		visualNode, data, name, value);
	if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_VALUE_ATTR)) {
	    String popup = sourceElement.getAttribute(POPUP);
	    if (popup != null && popup.equalsIgnoreCase("false"))
		return;
	    nsIDOMElement element = (nsIDOMElement) visualNode
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMNodeList list = element.getChildNodes();
	    nsIDOMNode tableNode = list.item(0);
	    nsIDOMElement input = (nsIDOMElement) tableNode
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    input.setAttribute(HtmlComponentUtil.HTML_VALUE_ATTR, value);
	}

    }


    /* (non-Javadoc)
     * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#removeAttribute(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMNode, java.lang.Object, java.lang.String)
     */
    public void removeAttribute(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMNode visualNode, Object data, String name) {
	super.removeAttribute(pageContext, sourceElement, visualDocument,
		visualNode, data, name);
	if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_VALUE_ATTR)) {
	    String popup = sourceElement.getAttribute(POPUP);
	    if (popup != null && popup.equalsIgnoreCase("false"))
		return;
	    nsIDOMElement element = (nsIDOMElement) visualNode
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    nsIDOMNodeList list = element.getChildNodes();
	    nsIDOMNode tableNode = list.item(0);
	    nsIDOMElement input = (nsIDOMElement) tableNode
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    input.removeAttribute(HtmlComponentUtil.HTML_VALUE_ATTR);
	}
    }
    
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readAttributes(Node sourceNode) {
		Element source = (Element) sourceNode;
		buttonLabel = source.getAttribute(BUTTON_LABEL);
		buttonIcon = source.getAttribute(BUTTON_ICON);
		direction = source.getAttribute(DIRECTION);
		cellHeight = source.getAttribute(CELL_HEIGHT);
		cellWidth = source.getAttribute(CELL_WIDTH);
		datePattern = source.getAttribute(DATE_PATTERN);
		value = source.getAttribute(VALUE);
		disabled = source.getAttribute(DISABLED);
		enableManualInput = source.getAttribute(ENABLE_MANUAL_INPUT);
		horizontalOffset = source.getAttribute(HORIZONTAL_OFFSET);
		verticalOffset = source.getAttribute(VERTICAL_OFFSET);
		locale = source.getAttribute(LOCALE);
		popup = source.getAttribute(POPUP);
		showApplyButton = source.getAttribute(SHOW_APPLY_BUTTON);
		showInput = source.getAttribute(SHOW_INPUT);
		showWeekDaysBar = source.getAttribute(SHOW_SHOW_WEEKS_DAY_BAR);
		showWeeksBar = source.getAttribute(SHOW_WEEKS_BAR);
		todayControlMode = source.getAttribute(TODAY_CONTROL_MODE);
		showHeader = source.getAttribute(SHOW_HEADER);
		showFooter = source.getAttribute(SHOW_FOOTER);
	}
    
	
    /**
     * Checks is attribute presents.
     * 
     * @param attr the attribute
     * 
     * @return true, if successful
     */
    private boolean attrPresents(String attr) {
		return ((null != attr) && (!"".equals(attr)));
	}

	/**
	 * Inits the calendar.
	 */
	private void initCalendar() {
		Calendar cal = getCalendarWithLocale();

		int firstDayOfWeek = cal.getFirstDayOfWeek();
		while (firstDayOfWeek != cal.get(Calendar.DAY_OF_WEEK)) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("EE");
		String dayOfWeek = "";
		for (int i = 0; i < NUM_DAYS_IN_WEEK; i++) {
			dayOfWeek = sdf.format(cal.getTime());
			weeks[i] = dayOfWeek;
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				holidays.add(dayOfWeek);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	
	/**
	 * Gets the calendar with locale.
	 * 
	 * @return the calendar with locale
	 */
	private Calendar getCalendarWithLocale() {
		Locale customLocale;
		if (attrPresents(this.locale)) {
			customLocale = parseLocale(locale);
			return Calendar.getInstance(customLocale);
		}
		return Calendar.getInstance();
	}
    
    public Locale parseLocale(String localeStr) {
		int length = localeStr.length();
		if (null == localeStr || length < 2) {
			return Locale.getDefault();
		}
		// Lookup index of first '_' in string locale representation.
		int index1 = localeStr.indexOf("_");
		// Get first charters (if exist) from string
		String language = null;
		if (index1 != -1) {
			language = localeStr.substring(0, index1);
		} else {
			return new Locale(localeStr);
		}
		// Lookup index of second '_' in string locale representation.
		int index2 = localeStr.indexOf("_", index1 + 1);
		String country = null;
		if (index2 != -1) {
			country = localeStr.substring(index1 + 1, index2);
			String variant = localeStr.substring(index2 + 1);
			return new Locale(language, country, variant);
		} else {
			country = localeStr.substring(index1 + 1);
			return new Locale(language, country);
		}
	}
    
	public void stopToggling(Node sourceNode) {
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
		showPopupCalendar = !showPopupCalendar;
	}
	
    @Override
    public void setSourceAttributeSelection(VpePageContext pageContext,
	    Element sourceElement, int offset, int length, Object data) {
	VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	sourceBuilder.setSelection(sourceElement, 0, 0);
    }
    
}
