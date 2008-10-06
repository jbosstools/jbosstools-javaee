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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces DataTableScroller
 */
public class RichFacesDataTableScrollerTemplate extends VpeAbstractTemplate {

	final static int EMPTY_CELL = 1;

	final static int NUM_CELL = 2;

	final static int SCROLL_CELL = 3;

	final static String STYLE_PATH = "dataTableScroller/dataTableScroller.css"; //$NON-NLS-1$

	/**
	 * Default size component This parameter need for calculate default number
	 * of cells
	 */
	final static String DEFAULT_STYLE_WIDTH = "width : 400px;"; //$NON-NLS-1$

	/**
	 * Minimal size component This parameter need for calculate minimal number
	 * of cells
	 */
	final static String MIN_STYLE_WIDTH = "width : 225px;"; //$NON-NLS-1$

	final static String RIGHT_DOUBLE_SCROLL_SYMBOL = ">>"; //$NON-NLS-1$

	final static String RIGHT_SINGLE_SCROLL_SYMBOL = ">"; //$NON-NLS-1$

	final static String LEFT_DOUBLE_SCROLL_SYMBOL = "<<"; //$NON-NLS-1$

	final static String LEFT_SINGLE_SCROLL_SYMBOL = "<"; //$NON-NLS-1$

	/**
	 * Minimal cells in datascroller
	 */
	final static int MIN_NUM_CELLS = 9;

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
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element source = (Element) sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH,
				"richFacesDataScrollerTable"); //$NON-NLS-1$
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		String style = source.getAttribute(RichFaces.ATTR_STYLE);
		div.setAttribute(HTML.ATTR_CLASS, "dr-div-heigth"); //$NON-NLS-1$
		if (style == null) {
			style = DEFAULT_STYLE_WIDTH;
		}

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		table.setAttribute(HTML.ATTR_CLASS,
				"dr-dscr-t dr-tbpnl-cntnt"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_CELLSPACING, "1"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
		table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		tbody.appendChild(tr);
		table.appendChild(tbody);
		
		VpeCreationData creationData = new VpeCreationData(div);
		/* Add scroll cells */
		nsIDOMElement child1 = createCell(visualDocument, false, LEFT_DOUBLE_SCROLL_SYMBOL, SCROLL_CELL);
		nsIDOMElement child2 = createCell(visualDocument, false, LEFT_SINGLE_SCROLL_SYMBOL, SCROLL_CELL);
		/* Add empty cells */
		nsIDOMElement child3 = createCell(visualDocument, false, Constants.EMPTY, EMPTY_CELL);
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
			nsIDOMElement child = createCell(visualDocument, (i == 0 ? true : false), Constants.EMPTY + (i + 1), NUM_CELL);
			tr.appendChild(child);
		}
		/* Add empty cell */
		child1 = createCell(visualDocument, false, Constants.EMPTY, EMPTY_CELL);
		/* Add scroll cells */
		child2 = createCell(visualDocument, false, RIGHT_SINGLE_SCROLL_SYMBOL,
				SCROLL_CELL);
		child3 = createCell(visualDocument, false, RIGHT_DOUBLE_SCROLL_SYMBOL,
				SCROLL_CELL);
		tr.appendChild(child1);
		tr.appendChild(child2);
		tr.appendChild(child3);

		div.setAttribute(HTML.ATTR_STYLE, style);
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
	private nsIDOMElement createCell(nsIDOMDocument visualDocument, boolean active, String text, int sellType) {
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		td.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_ALIGN_CENTER);
		nsIDOMText d = visualDocument.createTextNode(text);
		if (sellType == NUM_CELL) {
			td.setAttribute(HTML.ATTR_CLASS,
					(active ? "dr-dscr-act" : "dr-dscr-inact")); //$NON-NLS-1$ //$NON-NLS-2$
			td.appendChild(d);
		} else if (sellType == EMPTY_CELL) {
			td
					.setAttribute(HTML.ATTR_CLASS,
							"dr-dscr-button"); //$NON-NLS-1$
		} else {
			td
					.setAttribute(HTML.ATTR_CLASS,
							"dr-dscr-button"); //$NON-NLS-1$
			td.appendChild(d);
		}
		return td;
	}

	/**
	 * Method for remove attributes .
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,		Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode,Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument,	visualNode, data, name);
		nsIDOMElement element = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		element.removeAttribute(name);
	}
	

	/*
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name,	String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		nsIDOMElement element = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		int size = 45;
		if (name.equalsIgnoreCase(RichFaces.ATTR_STYLE)) {
			String str = getWidth(value);
			size = getSize(str);
			int minSize = getSize(getWidth(MIN_STYLE_WIDTH));
			if (size < minSize) {
				size = minSize;
				value = MIN_STYLE_WIDTH;
			}
			size /= (minSize / MIN_NUM_CELLS);
			nsIDOMElement tr = getTR(element);
			nsIDOMNodeList nodes = tr.getChildNodes();
			long nodesLength = nodes.getLength();
			if (nodesLength != size) {
				if (size < nodesLength) {
					// Remove cells in datascroller
					for (int i = size; i < nodesLength; i++) {
						tr.removeChild(nodes.item(size - 3));
					}
				} else {
					// Remove cells in datascroller 
					for (int i = 0; i < 3; i++) {
						tr.removeChild(nodes.item(nodesLength - 3));
					}
					// Add cells in datascroller 
					for (int i = 0; i < (size - nodesLength); i++) {
						nsIDOMElement cell = createCell(visualDocument, false,Constants.EMPTY + (nodesLength - 5 + i), NUM_CELL);
						tr.appendChild(cell);
					}
					nsIDOMElement child1 = createCell(visualDocument, false, Constants.EMPTY, EMPTY_CELL);
					nsIDOMElement child2 = createCell(visualDocument, false,RIGHT_SINGLE_SCROLL_SYMBOL, SCROLL_CELL);
					nsIDOMElement child3 = createCell(visualDocument, false, RIGHT_DOUBLE_SCROLL_SYMBOL, SCROLL_CELL);
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
		String[] sub = style.split(Constants.SEMICOLON);
		for (int i = 0; i < sub.length; i++) {
			sub[i] = sub[i].trim();
			sub[i] = sub[i].toLowerCase();
			int pos = sub[i].indexOf(HTML.STYLE_PARAMETER_WIDTH);
			if (pos != -1) {
				if (pos == 0 || sub[i].charAt(pos - 1) != '-') {
					pos = sub[i].indexOf(Constants.COLON);
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
		String num = size;
		int pos = size.indexOf(Constants.PIXEL);
		if (pos != -1) {
			num = size.substring(0, pos);
		}
		try {
			num = num.trim();
			Integer i = Integer.valueOf(num);
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
	private nsIDOMElement getTR(nsIDOMElement parent) {
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tableNode = list.item(0);
		nsIDOMElement table = (nsIDOMElement) tableNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		nsIDOMNodeList tableList = table.getChildNodes();
		nsIDOMNode tbodyNode = tableList.item(0);
		nsIDOMElement tbody = (nsIDOMElement) tbodyNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		nsIDOMNodeList tbodyList = tbody.getChildNodes();
		nsIDOMNode tempNode = tbodyList.item(0);
		nsIDOMElement returnElement = (nsIDOMElement) tempNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			
		return returnElement;
	}
}