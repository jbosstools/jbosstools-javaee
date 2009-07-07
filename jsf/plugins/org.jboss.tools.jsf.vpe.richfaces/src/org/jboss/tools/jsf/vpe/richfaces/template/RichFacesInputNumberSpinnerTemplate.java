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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.NodeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
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
	final static private String DEFAULT_INPUT_SIZE = "10px"; //$NON-NLS-1$
	final static private String DEFAULT_ZERO_SIZE = "0px"; //$NON-NLS-1$
	final static private String DEFAULT_WIDTH = "1%"; //$NON-NLS-1$
	final static private String DEFAULT_CONTAINER_WRAPPER_WIDTH = "2%"; //$NON-NLS-1$

	/* Default and RichFaces styles */
	/** DEFAULT_CONTAINER_STYLE */
	final static private String DEFAULT_CONTAINER_STYLE = "dr-spnr-c"; //$NON-NLS-1$

	/** DEFAULT_INPUT_STYLE */
	final static private String DEFAULT_INPUT_STYLE = "dr-spnr-i"; //$NON-NLS-1$

	/** DEFAULT_INPUT_CONTAINER_STYLE */
	final static private String DEFAULT_INPUT_CONTAINER_STYLE = "dr-spnr-e"; //$NON-NLS-1$

	/** DEFAULT_BUTTONS_STYLE */
	final static private String DEFAULT_BUTTONS_STYLE = "dr-spnr-b"; //$NON-NLS-1$

	/** DEFAULT_BUTTON_STYLE */
	final static private String DEFAULT_BUTTON_STYLE = "dr-spnr-bn"; //$NON-NLS-1$

	/** DISABLED_INPUT_STYLE applied for INPUT tag element in case of DISABLING */
	final static private String DISABLED_INPUT_STYLE = "color:grey"; //$NON-NLS-1$

	/* RichFaces styles for component elements */
	/** RICH_SPINNER_C_STYLE */
	final static private String RICH_SPINNER_C_STYLE = "rich-spinner-c"; //$NON-NLS-1$

	/** RICH_SPINNER_INPUT_CONTAINER_STYLE */
	final static private String RICH_SPINNER_INPUT_CONTAINER_STYLE = "rich-spinner-input-container"; //$NON-NLS-1$

	/** RICH_SPINNER_INPUT_STYLE */
	final static private String RICH_SPINNER_INPUT_STYLE = "rich-spinner-input"; //$NON-NLS-1$

	/** RICH_SPINNER_BUTTON_STYLE */
	final static private String RICH_SPINNER_BUTTON_STYLE = "rich-spinner-button"; //$NON-NLS-1$

	/** RICH_SPINNER_BUTTONS_STYLE */
	final static private String RICH_SPINNER_BUTTONS_STYLE = "rich-spinner-buttons"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public RichFacesInputNumberSpinnerTemplate() {
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
		// Set a css for this element
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, "richFacesInputNumberSpinner"); //$NON-NLS-1$
		Element sourceElement = (Element) sourceNode;

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		table.setAttribute(HTML.ATTR_BORDER, DEFAULT_ZERO_SIZE);
		table.setAttribute(HTML.ATTR_CELLPADDING, DEFAULT_ZERO_SIZE);
		table.setAttribute(HTML.ATTR_CELLSPACING, DEFAULT_ZERO_SIZE);
		table.setAttribute(HTML.ATTR_WIDTH, DEFAULT_CONTAINER_WRAPPER_WIDTH);

		VpeElementData elementData = new VpeElementData();

		nsIDOMElement row = visualDocument.createElement(HTML.TAG_TR);

		// create input element
		nsIDOMElement cellInput = visualDocument.createElement(HTML.TAG_TD);
		cellInput.setAttribute(HTML.ATTR_CLASS, DEFAULT_INPUT_CONTAINER_STYLE + Constants.WHITE_SPACE
				+ RICH_SPINNER_INPUT_CONTAINER_STYLE);
		cellInput.setAttribute(HTML.ATTR_WIDTH, DEFAULT_WIDTH);
		cellInput.appendChild(createInputElement(visualDocument, sourceElement, elementData));
		row.appendChild(cellInput);

		// create arrows cell
		nsIDOMElement cellArrows = visualDocument.createElement(HTML.TAG_TD);
		cellArrows.setAttribute(HTML.ATTR_CLASS, DEFAULT_BUTTONS_STYLE + Constants.WHITE_SPACE
				+ RICH_SPINNER_BUTTONS_STYLE);
		cellArrows.setAttribute(HTML.ATTR_WIDTH, DEFAULT_WIDTH);
		cellArrows.appendChild(createArrowsElement(visualDocument, sourceNode));
		row.appendChild(cellArrows);

		table.appendChild(row);

		String tmp = getAttribute(sourceElement, RichFaces.ATTR_STYLE);
		if (!tmp.equals(Constants.EMPTY)) {
			table.setAttribute(HTML.ATTR_STYLE, tmp);
		}
		tmp = getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
		tmp = new StringBuffer(DEFAULT_CONTAINER_STYLE).append(Constants.WHITE_SPACE).
				append(RICH_SPINNER_C_STYLE).append(Constants.WHITE_SPACE).append(tmp).toString();
		table.setAttribute(HTML.ATTR_CLASS, tmp);

		/*
         * https://jira.jboss.org/jira/browse/JBIDE-3225
         * Component should render its children.
         */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, table, HTML.TAG_DIV, visualDocument);

		return creationData;
	}

	/**
	 * Create a HTML-part containing arrows elements
	 * 
	 * @param visualDocument
	 *            The current node of the source tree.
	 * @param sourceNode
	 *            The document of the visual tree.
	 * @return a HTML-part containing arrows elements
	 */
	private nsIDOMElement createArrowsElement(nsIDOMDocument visualDocument,
			Node sourceNode) {
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);

		table.setAttribute(HTML.ATTR_BORDER, DEFAULT_ZERO_SIZE);
		table.setAttribute(HTML.ATTR_CELLPADDING, DEFAULT_ZERO_SIZE);
		table.setAttribute(HTML.ATTR_CELLSPACING, DEFAULT_ZERO_SIZE);

		nsIDOMElement rowUp = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement cellUp = visualDocument.createElement(HTML.TAG_TD);

		nsIDOMElement imageUpElement = visualDocument.createElement(HTML.TAG_INPUT);

		ComponentUtil.setImg(imageUpElement, IMAGE_NAME_UP);

		imageUpElement.setAttribute(HTML.ATTR_BORDER, DEFAULT_ZERO_SIZE);
		imageUpElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_IMAGE);
		imageUpElement.setAttribute(HTML.ATTR_CLASS, DEFAULT_BUTTON_STYLE + Constants.WHITE_SPACE
				+ RICH_SPINNER_BUTTON_STYLE);

		cellUp.appendChild(imageUpElement);
		rowUp.appendChild(cellUp);
		table.appendChild(rowUp);

		nsIDOMElement rowDown = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement cellDown = visualDocument.createElement(HTML.TAG_TD);

		nsIDOMElement imageDownElement = visualDocument.createElement(HTML.TAG_INPUT);

		ComponentUtil.setImg(imageDownElement, IMAGE_NAME_DOWN);

		imageDownElement.setAttribute(HTML.ATTR_BORDER, DEFAULT_ZERO_SIZE);
		imageDownElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_IMAGE);
		imageDownElement.setAttribute(HTML.ATTR_CLASS, DEFAULT_BUTTON_STYLE + Constants.WHITE_SPACE
				+ RICH_SPINNER_BUTTON_STYLE);
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
		nsIDOMElement inputElement = visualDocument.createElement(HTML.TAG_INPUT);

		inputElement.setAttribute(HTML.ATTR_CLASS, getInputClass(sourceElement));
		String attrStyle = getInputStyle(sourceElement);
		if (!attrStyle.equals(Constants.EMPTY)) {
			inputElement.setAttribute(HTML.ATTR_STYLE, attrStyle);
		}
		inputElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
		inputElement.setAttribute(HTML.ATTR_SIZE, getInputSize(sourceElement));
		inputElement.setAttribute(HTML.ATTR_VALUE, getInputValue(sourceElement));

		if ((sourceElement).hasAttribute(RichFaces.ATTR_VALUE)) {
			elementData.addNodeData(new NodeData(sourceElement
					.getAttributeNode(RichFaces.ATTR_VALUE), inputElement, true));
		} else {
			elementData.addNodeData(new AttributeData(RichFaces.ATTR_VALUE, inputElement, true));
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
		String returnValue = getAttribute(sourceElement, RichFaces.ATTR_INPUT_STYLE);
		if ((sourceElement).hasAttribute(RichFaces.ATTR_DISABLED)) {
			String disabled = getAttribute(sourceElement, RichFaces.ATTR_DISABLED);
			if (disabled != null && disabled.equals(Constants.TRUE)) {
				returnValue = new StringBuffer().append(returnValue).append(Constants.SEMICOLON)
						.append(DISABLED_INPUT_STYLE).toString();
			}
		}

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
		String returnValue = DEFAULT_INPUT_SIZE;
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
		String returnValue = DEFAULT_INPUT_STYLE;
		// append default richfaces component style class
		returnValue += Constants.WHITE_SPACE + RICH_SPINNER_INPUT_STYLE;
		// append custom input style class
		String tmp = getAttribute(sourceElement, RichFaces.ATTR_INPUT_CLASS);
		if (tmp.length() != 0) {
			returnValue = new StringBuffer().append(returnValue).append(Constants.WHITE_SPACE).append(tmp).toString();
		}
		return returnValue;
	}

	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#recreateAtAttrChange(VpePageContext, Element,
	 * nsIDOMDocument, nsIDOMElement, Object, String, String)
	 */
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
