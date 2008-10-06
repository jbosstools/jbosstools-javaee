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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for Rich Faces DataTableFilterSlider
 * 
 */
public class RichFacesDataFilterSliderTemplate extends AbstractRichFacesTemplate {

	/** IMAGE_SPACER */
	final static String IMAGE_SPACER = "/common/spacer.gif"; //$NON-NLS-1$
	
	final static String CENTER_SLIDER = "/dataFilterSlider/pos.gif"; //$NON-NLS-1$

	final static String STYLE_PATH = "/dataFilterSlider/dataFilterSlider.css"; //$NON-NLS-1$

	final static int DEFAULT_WIDTH = 260;

	/* intdent between slider and right border */
	final static int DEFAULT_PARAGRAPH = 60;

	final static int DEFAULT_HEIGHT = 20;

	final static String DEFAULT_SLIDER_POSITION = "left: -38px; width: 114px;"; //$NON-NLS-1$

	final static String DEFAULT_SLIDER_WIDTH = "7px"; //$NON-NLS-1$

	final static String DEFAULT_SLIDER_HEIGHT = "8px"; //$NON-NLS-1$

	final static String DEFAULT_SLIDER_BORDER = "0px"; //$NON-NLS-1$

	final static String FIELD_STYLE_CLASS_ATR = "fieldStyleClass"; //$NON-NLS-1$

	final static String HANDLE_STYLE_CLASS_ATR = "handleStyleClass"; //$NON-NLS-1$

	final static String RANGE_STYLE_CLASS_ATR = "rangeStyleClass"; //$NON-NLS-1$

	final static String TRACK_STYLE_CLASS_ATR = "trackStyleClass"; //$NON-NLS-1$

	final static String TRAILER_STYLE_CLASS_ATR = "trailerStyleClass"; //$NON-NLS-1$

