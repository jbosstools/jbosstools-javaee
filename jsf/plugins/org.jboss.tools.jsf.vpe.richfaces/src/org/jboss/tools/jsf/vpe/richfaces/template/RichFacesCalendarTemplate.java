/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Displays template for calendar
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesCalendarTemplate extends VpeAbstractTemplate implements
		VpeToggableTemplate {
	
	private static final WeakHashMap<Node, Object> expandedComboBoxes = new WeakHashMap<Node, Object>();

	final static int COLUMN = 8;
	final static String FILL_WIDTH = "100%"; //$NON-NLS-1$
	final static int NUM_DAYS_IN_WEEK = 7;
	final static int NUM_WEEK_ON_PAGE = 6;

	final static int CALENDAR_WIDTH = 200;
	final static int CALENDAR_INPUT_WIDTH = CALENDAR_WIDTH - 20;
	final static int CALENDAR_IMAGE_WIDTH = 20;
	final static int CALENDAR_CUSTOM_IMAGE_WIDTH = 40;
	final static int CALENDAR_BUTTON_WIDTH = 80;

	static final String DEFAULT_DATE_PATTERN = "MMM dd,yyyy"; //$NON-NLS-1$
	static final String VPE_USER_TOGGLE_ID_ATTR = "vpe-user-toggle-id"; //$NON-NLS-1$

	/* rich:calendar attributes */

	static final String ATTR_ENABLE_MANUAL_INPUT = "enableManualInput"; //$NON-NLS-1$

	static final String ATTR_TODAY_CONTROL_MODE = "todayControlMode"; //$NON-NLS-1$

	/* CSS classes */
	static final String CSS_R_C_INPUT = "rich-calendar-input"; //$NON-NLS-1$
	static final String CSS_R_C_BUTTON = "rich-calendar-button"; //$NON-NLS-1$
	static final String CSS_R_C_EXTERIOR = "rich-calendar-exterior"; //$NON-NLS-1$
	static final String CSS_R_C_HEADER = "rich-calendar-header"; //$NON-NLS-1$
	static final String CSS_R_C_TOOL = "rich-calendar-tool"; //$NON-NLS-1$
	static final String CSS_R_C_MONTH = "rich-calendar-month"; //$NON-NLS-1$
	static final String CSS_R_C_TOOL_CLOSE = "rich-calendar-tool-close"; //$NON-NLS-1$
	static final String CSS_R_C_DAYS = "rich-calendar-days"; //$NON-NLS-1$
	static final String CSS_R_C_WEEKENDS = "rich-calendar-weekends"; //$NON-NLS-1$
	static final String CSS_R_C_WEEK = "rich-calendar-week"; //$NON-NLS-1$
	static final String CSS_R_C_CELL = "rich-calendar-cell"; //$NON-NLS-1$
	static final String CSS_R_C_CELL_SIZE = "rich-calendar-cell-size"; //$NON-NLS-1$
	static final String CSS_R_C_HOLLY = "rich-calendar-holly"; //$NON-NLS-1$
	static final String CSS_R_C_BOUNDARY_DATES = "rich-calendar-boundary-dates"; //$NON-NLS-1$
	static final String CSS_R_C_BTN = "rich-calendar-btn"; //$NON-NLS-1$
	static final String CSS_R_C_TODAY = "rich-calendar-today"; //$NON-NLS-1$
	static final String CSS_R_C_SELECT = "rich-calendar-select"; //$NON-NLS-1$
	static final String CSS_R_C_TOOLFOOTER = "rich-calendar-toolfooter"; //$NON-NLS-1$
	static final String CSS_R_C_FOOTER = "rich-calendar-footer"; //$NON-NLS-1$
	static final String CSS_R_C_HEADER_OPTIONAL = "rich-calendar-header-optional"; //$NON-NLS-1$
	static final String CSS_R_C_FOOTER_OPTIONAL = "rich-calendar-footer-optional"; //$NON-NLS-1$
	static final String CSS_R_C_TOOL_BTN = "rich-calendar-tool-btn"; //$NON-NLS-1$

	private final static String WEEK_DAY_HTML_CLASS_ATTR = CSS_R_C_DAYS;
	private final static String HOL_WEEK_DAY_HTML_CLASS_ATTR = CSS_R_C_DAYS
			+ " " + CSS_R_C_WEEKENDS; //$NON-NLS-1$
	private final static String TODAY_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE
			+ " " + CSS_R_C_CELL + " " + CSS_R_C_TODAY; //$NON-NLS-1$ //$NON-NLS-2$
	private final static String CUR_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE
			+ " " + CSS_R_C_CELL; //$NON-NLS-1$
	private final static String HOL_CUR_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE
			+ " " + CSS_R_C_CELL + " " + CSS_R_C_HOLLY; //$NON-NLS-1$ //$NON-NLS-2$
	private final static String OTHER_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE
			+ " " + CSS_R_C_CELL + " " + CSS_R_C_BOUNDARY_DATES; //$NON-NLS-1$ //$NON-NLS-2$
	private final static String HOL_OTHER_MONTH_HTML_CLASS_ATTR = CSS_R_C_CELL_SIZE
			+ " " + CSS_R_C_CELL + " " + CSS_R_C_HOLLY + " " + CSS_R_C_BOUNDARY_DATES; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/* Attribute values */
	private static final String DIRECTIONS_TOP_LEFT = "top-left"; //$NON-NLS-1$
	private static final String DIRECTIONS_TOP_RIGHT = "top-right"; //$NON-NLS-1$
	private static final String DIRECTIONS_BOTTOM_LEFT = "bottom-left"; //$NON-NLS-1$
	private static final String DIRECTIONS_BOTTOM_RIGHT = "bottom-right"; //$NON-NLS-1$

	/* styles for direction table cells */
	private static final String STYLE_TOP_LEFT = "vertical-align: bottom; text-align: right;"; //$NON-NLS-1$
	private static final String STYLE_TOP_RIGHT = "vertical-align: bottom; text-align: left;"; //$NON-NLS-1$
	private static final String STYLE_BOTTOM_LEFT = "vertical-align: top; text-align: right;"; //$NON-NLS-1$
	private static final String STYLE_BOTTOM_RIGHT = "vertical-align: top; text-align: left;"; //$NON-NLS-1$

	final static String DIRECTION_PATTERN = "(top|bottom)-(left|right)"; //$NON-NLS-1$
	final static int DEFAULT_CELL_WIDTH = 25;
	final static int DEFAULT_CELL_HEIGHT = 22;

	final static int JOINT_POINT_BOTTOM = 7;
	final static int JOINT_POINT_TOP = -17;
	final static String TOP = "top"; //$NON-NLS-1$
	final static String LEFT = "left"; //$NON-NLS-1$
	final static String HIDDEN = "hidden"; //$NON-NLS-1$

	static Map<String, int[]> DIRECTIONS = new HashMap<String, int[]>(4);
	static {
		DIRECTIONS.put(DIRECTIONS_TOP_LEFT, new int[] { -1, -1 });
		DIRECTIONS.put(DIRECTIONS_TOP_RIGHT, new int[] { -1, 1 });
		DIRECTIONS.put(DIRECTIONS_BOTTOM_LEFT, new int[] { 1, -1 });
		DIRECTIONS.put(DIRECTIONS_BOTTOM_RIGHT, new int[] { 1, 1 });
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++ NEW FIELDS
	 */

	static final String ATTR_SHOW_SHOW_WEEKS_DAY_BAR = "showWeekDaysBar"; //$NON-NLS-1$
	static final String ATTR_SHOW_WEEKS_BAR = "showWeeksBar"; //$NON-NLS-1$
	static final String ATTR_SHOW_HEADER = "showHeader"; //$NON-NLS-1$
	static final String ATTR_SHOW_FOOTER = "showFooter"; //$NON-NLS-1$
	static final String ATTR_CELL_HEIGHT = "cellHeight"; //$NON-NLS-1$
	static final String ATTR_CELL_WIDTH = "cellWidth"; //$NON-NLS-1$
	static final String ATTR_BUTTON_LABEL = "buttonLabel"; //$NON-NLS-1$
	static final String ATTR_SHOW_APPLY_BUTTON = "showApplyButton"; //$NON-NLS-1$
	static final String ATTR_DATE_PATTERN = "datePattern"; //$NON-NLS-1$
	static final String ATTR_FIRST_WEEK_DAY = "firstWeekDay"; //$NON-NLS-1$

	final private static String DEFAULT_INPUT_STYLE = "vertical-align: middle;";//$NON-NLS-1$
	final private static String STYLE_PATH = "calendar/calendar.css"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_ICON = "calendar/calendar.gif"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_ICON_DISABLED = "calendar/disabled_button.gif"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_STYLE = "vertical-align: middle;";//$NON-NLS-1$

	final private static String NEXT_MONTH_CONTROL = ">";//$NON-NLS-1$
	final private static String PREVIOUS_MONTH_CONTROL = "<";//$NON-NLS-1$
	final private static String NEXT_YEAR_CONTROL = ">>";//$NON-NLS-1$
	final private static String PREVIOUS_YEAR_CONTROL = "<<";//$NON-NLS-1$
	final private static String APPLY_CONTROL = "Apply"; //$NON-NLS-1$
	final private static String TODAY_CONTROL = "Today"; //$NON-NLS-1$
	final private static String CLOSE_CONTROL = "X"; //$NON-NLS-1$
	final private static String VERTICAL_SEPARATOR = "|"; //$NON-NLS-1$

	private int tableWidth;
	private int tableHeight;
	private Calendar calendar;
	private Locale locale;
	private String[] weekDays;
	private String[] months;
	private int horizontalOffset;
	private int verticalOffset;
	private String currentMonthControl;
	private String currentDayControl;
	private DirectionData direction;
	private DirectionData jointPoint;

	private int cellHeight;
	private int cellWidth;
	private int zindex;
	private String value;

	private String inputStyle;
	private String inputClass;
	private String inputSize;

	private boolean disabled;

	private String buttonIcon;
	private String buttonLabel;
	private String buttonClass;

	private String style;
	private String styleClass;

	private boolean showInput;
	private boolean showWeekDaysBar;
	private boolean showWeeksBar;
	private boolean showHeader;
	private boolean showFooter;
	private boolean showApplyButton;
	private boolean showTodayControl;

	private boolean popup;

	private int firstWeekDay;

	private String datePattern;

	private String todayControlMode;

	private String enableManualInput;

	/**
	 * Instantiates a new rich faces calendar template.
	 */
	public RichFacesCalendarTemplate() {
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
		Element source = (Element) sourceNode;

		readAttributes(pageContext, sourceNode);

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "calendar"); //$NON-NLS-1$
		nsIDOMElement wrapper = visualDocument.createElement(HTML.TAG_SPAN);

		VpeCreationData creationData = new VpeCreationData(wrapper);

		nsIDOMElement calendar;
		nsIDOMElement calendarWithPopup;
		if (!popup) {
			if (disabled) {
				calendar = visualDocument.createElement(HTML.TAG_DIV);
			} else {
				calendar = createCalendar(visualDocument, creationData, source);
			}
			wrapper.appendChild(calendar);
		} else {
			calendarWithPopup = createPopupCalendar(visualDocument, source,
					creationData);

			wrapper.appendChild(calendarWithPopup);

		}
		return creationData;
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
	 *            Atrribute name
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

	/**
	 * 
	 * @param visualDocument
	 * @return Node of the visual tree.
	 */
	private nsIDOMElement createCalendar(nsIDOMDocument visualDocument,
			VpeCreationData creationData, Element sourceElement) {

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_CLASS, CSS_R_C_EXTERIOR
				+ Constants.WHITE_SPACE + styleClass);
		table.setAttribute(HTML.ATTR_STYLE, style);

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement header = null;
		nsIDOMElement calendarBody = createCalendarBody(visualDocument);
		nsIDOMElement footer = null;

		if (showHeader) {
			Element headerFacet = ComponentUtil.getFacet(sourceElement,
					RichFaces.NAME_FACET_HEADER);
			if (headerFacet != null) {
				header = createCalendarOptionalHeaderOrFooter(visualDocument,
						creationData, headerFacet, true);
			} else {

				String[] defaultHeaderContent = popup ? new String[] {
						PREVIOUS_YEAR_CONTROL, PREVIOUS_MONTH_CONTROL,
						currentMonthControl, NEXT_MONTH_CONTROL,
						NEXT_YEAR_CONTROL, CLOSE_CONTROL } : new String[] {
						PREVIOUS_YEAR_CONTROL, PREVIOUS_MONTH_CONTROL,
						currentMonthControl, NEXT_MONTH_CONTROL,
						NEXT_YEAR_CONTROL };

				String[] defaultHeaderContentStyles = popup ? new String[] {
						CSS_R_C_TOOL,
						CSS_R_C_TOOL,
						CSS_R_C_MONTH,
						CSS_R_C_TOOL,
						CSS_R_C_TOOL,
						CSS_R_C_TOOL + Constants.WHITE_SPACE
								+ CSS_R_C_TOOL_CLOSE } : new String[] {
						CSS_R_C_TOOL, CSS_R_C_TOOL, CSS_R_C_MONTH,
						CSS_R_C_TOOL, CSS_R_C_TOOL };

				// header = createDefaultCalendarHeader(visualDocument);
				header = createHeaderBlock(visualDocument, CSS_R_C_HEADER,
						defaultHeaderContent, defaultHeaderContentStyles);
			}
		}
		if (showFooter) {
			Element footerFacet = ComponentUtil.getFacet(sourceElement,
					RichFaces.NAME_FACET_FOOTER);
			if (footerFacet != null) {

				footer = createCalendarOptionalHeaderOrFooter(visualDocument,
						creationData, footerFacet, false);
			} else {

				String[] defaultFooterContent;
				String[] defaultFooterContentStyles;
				if (popup) {

					defaultFooterContent = new String[] {
							showTodayControl ? TODAY_CONTROL : Constants.EMPTY,
							showApplyButton ? APPLY_CONTROL : Constants.EMPTY };
					defaultFooterContentStyles = new String[] {
							CSS_R_C_TOOLFOOTER, CSS_R_C_TOOLFOOTER };

				} else {

					defaultFooterContent = new String[] { TODAY_CONTROL };
					defaultFooterContentStyles = new String[] { CSS_R_C_TOOLFOOTER };

				}

				// footer = createDefaultCalendarFooter(visualDocument);
				footer = createFooterBlock(visualDocument, CSS_R_C_FOOTER,
						defaultFooterContent, defaultFooterContentStyles);
			}
		}

		if (null != header) {
			tbody.appendChild(header);
		}
		tbody.appendChild(calendarBody);
		if (null != footer) {
			tbody.appendChild(footer);
		}

		table.appendChild(tbody);

		return table;
	}

	/**
	 * Creates the calendar optional header or footer.
	 * 
	 * @param visualDocument
	 *            the visual document
	 * @param creationData
	 *            the creation data
	 * @param facetBody
	 *            the facet body
	 * @param isHeader
	 *            the is header
	 * 
	 * @return the element
	 */
	private nsIDOMElement createCalendarOptionalHeaderOrFooter(
			nsIDOMDocument visualDocument, VpeCreationData creationData,
			Element facetBody, boolean isHeader) {
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		td.setAttribute(HTML.ATTR_COLSPAN, Constants.EMPTY + COLUMN);
		tr.appendChild(td);

		if (isHeader) {
			td.setAttribute(HTML.ATTR_CLASS, CSS_R_C_HEADER_OPTIONAL);
		} else {
			td.setAttribute(HTML.ATTR_CLASS, CSS_R_C_FOOTER_OPTIONAL);
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
	// private nsIDOMElement createDefaultCalendarHeader(
	// nsIDOMDocument visualDocument) {
	//
	// nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	// nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
	// td.setAttribute(HTML.ATTR_COLSPAN, Constants.EMPTY + COLUMN);
	//
	// nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
	//		table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
	//		table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
	//		table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
	// table.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
	//
	// nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
	//
	// nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR);
	// tr1.setAttribute(HTML.ATTR_CLASS, CSS_R_C_HEADER);
	//
	// for (int i = 0; i < defaultHeaderContent.length; i++) {
	// nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD);
	// td1.setAttribute(HTML.ATTR_CLASS, i == 2 ? CSS_R_C_MONTH
	// : CSS_R_C_TOOL);
	//
	// // close tool
	// if (i == 5) {
	//				td1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, "0"); //$NON-NLS-1$
	//				td1.setAttribute(HTML.ATTR_STYLE, "cursor: pointer;"); //$NON-NLS-1$
	// }
	//
	// nsIDOMText text1 = visualDocument
	// .createTextNode(defaultHeaderContent[i]);
	// td1.appendChild(text1);
	// tr1.appendChild(td1);
	// }
	//
	// tbody.appendChild(tr1);
	// table.appendChild(tbody);
	// td.appendChild(table);
	// tr.appendChild(td);
	// return tr;
	// }
	/**
	 * Creates the calendar footer.
	 * 
	 * @param visualDocument
	 *            the visual document
	 * 
	 * @return the ns idom element
	 */
	private nsIDOMElement createDefaultCalendarFooter(
			nsIDOMDocument visualDocument) {
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		td.setAttribute(HTML.ATTR_COLSPAN, Constants.EMPTY + COLUMN);
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);

		nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR);
		tr1.setAttribute(HTML.ATTR_CLASS, CSS_R_C_FOOTER);

		nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD);
		td1.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOLFOOTER);

		nsIDOMText text1 = visualDocument.createTextNode(currentDayControl);
		td1.appendChild(text1);
		tr1.appendChild(td1);

		nsIDOMElement td2 = visualDocument.createElement(HTML.TAG_TD);
		td2.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		tr1.appendChild(td2);

		nsIDOMElement td3 = visualDocument.createElement(HTML.TAG_TD);
		td3.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		td3.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOLFOOTER);
		td3.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_ALIGN_RIGHT);

		String nodeText;
		// if (showApplyButton) {
		//			if ("hidden".equalsIgnoreCase(todayControlMode)) { //$NON-NLS-1$
		// nodeText = Constants.EMPTY;
		// } else {
		// nodeText = TODAY;
		// }
		// } else {
		if ("hidden".equalsIgnoreCase(todayControlMode)) { //$NON-NLS-1$
			nodeText = APPLY_CONTROL;
		} else {
			nodeText = TODAY_CONTROL + VERTICAL_SEPARATOR + APPLY_CONTROL;
		}
		// }
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

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement weekDaysTR = visualDocument.createElement(HTML.TAG_TR);

		// Create week days row
		if (showWeekDaysBar) {
			for (int i = 0; i < COLUMN; i++) {
				nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
				if ((i == 0) && (showWeeksBar)) {
					td.setAttribute(HTML.ATTR_CLASS, WEEK_DAY_HTML_CLASS_ATTR);
					nsIDOMElement br = visualDocument
							.createElement(HTML.TAG_BR);
					td.appendChild(br);
					weekDaysTR.appendChild(td);
				} else if (i > 0) {

					int dayIndex = (i - 1 + firstWeekDay) % NUM_DAYS_IN_WEEK;
					if ((dayIndex + 1) == Calendar.SUNDAY
							|| (dayIndex + 1) == Calendar.SATURDAY) {
						td.setAttribute(HTML.ATTR_CLASS,
								HOL_WEEK_DAY_HTML_CLASS_ATTR);
					} else {
						td.setAttribute(HTML.ATTR_CLASS,
								WEEK_DAY_HTML_CLASS_ATTR);
					}
					nsIDOMText text = visualDocument
							.createTextNode(i == 0 ? Constants.EMPTY
									: weekDays[dayIndex]);
					td.appendChild(text);
					weekDaysTR.appendChild(td);
				}
			}
			tbody.appendChild(weekDaysTR);
		} // showWeekDaysBar

		// Calendar body

		int month = calendar.get(Calendar.MONTH);
		int dayN = calendar.get(Calendar.DAY_OF_MONTH);

		// shift 'cal' to month's start
		calendar.add(Calendar.DAY_OF_MONTH, -dayN);
		// shift 'cal' to week's start
		calendar.add(Calendar.DAY_OF_MONTH, -(calendar
				.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek()));

		// for number of week
		for (int i = NUM_WEEK_ON_PAGE; i > 0; i--) {

			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

			if (showWeeksBar) {
				// Week in year
				nsIDOMElement weekTD = visualDocument
						.createElement(HTML.TAG_TD);
				weekTD.setAttribute(HTML.ATTR_CLASS, CSS_R_C_WEEK);
				nsIDOMText weekText = visualDocument.createTextNode(String
						.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
				weekTD.appendChild(weekText);
				tr.appendChild(weekTD);
			}

			// for number of days in week
			for (int j = NUM_DAYS_IN_WEEK; j > 0; j--) {

				nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

				String currentAttr = Constants.EMPTY;

				int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

				// if 'cal' is a member of month
				if (calendar.get(Calendar.MONTH) == month) {

					// if this is current day
					if (calendar.get(Calendar.DAY_OF_MONTH) == dayN
							&& calendar.get(Calendar.MONTH) == month) {
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

				td.setAttribute(HTML.ATTR_CLASS, currentAttr);
				// if (socellWidth) && attrPresents(cellHeight)) {
				td.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH
						+ Constants.COLON + cellWidth
						+ "px;" + HTML.STYLE_PARAMETER_HEIGHT //$NON-NLS-1$
						+ Constants.COLON + cellHeight + "px;"); //$NON-NLS-1$ 
				// } else if (attrPresents(cellWidth)) {
				// td.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH
				//							+ Constants.COLON + cellWidth + "px;"); //$NON-NLS-1$ 
				// } else if (attrPresents(cellHeight)) {
				// td.setAttribute(HTML.ATTR_STYLE,
				// HTML.STYLE_PARAMETER_HEIGHT + Constants.COLON
				//									+ cellHeight + "px;"); //$NON-NLS-1$ 
				// }

				nsIDOMText text = visualDocument.createTextNode(Constants.EMPTY
						+ calendar.get(Calendar.DAY_OF_MONTH));
				td.appendChild(text);
				tr.appendChild(td);

				calendar.add(Calendar.DAY_OF_MONTH, 1);

			}
			tbody.appendChild(tr);
		}

		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayN);

		return tbody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#setAttribute(
	 * org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element,
	 * org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMNode,
	 * java.lang.Object, java.lang.String, java.lang.String)
	 */
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
			String name, String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		if (name.equalsIgnoreCase(RichFaces.ATTR_VALUE)) {
			String popup = sourceElement.getAttribute(RichFaces.ATTR_POPUP);
			if (popup != null && popup.equalsIgnoreCase(Constants.FALSE))
				return;
			nsIDOMElement element = (nsIDOMElement) visualNode
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			nsIDOMNodeList list = element.getChildNodes();
			nsIDOMNode tableNode = list.item(0);
			nsIDOMElement input = (nsIDOMElement) tableNode
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			input.setAttribute(HTML.ATTR_VALUE, value);
		}

	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readAttributes(VpePageContext pageContext, Node sourceNode) {

		Element sourceElement = (Element) sourceNode;

		// style
		style = sourceElement.getAttribute(RichFaces.ATTR_STYLE);

		// style
		styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);

		// input must be showed if there is no "showInput" attribute or if it
		// equals "true"
		showInput = (!sourceElement.hasAttribute(RichFaces.ATTR_SHOW_INPUT) || Constants.TRUE
				.equalsIgnoreCase(sourceElement
						.getAttribute(RichFaces.ATTR_SHOW_INPUT)));

		// inputStyle
		inputStyle = DEFAULT_INPUT_STYLE + Constants.SEMICOLON
				+ sourceElement.getAttribute(RichFaces.ATTR_INPUT_STYLE);

		// inputClass
		inputClass = CSS_R_C_INPUT + Constants.WHITE_SPACE
				+ sourceElement.getAttribute(RichFaces.ATTR_INPUT_CLASS);

		// inputSize
		inputSize = sourceElement.hasAttribute(RichFaces.ATTR_INPUT_SIZE) ? sourceElement
				.getAttribute(RichFaces.ATTR_INPUT_SIZE)
				: Constants.EMPTY;

		// disabled
		disabled = Constants.TRUE.equalsIgnoreCase(sourceElement
				.getAttribute(RichFaces.ATTR_DISABLED));

		// buttonLabel
		buttonLabel = sourceElement.getAttribute(ATTR_BUTTON_LABEL);

		// buttonIcon
		if (disabled) {
			if (sourceElement.hasAttribute(RichFaces.ATTR_BUTTON_ICON_DISABLED))
				buttonIcon = ComponentUtil
						.getAbsoluteWorkspacePath(
								sourceElement
										.getAttribute(RichFaces.ATTR_BUTTON_ICON_DISABLED),
								pageContext);
			else {
				buttonIcon = ComponentUtil
						.getAbsoluteResourcePath(DEFAULT_BUTTON_ICON_DISABLED);
			}
		} else {

			if (sourceElement.hasAttribute(RichFaces.ATTR_BUTTON_ICON))
				buttonIcon = ComponentUtil.getAbsoluteWorkspacePath(
						sourceElement.getAttribute(RichFaces.ATTR_BUTTON_ICON),
						pageContext);
			else {
				buttonIcon = ComponentUtil
						.getAbsoluteResourcePath(DEFAULT_BUTTON_ICON);
			}
		}

		// buttonClass
		buttonClass = sourceElement.getAttribute(RichFaces.ATTR_BUTTON_CLASS);

		// showWeekDaysBar
		showWeekDaysBar = (!sourceElement
				.hasAttribute(ATTR_SHOW_SHOW_WEEKS_DAY_BAR) || Constants.TRUE
				.equalsIgnoreCase(sourceElement
						.getAttribute(ATTR_SHOW_SHOW_WEEKS_DAY_BAR)));
		// showWeeksBar
		showWeeksBar = (!sourceElement.hasAttribute(ATTR_SHOW_WEEKS_BAR) || Constants.TRUE
				.equalsIgnoreCase(sourceElement
						.getAttribute(ATTR_SHOW_WEEKS_BAR)));

		// showHeader
		showHeader = (!sourceElement.hasAttribute(ATTR_SHOW_HEADER) || Constants.TRUE
				.equalsIgnoreCase(sourceElement.getAttribute(ATTR_SHOW_HEADER)));

		// showApplyButton
		showApplyButton = Constants.TRUE.equalsIgnoreCase(sourceElement
				.getAttribute(ATTR_SHOW_APPLY_BUTTON));

		showTodayControl = !HIDDEN.equalsIgnoreCase(sourceElement
				.getAttribute(ATTR_TODAY_CONTROL_MODE));

		// showFooter
		showFooter = (!sourceElement.hasAttribute(ATTR_SHOW_FOOTER) || Constants.TRUE
				.equalsIgnoreCase(sourceElement.getAttribute(ATTR_SHOW_FOOTER)))
				&& (showApplyButton || showTodayControl);

		// popup
		popup = (!sourceElement.hasAttribute(RichFaces.ATTR_POPUP) || Constants.TRUE
				.equalsIgnoreCase(sourceElement
						.getAttribute(RichFaces.ATTR_POPUP)));

		// value
		value = sourceElement.hasAttribute(RichFaces.ATTR_VALUE) ? sourceElement
				.getAttribute(RichFaces.ATTR_VALUE)
				: Constants.EMPTY;

		// locale
		locale = getLocale(sourceElement);

		// calendar
		calendar = Calendar.getInstance(locale);

		// weekDays
		weekDays = getWeekDays(sourceElement, locale);

		// months
		months = getMonths(sourceElement, locale);

		// firstWeekDay
		firstWeekDay = getFirstWeekDay(sourceElement, ATTR_FIRST_WEEK_DAY,
				calendar.getFirstDayOfWeek() - 1);

		calendar.setFirstDayOfWeek(firstWeekDay + 1);

		// currentDayControl
		currentMonthControl = months[calendar.get(Calendar.MONTH)]
				+ Constants.COMMA + Constants.WHITE_SPACE
				+ calendar.get(Calendar.YEAR);

		// currentDayControl
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);

		if (sourceElement.hasAttribute(ATTR_DATE_PATTERN)) {
			datePattern = sourceElement.getAttribute(ATTR_DATE_PATTERN);
			try {
				sdf.applyPattern(datePattern);
			} catch (IllegalArgumentException e) {
				// DEFAULT_DATE_PATTERN is used in this case
			}
		}

		// currentDayControl
		currentDayControl = sdf.format(calendar.getTime());

		// cellWidth
		cellWidth = parseSizeAttribute(sourceElement, ATTR_CELL_WIDTH,
				DEFAULT_CELL_WIDTH);

		// cellHeight
		cellHeight = parseSizeAttribute(sourceElement, ATTR_CELL_HEIGHT,
				DEFAULT_CELL_HEIGHT);

		// tableWidth
		tableWidth = (showWeeksBar ? DEFAULT_CELL_WIDTH : 0) + cellWidth
				* NUM_DAYS_IN_WEEK;

		// tableHeight
		tableHeight = (showHeader ? DEFAULT_CELL_HEIGHT : 0)
				+ (showFooter ? DEFAULT_CELL_HEIGHT : 0)
				+ (showWeekDaysBar ? DEFAULT_CELL_HEIGHT : 0) + cellHeight
				* NUM_WEEK_ON_PAGE;

		// jointPoint
		jointPoint = getDirection(sourceElement, RichFaces.ATTR_JOINT_POINT,
				DIRECTIONS_BOTTOM_LEFT);

		// direction
		direction = getDirection(sourceElement, RichFaces.ATTR_DIRECTION,
				DIRECTIONS_BOTTOM_RIGHT);

		//zindex
		zindex = parseNumberAttribute(sourceElement,
				RichFaces.ATTR_ZINDEX, 3);
		
		// horizontalOffset
		horizontalOffset = parseNumberAttribute(sourceElement,
				RichFaces.ATTR_HORIZONTAL_OFFSET, 0);

		// verticalOffset
		verticalOffset = parseNumberAttribute(sourceElement,
				RichFaces.ATTR_VERTICAL_OFFSET, 0);

		// todayControlMode
		todayControlMode = sourceElement.getAttribute(ATTR_TODAY_CONTROL_MODE);

		// enableManualInput
		enableManualInput = sourceElement
				.getAttribute(ATTR_ENABLE_MANUAL_INPUT);

	}

	/**
	 * Checks is attribute presents.
	 * 
	 * @param attr
	 *            the attribute
	 * 
	 * @return true, if successful
	 */
	private boolean attrPresents(String attr) {
		return ((null != attr) && (attr.length() != 0));
	}

	public void stopToggling(Node sourceNode) {
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
    	if (isExpanded(sourceNode)) {
    		expandedComboBoxes.remove(sourceNode);
    	} else {
    		expandedComboBoxes.put(sourceNode, null);
    	}
	}

	private boolean isExpanded(Node sourceNode) {
		return expandedComboBoxes.containsKey(sourceNode);
	}

	/**
	 * @param visualDocument
	 * @param sourceElement
	 * @param creationData
	 * @return
	 */
	private nsIDOMElement createPopupCalendar(nsIDOMDocument visualDocument,
			Element sourceElement, VpeCreationData creationData) {

		nsIDOMElement popupCalendar = visualDocument
				.createElement(HTML.TAG_SPAN);

		if (showInput) {

			nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);

			input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
			input.setAttribute(HTML.ATTR_STYLE, inputStyle);
			input.setAttribute(HTML.ATTR_CLASS, inputClass);
			input.setAttribute(HTML.ATTR_SIZE, inputSize);
			input.setAttribute(HTML.ATTR_VALUE, value);

			popupCalendar.appendChild(input);

			VpeElementData elementData = new VpeElementData();
			if (sourceElement.hasAttribute(RichFaces.ATTR_VALUE)) {

				Attr attr = sourceElement
						.getAttributeNode(RichFaces.ATTR_VALUE);
				elementData.addNodeData(new AttributeData(attr, input, true));

			} else {

				elementData.addNodeData(new AttributeData(RichFaces.ATTR_VALUE,
						input, true));

			}

			creationData.setElementData(elementData);

		}

		nsIDOMElement button;

		if ((buttonLabel == null) || (buttonLabel.length() == 0)) {

			button = visualDocument.createElement(HTML.TAG_IMG);
			button.setAttribute(HTML.ATTR_SRC, buttonIcon);

		} else {
			button = visualDocument.createElement(HTML.TAG_BUTTON);
			button.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_BUTTON);

			nsIDOMNode buttonText = visualDocument.createTextNode(buttonLabel);
			button.appendChild(buttonText);

		}

		button.setAttribute(HTML.ATTR_STYLE, DEFAULT_BUTTON_STYLE
				+ ";position:relative;");
		button.setAttribute(HTML.ATTR_CLASS, CSS_R_C_BUTTON
				+ Constants.WHITE_SPACE + buttonClass);

		button.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, "true");

		popupCalendar.appendChild(button);

		if (isExpanded(sourceElement)) {
			nsIDOMElement wrapper = visualDocument
					.createElement(HTML.ATTR_SPAN);
			wrapper.setAttribute(HTML.ATTR_STYLE, "position: relative;");

			nsIDOMElement calendar = createCalendar(visualDocument,
					creationData, sourceElement);

			int top = (jointPoint.isTop() ? JOINT_POINT_TOP
					: JOINT_POINT_BOTTOM)
					+ ((direction.isTop() ? -1 : 1) * ((direction.isTop() ? tableHeight
							: 0) + verticalOffset));
			int left = (direction.isLeft() ? -1 : 1)
					* ((direction.isLeft() ? tableWidth : 0) + horizontalOffset);

			calendar.setAttribute(HTML.ATTR_STYLE,
					"position: absolute; z-index:" + zindex
							+ Constants.SEMICOLON + "top:" + top
							+ Constants.PIXEL + Constants.SEMICOLON + " left:"
							+ left + Constants.PIXEL + Constants.SEMICOLON
							+ " width:" + tableWidth + Constants.PIXEL
							+ Constants.SEMICOLON
							+ calendar.getAttribute(HTML.ATTR_STYLE));

			wrapper.appendChild(calendar);

			if (jointPoint.isLeft()) {
				popupCalendar.insertBefore(wrapper, popupCalendar
						.getFirstChild());

			} else {
				popupCalendar.appendChild(wrapper);
			}
		}

		return popupCalendar;
	}

	@Override
	public void setPseudoContent(VpePageContext pageContext,
			Node sourceContainer, nsIDOMNode visualContainer,
			nsIDOMDocument visualDocument) {
	}

	/**
	 * 
	 * @param sourceElement
	 * @return
	 */
	private Locale getLocale(Element sourceElement) {

		Locale locale;

		if (sourceElement.hasAttribute(RichFaces.ATTR_LOCALE)) {

			String localeAttr = sourceElement
					.getAttribute(RichFaces.ATTR_LOCALE);

			String[] localeInformation = localeAttr.split(Constants.UNDERSCORE);

			String language = localeInformation[0];
			String country = localeInformation.length > 1 ? localeInformation[1]
					: Constants.EMPTY;

			locale = new Locale(language, country);

		} else {

			locale = Locale.getDefault();
		}

		return locale;

	}

	/**
	 * @param sourceElement
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	private int getFirstWeekDay(Element sourceElement, String attributeName,
			int defaultValue) {

		// if source element has attribute
		if (sourceElement.hasAttribute(attributeName)) {
			// getAttribute
			String stringValue = sourceElement.getAttribute(attributeName);

			try {
				// decode attribute's value
				int intValue = Integer.decode(stringValue);

				// richfaces Calendar counts weekdays from 0 but
				// java.util.Calendar counts weekdays from 1
				return intValue < 0 ? NUM_DAYS_IN_WEEK + intValue
						% NUM_DAYS_IN_WEEK : intValue % NUM_DAYS_IN_WEEK;
			} catch (NumberFormatException e) {
				// if attribute's value is not number do nothing and then return
				// default value
			}

		}

		return defaultValue;

	}

	private int parseSizeAttribute(Element sourceElement, String attributeName,
			int defaultValue) {

		if (sourceElement.hasAttribute(attributeName)) {
			String attrValue = sourceElement.getAttribute(attributeName);

			if (attrValue.endsWith(Constants.PIXEL))
				attrValue = attrValue.substring(0, attrValue.length()
						- Constants.PIXEL.length());

			try {
				// decode attribute's value
				int intValue = Integer.decode(attrValue);

				// richfaces Calendar counts weekdays from 0 but
				// java.util.Calendar counts weekdays from 1
				return intValue;
			} catch (NumberFormatException e) {
				// if attribute's value is not number do nothing and then return
				// default value
			}
		}

		return defaultValue;
	}

	private int parseNumberAttribute(Element sourceElement,
			String attributeName, int defaultValue) {

		if (sourceElement.hasAttribute(attributeName)) {
			String attrValue = sourceElement.getAttribute(attributeName);

			try {
				// decode attribute's value
				int intValue = Integer.decode(attrValue);

				// richfaces Calendar counts weekdays from 0 but
				// java.util.Calendar counts weekdays from 1
				return intValue;
			} catch (NumberFormatException e) {
				// if attribute's value is not number do nothing and then return
				// default value
			}
		}

		return defaultValue;
	}

	/**
	 * @param sourceElement
	 * @param locale
	 * @return
	 */
	private String[] getWeekDays(Element sourceElement, Locale locale) {
		DateFormatSymbols formatSymbols = new DateFormatSymbols(locale);

		String[] days = new String[NUM_DAYS_IN_WEEK];

		System.arraycopy(formatSymbols.getShortWeekdays(), 1, days, 0,
				NUM_DAYS_IN_WEEK);

		return days;
	}

	/**
	 * @param locale
	 * @return
	 */
	private String[] getMonths(Element sourceElement, Locale locale) {

		DateFormatSymbols formatSymbols = new DateFormatSymbols(locale);
		return formatSymbols.getMonths();
	}

	/**
	 * 
	 * @param visualDocument
	 * @param style
	 * @param arrayContent
	 * @param arrayContentStyles
	 * @return
	 */
	private nsIDOMElement createHeaderBlock(nsIDOMDocument visualDocument,
			String blockClass, String[] arrayContent,
			String[] arrayContentStyles) {

		nsIDOMElement blockTr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement blockTd = visualDocument.createElement(HTML.TAG_TD);
		blockTd.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(COLUMN));
		blockTd.setAttribute(HTML.ATTR_CLASS, blockClass);

		nsIDOMElement blockTable = visualDocument.createElement(HTML.TAG_TABLE);

		blockTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		// blockTable.setAttribute(HTML.ATTR_STYLE, "display:inline-block;");

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

		for (int i = 0; i < arrayContent.length; i++) {
			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

			td.setAttribute(HTML.ATTR_CLASS, arrayContentStyles[i]);
			nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
			div.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOL_BTN);

			nsIDOMText text1 = visualDocument.createTextNode(arrayContent[i]);

			div.appendChild(text1);
			div.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, "true");
			td.appendChild(div);
			tr.appendChild(td);
		}

		tbody.appendChild(tr);
		blockTable.appendChild(tbody);
		blockTd.appendChild(blockTable);
		blockTr.appendChild(blockTd);
		return blockTr;

	}

	/**
	 * 
	 * @param visualDocument
	 * @param style
	 * @param arrayContent
	 * @param arrayContentStyles
	 * @return
	 */
	private nsIDOMElement createFooterBlock(nsIDOMDocument visualDocument,
			String blockClass, String[] arrayContent,
			String[] arrayContentStyles) {

		nsIDOMElement blockTr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement blockTd = visualDocument.createElement(HTML.TAG_TD);
		blockTd.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(COLUMN));
		blockTd.setAttribute(HTML.ATTR_CLASS, blockClass);

		nsIDOMElement blockTable = visualDocument.createElement(HTML.TAG_TABLE);
		blockTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		// blockTable.setAttribute(HTML.ATTR_STYLE, "display:inline;");
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

		nsIDOMElement fillingTd = visualDocument.createElement(HTML.TAG_TD);
		fillingTd.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		tr.appendChild(fillingTd);

		for (int i = 0; i < arrayContent.length; i++) {
			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
			td.setAttribute(HTML.ATTR_CLASS, arrayContentStyles[i]);
			nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
			div.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOL_BTN);

			nsIDOMText text1 = visualDocument.createTextNode(arrayContent[i]);

			div.appendChild(text1);
			td.appendChild(div);
			tr.appendChild(td);
		}

		tbody.appendChild(tr);
		blockTable.appendChild(tbody);
		blockTd.appendChild(blockTable);
		blockTr.appendChild(blockTd);
		return blockTr;

	}

	private DirectionData getDirection(Element sourceElement,
			String attributeName, String defaultDirection) {

		String value = defaultDirection;
		DirectionData directionData = new DirectionData();

		if (sourceElement.hasAttribute(attributeName)) {

			String attributeValue = sourceElement.getAttribute(attributeName)
					.toLowerCase();

			Matcher matcher = Pattern.compile(DIRECTION_PATTERN).matcher(value);

			if (matcher.find()) {
				value = attributeValue;
			}

		}

		directionData.setTop(value.startsWith(TOP));
		directionData.setLeft(value.endsWith(LEFT));

		return directionData;

	}

	class DirectionData {

		private boolean top;
		private boolean left;

		public boolean isTop() {
			return top;
		}

		public void setTop(boolean top) {
			this.top = top;
		}

		public boolean isLeft() {
			return left;
		}

		public void setLeft(boolean left) {
			this.left = left;
		}

	}

}
