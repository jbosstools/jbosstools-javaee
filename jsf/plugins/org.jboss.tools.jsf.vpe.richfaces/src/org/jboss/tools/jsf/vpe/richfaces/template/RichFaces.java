/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilder;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilderException;
import org.jboss.tools.vpe.editor.util.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * contain rich faces tags and general attributes.
 * 
 * @author Sergey Dzmitrovich
 */
public class RichFaces {
	/**
	 * The Constructor.
	 */
	private RichFaces() {
	}

	public static final String ATTR_ADD_CONTROL_LABEL = "addControlLabel"; //$NON-NLS-1$
	public static final String ATTR_ALIGN = "align"; //$NON-NLS-1$
	public static final String ATTR_BREAK_BEFORE = "breakBefore"; //$NON-NLS-1$
	public static final String ATTR_BREAK_ROW_BEFORE = "breakRowBefore"; // RichFaces 4.0 attribute //$NON-NLS-1$
	public static final String ATTR_CAPTION_CLASS = "captionClass"; //$NON-NLS-1$
	public static final String ATTR_CAPTION_STYLE = "captionStyle"; //$NON-NLS-1$
	public static final String ATTR_COLLAPSE_ICON = "collapseIcon"; //$NON-NLS-1$
	public static final String ATTR_COLLAPSE_LABEL = "collapseLabel"; //$NON-NLS-1$
	public static final String ATTR_COLUMN_CLASSES = "columnClasses"; //$NON-NLS-1$
	public static final String ATTR_COLUMNS = "columns"; //$NON-NLS-1$
	public static final String ATTR_COLUMNS_WIDTH = "columnsWidth"; //$NON-NLS-1$
	public static final String ATTR_BUTTON_ICON = "buttonIcon"; //$NON-NLS-1$
	public static final String ATTR_BUTTON_ICON_DISABLED = "buttonIconDisabled"; //$NON-NLS-1$
	public static final String ATTR_BUTTON_CLASS = "buttonClass"; //$NON-NLS-1$
	public static final String ATTR_SHOW_INPUT = "showInput"; //$NON-NLS-1$
	public static final String ATTR_LOCALE = "locale"; //$NON-NLS-1$
	public static final String ATTR_CONTROLS_TYPE = "controlsType"; //$NON-NLS-1$
	public static final String ATTR_DEFAULT_LABEL = "defaultLabel"; //$NON-NLS-1$
	public static final String ATTR_LABEL = "label"; //$NON-NLS-1$
	public static final String ATTR_DIRECTION = "direction"; //$NON-NLS-1$
	public static final String ATTR_JOINT_POINT = "jointPoint"; //$NON-NLS-1$
	public static final String ATTR_DISABLED = "disabled";//$NON-NLS-1$
	public static final String ATTR_ELEMENTS = "elements"; //$NON-NLS-1$
	public static final String ATTR_HEADER_CLASS = "headerClass"; //$NON-NLS-1$
	public static final String ATTR_BODY_CLASS = "bodyClass"; //$NON-NLS-1$
	public static final String ATTR_FOOTER_CLASS = "footerClass"; //$NON-NLS-1$
	public static final String ATTR_INPUT_CLASS = "inputClass"; //$NON-NLS-1$
	public static final String ATTR_INPUT_SIZE = "inputSize"; //$NON-NLS-1$
	public static final String ATTR_INPUT_STYLE = "inputStyle"; //$NON-NLS-1$
	public static final String ATTR_LIST_HEIGHT = "listHeight"; //$NON-NLS-1$
	public static final String ATTR_LIST_WIDTH = "listWidth"; //$NON-NLS-1$
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_POPUP = "popup";//$NON-NLS-1$
	public static final String ATTR_ROWS = "rows"; //$NON-NLS-1$
	public static final String ATTR_ROW_CLASS = "rowClass"; //$NON-NLS-1$
	public static final String ATTR_ROW_CLASSES = "rowClasses"; //$NON-NLS-1$
	public static final String ATTR_SELECT_ITEM_LABEL = "itemLabel"; //$NON-NLS-1$
	public static final String ATTR_SELECT_ITEM_VALUE = "itemValue"; //$NON-NLS-1$showButton
	public static final String ATTR_SHOW_BUTTON = "showButton"; //$NON-NLS-1$
	public static final String ATTR_SHOW_BUTTON_LABELS = "showButtonLabels"; //$NON-NLS-1$
	public static final String ATTR_SORT_BY = "sortBy"; //$NON-NLS-1$
	public static final String ATTR_SORT_ICON = "sortIcon"; //$NON-NLS-1$
	public static final String ATTR_SORTABLE = "sortable"; //$NON-NLS-1$
	public static final String ATTR_STYLE = "style"; //$NON-NLS-1$
	public static final String ATTR_STYLE_CLASS = "styleClass"; //$NON-NLS-1$
	public static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	public static final String ATTR_VALUE = "value"; //$NON-NLS-1$
	public static final String ATTR_VISIBLE = "visible"; //$NON-NLS-1$
	public static final String ATTR_WIDTH = "width"; //$NON-NLS-1$
	public static final String ATTR_HEIGHT = "height"; //$NON-NLS-1$
	public static final String ATTR_HORIZONTAL_OFFSET = "horizontalOffset"; //$NON-NLS-1$
	public static final String ATTR_VERTICAL_OFFSET = "verticalOffset"; //$NON-NLS-1$
	public static final String ATTR_ZINDEX = "zindex"; //$NON-NLS-1$
	public static final String ATTR_POSITION = "position"; //$NON-NLS-1$
	
