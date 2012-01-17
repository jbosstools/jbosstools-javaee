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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces DataTableFilterSlider
 */
public class RichFacesDataFilterSliderTemplate extends AbstractRichFacesTemplate {

	/** path to CSS file */
	static final String STYLE_PATH = "/dataFilterSlider/dataFilterSlider.css"; //$NON-NLS-1$

	static final String CENTER_SLIDER = "/dataFilterSlider/pos.gif"; //$NON-NLS-1$

	private static final int DEFAULT_WIDTH = 200;
	private static final int ZERO = 0;
	private static final int TRAILER_LEFT_OFFSET = -38;

	private static final String DEFAULT_SLIDER_WIDTH = "7px"; //$NON-NLS-1$
	private static final String DEFAULT_SLIDER_HEIGHT = "8px"; //$NON-NLS-1$
	private static final String DEFAULT_SLIDER_BORDER = "0px"; //$NON-NLS-1$

	/* Default and RichFaces styles */
	/** DEFAULT_SLIDER_CONTAINER_STYLE */
	final static private String DEFAULT_SLIDER_CONTAINER_STYLE = "slider-container"; //$NON-NLS-1$

	/** DEFAULT_RANGE_STYLE */
	final static private String DEFAULT_RANGE_STYLE = "range"; //$NON-NLS-1$

	/** DEFAULT_RANGE_DECOR_STYLE */
	final static private String DEFAULT_RANGE_DECOR_STYLE = "range-decor"; //$NON-NLS-1$

	/** DEFAULT_TRAILER_STYLE */
	final static private String DEFAULT_TRAILER_STYLE = "trailer"; //$NON-NLS-1$

	/** DEFAULT_TRACK_STYLE */
	final static private String DEFAULT_TRACK_STYLE = "track"; //$NON-NLS-1$

	/** DEFAULT_HANDLE_STYLE */
	final static private String DEFAULT_HANDLE_STYLE = "handle"; //$NON-NLS-1$

	/** DEFAULT_HANDLE_STYLE */
	final static private String DEFAULT_SLIDER_INPUT_FIELD_STYLE = "slider-input-field"; //$NON-NLS-1$

	/** RICH_DFS_CONTAINER_STYLE */
	final static private String RICH_DFS_CONTAINER_STYLE = "rich-dataFilterSlider-container"; //$NON-NLS-1$

	/** RICH_DFS_RANGE_STYLE */
	final static private String RICH_DFS_RANGE_STYLE = "rich-dataFilterSlider-range"; //$NON-NLS-1$

	/** RICH_DFS_RANGE_DECOR_STYLE */
	final static private String RICH_DFS_RANGE_DECOR_STYLE = "rich-dataFilterSlider-range-decor"; //$NON-NLS-1$

	/** RICH_DFS_TRAILER_STYLE */
	final static private String RICH_DFS_TRAILER_STYLE = "rich-dataFilterSlider-trailer"; //$NON-NLS-1$

	/** RICH_DFS_TRACK_STYLE */
	final static private String RICH_DFS_TRACK_STYLE = "rich-dataFilterSlider-track"; //$NON-NLS-1$

	/** RICH_DFS_HANDLE_STYLE */
	final static private String RICH_DFS_HANDLE_STYLE = "rich-dataFilterSlider-handle"; //$NON-NLS-1$

	/** RICH_DFS_INPUT_FIELD_STYLE */
	final static private String RICH_DFS_INPUT_FIELD_STYLE = "rich-dataFilterSlider-input-field"; //$NON-NLS-1$

	/** Component style attributes */
	final static String RANGE_STYLE_CLASS_ATTR = "rangeStyleClass"; //$NON-NLS-1$
	final static String FIELD_STYLE_CLASS_ATTR = "fieldStyleClass"; //$NON-NLS-1$
	final static String HANDLE_STYLE_CLASS_ATTR = "handleStyleClass"; //$NON-NLS-1$
	final static String TRACK_STYLE_CLASS_ATTR = "trackStyleClass"; //$NON-NLS-1$
	final static String TRAILER_STYLE_CLASS_ATTR = "trailerStyleClass"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public RichFacesDataFilterSliderTemplate() {
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
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "richFacesDataFilterSlider"); //$NON-NLS-1$

		Element sourceElement = (Element) sourceNode;

