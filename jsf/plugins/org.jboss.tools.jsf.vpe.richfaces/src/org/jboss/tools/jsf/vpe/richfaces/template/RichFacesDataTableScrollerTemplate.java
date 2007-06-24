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
 * Template for Rich Faces DataTableScroller
 */
public class RichFacesDataTableScrollerTemplate extends VpeAbstractTemplate {

	final static int EMPTY_CELL = 1;

	final static int NUM_CELL = 2;

	final static int SCROLL_CELL = 3;

	final static String STYLE_PATH = "dataTableScroller/dataTableScroller.css";

	/**
	 * Default size component This parameter need for calculate default number
	 * of cells
	 */
	final static String DEFAULT_STYLE_WIDTH = "width : 400px;";

	/**
	 * Minimal size component This parameter need for calculate minimal number
	 * of cells
	 */
	final static String MIN_STYLE_WIDTH = "width : 225px;";

	final static String RIGHT_DOUBLE_SCROLL_SYMBOL = ">>";

	final static String RIGHT_SINGLE_SCROLL_SYMBOL = ">";

	final static String LEFT_DOUBLE_SCROLL_SYMBOL = "<<";

	final static String LEFT_SINGLE_SCROLL_SYMBOL = "<";

	/**
	 * Minimal cells in datascroller
	 */
	final static int MIN_NUM_CELLS = 9;

	final static String PIXEL_PREFIX = "px";

	/**
	 * 
	 * Constructor.
	 */
	public RichFacesDataTableScrollerTemplate() {
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
		Element source = (Element) sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH,
				"richFacesDataScrollerTable");
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		String style = source.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		div.setAttribute("class", "dr-div-heigth");
		if (style == null) {
			style = DEFAULT_STYLE_WIDTH;
		}

