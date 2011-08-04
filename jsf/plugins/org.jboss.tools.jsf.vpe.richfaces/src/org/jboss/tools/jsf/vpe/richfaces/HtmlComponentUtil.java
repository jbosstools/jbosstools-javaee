/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces;

import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.util.HTML;

/**
 * Util class which contains basic html tags.
 * 
 * @deprecated use org.jboss.tools.vpe.editor.util.HTML and
 *             org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces
 * @author Max Areshkau
 * @author yradtsevich
 */
public interface HtmlComponentUtil {

	/**
	 * @deprecated use {@link HTML#TAG_DL} instead
	 */
	static final String HTML_TAG_DL = "dl"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_BR} instead 
	 */
	static final String HTML_TAG_BR = "br"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_COLGROUP} instead 
	 */
	static final String HTML_TAG_COLGROUP = "colgroup"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_THEAD} instead 
	 */
	static final String HTML_TAG_THEAD = "thead"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TFOOT} instead 
	 */
	static final String HTML_TAG_TFOOT = "tfoot"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_CAPTION} instead 
	 */
	static final String HTML_TAG_CAPTION = "caption"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_DT} instead 
	 */
	static final String HTML_TAG_DT = "dt"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_DD} instead 
	 */
	static final String HTML_TAG_DD = "dd"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TABLE} instead 
	 */
	static final String HTML_TAG_TABLE = "TABLE"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TBODY} instead 
	 */
	static final String HTML_TAG_TBODY = "TBODY"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TR} instead 
	 */
	static final String HTML_TAG_TR = "TR"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TD} instead 
	 */
	static final String HTML_TAG_TD = "TD"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_TH} instead 
	 */
	static final String HTML_TAG_TH = "TH"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_INPUT} instead 
	 */
	static final String HTML_TAG_INPUT = "INPUT"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_IMG} instead 
	 */
	static final String HTML_TAG_IMG = "IMG"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_DIV} instead 
	 */
	static final String HTML_TAG_DIV = "DIV"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_SPAN} instead 
	 */
	static final String HTML_TAG_SPAN = "SPAN"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_A} instead 
	 */
	static final String HTML_TAG_A = "A"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_B} instead 
	 */
	static final String HTML_TAG_B = "B"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#TAG_LI} instead 
	 */
	static final String HTML_TAG_LI = "LI"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#ATTR_COLSPAN} instead 
	 */
	static final String HTML_TABLE_COLSPAN = "colspan"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link HTML#ATTR_HEIGHT} instead 
	 */
	static final String HTML_HEIGHT_ATTR = "height"; //$NON-NLS-1$

	/** 
	 * 	@deprecated use {@link RichFaces#ATTR_STYLE_CLASS} instead 
	 */
	static final String HTML_STYLECLASS_ATTR = "styleClass"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_CLASS} instead 
	 */
	static final String HTML_CLASS_ATTR = "class"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_CELLSPACING} instead 
	 */
	static final String HTML_CELLSPACING_ATTR = "cellspacing"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_CELLPADDING} instead 
	 */
	static final String HTML_CELLPADDING_ATTR = "cellpadding"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#VALUE_ALIGN_LEFT} instead 
	 */
	static final String HTML_ALIGN_LEFT_VALUE = "left"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#VALUE_ALIGN_RIGHT} instead 
	 */
	static final String HTML_ALIGN_RIGHT_VALUE = "right"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#VALUE_ALIGN_CENTER} instead 
	 */
	static final String HTML_ALIGN_CENTER_VALUE = "center"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_WIDTH} instead 
	 */
	static final String HTML_ATR_WIDTH = "width"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_HEIGHT} instead 
	 */
	static final String HTML_ATR_HEIGHT = "height"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_SRC} instead 
	 */
	static final String HTML_ATR_SRC = "src"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_STYLE} instead 
	 */
	static final String HTML_STYLE_ATTR = "style"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_SCOPE} instead 
	 */
	static final String HTML_SCOPE_ATTR = "scope"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_BORDER} instead 
	 */
	static final String HTML_BORDER_ATTR = "border"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_ALIGN} instead 
	 */
	static final String HTML_ALIGN_ATTR = "align"; //$NON-NLS-1$

	// TODO: move the constant from this class to somewhere
	static final String FILE_PROTOCOL = "file://"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_COLSPAN} instead 
	 */
	static final String HTML_COLSPAN_ATTR = "colspan"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_ROWSPAN} instead 
	 */
	static final String HTML_ROWSPAN_ATTR = "rowspan"; //$NON-NLS-1$

	/** @deprecated there is no tag with row attribute */
	// TODO: remove the attribute from the code
	static final String HTML_ROW_ATTR = "row"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_SIZE} instead 
	 */
	static final String HTML_SIZE_ATTR = "size"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_TYPE} instead 
	 */
	static final String HTML_TYPE_ATTR = "type"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_READONLY} instead 
	 */
	static final String HTML_READONLY_ATTR = "readonly"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#TAG_BUTTON} instead 
	 */
	static final String HTML_TAG_BUTTON = "button"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_VALUE} instead 
	 */
	static final String HTML_VALUE_ATTR = "value"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#STYLE_PARAMETER_BORDER_WIDTH} instead 
	 */
	static final String CSS_BORDER_WIDTH = "border-width"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#STYLE_PARAMETER_BORDER_STYLE} instead 
	 */
	static final String CSS_BORDER_STYLE = "border-style"; //$NON-NLS-1$

	/** 
	 * @deprecated use {@link HTML#STYLE_PARAMETER_DISPLAY} instead 
	 */
	static final String CSS_DISPLAY = "display"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_WIDTH} instead 
	 */
	static final String HTML_WIDTH_ATTR = "width"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_VALIGN} instead 
	 */
	static final String HTML_ATTR_VALIGN = "valign"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#STYLE_VALUE_MIDDLE} instead 
	 */
	static final String HTML_ATTR_VALIGN_MIDDLE_VALUE = "middle"; //$NON-NLS-1$

	/**
	 * 	@deprecated use {@link HTML#ATTR_BACKGROUND} instead 
	 */
	static final String HTML_ATTR_BACKGROUND = "background"; //$NON-NLS-1$

	/**
	 *	@deprecated use {@link HTML#ATTR_DISABLED} instead 
	 */
	static final String HTML_ATTR_DISABLED = "disabled"; //$NON-NLS-1$
}
