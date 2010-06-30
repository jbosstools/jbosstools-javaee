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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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

	private static final WeakHashMap<Node, Object> expandedCalendars = new WeakHashMap<Node, Object>();

	final static int COLUMN = 8;
	final static String FILL_WIDTH = "100%"; //$NON-NLS-1$
	final static int NUM_DAYS_IN_WEEK = 7;
	final static int NUM_WEEK_ON_PAGE = 6;
	final static int NUM_MONTHS = 12;

	final static int CALENDAR_WIDTH = 200;
	final static int CALENDAR_INPUT_WIDTH = CALENDAR_WIDTH - 20;
	final static int CALENDAR_IMAGE_WIDTH = 20;
	final static int CALENDAR_CUSTOM_IMAGE_WIDTH = 40;
	final static int CALENDAR_BUTTON_WIDTH = 80;

	static final String DEFAULT_DATE_PATTERN = "MMM dd,yyyy"; //$NON-NLS-1$

	/* rich:calendar attributes */

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

	final static String DIRECTION_PATTERN = "(top|bottom)-(left|right)"; //$NON-NLS-1$
	final static String UNDEFINED = "undefined"; //$NON-NLS-1$
	final static int DEFAULT_CELL_WIDTH = 25;
	final static int DEFAULT_CELL_HEIGHT = 22;
	final static int DEFAULT_OPTIONAL_CELL_HEIGHT = 26;

	final static int JOINT_POINT_BOTTOM = 5;
	final static int JOINT_POINT_TOP = -17;
	final static String TOP = "top"; //$NON-NLS-1$
	final static String LEFT = "left"; //$NON-NLS-1$
	final static String HIDDEN = "hidden"; //$NON-NLS-1$

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
	static final String ATTR_WEEK_DAY_LABELS_SHORT = "weekDayLabelsShort"; //$NON-NLS-1$
	static final String ATTR_MONTH_LABELS = "monthLabels"; //$NON-NLS-1$
	static final String ATTR_ENABLE_MANUAL_INPUT = "enableManualInput"; //$NON-NLS-1$
	static final String ATTR_TODAY_CONTROL_MODE = "todayControlMode"; //$NON-NLS-1$

	static final String NAME_FACET_OPTIONAL_FOOTER = "optionalFooter"; //$NON-NLS-1$
	static final String NAME_FACET_OPTIONAL_HEADER = "optionalHeader"; //$NON-NLS-1$

	static final String NAME_FACET_WEEK_DAY = "weekDay"; //$NON-NLS-1$
	static final String NAME_FACET_WEEK_NUMBER = "weekNumber"; //$NON-NLS-1$

	static final boolean FACET_SEARCH_ORDER = true; // it means that facet will
	// be look for from end

	final private static String DEFAULT_INPUT_STYLE = "vertical-align: middle;";//$NON-NLS-1$
	final private static String POSITION_RELATIVE_STYLE = "position: relative;";//$NON-NLS-1$
	final private static String POSITION_ABSOLUTE_STYLE = "position: absolute;";//$NON-NLS-1$
	final private static String STYLE_PATH = "calendar/calendar.css"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_ICON = "calendar/calendar.gif"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_ICON_DISABLED = "calendar/disabled_button.gif"; //$NON-NLS-1$
	final private static String SPARATOR_IMG = "calendar/separator.gif"; //$NON-NLS-1$
	final private static String DEFAULT_BUTTON_STYLE = "vertical-align: middle;";//$NON-NLS-1$

	final private static String NEXT_MONTH_CONTROL = ">";//$NON-NLS-1$
	final private static String PREVIOUS_MONTH_CONTROL = "<";//$NON-NLS-1$
	final private static String NEXT_YEAR_CONTROL = ">>";//$NON-NLS-1$
	final private static String PREVIOUS_YEAR_CONTROL = "<<";//$NON-NLS-1$
	final private static String APPLY_CONTROL = "Apply"; //$NON-NLS-1$
	final private static String TODAY_CONTROL = "Today"; //$NON-NLS-1$
	final private static String CLOSE_CONTROL = "X"; //$NON-NLS-1$

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

		VpeCreationData creationData = new VpeCreationData(wrapper, true);

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
	public boolean recreateAtAttrChange(VpePageContext pageContext,
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

		nsIDOMElement optionalHeader = null;
		nsIDOMElement header = null;
		nsIDOMElement calendarBody = createCalendarBody(visualDocument,
				creationData, sourceElement);
		nsIDOMElement footer = null;
		nsIDOMElement optionalFooter = null;

		Element optionalHeaderFacet = ComponentUtil.getFacetElement(
				sourceElement, NAME_FACET_OPTIONAL_HEADER, FACET_SEARCH_ORDER);

		if (optionalHeaderFacet != null) {
			optionalHeader = createCustomBlock(visualDocument,
					optionalHeaderFacet, creationData, CSS_R_C_HEADER_OPTIONAL);
			tableHeight += DEFAULT_OPTIONAL_CELL_HEIGHT;
		}

		if (showHeader) {
			Element headerFacet = ComponentUtil.getFacetElement(sourceElement,
					RichFaces.NAME_FACET_HEADER, FACET_SEARCH_ORDER);
			if (headerFacet != null) {
				header = createCustomBlock(visualDocument, headerFacet,
						creationData, CSS_R_C_HEADER);
			} else {

				List<Cell> headerContent = new ArrayList<Cell>();
				headerContent
						.add(new Cell(PREVIOUS_YEAR_CONTROL, CSS_R_C_TOOL));
				headerContent
						.add(new Cell(PREVIOUS_MONTH_CONTROL, CSS_R_C_TOOL));
				headerContent.add(new Cell(currentMonthControl, CSS_R_C_MONTH));
				headerContent.add(new Cell(NEXT_MONTH_CONTROL, CSS_R_C_TOOL));
				headerContent.add(new Cell(NEXT_YEAR_CONTROL, CSS_R_C_TOOL));
				if (popup)
					headerContent.add(new Cell(CLOSE_CONTROL, CSS_R_C_TOOL
							+ Constants.WHITE_SPACE + CSS_R_C_TOOL_CLOSE, true,
							true));

				header = createHeaderBlock(visualDocument, CSS_R_C_HEADER,
						headerContent);
			}
		}
		if (showFooter) {
			Element footerFacet = ComponentUtil.getFacetElement(sourceElement,
					RichFaces.NAME_FACET_FOOTER, FACET_SEARCH_ORDER);
			if (footerFacet != null) {

				footer = createCustomBlock(visualDocument, footerFacet,
						creationData, CSS_R_C_FOOTER);
			} else {

				List<Cell> footerContent = new ArrayList<Cell>();
				if (showTodayControl)
					footerContent.add(new Cell(TODAY_CONTROL,
							CSS_R_C_TOOLFOOTER, showTodayControl
									&& showApplyButton));
				if (popup && showApplyButton)
					footerContent.add(new Cell(APPLY_CONTROL,
							CSS_R_C_TOOLFOOTER, false, true));

				footer = createFooterBlock(visualDocument, CSS_R_C_FOOTER,
						footerContent);
			}
		}

		Element optionalFooterFacet = ComponentUtil.getFacetElement(
				sourceElement, NAME_FACET_OPTIONAL_FOOTER, FACET_SEARCH_ORDER);

		if (optionalFooterFacet != null) {
			optionalFooter = createCustomBlock(visualDocument,
					optionalFooterFacet, creationData, CSS_R_C_FOOTER_OPTIONAL);
			tableHeight += DEFAULT_OPTIONAL_CELL_HEIGHT;
		}

		if (optionalHeader != null) {
			tbody.appendChild(optionalHeader);
		}

		if (null != header) {
			tbody.appendChild(header);
		}
		tbody.appendChild(calendarBody);
		if (null != footer) {
			tbody.appendChild(footer);
		}

		if (optionalFooter != null) {
			tbody.appendChild(optionalFooter);
		}

		table.appendChild(tbody);

		return table;
	}

	/**
	 * 
	 * @param visualDocument
	 * @return Node of the visual tree.
	 */
	private nsIDOMElement createCalendarBody(nsIDOMDocument visualDocument,
			VpeCreationData creationData, Element sourceElement) {

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement weekDaysTR = visualDocument.createElement(HTML.TAG_TR);

		// Create week days row
		if (showWeekDaysBar) {

			Element weekDayFacet = ComponentUtil.getFacetElement(sourceElement,
					NAME_FACET_WEEK_DAY, FACET_SEARCH_ORDER);

			for (int i = 0; i < COLUMN; i++) {
				nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
				if ((i == 0) && (showWeeksBar)) {
					td.setAttribute(HTML.ATTR_CLASS, WEEK_DAY_HTML_CLASS_ATTR);
//					nsIDOMElement br = visualDocument
//							.createElement(HTML.TAG_BR);
//					td.appendChild(br);
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

					if (weekDayFacet != null) {

						VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
						childrenInfo.addSourceChild(weekDayFacet);
						creationData.addChildrenInfo(childrenInfo);

					} else {
						nsIDOMNode weekDayNode = visualDocument
								.createTextNode(i == 0 ? Constants.EMPTY
										: weekDays[dayIndex]);

						td.appendChild(weekDayNode);
					}

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

		Element weekNumberFacet = ComponentUtil.getFacetElement(sourceElement,
				NAME_FACET_WEEK_NUMBER, FACET_SEARCH_ORDER);

		// for number of week
		for (int i = NUM_WEEK_ON_PAGE; i > 0; i--) {

			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

			if (showWeeksBar) {
				// Week in year
				nsIDOMElement weekTD = visualDocument
						.createElement(HTML.TAG_TD);
				weekTD.setAttribute(HTML.ATTR_CLASS, CSS_R_C_WEEK);

				if (weekNumberFacet != null) {

					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(weekTD);
					childrenInfo.addSourceChild(weekNumberFacet);
					creationData.addChildrenInfo(childrenInfo);

				} else {
					nsIDOMText weekText = visualDocument.createTextNode(String
							.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
					weekTD.appendChild(weekText);
				}

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
			nsIDOMElement element = queryInterface(visualNode, nsIDOMElement.class);
			nsIDOMNodeList list = element.getChildNodes();
			nsIDOMNode tableNode = list.item(0);
			nsIDOMElement input = queryInterface(tableNode, nsIDOMElement.class);
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

		String inputClassAttrVal = sourceElement.hasAttribute(RichFaces.ATTR_INPUT_CLASS) ? sourceElement.getAttribute(RichFaces.ATTR_INPUT_CLASS) : null;
		// inputClass
		inputClass = CSS_R_C_INPUT + Constants.WHITE_SPACE + inputClassAttrVal;

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
				buttonIcon = Constants.FILE_PREFIX
						+ ComponentUtil
								.getAbsoluteResourcePath(DEFAULT_BUTTON_ICON_DISABLED);
			}
		} else {

			if (sourceElement.hasAttribute(RichFaces.ATTR_BUTTON_ICON))
				buttonIcon = ComponentUtil.getAbsoluteWorkspacePath(
						sourceElement.getAttribute(RichFaces.ATTR_BUTTON_ICON),
						pageContext);
			else {
				buttonIcon = Constants.FILE_PREFIX
						+ ComponentUtil
								.getAbsoluteResourcePath(DEFAULT_BUTTON_ICON);
			}
		}
		buttonIcon = buttonIcon.replace('\\', '/');

		// buttonClass
		buttonClass = sourceElement.hasAttribute(RichFaces.ATTR_BUTTON_CLASS) ? sourceElement.getAttribute(RichFaces.ATTR_BUTTON_CLASS) : null;

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
		cellWidth = ComponentUtil.parseSizeAttribute(sourceElement,
				ATTR_CELL_WIDTH, DEFAULT_CELL_WIDTH);

		// cellHeight
		cellHeight = ComponentUtil.parseSizeAttribute(sourceElement,
				ATTR_CELL_HEIGHT, DEFAULT_CELL_HEIGHT);

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

		// zindex
		zindex = ComponentUtil.parseNumberAttribute(sourceElement,
				RichFaces.ATTR_ZINDEX, 3);

		// horizontalOffset
		horizontalOffset = ComponentUtil.parseNumberAttribute(sourceElement,
				RichFaces.ATTR_HORIZONTAL_OFFSET, 0);

		// verticalOffset
		verticalOffset = ComponentUtil.parseNumberAttribute(sourceElement,
				RichFaces.ATTR_VERTICAL_OFFSET, 0);

	}

	public void stopToggling(Node sourceNode) {
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
		if (isExpanded(sourceNode)) {
			expandedCalendars.remove(sourceNode);
		} else {
			expandedCalendars.put(sourceNode, null);
		}
	}

	private boolean isExpanded(Node sourceNode) {
		return expandedCalendars.containsKey(sourceNode);
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

		button.setAttribute(HTML.ATTR_STYLE, DEFAULT_BUTTON_STYLE);
		button.setAttribute(HTML.ATTR_CLASS, CSS_R_C_BUTTON
				+ Constants.WHITE_SPACE + buttonClass);

		button.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID,
				Constants.TRUE);

		popupCalendar.appendChild(button);

		if (isExpanded(sourceElement)) {
			nsIDOMElement wrapper = visualDocument
					.createElement(HTML.ATTR_SPAN);
			wrapper.setAttribute(HTML.ATTR_STYLE, POSITION_RELATIVE_STYLE);

			nsIDOMElement calendar = createCalendar(visualDocument,
					creationData, sourceElement);

			int top = (jointPoint.isTop() ? JOINT_POINT_TOP
					: JOINT_POINT_BOTTOM)
					+ ((direction.isTop() ? -1 : 1) * ((direction.isTop() ? tableHeight
							: 0) + verticalOffset));
			int left = (direction.isLeft() ? -1 : 1)
					* ((direction.isLeft() ? tableWidth : 0) + horizontalOffset);

			calendar.setAttribute(HTML.ATTR_STYLE, POSITION_ABSOLUTE_STYLE
					+ HTML.STYLE_PARAMETER_ZINDEX + Constants.COLON + zindex
					+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_TOP
					+ Constants.COLON + top + Constants.PIXEL
					+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_LEFT
					+ Constants.COLON + left + Constants.PIXEL
					+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_WIDTH
					+ Constants.COLON + tableWidth + Constants.PIXEL
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

	/**
	 * @param sourceElement
	 * @param locale
	 * @return
	 */
	private String[] getWeekDays(Element sourceElement, Locale locale) {

		String[] days = new String[NUM_DAYS_IN_WEEK];
		if (sourceElement.hasAttribute(ATTR_WEEK_DAY_LABELS_SHORT)) {

			String attrValue = sourceElement
					.getAttribute(ATTR_WEEK_DAY_LABELS_SHORT);
			String[] parsedDays = attrValue.split(Constants.COMMA);

			System.arraycopy(parsedDays, 0, days, 0, parsedDays.length);
			if (parsedDays.length < NUM_DAYS_IN_WEEK) {

				Arrays.fill(days, parsedDays.length, days.length - 1,
						Constants.EMPTY);
			}

		} else {

			DateFormatSymbols formatSymbols = new DateFormatSymbols(locale);
			System.arraycopy(formatSymbols.getShortWeekdays(), 1, days, 0,
					NUM_DAYS_IN_WEEK);
		}
		return days;
	}

	/**
	 * @param locale
	 * @return
	 */
	private String[] getMonths(Element sourceElement, Locale locale) {
		String[] months;
		if (sourceElement.hasAttribute(ATTR_MONTH_LABELS)) {

			months = new String[NUM_MONTHS];

			String attrValue = sourceElement.getAttribute(ATTR_MONTH_LABELS);
			String[] parsedMonths = attrValue.split(Constants.COMMA);

			System.arraycopy(parsedMonths, 0, months, 0, parsedMonths.length);

			if (parsedMonths.length < NUM_MONTHS) {

				Arrays.fill(months, parsedMonths.length, months.length - 1,
						UNDEFINED);
			}

		} else {
			DateFormatSymbols formatSymbols = new DateFormatSymbols(locale);
			months = formatSymbols.getMonths();
		}
		return months;
	}

	/**
	 * 
	 * @param visualDocument
	 * @param customChild
	 * @param creationData
	 * @param blockClass
	 * @param content
	 * @return
	 */
	private nsIDOMElement createCustomBlock(nsIDOMDocument visualDocument,
			Node customChild, VpeCreationData creationData, String blockClass) {

		nsIDOMElement blockTr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement blockTd = visualDocument.createElement(HTML.TAG_TD);
		blockTd.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(COLUMN));
		blockTd.setAttribute(HTML.ATTR_CLASS, blockClass);
		blockTr.appendChild(blockTd);

		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(blockTd);
		childrenInfo.addSourceChild(customChild);

		creationData.addChildrenInfo(childrenInfo);

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
	private nsIDOMElement createHeaderBlock(nsIDOMDocument visualDocument,
			String blockClass, List<Cell> content) {

		nsIDOMElement blockTr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement blockTd = visualDocument.createElement(HTML.TAG_TD);
		blockTd.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(COLUMN));
		blockTd.setAttribute(HTML.ATTR_CLASS, blockClass);

		nsIDOMElement blockTable = visualDocument.createElement(HTML.TAG_TABLE);

		blockTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);

		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

		for (Cell cell : content) {

			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

			td.setAttribute(HTML.ATTR_CLASS, cell.getCellClass());
			nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
			div.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOL_BTN);

			nsIDOMText text1 = visualDocument
					.createTextNode(cell.getCellText());

			div.appendChild(text1);

			if (cell.isToggle()) {
				div.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID,
						Constants.TRUE);
			}

			if (cell.isSeparate()) {

				td.setAttribute(HTML.ATTR_STYLE, ComponentUtil
						.getBackgoundImgStyle(SPARATOR_IMG));

			}

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
			String blockClass, List<Cell> content) {

		nsIDOMElement blockTr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement blockTd = visualDocument.createElement(HTML.TAG_TD);
		blockTd.setAttribute(HTML.ATTR_COLSPAN, String.valueOf(COLUMN));
		blockTd.setAttribute(HTML.ATTR_CLASS, blockClass);

		nsIDOMElement blockTable = visualDocument.createElement(HTML.TAG_TABLE);
		blockTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		blockTable.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

		nsIDOMElement fillingTd = visualDocument.createElement(HTML.TAG_TD);
		fillingTd.setAttribute(HTML.ATTR_WIDTH, FILL_WIDTH);
		tr.appendChild(fillingTd);

		for (Cell cell : content) {

			nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
			td.setAttribute(HTML.ATTR_CLASS, cell.getCellClass());
			nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
			div.setAttribute(HTML.ATTR_CLASS, CSS_R_C_TOOL_BTN);

			nsIDOMText text = visualDocument.createTextNode(cell.getCellText());

			if (cell.isToggle()) {
				div.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID,
						Constants.TRUE);
			}

			if (cell.isSeparate()) {

				td.setAttribute(HTML.ATTR_STYLE, ComponentUtil
						.getBackgoundImgStyle(SPARATOR_IMG));

			}

			div.appendChild(text);
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

	class Cell {

		private String cellText;

		private String cellClass;

		private boolean separate;

		private boolean toggle;

		public boolean isToggle() {
			return toggle;
		}

		public void setToggle(boolean toggle) {
			this.toggle = toggle;
		}

		public Cell(String cellText, String cellClass) {
			this.cellText = cellText;
			this.cellClass = cellClass;
			this.separate = false;
			this.toggle = false;
		}

		public Cell(String cellText, String cellClass, boolean separate) {
			this.cellText = cellText;
			this.cellClass = cellClass;
			this.separate = separate;
			this.toggle = false;
		}

		public Cell(String cellText, String cellClass, boolean separate,
				boolean toggle) {
			this.cellText = cellText;
			this.cellClass = cellClass;
			this.separate = separate;
			this.toggle = toggle;
		}

		public String getCellText() {
			return cellText;
		}

		public void setCellText(String cellText) {
			this.cellText = cellText;
		}

		public String getCellClass() {
			return cellClass;
		}

		public void setCellClass(String cellClass) {
			this.cellClass = cellClass;
		}

		public boolean isSeparate() {
			return separate;
		}

		public void setSeparate(boolean separate) {
			this.separate = separate;
		}

	}

}
