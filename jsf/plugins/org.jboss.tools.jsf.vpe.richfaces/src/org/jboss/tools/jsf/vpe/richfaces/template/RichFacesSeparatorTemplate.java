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
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces separator
 */
public class RichFacesSeparatorTemplate extends VpeAbstractTemplate {

	final static String STYLE_PATH = "separator/separator.css"; //$NON-NLS-1$

	final static String LINE_SOLID = "solid"; //$NON-NLS-1$

	final static String LINE_DOUBLE = "double"; //$NON-NLS-1$

	final static String LINE_DOTTED = "dotted"; //$NON-NLS-1$

	final static String LINE_DASHED = "dashed"; //$NON-NLS-1$

	final static String LINE_BEVELED = "beveled"; //$NON-NLS-1$

	final static String PIXEL_PREFIX = "px"; //$NON-NLS-1$

	final static String PERCENT_PREFIX = "%"; //$NON-NLS-1$

	final static String[] LINE_TYPES = { LINE_SOLID, LINE_DOUBLE, LINE_DOTTED,
			LINE_DASHED, LINE_BEVELED };

	final static String LINE_TYPE_ATTR = "lineType"; //$NON-NLS-1$

	final static String DEFAULT_HEIGHT = "6px"; //$NON-NLS-1$

	final static String DEFAULT_ALIGN = "left"; //$NON-NLS-1$

	final static String DEFAULT_WIDTH = "100%"; //$NON-NLS-1$

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
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "richFacesSeparator"); //$NON-NLS-1$
		Element sourceElement = (Element) sourceNode;
		/* Create new html element table */
		nsIDOMElement separator = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement line = visualDocument
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
				"td-parentdiv"); //$NON-NLS-1$
		String lineClass = sourceElement.getAttribute("styleClass"); //$NON-NLS-1$
		/* Apply class for separator */
		String lineType = sourceElement.getAttribute(LINE_TYPE_ATTR);
		String style = sourceElement.getAttribute("style"); //$NON-NLS-1$
		if (lineType == null || lineType.equalsIgnoreCase(LINE_BEVELED)) {
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					"dr-table-header rich-table-header-continue" //$NON-NLS-1$
							+ (lineClass == null ? "" : lineClass)); //$NON-NLS-1$
			style = setBeveledStyle(width, height, ComponentUtil
					.getHeaderBackgoundImgStyle()
					+ ";" + (style == null ? "" : style)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					(lineClass == null ? "" : lineClass)); //$NON-NLS-1$
			style = setStyle(lineType, width, height, style);
			line.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					"separator-color"); //$NON-NLS-1$
		}
		line.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		separator.appendChild(line);
		return creationData;
	}

	/**
	 * Method for remove attributes in separator
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,  Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument, visualNode, data, name);
		nsIDOMElement element = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement line = getLineElement(element);
		String style = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		String width = sourceElement.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
		String newStyle;
		String height = sourceElement.getAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);

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
						+ ";" + (style == null ? "" : style)); //$NON-NLS-1$ //$NON-NLS-2$
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
						(height == null ? DEFAULT_HEIGHT : height), ""); //$NON-NLS-1$
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
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name,	String value) {
		String newStyle;
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		nsIDOMElement element = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement line = getLineElement(element);
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
						+ ";" + (style == null ? "" : style)); //$NON-NLS-1$ //$NON-NLS-2$
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
						+ ";" + (style == null ? "" : style)); //$NON-NLS-1$ //$NON-NLS-2$
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
						+ ";" + (style == null ? "" : style)); //$NON-NLS-1$ //$NON-NLS-2$
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
						+ ";" + (value == null ? "" : value)); //$NON-NLS-1$ //$NON-NLS-2$
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
			Integer.valueOf(num);
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

	private nsIDOMElement getLineElement(nsIDOMElement parent) {
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode node = list.item(0);
		nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		return element;
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
		newStyle.append(HtmlComponentUtil.CSS_BORDER_STYLE + ":"  + (lineType == null ? LINE_SOLID : lineType) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		newStyle.append(HtmlComponentUtil.CSS_BORDER_WIDTH + ":" + (height == null ? DEFAULT_HEIGHT : height) + " 0px 0px;"); //$NON-NLS-1$ //$NON-NLS-2$
		newStyle.append(HtmlComponentUtil.HTML_ATR_WIDTH + ":"  + (width == null ? DEFAULT_WIDTH : width) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		newStyle.append((style == null ? "" : style)); //$NON-NLS-1$
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
		newStyle.append(HtmlComponentUtil.HTML_ATR_HEIGHT + ":" + (height == null ? DEFAULT_HEIGHT : height) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		newStyle.append(HtmlComponentUtil.HTML_ATR_WIDTH + ":" + (width == null ? DEFAULT_WIDTH : width) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		newStyle.append((style == null ? "" : style)); //$NON-NLS-1$
		return newStyle.toString();
	}

}