		// create high level container DIV tag element
		String style = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE);
		String styleClass = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
		styleClass = DEFAULT_SLIDER_CONTAINER_STYLE + Constants.WHITE_SPACE + RICH_DFS_CONTAINER_STYLE + Constants.WHITE_SPACE + styleClass;
		nsIDOMElement parentDiv = createDIV(visualDocument, styleClass, style);

		// create RANGE container DIV tag element
		String width = ComponentUtil.getAttribute(sourceElement, HTML.STYLE_PARAMETER_WIDTH);
		if (width == null || width.equals(Constants.EMPTY)) {
			width = new Integer(DEFAULT_WIDTH).toString();
		}
		style = HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + width + Constants.PIXEL + Constants.SEMICOLON;
		styleClass = ComponentUtil.getAttribute(sourceElement, RANGE_STYLE_CLASS_ATTR);
		styleClass = DEFAULT_RANGE_STYLE + Constants.WHITE_SPACE
						+ RICH_DFS_RANGE_STYLE + Constants.WHITE_SPACE + styleClass;
		nsIDOMElement rangeDiv = createDIV(visualDocument, styleClass, style);

		// create RANGE-DECOR container DIV tag element
		styleClass = DEFAULT_RANGE_DECOR_STYLE + Constants.WHITE_SPACE
				+ RICH_DFS_RANGE_DECOR_STYLE;
		nsIDOMElement rangeDecorDiv = createDIV(visualDocument, styleClass, null);

		// create TRAILER container DIV tag element
		style = HTML.STYLE_PARAMETER_LEFT + Constants.COLON + TRAILER_LEFT_OFFSET + Constants.PIXEL + Constants.SEMICOLON;
		styleClass = ComponentUtil.getAttribute(sourceElement, TRAILER_STYLE_CLASS_ATTR);
		styleClass = DEFAULT_TRAILER_STYLE + Constants.WHITE_SPACE
					+ RICH_DFS_TRAILER_STYLE + Constants.WHITE_SPACE +styleClass;
		nsIDOMElement trailerDiv = createDIV(visualDocument, styleClass, style);

		// create TRACK container DIV tag element
		style = HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + DEFAULT_WIDTH + Constants.PIXEL + Constants.SEMICOLON;
		
		styleClass = DEFAULT_TRACK_STYLE 
				+ Constants.WHITE_SPACE 
				+ RICH_DFS_TRACK_STYLE 
				+ Constants.WHITE_SPACE 
				+ ComponentUtil.getAttribute(sourceElement, TRACK_STYLE_CLASS_ATTR);
		nsIDOMElement trackDiv = createDIV(visualDocument, styleClass, style);

		// create HANDLE container DIV tag element
		style = HTML.STYLE_PARAMETER_LEFT + Constants.COLON + ZERO + Constants.PIXEL + Constants.SEMICOLON;
		styleClass = DEFAULT_HANDLE_STYLE 
				+ Constants.WHITE_SPACE 
				+ RICH_DFS_HANDLE_STYLE 
				+ Constants.WHITE_SPACE 
				+ ComponentUtil.getAttribute(sourceElement, HANDLE_STYLE_CLASS_ATTR);
		nsIDOMElement handleDiv = createDIV(visualDocument, styleClass, style);

		// create element that represents trailer element
		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(img, CENTER_SLIDER);
		img.setAttribute(HTML.ATTR_WIDTH, DEFAULT_SLIDER_WIDTH);
		img.setAttribute(HTML.ATTR_BORDER, DEFAULT_SLIDER_BORDER);
		img.setAttribute(HTML.ATTR_HEIGHT, DEFAULT_SLIDER_HEIGHT);

		// create INPUT tag element
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
		setAttributesToInputElement(input, sourceElement);

		// create BR tag element
		nsIDOMElement br = visualDocument.createElement(HTML.TAG_BR);
		style = HTML.STYLE_PARAMETER_CLEAR + Constants.COLON + HTML.VALUE_CLEAR_BOTH + Constants.SEMICOLON;
		br.setAttribute(HTML.ATTR_STYLE, style);

		// create DOM tree in correspondence order
		parentDiv.appendChild(rangeDiv);
		rangeDiv.appendChild(rangeDecorDiv);
		rangeDecorDiv.appendChild(trailerDiv);
		trailerDiv.appendChild(trackDiv);
		trackDiv.appendChild(handleDiv);
		handleDiv.appendChild(img);

		parentDiv.appendChild(input);
		parentDiv.appendChild(br);

		// Create return variable contains template
		VpeCreationData creationData = new VpeCreationData(parentDiv);
		return creationData;
	}

	/**
	 * Method for create DIV tag and set attributes
	 *
	 * @param visualDocument nsIDOMDocument value
	 * @param styleClass String value
	 * @param style String value
	 */
	private nsIDOMElement createDIV(nsIDOMDocument visualDocument, String styleClass, String style) {
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		if (styleClass != null && !styleClass.equals(Constants.EMPTY)) {
			div.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		if (style != null && !style.equals(Constants.EMPTY)) {
			div.setAttribute(HTML.ATTR_STYLE, style);
		}
		return div;
	}

	/**
	 * Set attributes for INPUT element
	 *
	 * @param inputElement nsIDOMElement object
	 * @param sourceElement Element object 
	 */
	private void setAttributesToInputElement(nsIDOMElement inputElement, Element sourceElement) {
		String styleClass = ComponentUtil.getAttribute(sourceElement, FIELD_STYLE_CLASS_ATTR);
		styleClass = DEFAULT_SLIDER_INPUT_FIELD_STYLE + Constants.WHITE_SPACE + RICH_DFS_INPUT_FIELD_STYLE + Constants.WHITE_SPACE + styleClass;
		String value = getAttribute("handleValue", sourceElement); //$NON-NLS-1$
		inputElement.setAttribute(HTML.ATTR_CLASS, styleClass);
		inputElement.setAttribute(HTML.ATTR_VALUE, value);
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
