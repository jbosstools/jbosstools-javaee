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
package org.jboss.tools.jsf.vpe.richfaces;

/**
 * Util class which contains basic html tags.
 * 
 * @deprecated use org.jboss.tools.vpe.editor.util.HTML and
 *             org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces
 * @author Max Areshkau
 * 
 */
public class HtmlComponentUtil {

	/** HTML TAG DL */
	public static final String HTML_TAG_DL = "dl"; //$NON-NLS-1$

	/** HTML TAG BR */
	public static final String HTML_TAG_BR = "br"; //$NON-NLS-1$

	/** HTML TAG COLGROUP */
	public static final String HTML_TAG_COLGROUP = "colgroup"; //$NON-NLS-1$

	/** HTML TAG THEAD */
	public static final String HTML_TAG_THEAD = "thead"; //$NON-NLS-1$

	/** HTML TAG TFOOT */
	public static final String HTML_TAG_TFOOT = "tfoot"; //$NON-NLS-1$

	/** HTML TAG CAPTION */
	public static final String HTML_TAG_CAPTION = "caption"; //$NON-NLS-1$

	/** HTML TAG DT */
	public static final String HTML_TAG_DT = "dt"; //$NON-NLS-1$

	/** HTML TAG DD */
	public static final String HTML_TAG_DD = "dd"; //$NON-NLS-1$

	/** HTML_TAG_TABLE * */
	public static final String HTML_TAG_TABLE = "TABLE"; //$NON-NLS-1$

	/** HTML_TAG_TBODY * */
	public static final String HTML_TAG_TBODY = "TBODY"; //$NON-NLS-1$

	/** HTML_TAG_TR * */
	public static final String HTML_TAG_TR = "TR"; //$NON-NLS-1$

	/** HTML_TAG_TD * */
	public static final String HTML_TAG_TD = "TD"; //$NON-NLS-1$

	/** HTML_TAG_TH * */
	public static final String HTML_TAG_TH = "TH"; //$NON-NLS-1$

	/** HTML_TAG_INPUT * */
	public static final String HTML_TAG_INPUT = "INPUT"; //$NON-NLS-1$

	/** HTML_TAG_IMG * */
	public static final String HTML_TAG_IMG = "IMG"; //$NON-NLS-1$

	/** HTML_TAG_DIV */
	public static final String HTML_TAG_DIV = "DIV"; //$NON-NLS-1$

	/** HTML_TAG_SPAN */
	public static final String HTML_TAG_SPAN = "SPAN"; //$NON-NLS-1$

	/** HTML_TAG_A */
	public static final String HTML_TAG_A = "A"; //$NON-NLS-1$

	/** HTML_TAG_B */
	public static final String HTML_TAG_B = "B"; //$NON-NLS-1$

	/** HTML_TAG_LI */
	public static final String HTML_TAG_LI = "LI"; //$NON-NLS-1$

	/** HTML_TABLE_COLSPAN * */
	public static final String HTML_TABLE_COLSPAN = "colspan"; //$NON-NLS-1$

	/** HTML_HEIGHT_ATTR * */
	public static final String HTML_HEIGHT_ATTR = "height"; //$NON-NLS-1$

	/** HTML_CLASS_ATTR * */
	public static final String HTML_STYLECLASS_ATTR = "styleClass"; //$NON-NLS-1$

	/** HTML_CLASS_ATTR * */
	public static final String HTML_CLASS_ATTR = "class"; //$NON-NLS-1$

	/** HTML_CELLSPACING_ATTR * */
	public static final String HTML_CELLSPACING_ATTR = "cellspacing"; //$NON-NLS-1$

	/** HTML_CELLPADDING_ATTR * */
	public static final String HTML_CELLPADDING_ATTR = "cellpadding"; //$NON-NLS-1$

	/** HTML_ALIGN_LEFT_VALUE * */
	public static final String HTML_ALIGN_LEFT_VALUE = "left"; //$NON-NLS-1$

	/** HTML_ALIGN_RIGHT_VALUE * */
	public static final String HTML_ALIGN_RIGHT_VALUE = "right"; //$NON-NLS-1$

	/** HTML_ALIGN_CENTER_VALUE * */
	public static final String HTML_ALIGN_CENTER_VALUE = "center"; //$NON-NLS-1$

	/** HTML_ATR_WIDTH */
	public static final String HTML_ATR_WIDTH = "width"; //$NON-NLS-1$

	/** HTML_ATR_WIDTH */
	public static final String HTML_ATR_HEIGHT = "height"; //$NON-NLS-1$

	/** HTML_ATR_src */
	public static final String HTML_ATR_SRC = "src"; //$NON-NLS-1$

	/** style */
	public static final String HTML_STYLE_ATTR = "style"; //$NON-NLS-1$

	/** scope */
	public static final String HTML_SCOPE_ATTR = "scope"; //$NON-NLS-1$

	/** HTML_TABLE_ATR_ */
	public static final String HTML_BORDER_ATTR = "border"; //$NON-NLS-1$

	/** HTML_ALIGN_ATR */
	public static final String HTML_ALIGN_ATTR = "align"; //$NON-NLS-1$

	/** HTML_TABLE_ATR_ */
	public static final String FILE_PROTOCOL = "file://"; //$NON-NLS-1$

	/** HTML_COLSPAN_ATTR * */
	public static final String HTML_COLSPAN_ATTR = "colspan"; //$NON-NLS-1$

	/** HTML_ROWSPAN_ATTR * */
	public static final String HTML_ROWSPAN_ATTR = "rowspan"; //$NON-NLS-1$

	/** HTML_ROW_ATTR * */
	public static final String HTML_ROW_ATTR = "row"; //$NON-NLS-1$

	/** HTML_SIZE_ATTR * */
	public static final String HTML_SIZE_ATTR = "size"; //$NON-NLS-1$

	/** HTML_TYPE_ATTR * */
	public static final String HTML_TYPE_ATTR = "type"; //$NON-NLS-1$

	/** HTML_READONLY_ATTR * */
	public static final String HTML_READONLY_ATTR = "readonly"; //$NON-NLS-1$

	/** HTML_TAG_BUTTON * */
	public static final String HTML_TAG_BUTTON = "button"; //$NON-NLS-1$

	/** HTML_VALUE_ATTR * */
	public static final String HTML_VALUE_ATTR = "value"; //$NON-NLS-1$

	/** CSS_BORDER_WIDTH */
	public static final String CSS_BORDER_WIDTH = "border-width"; //$NON-NLS-1$

	/** CSS_BORDER_STYLE */
	public static final String CSS_BORDER_STYLE = "border-style"; //$NON-NLS-1$

	public static final String CSS_DISPLAY = "display"; //$NON-NLS-1$

	/** HTML_WIDTH_ATTR * */
	public static final String HTML_WIDTH_ATTR = "width"; //$NON-NLS-1$

	/** HTML_ATTR_VALIGN */
	public static final String HTML_ATTR_VALIGN = "valign"; //$NON-NLS-1$

	/** HTML_ATTR_VALIGN_MIDDLE_VALUE */
	public static final String HTML_ATTR_VALIGN_MIDDLE_VALUE = "middle"; //$NON-NLS-1$

	/** HTML_ATTR_BACKGROUND */
	public static final String HTML_ATTR_BACKGROUND = "background"; //$NON-NLS-1$

	/** HTML_ATTR_BACKGROUND */
	public static final String HTML_ATTR_DISABLED = "disabled"; //$NON-NLS-1$
}
