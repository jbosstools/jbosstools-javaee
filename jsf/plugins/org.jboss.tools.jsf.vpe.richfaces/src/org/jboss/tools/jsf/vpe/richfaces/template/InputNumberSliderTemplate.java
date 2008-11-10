/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.NodeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLInputElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Sergey Dzmitrovich
 * 
 */
public class InputNumberSliderTemplate extends
		AbstractEditableRichFacesTemplate {

	/**
	 * path to file which contains css styles for component
	 */
	private static final String STYLE_PATH = "/inputNumberSlider/numberSlider.css"; //$NON-NLS-1$

	/**
	 * path to bar image
	 */
	private static final String SPACER_IMAGE_PATH = "/inputNumberSlider/spacer.gif"; //$NON-NLS-1$

	/**
	 * path to bar image
	 */
	private static final String INPUT_BACKGROUND_IMAGE_PATH = "/inputNumberSlider/input.gif"; //$NON-NLS-1$

	/**
	 * path to bar image
	 */
	private static final String BAR_BACKGROUND_IMAGE_PATH = "/inputNumberSlider/track.gif"; //$NON-NLS-1$

	/**
	 * path to handler image
	 */
	private static final String HANDLER_IMAGE_PATH = "/inputNumberSlider/handler.gif"; //$NON-NLS-1$

	/**
	 * "showInput" attribute
	 */
	private static final String SHOW_INPUT_ATTR = "showInput"; //$NON-NLS-1$

	/**
	 * "showBoundaryValues" attribute
	 */
	private static final String SHOW_BOUNDARY_VALUES = "showBoundaryValues"; //$NON-NLS-1$
	/**
	 * "inputPosition" attribute
	 */
	private static final String INPUT_POSITION_ATTR = "inputPosition"; //$NON-NLS-1$

	/**
	 * "minValue" attribute
	 */
	private static final String MIN_VALUE_ATTR = "minValue"; //$NON-NLS-1$

	/**
	 * "maxValue" attribute
	 */
	private static final String MAX_VALUE_ATTR = "maxValue"; //$NON-NLS-1$

	/**
	 * "barStyle" attribute
	 */
	private static final String BAR_STYLE_ATTR = "barStyle"; //$NON-NLS-1$

	/**
	 * default min value
	 */
	private static final String MIN_VALUE_DEFAULT = "0"; //$NON-NLS-1$

	/**
	 * default max value
	 */
	private static final String MAX_VALUE_DEFAULT = "100"; //$NON-NLS-1$

	/**
	 * default input size
	 */
	private static final String INPUT_SIZE_DEFAULT = "3"; //$NON-NLS-1$

	/**
	 * default slyder width
	 */
	private static final String SLYDER_WIDTH_DEFAULT = "200"; //$NON-NLS-1$

	/**
	 * default max value
	 */
	private static final String INPUT_FIELD_STYLE = "text-align: left; vertical-align: bottom;"; //$NON-NLS-1$

	/**
	 * spacer image style
	 */
	private static final String SPACER_IMAGE_STYLE = "display: block;"; //$NON-NLS-1$

	/**
	 * default max value
	 */
	private static final String HANDLER_WRAPPER_STYLE = "position: relative;"; //$NON-NLS-1$

	/**
	 * contains default css styles for different elements which can define using
	 * attributes
	 * 
	 * key + "Class" = name of some element's style attribute
	 */
	private static final Map<String, String> defaultStyleClasses;

	static {
		defaultStyleClasses = new HashMap<String, String>();

		// general style
		defaultStyleClasses.put("style", "dr-insldr rich-slider"); //$NON-NLS-1$//$NON-NLS-2$

		// input style
		defaultStyleClasses.put("input", "rich-inslider-field"); //$NON-NLS-1$//$NON-NLS-2$

		// bar style
		defaultStyleClasses.put("bar", "dr-insldr-track rich-inslider-track"); //$NON-NLS-1$//$NON-NLS-2$

		// bar style
		defaultStyleClasses.put(
				"handle", "dr-insldr-handler rich-inslider-handler"); //$NON-NLS-1$//$NON-NLS-2$

	}

	/**
	 * min value style classes
	 */
	private static final String MIN_VALUE_STYLE_CLASSES = "dr-insldr-left-num rich-inslider-left-num"; //$NON-NLS-1$

	/**
	 * max value style classes
	 */
	private static final String MAX_VALUE_STYLE_CLASSES = "dr-insldr-right-num rich-inslider-right-num"; //$NON-NLS-1$

	/**
	 * input left style classes
	 */
	private static final String INPUT_LEFT_STYLE_CLASSES = "dr-insldr-field dr-insldr-field-left"; //$NON-NLS-1$

	/**
	 * input right style classes
	 */
	private static final String INPUT_RIGHT_STYLE_CLASSES = "dr-insldr-field dr-insldr-field-right"; //$NON-NLS-1$

	/**
	 * slider style classes
	 */
	private static final String SLIDER_STYLE_CLASSES = "dr-insldr-size dr-insldr-vert-spacer"; //$NON-NLS-1$

	/**
	 * track decor style classes
	 */
	private static final String TRACK_DECOR_1_CLASSES = "dr-insldr-track-decor-1"; //$NON-NLS-1$

	/**
	 * slider style classes
	 */
	private static final String TRACK_DECOR_2_CLASSES = "dr-insldr-track-decor-2"; //$NON-NLS-1$

	/**
	 * contains prepare css styles ( added user css classes besides default
	 * styles )
	 */
	private static final Map<String, String> styleClasses = new HashMap<String, String>();

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		// cast to Element
		Element sourceElement = (Element) sourceNode;

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "inputNumberSlider"); //$NON-NLS-1$

		prepareData(sourceElement);

		VpeElementData elementData = new VpeElementData();

		// create and initialize basic table element
		nsIDOMElement basicTable = visualDocument.createElement(HTML.TAG_TABLE);
		basicTable.setAttribute(HTML.ATTR_STYLE, HTML.ATTR_WIDTH
				+ ":" //$NON-NLS-1$
				+ getNumberValue(sourceElement, RichFaces.ATTR_WIDTH,
						SLYDER_WIDTH_DEFAULT) + ";" //$NON-NLS-1$
				+ getAttribute(sourceElement, RichFaces.ATTR_STYLE));
		basicTable.setAttribute(HTML.ATTR_CLASS, styleClasses.get("style")); //$NON-NLS-1$
		basicTable.setAttribute(HTML.ATTR_CELLPADDING, MIN_VALUE_DEFAULT);
		basicTable.setAttribute(HTML.ATTR_CELLSPACING, MIN_VALUE_DEFAULT);
		basicTable.setAttribute(HTML.ATTR_BORDER, MIN_VALUE_DEFAULT);

		nsIDOMElement valuesBlock = createValuesBlock(sourceElement,
				visualDocument, elementData);

		nsIDOMElement sliderBlock = createSliderBlock(sourceElement,
				visualDocument);

		basicTable.appendChild(valuesBlock);
		basicTable.appendChild(sliderBlock);

		// create creation data
		VpeCreationData creationData = new VpeCreationData(basicTable);
		creationData.setElementData(elementData);
		return creationData;
	}

	/**
	 * prepare
	 * 
	 * @param sourceElement
	 */
	private void prepareData(Element sourceElement) {

		// prepare style classes
		Set<String> styleClassesKeys = defaultStyleClasses.keySet();

		styleClasses.clear();
		for (String key : styleClassesKeys) {

			if (sourceElement.hasAttribute(key + "Class")) //$NON-NLS-1$
				styleClasses.put(key, defaultStyleClasses.get(key) + " " //$NON-NLS-1$
						+ sourceElement.getAttribute(key + "Class")); //$NON-NLS-1$
			else
				styleClasses.put(key, defaultStyleClasses.get(key));
		}

	}

	/**
	 * 
	 * @param sourceElement
	 * @param visualDocument
	 * @param elementData
	 * @return
	 */
	private nsIDOMElement createInputBlock(Element sourceElement,
			nsIDOMDocument visualDocument, VpeElementData elementData) {

		// create input block
		nsIDOMElement inputTd = visualDocument.createElement(HTML.TAG_TD);
		inputTd.setAttribute(HTML.ATTR_STYLE, INPUT_FIELD_STYLE);
		inputTd.setAttribute(HTML.ATTR_ROWSPAN, "2"); //$NON-NLS-1$

		// create input field
		nsIDOMElement inputField = visualDocument.createElement(HTML.TAG_INPUT);
		inputField.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT); 

		inputField.setAttribute(HTML.ATTR_SIZE, getNumberValue(sourceElement,
				RichFaces.ATTR_INPUT_SIZE, INPUT_SIZE_DEFAULT));

		inputField.setAttribute(HTML.ATTR_STYLE, ComponentUtil
				.getBackgoundImgStyle(INPUT_BACKGROUND_IMAGE_PATH)
				+ getAttribute(sourceElement, RichFaces.ATTR_INPUT_STYLE));

		NodeData attributeData;

		if (sourceElement.hasAttribute(RichFaces.ATTR_VALUE)) {

			inputField.setAttribute(HTML.ATTR_VALUE, sourceElement
					.getAttribute(RichFaces.ATTR_VALUE));

			attributeData = new NodeData(sourceElement
					.getAttributeNode(RichFaces.ATTR_VALUE), inputField);

		} else {
			inputField.setAttribute(HTML.ATTR_VALUE, ""); //$NON-NLS-1$

			attributeData = new AttributeData(RichFaces.ATTR_VALUE,
					inputField);
		}

		elementData.addNodeData(attributeData);

		// get class attribute
		String inputClass = null;

		if (isRightInputPosition(sourceElement))
			inputClass = INPUT_RIGHT_STYLE_CLASSES;
		else
			inputClass = INPUT_LEFT_STYLE_CLASSES;

		inputClass += " " + styleClasses.get("input"); //$NON-NLS-1$ //$NON-NLS-2$

		inputField.setAttribute(HTML.ATTR_CLASS, inputClass);

		nsIDOMHTMLInputElement iDOMInputElement = (nsIDOMHTMLInputElement) inputField
				.queryInterface(nsIDOMHTMLInputElement.NS_IDOMHTMLINPUTELEMENT_IID);
		iDOMInputElement.setReadOnly(false);

		inputTd.appendChild(inputField);

		return inputTd;

	}

	/**
	 * 
	 * @param sourceElement
	 * @param visualDocument
	 * @return
	 */
	private nsIDOMElement createSliderBlock(Element sourceElement,
			nsIDOMDocument visualDocument) {

		// create slider block - tr tag
		nsIDOMElement sliderBlock = visualDocument.createElement(HTML.TAG_TR);

		// create td
		nsIDOMElement sliderTd = visualDocument.createElement(HTML.TAG_TD);
		sliderTd.setAttribute(HTML.ATTR_CLASS, SLIDER_STYLE_CLASSES);
		sliderTd.setAttribute(HTML.ATTR_COLSPAN, "2"); //$NON-NLS-1$

		nsIDOMElement handlerWrapper = visualDocument
				.createElement(HTML.TAG_DIV);
		handlerWrapper.setAttribute(HTML.ATTR_STYLE, HANDLER_WRAPPER_STYLE);

		nsIDOMElement handler = visualDocument.createElement(HTML.TAG_DIV);
		handler.setAttribute(HTML.ATTR_CLASS, styleClasses.get("handle")); //$NON-NLS-1$
		handler.setAttribute(HTML.ATTR_STYLE, ComponentUtil
				.getBackgoundImgStyle(HANDLER_IMAGE_PATH));

		handlerWrapper.appendChild(handler);

		// create bar - div tag
		nsIDOMElement bar = visualDocument.createElement(HTML.TAG_DIV);
		bar.setAttribute(HTML.ATTR_CLASS, styleClasses.get("bar")); //$NON-NLS-1$
		bar.setAttribute(HTML.TAG_STYLE, ComponentUtil
				.getBackgoundImgStyle(BAR_BACKGROUND_IMAGE_PATH)
				+ getAttribute(sourceElement, BAR_STYLE_ATTR));

		// create table
		nsIDOMElement barTable = visualDocument.createElement(HTML.TAG_TABLE);
		barTable.setAttribute(HTML.ATTR_CLASS, TRACK_DECOR_1_CLASSES);
		barTable.setAttribute(HTML.ATTR_CELLPADDING, MIN_VALUE_DEFAULT);
		barTable.setAttribute(HTML.ATTR_CELLSPACING, MIN_VALUE_DEFAULT);

		// create tr
		nsIDOMElement barTr = visualDocument.createElement(HTML.TAG_TR);

		// create td
		nsIDOMElement barTd = visualDocument.createElement(HTML.TAG_TD);
		barTd.setAttribute(HTML.ATTR_CLASS, TRACK_DECOR_2_CLASSES);

		// create image
		nsIDOMElement barImage = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(barImage, SPACER_IMAGE_PATH);
		barImage.setAttribute(HTML.ATTR_STYLE, SPACER_IMAGE_STYLE);

		// insert image to td
		barTd.appendChild(barImage);

		// insert td to tr
		barTr.appendChild(barTd);

		// insert tr to table
		barTable.appendChild(barTr);

		// insert table to bar
		bar.appendChild(barTable);

		sliderTd.appendChild(handlerWrapper);
		sliderTd.appendChild(bar);
		sliderBlock.appendChild(sliderTd);

		return sliderBlock;
	}

	/**
	 * 
	 * @param sourceElement
	 * @param visualDocument
	 * @param elementData
	 * @return
	 */
	private nsIDOMElement createValuesBlock(Element sourceElement,
			nsIDOMDocument visualDocument, VpeElementData elementData) {

		// create numbers block
		nsIDOMElement valuesBlock = visualDocument.createElement(HTML.TAG_TR);

		// create minValue block
		nsIDOMElement minValueTd = visualDocument.createElement(HTML.TAG_TD);
		minValueTd.setAttribute(HTML.ATTR_CLASS, MIN_VALUE_STYLE_CLASSES);

		// create maxValue block
		nsIDOMElement maxValueTd = visualDocument.createElement(HTML.TAG_TD);
		maxValueTd.setAttribute(HTML.ATTR_CLASS, MAX_VALUE_STYLE_CLASSES);

		if (isShowBoundaryValues(sourceElement)) {

			nsIDOMText minValueText;
			NodeData minValueData;
			if (sourceElement.hasAttribute(MIN_VALUE_ATTR)) {

				// create minValue text
				minValueText = visualDocument.createTextNode(sourceElement
						.getAttribute(MIN_VALUE_ATTR));

				minValueData = new NodeData(sourceElement
						.getAttributeNode(MIN_VALUE_ATTR), minValueText);

			} else {

				// create minValue text
				minValueText = visualDocument.createTextNode(MIN_VALUE_DEFAULT);

				minValueData = new AttributeData(MIN_VALUE_ATTR,
						minValueText);

			}
			// add text to td
			minValueTd.appendChild(minValueText);
			elementData.addNodeData(minValueData);

			nsIDOMText maxValueText;
			NodeData maxValueData;
			if (sourceElement.hasAttribute(MAX_VALUE_ATTR)) {

				// create minValue text
				maxValueText = visualDocument.createTextNode(sourceElement
						.getAttribute(MAX_VALUE_ATTR));

				maxValueData = new NodeData(sourceElement
						.getAttributeNode(MAX_VALUE_ATTR), maxValueText);

			} else {

				// create minValue text
				maxValueText = visualDocument.createTextNode(MAX_VALUE_DEFAULT);

				maxValueData = new AttributeData(MAX_VALUE_ATTR,
						maxValueText);

			}
			// add text to td
			maxValueTd.appendChild(maxValueText);
			elementData.addNodeData(maxValueData);
		}

		valuesBlock.appendChild(minValueTd);
		valuesBlock.appendChild(maxValueTd);

		// if input field is showed
		if (isShowInput(sourceElement)) {

			nsIDOMElement inputTd = createInputBlock(sourceElement,
					visualDocument, elementData);

			if (isRightInputPosition(sourceElement))
				valuesBlock.appendChild(inputTd);
			else
				valuesBlock.insertBefore(inputTd, minValueTd);
		}

		return valuesBlock;
	}

	/**
	 * 
	 * @param sourceElement
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	private String getNumberValue(Element sourceElement, String attributeName,
			String defaultValue) {

		// if source element has attribute
		if (sourceElement.hasAttribute(attributeName)) {
			// getAttribute
			String stringValue = sourceElement.getAttribute(attributeName);

			try {
				// decode attribute's value
				Integer.decode(stringValue);
				// if it is number (there is not exception) return attribute's
				// value
				return stringValue;
			} catch (NumberFormatException e) {
				// if attribute's value is not number do nothing and then return
				// default value
			}

		}

		return defaultValue;

	}

	/**
	 * if input field must represent return true
	 * 
	 * @param sourceElement
	 * @return
	 */
	private boolean isShowInput(Element sourceElement) {

		// if source element has "showInput" attribute
		if (sourceElement.hasAttribute(SHOW_INPUT_ATTR)) {

			// get this attribute
			String showInput = sourceElement.getAttribute(SHOW_INPUT_ATTR);

			// if this attribute equals "true"
			if ("true".equalsIgnoreCase(showInput)) //$NON-NLS-1$
				return true;
			// in other cases return false
			return false;

		}

		// default value is true
		return true;

	}

	/**
	 * Return true if input position is right. Return false if input position is
	 * left
	 * 
	 * @param sourceElement
	 * @return
	 */
	private boolean isRightInputPosition(Element sourceElement) {

		if (sourceElement.hasAttribute(INPUT_POSITION_ATTR)
				&& ("left".equalsIgnoreCase(sourceElement //$NON-NLS-1$
						.getAttribute(INPUT_POSITION_ATTR))))
			return false;

		return true;

	}

	/**
	 * 
	 * @param sourceElement
	 * @return
	 */
	private boolean isShowBoundaryValues(Element sourceElement) {
		if ((sourceElement.hasAttribute(SHOW_BOUNDARY_VALUES) && "false" //$NON-NLS-1$
		.equalsIgnoreCase(sourceElement.getAttribute(SHOW_BOUNDARY_VALUES))))
			return false;
		return true;
	}

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}