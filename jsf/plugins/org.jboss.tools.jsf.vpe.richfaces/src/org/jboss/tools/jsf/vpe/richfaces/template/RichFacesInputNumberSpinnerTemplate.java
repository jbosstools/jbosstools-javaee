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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.NodeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for input number spinner control
 */
public class RichFacesInputNumberSpinnerTemplate extends
/* AbstractRichFacesInputNumberTemplate */AbstractEditableRichFacesTemplate {

	/** CSS_FILE_NAME */
	final static private String CSS_FILE_NAME = "inputNumberSpinner/inputNumberSpinner.css"; //$NON-NLS-1$

	/** IMAGE_NAME_UP */
	final static private String IMAGE_NAME_UP = "/inputNumberSpinner/up.gif"; //$NON-NLS-1$

	/** IMAGE_NAME_DOWN */
	final static private String IMAGE_NAME_DOWN = "/inputNumberSpinner/down.gif"; //$NON-NLS-1$

	/** DEFAULT_INPUT_SIZE */
	final static private String DEFAULT_INPUT_SIZE = "10"; //$NON-NLS-1$

	/** DEFAULT_INPUT_STYLE */
	final static private String DEFAULT_INPUT_STYLE = "ins-input"; //$NON-NLS-1$

	final static private String ZERO_STRING = "0"; //$NON-NLS-1$

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

		// Set a css for this element
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME,
				"richFacesInputNumberSpinner"); //$NON-NLS-1$

		Element sourceElement = (Element) sourceNode;

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		table.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
		table.setAttribute(HTML.ATTR_CELLPADDING, ZERO_STRING);
		table.setAttribute(HTML.ATTR_CELLSPACING, ZERO_STRING);

		VpeElementData elementData = new VpeElementData();

		nsIDOMElement row = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// create input element
		nsIDOMElement cellInput = visualDocument.createElement(HTML.TAG_TD);
		cellInput.setAttribute(HTML.ATTR_CLASS, "ins-dr-spnr-e"); //$NON-NLS-1$
		cellInput.setAttribute(HTML.ATTR_VALIGN, HTML.VALUE_TOP_ALIGN);
		cellInput.appendChild(createInputElement(visualDocument, sourceElement,
				elementData));
		row.appendChild(cellInput);

		// create arrows cell
		nsIDOMElement cellArrows = visualDocument.createElement(HTML.TAG_TD);
		cellArrows.setAttribute(HTML.ATTR_CLASS, "dr-spnr-b"); //$NON-NLS-1$
		cellArrows.setAttribute(HTML.ATTR_VALIGN, HTML.VALUE_MIDDLE_ALIGN);
		cellArrows.appendChild(createArrowsElement(visualDocument, sourceNode));
		row.appendChild(cellArrows);

		table.appendChild(row);

		String tmp = getAttribute(sourceElement, RichFaces.ATTR_STYLE);
		table.setAttribute(HTML.ATTR_STYLE, tmp);

		// Create return variable contain template
		VpeCreationData creationData = new VpeCreationData(table);

		creationData.setElementData(elementData);

		return creationData;
	}

	/**
	 * Create a HTML-part containg arrows elements
	 * 
	 * @param visualDocument
	 *            The current node of the source tree.
	 * @param sourceNode
	 *            The document of the visual tree.
	 * @return a HTML-part containg arrows elements
	 */
	private nsIDOMElement createArrowsElement(nsIDOMDocument visualDocument,
			Node sourceNode) {
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);

		table.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
		table.setAttribute(HTML.ATTR_CELLPADDING, ZERO_STRING);
		table.setAttribute(HTML.ATTR_CELLSPACING, ZERO_STRING);

		nsIDOMElement rowUp = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement cellUp = visualDocument.createElement(HTML.TAG_TD);

		nsIDOMElement imageUpElement = visualDocument
				.createElement(HTML.TAG_INPUT);

		ComponentUtil.setImg(imageUpElement, IMAGE_NAME_UP);

		imageUpElement.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
		imageUpElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_IMAGE_TYPE);
		imageUpElement.setAttribute(HTML.ATTR_CLASS, "dr-spnr-bn"); //$NON-NLS-1$

		cellUp.appendChild(imageUpElement);
		rowUp.appendChild(cellUp);
		table.appendChild(rowUp);

		nsIDOMElement rowDown = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement cellDown = visualDocument.createElement(HTML.TAG_TD);

		nsIDOMElement imageDownElement = visualDocument
				.createElement(HTML.TAG_INPUT);

		ComponentUtil.setImg(imageDownElement, IMAGE_NAME_DOWN);

		imageDownElement.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
		imageDownElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_IMAGE_TYPE);
		imageDownElement.setAttribute(HTML.ATTR_CLASS, "dr-spnr-bn"); //$NON-NLS-1$
		cellDown.appendChild(imageDownElement);
		rowDown.appendChild(cellDown);
		table.appendChild(rowDown);

		return table;
	}

	/**
	 * Create a HTML-part containg input element
	 * 
	 * @param visualDocument
	 *            The current node of the source tree.
	 * @param sourceNode
	 *            The document of the visual tree.
	 * @param elementData
	 * @return a HTML-part containg input element
	 */
	private nsIDOMElement createInputElement(nsIDOMDocument visualDocument,
			Element sourceElement, VpeElementData elementData) {
		nsIDOMElement inputElement = visualDocument
				.createElement(HTML.TAG_INPUT);

		inputElement
				.setAttribute(HTML.ATTR_CLASS, getInputClass(sourceElement));

		inputElement
				.setAttribute(HTML.ATTR_STYLE, getInputStyle(sourceElement));

		inputElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE);

		inputElement.setAttribute(HTML.ATTR_SIZE, getInputSize(sourceElement));
		inputElement
				.setAttribute(HTML.ATTR_VALUE, getInputValue(sourceElement));

		if ((sourceElement).hasAttribute(RichFaces.ATTR_VALUE)) {
			elementData.addNodeData(new NodeData(sourceElement
					.getAttributeNode(RichFaces.ATTR_VALUE), inputElement, true));
		} else {
			elementData.addNodeData(new AttributeData(
					RichFaces.ATTR_VALUE, inputElement, true));
		}

		return inputElement;
	}

	/**
	 * Return a input value
	 * 
	 * @param sourceNode
	 *            a sourceNode
	 * @return a input value
	 */
	private String getInputValue(Element sourceElement) {
		String returnValue = getAttribute(sourceElement, RichFaces.ATTR_VALUE);
		return returnValue;
	}

	/**
	 * Return a input style
	 * 
	 * @param sourceNode
	 *            a sourceNode
	 * @return a input style
	 */
	private String getInputStyle(Element sourceElement) {
		String returnValue = getAttribute(sourceElement,
				RichFaces.ATTR_INPUT_STYLE);
		return returnValue;
	}

	/**
	 * Return a input size
	 * 
	 * @param sourceNode
	 *            a sourceNode
	 * @return a input size
	 */
	protected String getInputSize(Element sourceElement) {
		String returnValue = getDefaultInputSize();
		String tmp = getAttribute(sourceElement, RichFaces.ATTR_INPUT_SIZE);
		if (tmp.length() != 0) {
			returnValue = tmp;
		}
		return returnValue;
	}

	/**
	 * Return a input class
	 * 
	 * @param sourceNode
	 *            a sourceNode
	 * @return a input class
	 */
	public String getInputClass(Element sourceElement) {
		String returnValue = getDefaultInputClass();
		String tmp = getAttribute(sourceElement, RichFaces.ATTR_INPUT_CLASS);
		if (tmp.length() != 0) {
			returnValue = new StringBuffer().append(returnValue).append(" ") //$NON-NLS-1$
					.append(tmp).toString();
		}
		return returnValue;
	}

	/**
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
			String name, String value) {
		// 1. Call super method
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);

		nsIDOMElement table = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMNodeList listTable = table.getChildNodes();
		nsIDOMNode nodeTr = listTable.item(0);
		nsIDOMNodeList listTr = nodeTr.getChildNodes();
		nsIDOMNode nodeTd = listTr.item(0);

		nsIDOMNodeList listTd = nodeTd.getChildNodes();
		nsIDOMNode entry0 = listTd.item(0);

		nsIDOMElement inputElement = (nsIDOMElement) entry0
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		inputElement
				.setAttribute(HTML.ATTR_CLASS, getInputClass(sourceElement));

		inputElement
				.setAttribute(HTML.ATTR_STYLE, getInputStyle(sourceElement));

		inputElement.setAttribute(HTML.ATTR_SIZE, getInputSize(sourceElement));
		inputElement
				.setAttribute(HTML.ATTR_VALUE, getInputValue(sourceElement));

		// 3. Set a style for main container
		String strStyle = getAttribute(sourceElement, RichFaces.ATTR_STYLE);
		table.setAttribute(HTML.ATTR_STYLE, strStyle);
	}

	public String getDefaultInputSize() {
		return DEFAULT_INPUT_SIZE;
	}

	public String getDefaultInputClass() {
		return DEFAULT_INPUT_STYLE;
	}

}
