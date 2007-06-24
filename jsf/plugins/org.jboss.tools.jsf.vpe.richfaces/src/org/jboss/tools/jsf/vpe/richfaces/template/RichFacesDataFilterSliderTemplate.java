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
 * Template for Rich Faces DataTableFilterSlider
 * 
 */
public class RichFacesDataFilterSliderTemplate extends AbstractRichFacesTemplate {

	/** IMAGE_SPACER */
	final static String IMAGE_SPACER = "/common/spacer.gif";
	
	final static String CENTER_SLIDER = "/dataFilterSlider/pos.gif";

	final static String STYLE_PATH = "/dataFilterSlider/dataFilterSlider.css";

	final static int DEFAULT_WIDTH = 260;

	/* intdent between slider and right border */
	final static int DEFAULT_PARAGRAPH = 60;

	final static int DEFAULT_HEIGHT = 20;

	final static String DEFAULT_SLIDER_POSITION = "left: -38px; width: 114px;";

	final static String DEFAULT_SLIDER_WIDTH = "7px";

	final static String DEFAULT_SLIDER_HEIGHT = "8px";

	final static String DEFAULT_SLIDER_BORDER = "0px";

	final static String PIXEL_PREFIX = "px";

	final static String PERCENT_PREFIX = "%";

	final static String FIELD_STYLE_CLASS_ATR = "fieldStyleClass";

	final static String HANDLE_STYLE_CLASS_ATR = "handleStyleClass";

	final static String RANGE_STYLE_CLASS_ATR = "rangeStyleClass";

	final static String TRACK_STYLE_CLASS_ATR = "trackStyleClass";

