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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.mozilla.interfaces.nsIDOMHTMLInputElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for input number slider control
 *
 * @author Sergey Dzmitrovich
 */
public class InputNumberSliderTemplate extends AbstractEditableRichFacesTemplate {

	/** path to file which contains css styles for component */
	private static final String STYLE_PATH = "/inputNumberSlider/numberSlider.css"; //$NON-NLS-1$

	/** path to bar image */
	private static final String SPACER_IMAGE_PATH = "/inputNumberSlider/spacer.gif"; //$NON-NLS-1$

	/** "showInput" attribute */
	private static final String SHOW_INPUT_ATTR = "showInput"; //$NON-NLS-1$
	/** "showBoundaryValues" attribute */
	private static final String SHOW_BOUNDARY_VALUES = "showBoundaryValues"; //$NON-NLS-1$
	/** "inputPosition" attribute */
	private static final String INPUT_POSITION_ATTR = "inputPosition"; //$NON-NLS-1$
	/** "minValue" attribute */
	private static final String MIN_VALUE_ATTR = "minValue"; //$NON-NLS-1$
	/** "maxValue" attribute */
	private static final String MAX_VALUE_ATTR = "maxValue"; //$NON-NLS-1$
	/** "barStyle" attribute */
	private static final String BAR_STYLE_ATTR = "barStyle"; //$NON-NLS-1$

	/** default min value */
	private static final String MIN_VALUE_DEFAULT = "0"; //$NON-NLS-1$
	/** default max value */
	private static final String MAX_VALUE_DEFAULT = "100"; //$NON-NLS-1$
	/** default input size */
	private static final String INPUT_SIZE_DEFAULT = "3"; //$NON-NLS-1$
	/** default slider width */
	private static final String SLYDER_WIDTH_DEFAULT = "200"; //$NON-NLS-1$
	/** default input field block style */
	private static final String INPUT_FIELD_STYLE = "text-align: left; vertical-align: bottom;"; //$NON-NLS-1$
	/** spacer image style */
	private static final String SPACER_IMAGE_STYLE = "display: block;"; //$NON-NLS-1$
	/** HANDLER_WRAPPER_STYLE */
	private static final String HANDLER_WRAPPER_STYLE = "position: relative;"; //$NON-NLS-1$
	/** HANDLER_STYLE */
	private static final String HANDLER_STYLE = "visibility: visible;"; //$NON-NLS-1$

	/**
	 * Contains default CSS styles for different elements which can define using attributes
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
		defaultStyleClasses.put("handle", "dr-insldr-handler rich-inslider-handler"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/* Default and RichFaces styles */
	/** min value style classes */
	private static final String MIN_VALUE_STYLE_CLASSES = "dr-insldr-left-num rich-inslider-left-num"; //$NON-NLS-1$
	/** max value style classes */
	private static final String MAX_VALUE_STYLE_CLASSES = "dr-insldr-right-num rich-inslider-right-num"; //$NON-NLS-1$
	/** input left style classes */
	private static final String INPUT_LEFT_STYLE_CLASSES = "dr-insldr-field dr-insldr-field-left"; //$NON-NLS-1$
	/** input right style classes */
	private static final String INPUT_RIGHT_STYLE_CLASSES = "dr-insldr-field dr-insldr-field-right"; //$NON-NLS-1$
	/** slider style classes */
	private static final String SLIDER_STYLE_CLASSES = "dr-insldr-size dr-insldr-vert-spacer"; //$NON-NLS-1$
	/** track decor style classes */
	private static final String TRACK_DECOR_1_CLASSES = "dr-insldr-track-decor-1"; //$NON-NLS-1$
	/** slider style classes */
	private static final String TRACK_DECOR_2_CLASSES = "dr-insldr-track-decor-2"; //$NON-NLS-1$

