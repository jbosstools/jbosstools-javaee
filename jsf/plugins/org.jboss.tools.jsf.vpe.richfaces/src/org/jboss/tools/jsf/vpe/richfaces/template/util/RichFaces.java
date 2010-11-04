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

package org.jboss.tools.jsf.vpe.richfaces.template.util;

import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilder;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilderException;

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
}