	final static String TRAILER_STYLE_CLASS_ATR = "trailerStyleClass";

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
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		int numWidth = 0;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH,
				"richFacesDataFilterSlider");
		Element sourceElement = (Element) sourceNode;
		String style = ComponentUtil.getAttribute(sourceElement,
				HtmlComponentUtil.HTML_STYLE_ATTR);
		String width = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
		if (width != null) {
			numWidth = getSize(width);
			if (numWidth < DEFAULT_WIDTH) {
				numWidth = DEFAULT_WIDTH;
			}
		} else {
			numWidth = DEFAULT_WIDTH;
		}
		String defaultStyle = style + ";" + HtmlComponentUtil.HTML_ATR_WIDTH
				+ " : " + numWidth + "px ; ";
		Element parentDiv = createDIV(visualDocument, "slider-container",
				defaultStyle);
		String rangeStyleClass = ComponentUtil.getAttribute(sourceElement,
				RANGE_STYLE_CLASS_ATR);
		Element rangeDiv = createDIV(visualDocument,
				"range " + rangeStyleClass, HtmlComponentUtil.HTML_ATR_WIDTH
						+ " : " + (numWidth - DEFAULT_PARAGRAPH) + "px;");
		Element rangeDecorDiv = createDIV(visualDocument, "range-decor", null);
		String trailerStyleClass = ComponentUtil.getAttribute(sourceElement,
				TRAILER_STYLE_CLASS_ATR);
		Element trailerDiv = createDIV(visualDocument, "trailer "
				+ trailerStyleClass, DEFAULT_SLIDER_POSITION);
		String trackStyleClass = ComponentUtil.getAttribute(sourceElement,
				TRACK_STYLE_CLASS_ATR);
		Element trackDiv = createDIV(visualDocument,
				"track " + trackStyleClass, HtmlComponentUtil.HTML_ATR_WIDTH
						+ " : " + (numWidth - DEFAULT_PARAGRAPH) + "px;");

		String handleStyleClass = ComponentUtil.getAttribute(sourceElement,HANDLE_STYLE_CLASS_ATR);
		Element handleDiv = createDIV(visualDocument, "handle " + handleStyleClass, null);

		Element img = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		ComponentUtil.setImg(img, CENTER_SLIDER);
		img
				.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH,
						DEFAULT_SLIDER_WIDTH);
		img.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR,
				DEFAULT_SLIDER_BORDER);
		img.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT,
				DEFAULT_SLIDER_HEIGHT);

		/* Set input component */
		Element input = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
		input.setAttribute(HtmlComponentUtil.HTML_TYPE_ATTR, "text");
		
		setAttributesToInputElement(input, sourceElement);


		parentDiv.appendChild(rangeDiv);
		rangeDiv.appendChild(rangeDecorDiv);
		rangeDecorDiv.appendChild(trailerDiv);
		trailerDiv.appendChild(trackDiv);
		trackDiv.appendChild(handleDiv);
		handleDiv.appendChild(img);
		
		Element tableSpacer2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		tableSpacer2.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0px");
		tableSpacer2.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0px");
		tableSpacer2.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0px");		
		tableSpacer2.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%" );
		tableSpacer2.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%" );
		
		Element trSpacer2 =  visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		
		Element tdSpacer2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tdSpacer2.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, "right");
		tdSpacer2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,"font-family: Arial, Verdana, sans-serif; font-size: 5px; color: white;");
		trSpacer2.appendChild(tdSpacer2);

		Element imageSpacer2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		ComponentUtil.setImg(imageSpacer2, IMAGE_SPACER);
		imageSpacer2.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
		imageSpacer2.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%");
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
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		int numWidth = 0;
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
		Element element = (Element) visualNode;
		
		Element input = getInputElement(element);
		setAttributesToInputElement(input, sourceElement);

		if (name.equalsIgnoreCase(RANGE_STYLE_CLASS_ATR)) {
			Element range = getRangeElement(element);
			range.removeAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
			range.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "range");
		} else if (name.equalsIgnoreCase(TRAILER_STYLE_CLASS_ATR)) {
			Element trailer = getTrailerElement(element);
			trailer.removeAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
			trailer.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "trailer");
		} else if (name.equalsIgnoreCase(TRACK_STYLE_CLASS_ATR)) {
			Element track = getTrackElement(element);
			track.removeAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
			track.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "track");
		} else if (name.equalsIgnoreCase(HANDLE_STYLE_CLASS_ATR)) {
			Element handle = getHandleElement(element);
			handle.removeAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
			handle.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "handle");
		} else if (name.equalsIgnoreCase(FIELD_STYLE_CLASS_ATR)) {
			Element field = getInputElement(element);
			setAttributesToInputElement( field, sourceElement );
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_WIDTH)) {
			Element range = getRangeElement(element);
			String style = ComponentUtil.getAttribute(sourceElement,
					HtmlComponentUtil.HTML_STYLE_ATTR);
			element.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style + ";"
					+ HtmlComponentUtil.HTML_ATR_WIDTH + " : " + DEFAULT_WIDTH
					+ "px ; ");
			String rangeStyle = ComponentUtil.getAttribute(range,
					HtmlComponentUtil.HTML_STYLE_ATTR);
			range.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, rangeStyle
					+ ";" + HtmlComponentUtil.HTML_ATR_WIDTH + " : "
					+ (DEFAULT_WIDTH - DEFAULT_PARAGRAPH) + "px;");
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR)) {
			String width = sourceElement
					.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
			if (width != null) {
				numWidth = getSize(width);
				if (numWidth < DEFAULT_WIDTH) {
					numWidth = DEFAULT_WIDTH;
				}
			} else {
				numWidth = DEFAULT_WIDTH;
			}
			String style = HtmlComponentUtil.HTML_ATR_WIDTH + " : " + numWidth
					+ "px ; ";
			element.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
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
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name, value);
		Element parentDiv = (Element) visualNode;

		Element input = getInputElement(parentDiv);
		setAttributesToInputElement(input, sourceElement);

		
		int numWidth = 0;
		if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_ATR_WIDTH)) {
			int size = getSize(value);
			if (size < DEFAULT_WIDTH) {
				size = DEFAULT_WIDTH;
			}
			Element rangeDiv = getRangeElement(parentDiv);
			Element trackDiv = getTrackElement(parentDiv);

			String style = ComponentUtil.getAttribute(parentDiv,
					HtmlComponentUtil.HTML_STYLE_ATTR);
			style = style + HtmlComponentUtil.HTML_ATR_WIDTH + " : " + size
					+ PIXEL_PREFIX + ";";
			parentDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
			String rangeStyle = ComponentUtil.getAttribute(rangeDiv,
					HtmlComponentUtil.HTML_STYLE_ATTR);
			rangeDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, rangeStyle
					+ HtmlComponentUtil.HTML_ATR_WIDTH + " : "
					+ (size - DEFAULT_PARAGRAPH) + "px;");
			String trackStyle = ComponentUtil.getAttribute(trackDiv,
					HtmlComponentUtil.HTML_STYLE_ATTR);
			trackDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, trackStyle
					+ HtmlComponentUtil.HTML_ATR_WIDTH + " : "
					+ (size - DEFAULT_PARAGRAPH) + "px;");

		} else if (name.equalsIgnoreCase(RANGE_STYLE_CLASS_ATR)) {
			Element range = getRangeElement(parentDiv);
			range.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "range "
					+ value);
		} else if (name.equalsIgnoreCase(TRACK_STYLE_CLASS_ATR)) {
			Element track = getTrackElement(parentDiv);
			track.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "track "
					+ value);
		} else if (name.equalsIgnoreCase(TRAILER_STYLE_CLASS_ATR)) {
			Element trailer = getTrailerElement(parentDiv);
			trailer.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "trailer "
					+ value);
		} else if (name.equalsIgnoreCase(HANDLE_STYLE_CLASS_ATR)) {
			Element handle = getHandleElement(parentDiv);
			handle.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "handle "
					+ value);
		} else if (name.equalsIgnoreCase(FIELD_STYLE_CLASS_ATR)) {
		} else if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_STYLE_ATTR)) {
			String width = sourceElement
					.getAttribute(HtmlComponentUtil.HTML_ATR_WIDTH);
			if (width != null) {
				numWidth = getSize(width);
				if (numWidth < DEFAULT_WIDTH) {
					numWidth = DEFAULT_WIDTH;
				}
			} else {
				numWidth = DEFAULT_WIDTH;
			}
			String style = HtmlComponentUtil.HTML_ATR_WIDTH + " : " + numWidth
					+ "px ; " + value;
			parentDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
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
	private Element createDIV(Document visualDocument, String styleClass,
			String style) {
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		if (styleClass != null) {
			div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		}
		if (style != null) {
			div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
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
		String num = new String(size);
		int pos = num.indexOf(PIXEL_PREFIX);
		if (pos != -1) {
			num = num.substring(0, pos);
		}
		pos = num.indexOf(PERCENT_PREFIX);
		if (pos != -1) {
			num = num.substring(0, pos);
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
	 * Get Range element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return range element
	 */
	private Element getRangeElement(Element parent) {
		NodeList list = parent.getChildNodes();
		return (Element) list.item(0);
	}

	/**
	 * Get Trailer element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return trailer element
	 */
	private Element getTrailerElement(Element parent) {
		NodeList list = parent.getChildNodes();
		Element slider = (Element) list.item(0);
		NodeList sliderList = slider.getChildNodes();
		Element trailer = (Element) sliderList.item(0);
		NodeList trailerList = trailer.getChildNodes();
		return (Element) trailerList.item(0);
	}

	/**
	 * Get Track element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return track element
	 */
	private Element getTrackElement(Element parent) {
		NodeList list = parent.getChildNodes();
		Element range = (Element) list.item(0);
		NodeList rangeList = range.getChildNodes();
		Element rangeDecor = (Element) rangeList.item(0);
		NodeList rangeDecorList = rangeDecor.getChildNodes();
		Element trailer = (Element) rangeDecorList.item(0);
		NodeList trailerList = trailer.getChildNodes();
		return (Element) trailerList.item(0);
	}

	/**
	 * Get Handle element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return handle element
	 */
	private Element getHandleElement(Element parent) {
		NodeList list = parent.getChildNodes();
		Element range = (Element) list.item(0);
		NodeList rangeList = range.getChildNodes();
		Element rangeDecor = (Element) rangeList.item(0);
		NodeList rangeDecorList = rangeDecor.getChildNodes();
		Element trailer = (Element) rangeDecorList.item(0);
		NodeList trailerList = trailer.getChildNodes();
		Element track = (Element) trailerList.item(0);
		NodeList trackList = track.getChildNodes();
		return (Element) trackList.item(0);
	}

	/**
	 * Get Input element from parent element.
	 * 
	 * @param parent
	 *            element
	 * @return input element
	 */
	private Element getInputElement(Element parent) {
		NodeList list = parent.getChildNodes();
		return (Element) list.item(1);
	}
	
	
	private void setAttributesToInputElement( Element inputElement, Element sourceElement) {
		String styleClass = getAttribute(FIELD_STYLE_CLASS_ATR, sourceElement);
		String value = getAttribute("handleValue", sourceElement);
		
		if ( value.length() == 0 ) {
			value = "N/A";
		}
		
		inputElement.setAttribute(
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				new StringBuffer().append("slider-input-field").append(" ").append(styleClass).toString() 
				);
		
		inputElement.setAttribute(
				HtmlComponentUtil.HTML_VALUE_ATTR, 
				value 
				);
	}
}
