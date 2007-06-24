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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Template for input number spinner control
 */
public class RichFacesInputNumberSpinnerTemplate extends AbstractRichFacesInputNumberTemplate {

	/** CSS_FILE_NAME */
	final static private String CSS_FILE_NAME = "inputNumberSpinner/inputNumberSpinner.css";

	/** IMAGE_NAME_UP */
	final static private String IMAGE_NAME_UP = "/inputNumberSpinner/up.gif";

	/** IMAGE_NAME_DOWN */
	final static private String IMAGE_NAME_DOWN = "/inputNumberSpinner/down.gif";

	/** DEFAULT_INPUT_SIZE */
	final static private String DEFAULT_INPUT_SIZE = "10";

	/** DEFAULT_INPUT_STYLE */
	final static private String DEFAULT_INPUT_STYLE = "ins-input";


	/** INPUTSTYLE_ATTRIBURE */
	final static private String INPUTSTYLE_ATTRIBURE = "inputStyle";

	/** INPUTVALUE_ATTRIBURE */
	final static private String INPUTVALUE_ATTRIBURE = "value";
	

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

		// Set a css for this element
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME,
				"richFacesInputNumberSpinner");

		Element table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0px");
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
		
		Element row = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// create input element		
		Element cellInput = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);		
		cellInput.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "ins-dr-spnr-e");
		cellInput.setAttribute("valign", "top");
		cellInput.appendChild(createInputElement(visualDocument, sourceNode));
		row.appendChild(cellInput);
		

		// create arrows cell 
		Element cellArrows = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		cellArrows.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-spnr-b");
		cellArrows.setAttribute("valign", "middle");
		cellArrows.appendChild(createArrowsElement(visualDocument, sourceNode));
		row.appendChild(cellArrows);

		table.appendChild(row);

		String tmp = getAttribute("style", sourceNode);
		table.setAttribute("style", tmp);

		// Create return variable contain template
		VpeCreationData creationData = new VpeCreationData(table);

		return creationData;
	}

	/**
	 * Create a HTML-part containg arrows elements  
	 * @param visualDocument The current node of the source tree.
	 * @param sourceNode The document of the visual tree.
	 * @return a HTML-part containg arrows elements
	 */
	private Element createArrowsElement(Document visualDocument, Node sourceNode) {
		Element table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
		
		Element rowUp = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		Element cellUp = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		
		Element imageUpElement = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
		
		ComponentUtil.setImg(imageUpElement, IMAGE_NAME_UP);

		imageUpElement.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
		imageUpElement.setAttribute("type", "image");
		imageUpElement.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-spnr-bn");		

		cellUp.appendChild(imageUpElement);
		rowUp.appendChild(cellUp);
		table.appendChild(rowUp);		

		Element rowDown = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		Element cellDown = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		Element imageDownElement = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);

		ComponentUtil.setImg(imageDownElement, IMAGE_NAME_DOWN);

		imageDownElement.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
		imageDownElement.setAttribute("type", "image");
		imageDownElement.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-spnr-bn");
		cellDown.appendChild(imageDownElement);
		rowDown.appendChild(cellDown);
		table.appendChild(rowDown);	

		return table;
	}
	
	
	/**
	 * Create a HTML-part containg input element
	 * @param visualDocument The current node of the source tree.
	 * @param sourceNode The document of the visual tree.
	 * @return a HTML-part containg input element
	 */
	private Element createInputElement(Document visualDocument, Node sourceNode) {
		Element inputElement = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);

		inputElement.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				getInputClass(sourceNode));

		inputElement.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				getInputStyle(sourceNode));

		inputElement.setAttribute("type", "text");
		
		inputElement.setAttribute("size", getInputSize(sourceNode));
		inputElement.setAttribute("value", getInputValue(sourceNode));

		return inputElement;
	}
	
	
	/** 
	 * Return a input value
	 * @param sourceNode a sourceNode
	 * @return a input value
	 */
	private String getInputValue(Node sourceNode) {
		String returnValue = getAttribute(INPUTVALUE_ATTRIBURE, sourceNode); 
		return returnValue;
	}	

	/** 
	 * Return a input style
	 * @param sourceNode a sourceNode
	 * @return a input style
	 */
	private String getInputStyle(Node sourceNode) {
		String returnValue = getAttribute(INPUTSTYLE_ATTRIBURE, sourceNode); 
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
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		// 1. Call super method
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);

		Element table = (Element) visualNode;
		NodeList listTable = table.getChildNodes();
		Node nodeTr = listTable.item(0);
		NodeList listTr = nodeTr.getChildNodes();
		Node nodeTd = listTr.item(0);
		
		NodeList listTd = nodeTd.getChildNodes();
	
		Element inputElement = (Element) listTd.item(0);

		inputElement.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				getInputClass(sourceElement));

		inputElement.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				getInputStyle(sourceElement));
		
		inputElement.setAttribute(HtmlComponentUtil.HTML_SIZE_ATTR, getInputSize(sourceElement));
		inputElement.setAttribute("value", getInputValue(sourceElement));

		// 3. Set a style for main container
		String strStyle =  getAttribute("style", sourceElement);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, strStyle);
	}

	
	
	public String getDefaultInputSize() {
		return DEFAULT_INPUT_SIZE;
	}
	
	public String getDefaultInputClass() {
		return DEFAULT_INPUT_STYLE;
	}

}