	/** contains prepare css styles (added user css classes besides default styles) */
	private static final Map<String, String> styleClasses = new HashMap<String, String>();

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have child nodes.
	 *
	 * @param pageContext Contains the information on edited page.
	 * @param sourceNode The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		// Set a css for this element
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "inputNumberSlider"); //$NON-NLS-1$
		// cast to Element
		Element sourceElement = (Element) sourceNode;

		// prepare style classes for input number slider part controls
		prepareData(sourceElement);

		// create and initialize basic table element
		nsIDOMElement basicTable = visualDocument.createElement(HTML.TAG_TABLE);
		String style = new StringBuffer(HTML.STYLE_PARAMETER_WIDTH).append(Constants.COLON).
			append(getNumberValue(sourceElement, RichFaces.ATTR_WIDTH, SLYDER_WIDTH_DEFAULT)).
			append(Constants.PIXEL).append(Constants.SEMICOLON).
			append(getAttribute(sourceElement, RichFaces.ATTR_STYLE)).append(Constants.SEMICOLON).toString();
		basicTable.setAttribute(HTML.ATTR_STYLE, style);
		basicTable.setAttribute(HTML.ATTR_CLASS, styleClasses.get("style")); //$NON-NLS-1$
		basicTable.setAttribute(HTML.ATTR_CELLPADDING, MIN_VALUE_DEFAULT);
		basicTable.setAttribute(HTML.ATTR_CELLSPACING, MIN_VALUE_DEFAULT);
		basicTable.setAttribute(HTML.ATTR_BORDER, MIN_VALUE_DEFAULT);

		VpeElementData elementData = new VpeElementData();
		// create block with min/max and input components
		nsIDOMElement valuesBlock = createValuesBlock(sourceElement, visualDocument, elementData);
		// create slider component
		nsIDOMElement sliderBlock = createSliderBlock(sourceElement, visualDocument);

		basicTable.appendChild(valuesBlock);
		basicTable.appendChild(sliderBlock);

		/*
         * https://jira.jboss.org/jira/browse/JBIDE-3225
         * Component should render its children.
         */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, basicTable, HTML.TAG_DIV, visualDocument);
		creationData.setElementData(elementData);

		return creationData;
	}

	/**
	 * Prepare style classes.
	 *
	 * @param sourceElement Element source object
	 */
	private void prepareData(Element sourceElement) {
		// prepare style classes
		Set<String> styleClassesKeys = defaultStyleClasses.keySet();

		styleClasses.clear();
		for (String key : styleClassesKeys) {
			if (sourceElement.hasAttribute(key + "Class")) { //$NON-NLS-1$
				styleClasses.put(key, defaultStyleClasses.get(key) + Constants.WHITE_SPACE
						+ sourceElement.getAttribute(key + "Class")); //$NON-NLS-1$
			} else {
				styleClasses.put(key, defaultStyleClasses.get(key));
			}
		}
	}

	/**
	 * Create value block container with min, max, input components.
	 *
	 * @param sourceElement The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @param elementData the VpeElementData object
	 * @return nsIDOMElement object
	 */
	private nsIDOMElement createValuesBlock(Element sourceElement, nsIDOMDocument visualDocument,
			VpeElementData elementData) {
		// create numbers block
		nsIDOMElement valuesBlock = visualDocument.createElement(HTML.TAG_TR);

		// create minValue block
		nsIDOMElement minValueTD = visualDocument.createElement(HTML.TAG_TD);
		minValueTD.setAttribute(HTML.ATTR_CLASS, MIN_VALUE_STYLE_CLASSES);

		// create maxValue block
		nsIDOMElement maxValueTD = visualDocument.createElement(HTML.TAG_TD);
		maxValueTD.setAttribute(HTML.ATTR_CLASS, MAX_VALUE_STYLE_CLASSES);

		// checks if min/max values should be shown on component
		if (isShowBoundaryValues(sourceElement)) {
			NodeData minValueData = null;
			// create minValue text
			nsIDOMText minValueText = visualDocument.createTextNode(getNumberValue(sourceElement, MIN_VALUE_ATTR,
					MIN_VALUE_DEFAULT));
			if (sourceElement.hasAttribute(MIN_VALUE_ATTR)) {
				minValueData = new NodeData(sourceElement.getAttributeNode(MIN_VALUE_ATTR), minValueText);
			} else {
				minValueData = new AttributeData(MIN_VALUE_ATTR, minValueText);
			}
			// add text to TD
			minValueTD.appendChild(minValueText);
			elementData.addNodeData(minValueData);

			NodeData maxValueData;
			// create maxValue text
			nsIDOMText maxValueText = visualDocument.createTextNode(getNumberValue(sourceElement, MAX_VALUE_ATTR,
					MAX_VALUE_DEFAULT));
			if (sourceElement.hasAttribute(MAX_VALUE_ATTR)) {
				maxValueData = new NodeData(sourceElement.getAttributeNode(MAX_VALUE_ATTR), maxValueText);
			} else {
				maxValueData = new AttributeData(MAX_VALUE_ATTR, maxValueText);
			}
			// add text to tD
			maxValueTD.appendChild(maxValueText);
			elementData.addNodeData(maxValueData);
		}

		valuesBlock.appendChild(minValueTD);
		valuesBlock.appendChild(maxValueTD);

		// checks if input field should be shown on component
		if (isShowInput(sourceElement)) {
			nsIDOMElement inputTd = createInputBlock(sourceElement, visualDocument, elementData);
			// the location of input field (left/right)
			if (isRightInputPosition(sourceElement)) {
				valuesBlock.appendChild(inputTd);
			} else {
				valuesBlock.insertBefore(inputTd, minValueTD);
			}
		}

		return valuesBlock;
	}

	/**
	 * Create input block container with input component.
	 *
	 * @param sourceElement The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @param elementData the VpeElementData object
	 * @return nsIDOMElement object
	 */
	private nsIDOMElement createInputBlock(Element sourceElement, nsIDOMDocument visualDocument,
			VpeElementData elementData) {
		// create input block
		nsIDOMElement inputTD = visualDocument.createElement(HTML.TAG_TD);
		inputTD.setAttribute(HTML.ATTR_STYLE, INPUT_FIELD_STYLE);
		inputTD.setAttribute(HTML.ATTR_ROWSPAN, "2"); //$NON-NLS-1$

		// create input field
		nsIDOMElement inputField = visualDocument.createElement(HTML.TAG_INPUT);
		inputField.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT); 
		inputField.setAttribute(HTML.ATTR_SIZE, getNumberValue(sourceElement,
				RichFaces.ATTR_INPUT_SIZE, INPUT_SIZE_DEFAULT));

		inputField.setAttribute(HTML.ATTR_STYLE, getAttribute(sourceElement, RichFaces.ATTR_INPUT_STYLE));

		NodeData attributeData = null;
		inputField.setAttribute(HTML.ATTR_VALUE, ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_VALUE));
		if (sourceElement.hasAttribute(RichFaces.ATTR_VALUE)) {
			attributeData = new NodeData(sourceElement.getAttributeNode(RichFaces.ATTR_VALUE), inputField);
		} else {
			attributeData = new AttributeData(RichFaces.ATTR_VALUE, inputField);
		}
		elementData.addNodeData(attributeData);

		// get input class attribute
		String inputClass = null;
		if (isRightInputPosition(sourceElement)) {
			inputClass = INPUT_RIGHT_STYLE_CLASSES;
		} else {
			inputClass = INPUT_LEFT_STYLE_CLASSES;
		}
		inputClass = new StringBuffer(inputClass).append(Constants.WHITE_SPACE).
			append(styleClasses.get("input")).toString(); //$NON-NLS-1$
		inputField.setAttribute(HTML.ATTR_CLASS, inputClass);

		nsIDOMHTMLInputElement iDOMInputElement = (nsIDOMHTMLInputElement) inputField
				.queryInterface(nsIDOMHTMLInputElement.NS_IDOMHTMLINPUTELEMENT_IID);
		iDOMInputElement.setReadOnly(false);

		inputTD.appendChild(inputField);

		return inputTD;
	}

	/**
	 * Create slider block container with corresponding components.
	 *
	 * @param sourceElement The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @return nsIDOMElement object
	 */
	private nsIDOMElement createSliderBlock(Element sourceElement, nsIDOMDocument visualDocument) {
		// create slider block - TR tag
		nsIDOMElement sliderBlock = visualDocument.createElement(HTML.TAG_TR);

		// create TD
		nsIDOMElement sliderTD = visualDocument.createElement(HTML.TAG_TD);
		sliderTD.setAttribute(HTML.ATTR_CLASS, SLIDER_STYLE_CLASSES);
		sliderTD.setAttribute(HTML.ATTR_COLSPAN, "2"); //$NON-NLS-1$

		// create wrapper DIV component for slider element
		nsIDOMElement handlerWrapper = visualDocument.createElement(HTML.TAG_DIV);
		handlerWrapper.setAttribute(HTML.ATTR_STYLE, HANDLER_WRAPPER_STYLE);

		nsIDOMElement handler = visualDocument.createElement(HTML.TAG_DIV);
		handler.setAttribute(HTML.ATTR_STYLE, HANDLER_STYLE);
		handler.setAttribute(HTML.ATTR_CLASS, styleClasses.get("handle")); //$NON-NLS-1$

		handlerWrapper.appendChild(handler);

		// create bar DIV tag
		nsIDOMElement barDiv = visualDocument.createElement(HTML.TAG_DIV);
		barDiv.setAttribute(HTML.TAG_STYLE, ComponentUtil.getAttribute(sourceElement, BAR_STYLE_ATTR));
		barDiv.setAttribute(HTML.ATTR_CLASS, styleClasses.get("bar")); //$NON-NLS-1$

		// create table
		nsIDOMElement barTable = visualDocument.createElement(HTML.TAG_TABLE);
		barTable.setAttribute(HTML.ATTR_CLASS, TRACK_DECOR_1_CLASSES);
		barTable.setAttribute(HTML.ATTR_CELLPADDING, MIN_VALUE_DEFAULT);
		barTable.setAttribute(HTML.ATTR_CELLSPACING, MIN_VALUE_DEFAULT);

		// create TR
		nsIDOMElement barTR = visualDocument.createElement(HTML.TAG_TR);
		// create TD
		nsIDOMElement barTD = visualDocument.createElement(HTML.TAG_TD);
		barTD.setAttribute(HTML.ATTR_CLASS, TRACK_DECOR_2_CLASSES);

		// create image
		nsIDOMElement barImage = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(barImage, SPACER_IMAGE_PATH);
		barImage.setAttribute(HTML.ATTR_STYLE, SPACER_IMAGE_STYLE);

		// insert image to TD
		barTD.appendChild(barImage);
		// insert TD to TR
		barTR.appendChild(barTD);
		// insert TR to table
		barTable.appendChild(barTR);
		// insert table to bar
		barDiv.appendChild(barTable);

		sliderTD.appendChild(handlerWrapper);
		sliderTD.appendChild(barDiv);
		sliderBlock.appendChild(sliderTD);

		return sliderBlock;
	}

	/**
	 * Method is used retrieve attribute value from sourceElement for corresponding attributeName.
	 * If sourceElement doesn't contain attribute with name equals attributeName return default value.
	 *
	 * @param sourceElement DOM Element
	 * @param attributeName the name of attribute
	 * @param defaultValue the default value
	 * @return the value of attribute with corresponding name for source element
	 */
	private String getNumberValue(Element sourceElement, String attributeName, String defaultValue) {
		// if source element has attribute
		if (sourceElement.hasAttribute(attributeName)) {
			String stringValue = sourceElement.getAttribute(attributeName);
			try {
				// decode attribute's value
				Integer.decode(stringValue);
				// if it is number (there is not exception) return attribute's value
				return stringValue;
			} catch (NumberFormatException e) {
				// if attribute's value is not number do nothing and then return default value
			}
		}

		return defaultValue;
	}

	/**
	 * If input field must be represented. In this case return true, false - otherwise.
	 *
	 * @param sourceElement DOM source Element object
	 * @return true if input field must be represented, false - otherwise
	 */
	private boolean isShowInput(Element sourceElement) {
		// if source element has "showInput" attribute
		if (sourceElement.hasAttribute(SHOW_INPUT_ATTR)) {
			String showInput = sourceElement.getAttribute(SHOW_INPUT_ATTR);
			// if this attribute equals "true"
			if (Constants.TRUE.equalsIgnoreCase(showInput)) {
				return true;
			}
			// in other cases return false
			return false;
		}
		// default value is true
		return true;
	}

	/**
	 * Return true if input position is right. Return false if input position is left.
	 *
	 * @param sourceElement DOM source Element object
	 * @return false if input position is "left", true - otherwise
	 */
	private boolean isRightInputPosition(Element sourceElement) {

		if (sourceElement.hasAttribute(INPUT_POSITION_ATTR)
				&& ("left".equalsIgnoreCase(sourceElement.getAttribute(INPUT_POSITION_ATTR)))) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * Method checks if the min/max values are shown on the right/left borders of a control.
	 *
	 * @param sourceElement DOM source Element object
	 * @return true if min/max value must be shown, false - otherwise
	 */
	private boolean isShowBoundaryValues(Element sourceElement) {
		if ((sourceElement.hasAttribute(SHOW_BOUNDARY_VALUES) &&
				Constants.FALSE.equalsIgnoreCase(sourceElement.getAttribute(SHOW_BOUNDARY_VALUES)))) {
			return false;
		}
		return true;
	}
}