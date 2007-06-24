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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Template for Rich Faces separator
 */
public class RichFacesSeparatorTemplate extends VpeAbstractTemplate {

	final static String STYLE_PATH = "separator/separator.css";

	final static String LINE_SOLID = "solid";

	final static String LINE_DOUBLE = "double";

	final static String LINE_DOTTED = "dotted";

	final static String LINE_DASHED = "dashed";

	final static String LINE_BEVELED = "beveled";

	final static String PIXEL_PREFIX = "px";

	final static String PERCENT_PREFIX = "%";

	final static String[] LINE_TYPES = { LINE_SOLID, LINE_DOUBLE, LINE_DOTTED,
			LINE_DASHED, LINE_BEVELED };

	final static String LINE_TYPE_ATTR = "lineType";

	final static String DEFAULT_HEIGHT = "6px";

	final static String DEFAULT_ALIGN = "left";

	final static String DEFAULT_WIDTH = "100%";

	/**
	 * Constructor
	 */
	public RichFacesSeparatorTemplate() {
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
			Document visualDocument) {
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "richFacesSeparator");
		Element sourceElement = (Element) sourceNode;
		/* Create new html element table */
		Element separator = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		Element line = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		VpeCreationData creationData = new VpeCreationData(separator);
		String width = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
		String height = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);
		/* Set align for separator */
		String align = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR);
		separator.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				(align == null ? DEFAULT_ALIGN : align));
		separator.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				"td-parentdiv");
		String lineClass = sourceElement.getAttribute("styleClass");
		/* Apply class for separator */
		String lineType = sourceElement.getAttribute(LINE_TYPE_ATTR);
		String style = sourceElement.getAttribute("style");
		if (lineType == null || lineType.equalsIgnoreCase(LINE_BEVELED)) {
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					"dr-table-header rich-table-header-continue"
							+ (lineClass == null ? "" : lineClass));
			style = setBeveledStyle(width, height, ComponentUtil
					.getHeaderBackgoundImgStyle()
					+ ";" + (style == null ? "" : style));
		} else {
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					(lineClass == null ? "" : lineClass));
			style = setStyle(lineType, width, height, style);
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					"separator-color");
		}
		line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		separator.appendChild(line);
		return creationData;
	}

	/**
	 * Method for remove attributes in separator
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
		Element element = (Element) visualNode;
		Element line = getLineElement(element);
		String style = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		String width = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
		String newStyle;
		String height = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);

		String lineType = sourceElement.getAttribute(LINE_TYPE_ATTR);
		if (lineType == null) {
			lineType = LINE_BEVELED;
		}
		if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_HEIGHT)
				|| name.equalsIgnoreCase(LINE_TYPE_ATTR)
				|| name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_WIDTH)) {
			if (lineType != null && lineType.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(width, height, ComponentUtil
						.getHeaderBackgoundImgStyle()
						+ ";" + (style == null ? "" : style));
			} else {
				newStyle = setStyle(lineType, (width == null ? DEFAULT_WIDTH
						: addPrefixSize(width)),
						(height == null ? DEFAULT_HEIGHT
								: addPrefixSize(height)), style);
			}
			line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, newStyle);
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ALIGN_ATTR)) {
			element.removeAttribute(name);
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR)) {
			if (lineType != null && lineType.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(width, height, ComponentUtil
						.getHeaderBackgoundImgStyle());
			} else {
				newStyle = setStyle(lineType, (width == null ? DEFAULT_WIDTH
						: addPrefixSize(width)),
						(height == null ? DEFAULT_HEIGHT : height), "");
			}
			line.setAttribute(name, newStyle);

		} else {
			line.removeAttribute(name);
		}
	}

	/*
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		String newStyle;
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		Element element = (Element) visualNode;
		Element line = getLineElement(element);
		String style = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		String width = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
		String height = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);
		String lineType = sourceElement.getAttribute(LINE_TYPE_ATTR);
		if (lineType == null) {
			lineType = LINE_BEVELED;
		}
		if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_HEIGHT)) {
			if (!isNumber(value) && (value.indexOf(PIXEL_PREFIX)) == -1) {
				return;
			}
			if (lineType.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(width, value, ComponentUtil
						.getHeaderBackgoundImgStyle()
						+ ";" + (style == null ? "" : style));
			} else {
				newStyle = setStyle(
						lineType,
						(width == null ? DEFAULT_WIDTH : addPrefixSize(width)),
						(value == null ? DEFAULT_HEIGHT : addPrefixSize(value)),
						style);
			}
			line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, newStyle);
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_WIDTH)) {
			if (!isNumber(value) && !isHasPrefix(value)) {
				return;
			}
			if (lineType.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(value, height, ComponentUtil
						.getHeaderBackgoundImgStyle()
						+ ";" + (style == null ? "" : style));
			} else {
				newStyle = setStyle(lineType, (value == null ? DEFAULT_WIDTH
						: addPrefixSize(value)),
						(height == null ? DEFAULT_HEIGHT
								: addPrefixSize(height)), style);
			}
			line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, newStyle);
		} else if (name.equalsIgnoreCase(LINE_TYPE_ATTR)) {
			if (!isLineType(value)) {
				return;
			}
			if (value.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(width, height, ComponentUtil
						.getHeaderBackgoundImgStyle()
						+ ";" + (style == null ? "" : style));
			} else {
				newStyle = setStyle(value, (width == null ? DEFAULT_WIDTH
						: addPrefixSize(width)),
						(height == null ? DEFAULT_HEIGHT
								: addPrefixSize(height)), style);
			}
			line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, newStyle);
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ALIGN_ATTR)) {
			element.setAttribute(name, value);
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR)) {
			if (lineType.equalsIgnoreCase(LINE_BEVELED)) {
				newStyle = setBeveledStyle(width, value, ComponentUtil
						.getHeaderBackgoundImgStyle()
						+ ";" + (value == null ? "" : value));
			} else {
				newStyle = setStyle(lineType, (width == null ? DEFAULT_WIDTH
						: addPrefixSize(width)),
						(height == null ? DEFAULT_HEIGHT
								: addPrefixSize(height)), value);
			}
			line.setAttribute(name, newStyle);
		} else {
			line.setAttribute(name, value);
		}
	}

	/**
	 * Method add in size extention prefix(default 'px').
	 * 
	 * @param size
	 * @return size;
	 */
	private String addPrefixSize(String size) {
		if (size != null) {
			if (isHasPrefix(size) == false) {
				size = size + PIXEL_PREFIX;
			}
		}
		return size;
	}

	/**
	 * Method for checking String by number
	 * 
	 * @param num
	 * @return true - if String is number, false is not number
	 */
	private boolean isNumber(String num) {
		try {
			new Integer(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Method for checking String by consist prefix "px" or "%"
	 * 
	 * @param size
	 * @return true - if string consist prefix "px" or "%"
	 */
	private boolean isHasPrefix(String size) {
		int pos1 = size.indexOf(PIXEL_PREFIX);
		int pos2 = size.indexOf(PERCENT_PREFIX);
		if (pos1 == -1 && pos2 == -1) {
			return false;
		}
		return true;
	}

	/**
	 * Validate lineType .
	 * 
	 * @param lineType
	 * @return boolean
	 */
	private boolean isLineType(String lineType) {
		for (int i = 0; i < LINE_TYPES.length; i++) {
			if (lineType.equalsIgnoreCase(LINE_TYPES[i])) {
				return true;
			}
		}
		return false;
	}

	private Element getLineElement(Element parent) {
		NodeList list = parent.getChildNodes();
		return (Element) list.item(0);
	}

	/**
	 * Create new CSS style for separator .
	 * 
	 * @param lineType
	 * @param width
	 * @param height
	 * @param style
	 * @return new style
	 */
	private String setStyle(String lineType, String width, String height,
			String style) {
		StringBuffer newStyle = new StringBuffer();
		newStyle.append(HtmlComponentUtil.CSS_BORDER_STYLE + ":"
				+ (lineType == null ? LINE_SOLID : lineType) + ";");
		newStyle.append(HtmlComponentUtil.CSS_BORDER_WIDTH + ":"
				+ (height == null ? DEFAULT_HEIGHT : height) + " 0px 0px;");
		newStyle.append(HtmlComponentUtil.HTML_ATR_WIDTH + ":"
				+ (width == null ? DEFAULT_WIDTH : width) + ";");
		newStyle.append((style == null ? "" : style));
		return newStyle.toString();
	}

	/**
	 * Create new CSS style for beveled separator.
	 * 
	 * @param width
	 * @param height
	 * @param style
	 * @return new style
	 */
	private String setBeveledStyle(String width, String height, String style) {
		StringBuffer newStyle = new StringBuffer();
		newStyle.append(HtmlComponentUtil.HTML_ATR_HEIGHT + ":"
				+ (height == null ? DEFAULT_HEIGHT : height) + ";");
		newStyle.append(HtmlComponentUtil.HTML_ATR_WIDTH + ":"
				+ (width == null ? DEFAULT_WIDTH : width) + ";");
		newStyle.append((style == null ? "" : style));
		return newStyle.toString();
	}
}