	/** FACETS NAMES **/
	public static final String NAME_FACET_LABEL = "label"; //$NON-NLS-1$
	public static final String NAME_FACET_CAPTION = "caption"; //$NON-NLS-1$
	public static final String NAME_FACET_FOOTER = "footer"; //$NON-NLS-1$
	public static final String NAME_FACET_HEADER = "header"; //$NON-NLS-1$
	public static final String NAME_FACET_TERM = "term"; //$NON-NLS-1$
	public static final String NAME_FACET_CONTROLS = "controls"; //$NON-NLS-1$
	
	/** jsf tags which are used with richFaces. */
	public static final String TAG_COLUMN = "column"; //$NON-NLS-1$
	public static final String TAG_COLUMN_GROUP = "columnGroup"; //$NON-NLS-1$
	public static final String TAG_COLUMNS = "columns"; //$NON-NLS-1$
	public static final String TAG_FACET = "facet"; //$NON-NLS-1$
	public static final String TAG_SUB_TABLE = "subTable"; //$NON-NLS-1$
	
	public static final String VALUE_TRUE = "true"; //$NON-NLS-1$
	public static final String VALUE_FALSE = "false"; //$NON-NLS-1$
	public static final String VALUE_LEFT = "left"; //$NON-NLS-1$
	public static final String VALUE_RIGHT = "right"; //$NON-NLS-1$
	public static final String VALUE_TOP = "top"; //$NON-NLS-1$
	public static final String VALUE_BOTTOM = "bottom"; //$NON-NLS-1$
	public static final String VALUE_CENTER = "center"; //$NON-NLS-1$
	
	public static final String COLLAPSED_STATE = "collapsedState";
	
	private static VpeExpression exprColumnClasses = null;
	/**
	 * Returns the expression to extract style-classes from a {@code 'columnClasses'} attribute. 
	 */
	public static VpeExpression getExprColumnClasses() {
		if (exprColumnClasses == null) {
			try {
				exprColumnClasses = VpeExpressionBuilder
					.buildCompletedExpression("{@" + ATTR_COLUMN_CLASSES + "}", true) //$NON-NLS-1$ //$NON-NLS-2$
					.getExpression();
			} catch (VpeExpressionBuilderException e) {
				throw new RuntimeException(e);
			}
		}
		
		return exprColumnClasses;
	}

	private static VpeExpression exprRowClasses = null;
	/**
	 * Returns the expression to extract style-classes from a {@code 'rowClasses'} attribute. 
	 */
	public static VpeExpression getExprRowClasses() {
		if (exprRowClasses == null) {
			try {
				exprRowClasses = VpeExpressionBuilder
					.buildCompletedExpression("{@" + ATTR_ROW_CLASSES + "}", true) //$NON-NLS-1$ //$NON-NLS-2$
					.getExpression();
			} catch (VpeExpressionBuilderException e) {
				throw new RuntimeException(e);
			}
		}
		
		return exprRowClasses;
	}
	
	public static List<Element> findElementsById(Element root, String id, String tagName) {
		ArrayList<Element> list = new ArrayList<Element>();
		NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				if (childElement.getNodeName().endsWith(tagName)
						&& id.equals(childElement.getAttribute("id"))) { //$NON-NLS-1$
					list.add(childElement);
				}
				list.addAll(findElementsById(childElement, id, tagName));
			}
		}
		return list;
	}

	/**
	 * Reads COLLAPSED_STATE attribute's value from the source node
	 * 
	 * @param sourceNode the verifiable source node
	 * @return true, if node is collapsed
	 */
	public static boolean readCollapsedStateFromSourceNode(Node sourceNode) {
		boolean isCollapsed = false;
		String collapsedState = (String) sourceNode.getUserData(COLLAPSED_STATE);
		if ((collapsedState != null) && ("true".equalsIgnoreCase(collapsedState))){ //$NON-NLS-1$
			isCollapsed = true;
		}
		return isCollapsed;
	}
	
	public static ArrayList<Element> getColumns(Node parentSourceElement) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList children = parentSourceElement.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			if((child instanceof Element) && (nodeName.endsWith(TAG_COLUMN) 
					|| nodeName.endsWith(TAG_COLUMNS))) {
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
	
	public static int getColumnsCount(Element sourceElement, ArrayList<Element> columns) {
		int count = 0;
		// check for exact value in component
		try {
			count = Integer.parseInt(sourceElement.getAttribute(ATTR_COLUMNS));
		} catch (NumberFormatException e) {
			count = calculateRowColumns(sourceElement, columns);
		}
		return count;
	}

	/*
	 * Calculate max number of columns per row. 
	 * For rows, recursive calculate max length.
	 */
	public static int calculateRowColumns(Element sourceElement, ArrayList<Element> columns) {
		int count = 0;
		int currentLength = 0;
		for (Element column : columns) {
			if (ComponentUtil.isRendered(column)) {
				String nodeName = column.getNodeName();
				if (nodeName.endsWith(TAG_COLUMN_GROUP)) {
					// Store max calculated value of previous rows.
					count = Math.max(currentLength,count);
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement, getColumns(column));
					// Store max calculated value
					count = Math.max(currentLength,count);
					currentLength = 0;
				} else if (nodeName.equals(sourceElement.getPrefix() + Constants.COLON + TAG_COLUMN) ||
						nodeName.equals(sourceElement.getPrefix() + Constants.COLON + TAG_COLUMNS)) {
					// For new row, save length of previous.
					if (RichFacesColumnTemplate.isBreakBefore(column)) {
						count = Math.max(currentLength,count);
						currentLength = 0;
					}
					String colspanStr = column.getAttribute("colspan"); //$NON-NLS-1$
					try {
						currentLength += Integer.parseInt(colspanStr);
					} catch (NumberFormatException e) {
						currentLength++;
					}
				} else if (nodeName.endsWith(TAG_COLUMN)) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}
			}
		}
		return Math.max(currentLength, count);
	}
	
}
