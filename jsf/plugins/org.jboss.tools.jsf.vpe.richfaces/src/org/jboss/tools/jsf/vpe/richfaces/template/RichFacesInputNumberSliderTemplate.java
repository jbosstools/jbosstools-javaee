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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Template for input number slider control
 */
public class RichFacesInputNumberSliderTemplate extends
		AbstractRichFacesInputNumberTemplate {

	private static final String CSS_FILE = "/inputNumberSlider/inputNumberSlider.css";

	private static final String MIN_VALUE_STALE_CLASS = "minValueStyle";

	private static final String MAX_VALUE_STALE_CLASS = "maxValueStyle";

	private static final String TABLE_INPUT_CELL_STYLE = "manualInputFieldTableCellClass";

	private static final String CELL_FOR_TABLE_STYLE = "cellForTableStyleClass";

	private static final String INNER_TABLE_STYLE = "innerTableStyle";

	final static private String HTML_INPUTSIZE_DEFAULT = "3";

	final static private String HTML_INPUTTYPE_HIDDEN = "hidden";

	final static private String HTML_INPUTTYPE_TEXT = "text";

	final static private String HTML_INPUTVALUE_DEFAULT = "50";

	final static private String IMAGE_SLIDER = "/inputNumberSlider/pos.gif";

	final static private String SLIDER_MAXVALUE_ATTR = "maxValue";

	final static private String SLIDER_MAXVALUE_DEFAULT = "100";

	final static private String SLIDER_MINVALUE_ATTR = "minValue";

	final static private String SLIDER_VALUE_ATTR = "value";

	final static private String SLIDER_MINVALUE_DEFAULT = "0";

	final static private String SLIDER_SHOWBOUNDARY_ATTR = "showBoundaryValues";

	final static private String SLIDER_SHOWINPUT_ATTR = "showInput";

	final static private String SLIDER_STEP_ATTR = "step";

	/** INPUT_STYLE_CLASS */
	private static final String INPUT_STYLE_CLASS = "inputStyleClass";

	/** */
	final private Map<String, Method> mapAttributeToMethod;

	final private static Class[] defaultArgsMappedMethods = new Class[2];

	static {
		defaultArgsMappedMethods[0] = Element.class;
		defaultArgsMappedMethods[1] = Element.class;
	}

	/**
	 * Default constructor
	 */
	public RichFacesInputNumberSliderTemplate() {
		mapAttributeToMethod = new HashMap<String, Method>();

		addMapAttibuteToMethod("value", "setInputValue");
		addMapAttibuteToMethod("inputSize", "setInputSize");
		addMapAttibuteToMethod("inputClass", "setInputClass");
		addMapAttibuteToMethod("inputStyle", "setInputStyle");
		addMapAttibuteToMethod("maxlength", "setMaxlength");
		addMapAttibuteToMethod("showInput", "showInput");
		addMapAttibuteToMethod("showBoundaryValues", "showBoundaryValues");
		addMapAttibuteToMethod("maxValue", "setMaxValue");
		addMapAttibuteToMethod("minValue", "setMinValue");
		addMapAttibuteToMethod("width", "setWidth");
		addMapAttibuteToMethod("style", "setStyle");
		addMapAttibuteToMethod("class", "setClass");
	}

	/**
	 * 
	 * @param methodName
	 * @return
	 */
	private Method getMethodByName(String methodName) {
		Class clazz = this.getClass();
		Method m1 = null;
		try {
			m1 = clazz.getMethod(methodName, defaultArgsMappedMethods);
		} catch (SecurityException e) {
			RichFacesTemplatesActivator.getPluginLog().logError("SecurityException: " + e.getMessage() );
		} catch (NoSuchMethodException e) {
			RichFacesTemplatesActivator.getPluginLog().logWarning("NoSuchMethodException: " + methodName +  ":" + e.getMessage() );
		}

		return m1;
	}

	/**
	 * 
	 * @param attributeName
	 * @param methodName
	 */
	private void addMapAttibuteToMethod(String attributeName, String methodName) {
		Method method = getMethodByName(methodName);

		if (method != null) {
			mapAttributeToMethod.put(attributeName, method);
		}
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

		Element table1 = null;
		Element table1Tr1 = null;
		Element table1Tr1Td1 = null;
		Text table1Tr1Td1Text = null;
		Element table1Tr1Td2 = null;
		Text table1Tr1Td2Text = null;
		Element table1Tr1Td3 = null;
		Element table1Tr1Td3input = null;
		Element table1Tr2 = null;
		Element table1Tr2Td1 = null;
		Element table1Tr2Td1Table2 = null;
		Element table1Tr2Td1Table2Tr1 = null;
		Element table1Tr2Td1Table2Tr1Td1 = null;
		Element table1Tr2Td1Table2Tr1Td2 = null;
		Element table1Tr2Td1Table2Tr1Td2Img = null;
		Element table1Tr2Td1Table2Tr1Td3 = null;

		// sets css link
		ComponentUtil.setCSSLink(pageContext, CSS_FILE, "inputNumberSlider");

		// create table1
		table1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		addBasicTableAttributes(table1);

		// creates first row in table 1
		table1Tr1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		table1Tr1Td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr1Td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				MIN_VALUE_STALE_CLASS);
		table1Tr1Td1Text = visualDocument.createTextNode("0");
		table1Tr1Td2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr1Td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				MAX_VALUE_STALE_CLASS);
		table1Tr1Td2Text = visualDocument.createTextNode("100");
		table1Tr1Td3 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr1Td3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				TABLE_INPUT_CELL_STYLE);
		table1Tr1Td3.setAttribute(HtmlComponentUtil.HTML_ROWSPAN_ATTR, "2");
		table1Tr1Td3input = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
		table1Tr1Td3input.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				INPUT_STYLE_CLASS);
		table1Tr1Td3input.setAttribute(HtmlComponentUtil.HTML_SIZE_ATTR, "3");
		table1Tr1Td3input.setAttribute(HtmlComponentUtil.HTML_VALUE_ATTR, "50");

		table1Tr2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		table1Tr2Td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr2Td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				CELL_FOR_TABLE_STYLE);
		table1Tr2Td1.setAttribute(HtmlComponentUtil.HTML_COLSPAN_ATTR, "2");
		table1Tr2Td1Table2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		addBasicTableAttributes(table1Tr2Td1Table2);

		table1Tr2Td1Table2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				INNER_TABLE_STYLE);
		table1Tr2Td1Table2.setAttribute(
				HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table1Tr2Td1Table2.setAttribute(
				HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");

		table1Tr2Td1Table2Tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		table1Tr2Td1Table2Tr1Td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr2Td1Table2Tr1Td1.setAttribute(
				HtmlComponentUtil.HTML_CLASS_ATTR, "empty-cell-style");
		table1Tr2Td1Table2Tr1Td1.setAttribute(
				HtmlComponentUtil.HTML_WIDTH_ATTR, "50%");

		table1Tr2Td1Table2Tr1Td2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr2Td1Table2Tr1Td2.setAttribute(
				HtmlComponentUtil.HTML_CLASS_ATTR, "cell-with-picture");

		table1Tr2Td1Table2Tr1Td2Img = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		ComponentUtil.setImg(table1Tr2Td1Table2Tr1Td2Img, IMAGE_SLIDER);

		table1Tr2Td1Table2Tr1Td3 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		table1Tr2Td1Table2Tr1Td3.setAttribute(
				HtmlComponentUtil.HTML_CLASS_ATTR, "empty-cell-style");
		table1Tr2Td1Table2Tr1Td3.setAttribute(
				HtmlComponentUtil.HTML_WIDTH_ATTR, "50%");

		table1.appendChild(table1Tr1);
		table1.appendChild(table1Tr2);
		table1Tr1.appendChild(table1Tr1Td1);
		table1Tr1.appendChild(table1Tr1Td2);
		table1Tr1.appendChild(table1Tr1Td3);
		table1Tr2.appendChild(table1Tr2Td1);
		table1Tr1Td1.appendChild(table1Tr1Td1Text);
		table1Tr1Td2.appendChild(table1Tr1Td2Text);
		table1Tr1Td3.appendChild(table1Tr1Td3input);
		table1Tr2Td1.appendChild(table1Tr2Td1Table2);
		table1Tr2Td1Table2.appendChild(table1Tr2Td1Table2Tr1);
		table1Tr2Td1Table2Tr1.appendChild(table1Tr2Td1Table2Tr1Td1);
		table1Tr2Td1Table2Tr1.appendChild(table1Tr2Td1Table2Tr1Td2);
		table1Tr2Td1Table2Tr1.appendChild(table1Tr2Td1Table2Tr1Td3);
		table1Tr2Td1Table2Tr1Td2.appendChild(table1Tr2Td1Table2Tr1Td2Img);

		// 
		// set a default values
		//

		Object[] inPatams = new Element[2];
		inPatams[0] = (Element) table1;
		inPatams[1] = sourceNode;

		for (Map.Entry<String, Method> e : mapAttributeToMethod.entrySet()) {
			Method function = e.getValue();
			if (function != null) {
				try {
					function.invoke(this, inPatams);
				} catch (IllegalArgumentException e1) {
					RichFacesTemplatesActivator.getPluginLog().logWarning("IllegalArgumentException: " + e.getKey() +  ":" + e1.getMessage() );
				} catch (IllegalAccessException e2) {
					RichFacesTemplatesActivator.getPluginLog().logWarning("IllegalAccessException: " + e.getKey() +  ":" + e2.getMessage() );
				} catch (InvocationTargetException e3) {
					RichFacesTemplatesActivator.getPluginLog().logWarning("InvocationTargetException: " +  e3.getMessage() );
				}
			}
		}

		VpeCreationData creationData = new VpeCreationData(table1);
		return creationData;
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

		Method function = mapAttributeToMethod.get(name);
		if (function != null) {
			Object[] inPatams = new Element[2];
			inPatams[0] = (Element) visualNode;
			inPatams[1] = sourceElement;
			try {
				function.invoke(this, inPatams);
			} catch (IllegalArgumentException e1) {
				RichFacesTemplatesActivator.getPluginLog().logWarning("IllegalArgumentException: " + name +  ":" + e1.getMessage() );
			} catch (IllegalAccessException e2) {
				RichFacesTemplatesActivator.getPluginLog().logWarning("IllegalAccessException: " + name +  ":" + e2.getMessage() );
			} catch (InvocationTargetException e3) {
				RichFacesTemplatesActivator.getPluginLog().logWarning("InvocationTargetException: "  + name + ":"+  e3.getMessage() );
			}
		}
		correctArrowPosition(sourceElement, visualNode);
	}

	/**
	 * 
	 */
	public String getDefaultInputSize() {
		return HTML_INPUTSIZE_DEFAULT;
	}

	/**
	 * 
	 */
	public String getDefaultInputClass() {
		return INPUT_STYLE_CLASS;
	}

	/**
	 * Sets some attributes which necessary for displaying table as tree
	 * 
	 * @param tree
	 */
	private void addBasicTableAttributes(Element table) {
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "0");
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0");
	}

	/**
	 * 
	 * @param visualNode
	 * @return
	 */
	private Element getInputElement(Element visualNode) {
		Element table = (Element) visualNode;
		NodeList tableList = table.getChildNodes();
		Node tr = tableList.item(0);
		NodeList trList = tr.getChildNodes();
		Node td1 = trList.item(2);
		NodeList td1List = td1.getChildNodes();
		Element input = (Element) td1List.item(0);
		return input;
	}

	/**
	 * 
	 * @param visualNode
	 * @return
	 */
	private Node getMaxValueElement(Element visualNode) {
		Element table = (Element) visualNode;
		NodeList tableList = table.getChildNodes();
		Node tr = tableList.item(0);
		NodeList trList = tr.getChildNodes();
		Node td1 = trList.item(1);
		NodeList td1List = td1.getChildNodes();
		Node maxValue = td1List.item(0);
		return maxValue;
	}

	/**
	 * 
	 * @param visualNode
	 * @return
	 */
	private Node getMinValueElement(Element visualNode) {
		Element table = (Element) visualNode;
		NodeList tableList = table.getChildNodes();
		Node tr = tableList.item(0);
		NodeList trList = tr.getChildNodes();
		Node td1 = trList.item(0);
		NodeList td1List = td1.getChildNodes();
		Node minValue = td1List.item(0);
		return minValue;
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void showInput(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (getAttribute(SLIDER_SHOWINPUT_ATTR, sourceNode).equalsIgnoreCase(
				"false")) {
			input.setAttribute(HtmlComponentUtil.HTML_TYPE_ATTR,
					HTML_INPUTTYPE_HIDDEN);
		} else {
			input.setAttribute(HtmlComponentUtil.HTML_TYPE_ATTR,
					HTML_INPUTTYPE_TEXT);
		}
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void showBoundaryValues(Element visualNode, Element sourceNode) {
		setMaxValue(visualNode, sourceNode);
		setMinValue(visualNode, sourceNode);
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setInputSize(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (input != null) {
			input.setAttribute("size", getInputSize(sourceNode));
		}
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setInputClass(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (input != null) {
			String tmp = getInputClass(sourceNode);
			input.setAttribute("class", tmp);
		}
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setMaxValue(Element visualNode, Element sourceNode) {
		Node maxValue = getMaxValueElement(visualNode);
		if (maxValue != null) {
			if (getAttribute(SLIDER_SHOWBOUNDARY_ATTR, sourceNode)
					.equalsIgnoreCase("false")) {
				maxValue.setNodeValue("");
			} else {
				String tmp = getAttribute(SLIDER_MAXVALUE_ATTR, sourceNode);

				if (tmp.length() == 0) {
					maxValue.setNodeValue(SLIDER_MAXVALUE_DEFAULT);
				} else {
					maxValue.setNodeValue(tmp);
				}
			}
		}
		correctArrowPosition(sourceNode, visualNode);
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setMinValue(Element visualNode, Element sourceNode) {
		Node minValue = getMinValueElement(visualNode);
		if (minValue != null) {
			if (getAttribute(SLIDER_SHOWBOUNDARY_ATTR, sourceNode)
					.equalsIgnoreCase("false")) {
				minValue.setNodeValue("");
			} else {
				String tmp = getAttribute(SLIDER_MINVALUE_ATTR, sourceNode);
				if (tmp.length() == 0) {
					minValue.setNodeValue(SLIDER_MINVALUE_DEFAULT);
				} else {
					minValue.setNodeValue(tmp);
				}
			}
		}
		correctArrowPosition(sourceNode, visualNode);
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setWidth(Element visualNode, Element sourceNode) {
		Element table = visualNode;
		String tmp = getAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, sourceNode);
		if (tmp.length() == 0) {
			tmp = parseStyleWidth(sourceNode);
		}
		if (tmp.length() == 0) {
			table.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "200px;");
		} else {
			table.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, tmp);
		}

	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setStyle(Element visualNode, Element sourceNode) {
		/*
		 * Element table= visualNode; String tmp =
		 * getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,sourceNode);
		 */

	}

	/**
	 * Corrects arrow position accordinlye max, min and inputValues
	 * 
	 * @param sourceNode
	 * @param visulaNode
	 */
	private void correctArrowPosition(Element sourceNode, Node visulaNode) {

		String minValue = sourceNode.getAttribute(SLIDER_MINVALUE_ATTR);
		String maxValue = sourceNode.getAttribute(SLIDER_MAXVALUE_ATTR);
		String valueValue = sourceNode.getAttribute(SLIDER_VALUE_ATTR);
		String stepValue = sourceNode.getAttribute(SLIDER_STEP_ATTR);
		double min = 0;
		double max = 0;
		double value = 50;
		double step = 1;
		try {
			min = Integer.parseInt(minValue);
		} catch (NumberFormatException ex) {
			min = 0;
		}
		try {
			max = Integer.parseInt(maxValue);
		} catch (NumberFormatException ex) {
			max = 100;
		}

		try {
			value = Integer.parseInt(valueValue);
		} catch (NumberFormatException ex) {
			value = 50;
		}
		try {
			step = Integer.parseInt(stepValue);
			if (step < 1) {
				step=1;
			}
		} catch (NumberFormatException ex) {
			step = 1;
		}
		double h1 = ((roundForStep(step, value) - min) / (max - min)) * 100;
		double h2 = 100 - h1;
		Element table1Tr2Td1Table2Tr1Td1 = (Element) visulaNode.getChildNodes()
				.item(1).getChildNodes().item(0).getChildNodes().item(0)
				.getChildNodes().item(0).getChildNodes().item(0);
		table1Tr2Td1Table2Tr1Td1.setAttribute(
				HtmlComponentUtil.HTML_WIDTH_ATTR, (int) h1 + "%");
		Element table1Tr2Td1Table2Tr1Td2 = (Element) visulaNode.getChildNodes()
				.item(1).getChildNodes().item(0).getChildNodes().item(0)
				.getChildNodes().item(0).getChildNodes().item(2);
		table1Tr2Td1Table2Tr1Td2.setAttribute(
				HtmlComponentUtil.HTML_WIDTH_ATTR, (int) h2 + "%");
	}

	/**
	 * Round result accordinly step
	 * 
	 * @param step
	 * @param value
	 * @return
	 */
	private double roundForStep(double step, double value) {
		return Math.round(value / step) * step;
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setClass(Element visualNode, Element sourceNode) {
		Element table = visualNode;
		String tmp = getAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, sourceNode);
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				new StringBuffer().append(tmp).toString());
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setInputValue(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (input != null) {
			String tmp = getAttribute("value", sourceNode);
			if (tmp.length() == 0) {
				input.setAttribute("value", HTML_INPUTVALUE_DEFAULT);
			} else {
				input.setAttribute("value", tmp);
			}
		}
		correctArrowPosition(sourceNode, visualNode);
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setInputStyle(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (input != null) {
			String tmp = getAttribute("inputStyle", sourceNode);
			input.setAttribute("style", tmp);
		}
	}

	/**
	 * 
	 * @param visualNode
	 * @param sourceNode
	 */
	public void setMaxlength(Element visualNode, Element sourceNode) {
		Element input = getInputElement(visualNode);
		if (input != null) {
			String tmp = getAttribute("maxlength", sourceNode);
			input.setAttribute("maxlength", tmp);
		}
	}

}