		Element table = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				"dr-dscr-t dr-tbpnl-cntnt");
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "1");
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
		Element tbody = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		Element tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		tbody.appendChild(tr);
		table.appendChild(tbody);
		VpeCreationData creationData = new VpeCreationData(div);
		/* Add scroll cells */
		Element child1 = createCell(visualDocument, false,
				LEFT_DOUBLE_SCROLL_SYMBOL, SCROLL_CELL);
		Element child2 = createCell(visualDocument, false,
				LEFT_SINGLE_SCROLL_SYMBOL, SCROLL_CELL);
		/* Add empty cells */
		Element child3 = createCell(visualDocument, false, "", EMPTY_CELL);
		tr.appendChild(child1);
		tr.appendChild(child2);
		tr.appendChild(child3);

		String str = getWidth(style);
		int size = getSize(str);
		int minSize = getSize(getWidth(MIN_STYLE_WIDTH));
		/* check size */
		if (size < minSize) {
			size = minSize;
			style = MIN_STYLE_WIDTH;
		}
		size /= (minSize / MIN_NUM_CELLS);
		/* Add number cells in datascroller */
		for (int i = 0; i < (size - 6); i++) {
			Element child = createCell(visualDocument, (i == 0 ? true : false),
					"" + (i + 1), NUM_CELL);
			tr.appendChild(child);
		}
		/* Add empty cell */
		child1 = createCell(visualDocument, false, "", EMPTY_CELL);
		/* Add scroll cells */
		child2 = createCell(visualDocument, false, RIGHT_SINGLE_SCROLL_SYMBOL,
				SCROLL_CELL);
		child3 = createCell(visualDocument, false, RIGHT_DOUBLE_SCROLL_SYMBOL,
				SCROLL_CELL);
		tr.appendChild(child1);
		tr.appendChild(child2);
		tr.appendChild(child3);

		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		div.appendChild(table);
		return creationData;
	}

	/**
	 * 
	 * Method for creating one cell in table.
	 * 
	 * @param visualDocument
	 * @param color -
	 *            border and text color
	 * @param text -
	 *            text in cell
	 * @return Element
	 */
	private Element createCell(Document visualDocument, boolean active,
			String text, int sellType) {
		Element td = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, "center");
		Node d = visualDocument.createTextNode(text);
		if (sellType == NUM_CELL) {
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					(active ? "dr-dscr-act" : "dr-dscr-inact"));
			td.appendChild(d);
		} else if (sellType == EMPTY_CELL) {
			td
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							"dr-dscr-button");
		} else {
			td
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							"dr-dscr-button");
			td.appendChild(d);
		}
		return td;
	}

	/**
	 * Method for remove attributes .
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
		Element element = (Element) visualNode;
		element.removeAttribute(name);

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
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		Element element = (Element) visualNode;
		int size = 45;
		if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR)) {
			String str = getWidth(value);
			size = getSize(str);
			int minSize = getSize(getWidth(MIN_STYLE_WIDTH));
			if (size < minSize) {
				size = minSize;
				value = MIN_STYLE_WIDTH;
			}
			size /= (minSize / MIN_NUM_CELLS);
			Element tr = getTR(element);
			NodeList nodes = tr.getChildNodes();
			int nodesLength = nodes.getLength();
			if (nodesLength != size) {
				if (size < nodesLength) {
					/* Remove cells in datascroller */
					for (int i = size; i < nodesLength; i++) {
						tr.removeChild(nodes.item(size - 3));
					}
				} else {
					/* Remove cells in datascroller */
					for (int i = 0; i < 3; i++) {
						tr.removeChild(nodes.item(nodesLength - 3));
					}
					/* Add cells in datascroller */
					for (int i = 0; i < (size - nodesLength); i++) {
						Element cell = createCell(visualDocument, false, ""
								+ (nodesLength - 5 + i), NUM_CELL);
						tr.appendChild(cell);
					}
					Element child1 = createCell(visualDocument, false, "",
							EMPTY_CELL);
					Element child2 = createCell(visualDocument, false,
							RIGHT_SINGLE_SCROLL_SYMBOL, SCROLL_CELL);
					Element child3 = createCell(visualDocument, false,
							RIGHT_DOUBLE_SCROLL_SYMBOL, SCROLL_CELL);
					tr.appendChild(child1);
					tr.appendChild(child2);
					tr.appendChild(child3);
				}
			}
		}
		element.setAttribute(name, value);

	}

	/**
	 * Method for parse style and get width.
	 * 
	 * @param style
	 * @return size
	 */
	private String getWidth(String style) {
		String[] sub = style.split(";");
		for (int i = 0; i < sub.length; i++) {
			sub[i] = sub[i].trim();
			sub[i] = sub[i].toLowerCase();
			int pos = sub[i].indexOf(HtmlComponentUtil.HTML_ATR_WIDTH);
			if (pos != -1) {
				if (pos == 0 || sub[i].charAt(pos - 1) != '-') {
					pos = sub[i].indexOf(":");
					return sub[i].substring(pos + 1);
				}
			}
		}
		return null;
	}

	/**
	 * Method for convert String to number
	 * 
	 * @param size
	 * @return number
	 */
	private int getSize(String size) {
		if(size==null) {
			return 0;
		}
		String num = new String(size);
		int pos = size.indexOf(PIXEL_PREFIX);
		if (pos != -1) {
			num = size.substring(0, pos);
		}
		try {
			num = num.trim();
			Integer i = new Integer(num);
			return i.intValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 
	 * Get Tag TR Element.
	 * 
	 * @param parent
	 *            Element
	 * @return Element Tag TR
	 */
	private Element getTR(Element parent) {
		NodeList list = parent.getChildNodes();
		Element table = (Element) list.item(0);
		NodeList tableList = table.getChildNodes();
		Element tbody = (Element) tableList.item(0);
		NodeList tbodyList = tbody.getChildNodes();
		return (Element) tbodyList.item(0);
	}
}