	/**
	 * Constructor.
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
		int numWidth = 0;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "richFacesDataFilterSlider"); //$NON-NLS-1$
		Element sourceElement = (Element) sourceNode;
		String style = ComponentUtil.getAttribute(sourceElement,
				RichFaces.ATTR_STYLE);
		String width = sourceElement.getAttribute(RichFaces.ATTR_WIDTH);
		if (width != null) {
			numWidth = getSize(width);
			if (numWidth < DEFAULT_WIDTH) {
				numWidth = DEFAULT_WIDTH;
			}
		} else {
			numWidth = DEFAULT_WIDTH;
		}
		String defaultStyle = style + Constants.SEMICOLON + HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + numWidth + "px ; "; //$NON-NLS-1$
		nsIDOMElement parentDiv = createDIV(visualDocument, "slider-container",	defaultStyle); //$NON-NLS-1$
		String rangeStyleClass = ComponentUtil.getAttribute(sourceElement,	RANGE_STYLE_CLASS_ATR);
		nsIDOMElement rangeDiv = createDIV(visualDocument, "range " + rangeStyleClass,  //$NON-NLS-1$
				HTML.STYLE_PARAMETER_WIDTH	+ Constants.COLON + (numWidth - DEFAULT_PARAGRAPH) + "px;"); //$NON-NLS-1$
		nsIDOMElement rangeDecorDiv = createDIV(visualDocument, "range-decor", null); //$NON-NLS-1$
		String trailerStyleClass = ComponentUtil.getAttribute(sourceElement,
				TRAILER_STYLE_CLASS_ATR);
		nsIDOMElement trailerDiv = createDIV(visualDocument, "trailer " //$NON-NLS-1$
				+ trailerStyleClass, DEFAULT_SLIDER_POSITION);
		String trackStyleClass = ComponentUtil.getAttribute(sourceElement,
				TRACK_STYLE_CLASS_ATR);
		nsIDOMElement trackDiv = createDIV(visualDocument,
				"track " + trackStyleClass, HTML.STYLE_PARAMETER_WIDTH //$NON-NLS-1$
						+ Constants.COLON + (numWidth - DEFAULT_PARAGRAPH) + "px;"); //$NON-NLS-1$

		String handleStyleClass = ComponentUtil.getAttribute(sourceElement,HANDLE_STYLE_CLASS_ATR);
		nsIDOMElement handleDiv = createDIV(visualDocument, "handle " + handleStyleClass, null); //$NON-NLS-1$

		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(img, CENTER_SLIDER);
		img.setAttribute(HTML.ATTR_WIDTH,	DEFAULT_SLIDER_WIDTH);
		img.setAttribute(HTML.ATTR_BORDER,DEFAULT_SLIDER_BORDER);
		img.setAttribute(HTML.ATTR_HEIGHT,	DEFAULT_SLIDER_HEIGHT);

		/* Set input component */
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE);
		
		setAttributesToInputElement(input, sourceElement);


		parentDiv.appendChild(rangeDiv);
		rangeDiv.appendChild(rangeDecorDiv);
		rangeDecorDiv.appendChild(trailerDiv);
		trailerDiv.appendChild(trackDiv);
		trackDiv.appendChild(handleDiv);
		handleDiv.appendChild(img);
		
		nsIDOMElement tableSpacer2 = visualDocument.createElement(HTML.TAG_TABLE);
		tableSpacer2.setAttribute(HTML.ATTR_CELLSPACING, "0px"); //$NON-NLS-1$
		tableSpacer2.setAttribute(HTML.ATTR_CELLPADDING, "0px"); //$NON-NLS-1$
		tableSpacer2.setAttribute(HTML.ATTR_BORDER, "0px");		 //$NON-NLS-1$
		tableSpacer2.setAttribute(HTML.ATTR_WIDTH, "100%" ); //$NON-NLS-1$
		tableSpacer2.setAttribute(HTML.ATTR_HEIGHT, "100%" ); //$NON-NLS-1$
		
		nsIDOMElement trSpacer2 =  visualDocument.createElement(HTML.TAG_TR);
		
		nsIDOMElement tdSpacer2 = visualDocument.createElement(HTML.TAG_TD);
		tdSpacer2.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_RIGHT_ALIGN);
		tdSpacer2.setAttribute(HTML.ATTR_STYLE,"font-family: Arial, Verdana, sans-serif; font-size: 5px; color: white;"); //$NON-NLS-1$
		trSpacer2.appendChild(tdSpacer2);

		nsIDOMElement imageSpacer2 = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(imageSpacer2, IMAGE_SPACER);
		imageSpacer2.setAttribute(HTML.ATTR_WIDTH, "100%"); //$NON-NLS-1$
		imageSpacer2.setAttribute(HTML.ATTR_HEIGHT, "100%"); //$NON-NLS-1$
		tdSpacer2.appendChild(imageSpacer2);
		
		tableSpacer2.appendChild(trSpacer2);
		trackDiv.appendChild(tableSpacer2);
		
		parentDiv.appendChild(input);

		VpeCreationData creationData = new VpeCreationData(parentDiv);
		return creationData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#removeAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String)
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode,
			Object data, String name) {
		int numWidth = 0;
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
		
		nsIDOMElement element = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		nsIDOMElement input = getInputElement(element);
		setAttributesToInputElement(input, sourceElement);

		if (name.equalsIgnoreCase(RANGE_STYLE_CLASS_ATR)) {
			nsIDOMElement range = getRangeElement(element);
			range.removeAttribute(HTML.ATTR_CLASS);
			range.setAttribute(HTML.ATTR_CLASS, "range"); //$NON-NLS-1$
		} else if (name.equalsIgnoreCase(TRAILER_STYLE_CLASS_ATR)) {
			nsIDOMElement trailer = getTrailerElement(element);
			trailer.removeAttribute(HTML.ATTR_CLASS);
			trailer.setAttribute(HTML.ATTR_CLASS, "trailer"); //$NON-NLS-1$
		} else if (name.equalsIgnoreCase(TRACK_STYLE_CLASS_ATR)) {
			nsIDOMElement track = getTrackElement(element);
			track.removeAttribute(HTML.ATTR_CLASS);
			track.setAttribute(HTML.ATTR_CLASS, "track"); //$NON-NLS-1$
		} else if (name.equalsIgnoreCase(HANDLE_STYLE_CLASS_ATR)) {
			nsIDOMElement handle = getHandleElement(element);
			handle.removeAttribute(HTML.ATTR_CLASS);
			handle.setAttribute(HTML.ATTR_CLASS, "handle"); //$NON-NLS-1$
		} else if (name.equalsIgnoreCase(FIELD_STYLE_CLASS_ATR)) {
			nsIDOMElement field = getInputElement(element);
			setAttributesToInputElement( field, sourceElement );
		} else if (name.equalsIgnoreCase(RichFaces.ATTR_WIDTH)) {
			nsIDOMElement range = getRangeElement(element);
			String style = ComponentUtil.getAttribute(sourceElement,
					RichFaces.ATTR_STYLE);
			element.setAttribute(HTML.ATTR_STYLE, style + Constants.SEMICOLON
					+ HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + DEFAULT_WIDTH
					+ "px ; "); //$NON-NLS-1$
			String rangeStyle = ComponentUtil.getAttribute(range,
					HTML.ATTR_STYLE);
			range.setAttribute(HTML.ATTR_STYLE, rangeStyle
					+ Constants.SEMICOLON  + HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
					+ (DEFAULT_WIDTH - DEFAULT_PARAGRAPH) + "px;"); //$NON-NLS-1$
		} else if (name.equalsIgnoreCase(RichFaces.ATTR_STYLE)) {
			String width = sourceElement
					.getAttribute(RichFaces.ATTR_WIDTH);
			if (width != null) {
				numWidth = getSize(width);
				if (numWidth < DEFAULT_WIDTH) {
					numWidth = DEFAULT_WIDTH;
				}
			} else {
				numWidth = DEFAULT_WIDTH;
			}
			String style = HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + numWidth
					+ "px ; "; //$NON-NLS-1$
			element.setAttribute(HTML.ATTR_STYLE, style);
		} else {
			element.removeAttribute(name);
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name,
			String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument, visualNode, data, name, value);

		nsIDOMElement parentDiv = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		nsIDOMElement input = getInputElement(parentDiv);
		setAttributesToInputElement(input, sourceElement);

		
		int numWidth = 0;
		if (name.equalsIgnoreCase(RichFaces.ATTR_WIDTH)) {
			int size = getSize(value);
			if (size < DEFAULT_WIDTH) {
				size = DEFAULT_WIDTH;
			}
			nsIDOMElement rangeDiv = getRangeElement(parentDiv);
			nsIDOMElement trackDiv = getTrackElement(parentDiv);

			String style = ComponentUtil.getAttribute(parentDiv,
					HTML.ATTR_STYLE);
			style = style + HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + size
					+ Constants.PIXEL + Constants.SEMICOLON;
			parentDiv.setAttribute(HTML.ATTR_STYLE, style);
			String rangeStyle = ComponentUtil.getAttribute(rangeDiv,
					HTML.ATTR_STYLE);
			rangeDiv.setAttribute(HTML.ATTR_STYLE, rangeStyle
					+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
					+ (size - DEFAULT_PARAGRAPH) + "px;"); //$NON-NLS-1$
			String trackStyle = ComponentUtil.getAttribute(trackDiv,
					HTML.ATTR_STYLE);
			trackDiv.setAttribute(HTML.ATTR_STYLE, trackStyle
					+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_WIDTH+ Constants.COLON
					+ +(size - DEFAULT_PARAGRAPH) + "px;"); //$NON-NLS-1$

		} else if (name.equalsIgnoreCase(RANGE_STYLE_CLASS_ATR)) {
			nsIDOMElement range = getRangeElement(parentDiv);
			range.setAttribute(HTML.ATTR_CLASS, "range " //$NON-NLS-1$
					+ value);
		} else if (name.equalsIgnoreCase(TRACK_STYLE_CLASS_ATR)) {
			nsIDOMElement track = getTrackElement(parentDiv);
			track.setAttribute(HTML.ATTR_CLASS, "track " //$NON-NLS-1$
					+ value);
		} else if (name.equalsIgnoreCase(TRAILER_STYLE_CLASS_ATR)) {
			nsIDOMElement trailer = getTrailerElement(parentDiv);
			trailer.setAttribute(HTML.ATTR_CLASS, "trailer " //$NON-NLS-1$
					+ value);
		} else if (name.equalsIgnoreCase(HANDLE_STYLE_CLASS_ATR)) {
			nsIDOMElement handle = getHandleElement(parentDiv);
			handle.setAttribute(HTML.ATTR_CLASS, "handle " //$NON-NLS-1$
					+ value);
		} else if (name.equalsIgnoreCase(FIELD_STYLE_CLASS_ATR)) {
		} else if (name.equalsIgnoreCase(RichFaces.ATTR_STYLE)) {
			String width = sourceElement
					.getAttribute(RichFaces.ATTR_WIDTH);
			if (width != null) {
				numWidth = getSize(width);
				if (numWidth < DEFAULT_WIDTH) {
					numWidth = DEFAULT_WIDTH;
				}
			} else {
				numWidth = DEFAULT_WIDTH;
			}
			String style = HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + numWidth
					+ "px ; " + value; //$NON-NLS-1$
			parentDiv.setAttribute(HTML.ATTR_STYLE, style);
		} else {
			parentDiv.setAttribute(name, value);
		}
	}

	/**
	 * Method for create DIV tag and set attributes
	 * 
	 * @param visualDocument
	 * @param styleClass
	 * @param style
	 */
	private nsIDOMElement createDIV(nsIDOMDocument visualDocument, String styleClass, String style) {
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		if (styleClass != null) {
			div.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		if (style != null) {
			div.setAttribute(HTML.ATTR_STYLE, style);
		}
		return div;
	}

	/**
	 * Method for convert String to number
	 * 
	 * @param size
	 * @return number
	 */
	private int getSize(String size) {
		String num = size;
		int pos = num.indexOf(Constants.PIXEL);
		if (pos != -1) {
			num = num.substring(0, pos);
		}
		pos = num.indexOf(Constants.PERCENT);
		if (pos != -1) {
			num = num.substring(0, pos);
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
	 * Get Range element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return range element
	 */
	private nsIDOMElement getRangeElement(nsIDOMElement parent) {
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tempNode = list.item(0);
		nsIDOMElement returnElement = (nsIDOMElement)tempNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		return returnElement;
	}

	/**
	 * Get Trailer element from parent element.
	 * 
	 * @param parent element
	 * @return trailer element
	 */
	private nsIDOMElement getTrailerElement(nsIDOMElement parent) {
		// get a slider element
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tempNode = list.item(0);
		nsIDOMElement slider = (nsIDOMElement)tempNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a trailer element
		nsIDOMNodeList sliderList = slider.getChildNodes();
		nsIDOMNode tempTrailerNode = sliderList.item(0);
		nsIDOMElement trailer = (nsIDOMElement)tempTrailerNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a element
		nsIDOMNodeList trailerList = trailer.getChildNodes();
		nsIDOMNode temp2 = trailerList.item(0);
		nsIDOMElement returnElement = (nsIDOMElement)temp2.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		return returnElement;
	}

	/**
	 * Get Track element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return track element
	 */
	private nsIDOMElement getTrackElement(nsIDOMElement parent) {
		
		// get a range element
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tempRangeNode = list.item(0);
		nsIDOMElement range = (nsIDOMElement)tempRangeNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a range decoder element
		nsIDOMNodeList rangeList = range.getChildNodes();
		nsIDOMNode tempRangeDecorNode = rangeList.item(0);
		nsIDOMElement rangeDecor = (nsIDOMElement)tempRangeDecorNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a trailer
		nsIDOMNodeList rangeDecorList = rangeDecor.getChildNodes();
		nsIDOMNode tempTrailerNode = rangeDecorList.item(0);
		nsIDOMElement trailer = (nsIDOMElement)tempTrailerNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		// get a element
		nsIDOMNodeList trailerList = trailer.getChildNodes();
		nsIDOMNode temp = trailerList.item(0);
		nsIDOMElement returnElement = (nsIDOMElement)temp.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		return returnElement;
	}

	/**
	 * Get Handle element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return handle element
	 */
	private nsIDOMElement getHandleElement(nsIDOMElement parent) {
		// get a range element
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tempRangeNode = list.item(0);
		nsIDOMElement range = (nsIDOMElement)tempRangeNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a range decoder element
		nsIDOMNodeList rangeList = range.getChildNodes();
		nsIDOMNode tempRangeDecorNode = rangeList.item(0);
		nsIDOMElement rangeDecor = (nsIDOMElement)tempRangeDecorNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		// get a trailer
		nsIDOMNodeList rangeDecorList = rangeDecor.getChildNodes();
		nsIDOMNode tempTrailerNode = rangeDecorList.item(0);
		nsIDOMElement trailer = (nsIDOMElement)tempTrailerNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		// get a track
		nsIDOMNodeList trailerList = trailer.getChildNodes();
		nsIDOMNode tempTrackNode = trailerList.item(0);
		nsIDOMElement track = (nsIDOMElement)tempTrackNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		// get a element
		nsIDOMNodeList trackList = track.getChildNodes();
		nsIDOMNode temp = trackList.item(0);
		nsIDOMElement returnElement = (nsIDOMElement)temp.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		return returnElement;
	}

	/**
	 * Get Input element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return input element
	 */
	private nsIDOMElement getInputElement(nsIDOMElement parent) {
		nsIDOMNodeList list = parent.getChildNodes();
		nsIDOMNode tempNode = list.item(1);
		nsIDOMElement returnElement = (nsIDOMElement)tempNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
		return returnElement;
	}
	
	
	private void setAttributesToInputElement( nsIDOMElement inputElement, Element sourceElement) {
		String styleClass = getAttribute(FIELD_STYLE_CLASS_ATR, sourceElement);
		String value = getAttribute("handleValue", sourceElement); //$NON-NLS-1$
		
		if ( value.length() == 0 ) {
			value = "N/A"; //$NON-NLS-1$
		}
		
		inputElement.setAttribute(
				HTML.ATTR_CLASS, 
				new StringBuffer().append("slider-input-field").append(Constants.WHITE_SPACE).append(styleClass).toString()  //$NON-NLS-1$
				);
		
		inputElement.setAttribute(
				HTML.ATTR_VALUE, 
				value 
				);
	}
}
