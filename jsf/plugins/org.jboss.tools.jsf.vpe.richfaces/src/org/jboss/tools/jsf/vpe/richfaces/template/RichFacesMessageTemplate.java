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

import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesMessageTemplate extends VpeAbstractTemplate {

	protected static String PASSED_LABEL_ATTRIBUTE_NAME = "passedLabel";
	protected static String LABEL_CLASS_ATTRIBUTE_NAME = "labelClass";
	protected static String MARKER_CLASS_ATTRIBUTE_NAME = "markerClass";
	protected static String MARKER_STYLE_ATTRIBUTE_NAME = "markerStyle";

	protected static String ERROR_MARKER_CLASS_ATTRIBUTE_NAME = "errorMarkerClass";
	protected static String ERROR_LABEL_CLASS_ATTRIBUTE_NAME = "errorLabelClass";
	protected static String ERROR_CLASS_ATTRIBUTE_NAME = "errorClass";

	protected static String FATAL_MARKER_CLASS_ATTRIBUTE_NAME = "fatalMarkerClass";
	protected static String FATAL_LABEL_CLASS_ATTRIBUTE_NAME = "fatalLabelClass";
	protected static String FATAL_CLASS_ATTRIBUTE_NAME = "fatalClass";

	protected static String INFO_MARKER_CLASS_ATTRIBUTE_NAME = "infoMarkerClass";
	protected static String INFO_LABEL_CLASS_ATTRIBUTE_NAME = "infoLabelClass";
	protected static String INFO_CLASS_ATTRIBUTE_NAME = "infoClass";

	protected static String WARN_MARKER_CLASS_ATTRIBUTE_NAME = "warnMarkerClass";
	protected static String WARN_LABEL_CLASS_ATTRIBUTE_NAME = "warnLabelClass";
	protected static String WARN_CLASS_ATTRIBUTE_NAME = "warnClass";

	protected static String ERROR_MESSAGE = "Error message";
	protected static String FATAL_MESSAGE = "Fatal message";
	protected static String INFO_MESSAGE = "Info message";
	protected static String WARNING_MESSAGE = "Warning message";

	protected String passedLabelValue;
	protected String labelClassValue;
	protected String markerClassValue;
	protected String markerStyleValue;
	protected String errorMarkerClassValue;
	protected String errorLabelClassValue;
	protected String errorClassValue;
	protected String fatalMarkerClassValue;
	protected String fatalLabelClassValue;
	protected String fatalClassValue;
	protected String infoMarkerClassValue;
	protected String infoLabelClassValue;
	protected String infoClassValue;
	protected String warnMarkerClassValue;
	protected String warnLabelClassValue;
	protected String warnClassValue;
	protected String styleValue;
	protected String styleClassValue;

	protected static String[] markers = { "passedMarker", "errorMarker",
			"fatalMarker", "infoMarker", "warnMarker" };

	protected static String FACET_TAG_NAME = "f:facet";

	protected static String NAME_ATTRIBUTE_NAME = "name";

	private Element td1; // passed marker
	private Element td2; // passed label

	private Element tr2; // error message
	private Element td3; // error marker
	private Element td4; // error label

	private Element tr3; // fatal message
	private Element td5; // fatal marker
	private Element td6; // fatal label

	private Element tr4; // info message
	private Element td7; // info marker
	private Element td8; // info label

	private Element tr5; // warn message
	private Element td9; // warn marker
	private Element td10; // warn label

	protected VpeCreationData creationData;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {

		passedLabelValue = ((Element) sourceNode)
				.getAttribute(PASSED_LABEL_ATTRIBUTE_NAME);
		labelClassValue = ((Element) sourceNode)
				.getAttribute(LABEL_CLASS_ATTRIBUTE_NAME);
		markerClassValue = ((Element) sourceNode)
				.getAttribute(MARKER_CLASS_ATTRIBUTE_NAME);
		markerStyleValue = ((Element) sourceNode)
				.getAttribute(MARKER_STYLE_ATTRIBUTE_NAME);

		errorMarkerClassValue = ((Element) sourceNode)
				.getAttribute(ERROR_MARKER_CLASS_ATTRIBUTE_NAME);
		errorLabelClassValue = ((Element) sourceNode)
				.getAttribute(ERROR_LABEL_CLASS_ATTRIBUTE_NAME);
		errorClassValue = ((Element) sourceNode)
				.getAttribute(ERROR_CLASS_ATTRIBUTE_NAME);

		fatalMarkerClassValue = ((Element) sourceNode)
				.getAttribute(FATAL_MARKER_CLASS_ATTRIBUTE_NAME);
		fatalLabelClassValue = ((Element) sourceNode)
				.getAttribute(FATAL_LABEL_CLASS_ATTRIBUTE_NAME);
		fatalClassValue = ((Element) sourceNode)
				.getAttribute(FATAL_CLASS_ATTRIBUTE_NAME);

		infoMarkerClassValue = ((Element) sourceNode)
				.getAttribute(INFO_MARKER_CLASS_ATTRIBUTE_NAME);
		infoLabelClassValue = ((Element) sourceNode)
				.getAttribute(INFO_LABEL_CLASS_ATTRIBUTE_NAME);
		infoClassValue = ((Element) sourceNode)
				.getAttribute(INFO_CLASS_ATTRIBUTE_NAME);

		warnMarkerClassValue = ((Element) sourceNode)
				.getAttribute(WARN_MARKER_CLASS_ATTRIBUTE_NAME);
		warnLabelClassValue = ((Element) sourceNode)
				.getAttribute(WARN_LABEL_CLASS_ATTRIBUTE_NAME);
		warnClassValue = ((Element) sourceNode)
				.getAttribute(WARN_CLASS_ATTRIBUTE_NAME);

		styleValue = ((Element) sourceNode)
				.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		styleClassValue = ((Element) sourceNode)
				.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);

		createRichMessage(visualDocument, sourceNode);

		return creationData;
	}

	protected void createRichMessage(Document visualDocument, Node sourceNode) {

		// -------------------create common table
		Element tableHeader = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		creationData = new VpeCreationData(tableHeader);

		Element tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// in this td append not f:facet
		Element td01 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		Element td02 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// ----------------create second table
		Element table = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		if (styleValue != null && !styleValue.trim().equals(""))
			table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, styleValue);
		if (styleClassValue != null && !styleClassValue.trim().equals(""))
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					styleClassValue);

		// Create first row PASSED
		Element tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		td1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		td1.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		// set markerClass
		if (markerClassValue != null && !markerClassValue.trim().equals(""))
			td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					markerClassValue);

		// set markerStyle
		if (markerStyleValue != null && !markerStyleValue.trim().equals(""))
			td1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					markerStyleValue);

		td2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set labelClass
		if (labelClassValue != null && !labelClassValue.trim().equals(""))
			td2
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							labelClassValue);

		Text passedText = visualDocument
				.createTextNode(passedLabelValue == null ? ""
						: passedLabelValue);
		// ---------------------------------------------------------------------

		// Create second row ERROR
		tr2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// set errorClass
		if (errorClassValue != null && !errorClassValue.trim().equals(""))
			tr2
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							errorClassValue);

		td3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td3.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		// set errorMarkerClass
		if (errorMarkerClassValue != null
				&& !errorMarkerClassValue.trim().equals(""))
			td3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					errorMarkerClassValue);

		td4 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set errorLabelClass
		if (errorLabelClassValue != null
				&& !errorLabelClassValue.trim().equals(""))
			td4.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					errorLabelClassValue);

		Text errorText = visualDocument.createTextNode(ERROR_MESSAGE);
		// ---------------------------------------------------------------------

		// Create third row FATAL
		tr3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// set fatalClass
		if (fatalClassValue != null && !fatalClassValue.trim().equals(""))
			tr3
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							fatalClassValue);

		td5 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td5.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		// set fatalMarkerClass
		if (fatalMarkerClassValue != null
				&& !fatalMarkerClassValue.trim().equals(""))
			td5.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					fatalMarkerClassValue);

		td6 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set fatalLabelClass
		if (fatalLabelClassValue != null
				&& !fatalLabelClassValue.trim().equals(""))
			td6.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					fatalLabelClassValue);

		Text fatalText = visualDocument.createTextNode(FATAL_MESSAGE);
		// ---------------------------------------------------------------------

		// Create four row INFO
		tr4 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// set infoClass
		if (infoClassValue != null && !infoClassValue.trim().equals(""))
			tr4.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, infoClassValue);

		td7 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td7.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		// set infoMarkerClass
		if (infoMarkerClassValue != null
				&& !infoMarkerClassValue.trim().equals(""))
			td7.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					infoMarkerClassValue);

		td8 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set infoLabelClass
		if (infoLabelClassValue != null
				&& !infoLabelClassValue.trim().equals(""))
			td8.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					infoLabelClassValue);

		Text infoText = visualDocument.createTextNode(INFO_MESSAGE);
		// ---------------------------------------------------------------------

		// Create fifth row WARNING
		tr5 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

		// set warnClass
		if (warnClassValue != null && !warnClassValue.trim().equals(""))
			tr5.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, warnClassValue);

		td9 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		td9.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR,
				HtmlComponentUtil.HTML_ALIGN_RIGHT_VALUE);

		// set warnMarkerClass
		if (warnMarkerClassValue != null
				&& !warnMarkerClassValue.trim().equals(""))
			td9.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					warnMarkerClassValue);

		td10 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set warnLabelClass
		if (warnLabelClassValue != null
				&& !warnLabelClassValue.trim().equals(""))
			td10.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					warnLabelClassValue);

		Text warnText = visualDocument.createTextNode(WARNING_MESSAGE);

		NodeList nodeList = sourceNode.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {

			if (!(nodeList.item(i) instanceof Element))
				continue;

			Element elemFacet = (Element) nodeList.item(i);
			if (elemFacet.getNodeName().equalsIgnoreCase(FACET_TAG_NAME)
					&& searchInMarker(elemFacet
							.getAttribute(NAME_ATTRIBUTE_NAME))) {

				String markerName = elemFacet.getAttribute(NAME_ATTRIBUTE_NAME)
						.trim();
				// if f:facet not empty
				if (elemFacet.getChildNodes().getLength() != 0)
					if (markers[0].equalsIgnoreCase(markerName)) {
						createVisualFacet(td1, elemFacet);
					} else if (markers[1].equalsIgnoreCase(markerName)) {
						createVisualFacet(td3, elemFacet);
					} else if (markers[2].equalsIgnoreCase(markerName)) {
						createVisualFacet(td5, elemFacet);
					} else if (markers[3].equalsIgnoreCase(markerName)) {
						createVisualFacet(td7, elemFacet);
					} else if (markers[4].equalsIgnoreCase(markerName)) {
						createVisualFacet(td9, elemFacet);
					}
			}
		}

		addNotFacetComponent(td01, sourceNode);

		tableHeader.appendChild(tr);
		tr.appendChild(td01);
		tr.appendChild(td02);
		td02.appendChild(table);

		table.appendChild(tr1);
		tr1.appendChild(td1);
		tr1.appendChild(td2);
		td2.appendChild(passedText);

		table.appendChild(tr2);
		tr2.appendChild(td3);
		tr2.appendChild(td4);
		td4.appendChild(errorText);

		table.appendChild(tr3);
		tr3.appendChild(td5);
		tr3.appendChild(td6);
		td6.appendChild(fatalText);

		table.appendChild(tr4);
		tr4.appendChild(td7);
		tr4.appendChild(td8);
		td8.appendChild(infoText);

		table.appendChild(tr5);
		tr5.appendChild(td9);
		tr5.appendChild(td10);
		td10.appendChild(warnText);
	}

	/**
	 * Checks, whether it is necessary to re-create an element at change of
	 * attribute
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceElement
	 *            The current element of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param visualNode
	 *            The current node of the visual tree.
	 * @param data
	 *            The arbitrary data, built by a method <code>create</code>
	 * @param name
	 *            Atrribute name
	 * @param value
	 *            Attribute value
	 * @return <code>true</code> if it is required to re-create an element at
	 *         a modification of attribute, <code>false</code> otherwise.
	 */
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Element visualNode,
			Object data, String name, String value) {
		return true;
	}

	/**
	 * 
	 * @param td01
	 */
	protected void addNotFacetComponent(Element td01, Node sourceNode) {

		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td01);
		creationData.addChildrenInfo(childrenInfo);

		NodeList nodeList = sourceNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
			if (!FACET_TAG_NAME.equalsIgnoreCase(nodeList.item(i).getNodeName()
					.trim()))
				childrenInfo.addSourceChild(nodeList.item(i));

	}

	/**
	 * 
	 * @param markerName
	 *            Marker name
	 * @return True if marker name correct or false
	 */
	protected boolean searchInMarker(String markerName) {

		if (markerName == null)
			return false;

		for (int i = 0; i < markers.length; i++)
			if (markers[i].equalsIgnoreCase(markerName.trim()))
				return true;
		return false;
	}

	/**
	 * 
	 * @param td
	 * @param elemFacet
	 */
	protected void createVisualFacet(Element td, Element elemFacet) {
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
		creationData.addChildrenInfo(childrenInfo);

		NodeList nodeList = elemFacet.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++)
			if (!(nodeList.item(i) instanceof Element))
				continue;
			else {
				childrenInfo.addSourceChild(nodeList.item(i));
				return;
			}
	}
}

// HTML code component
// <html>
// <body>
//
// <table border="1px" style="border: 2px solid black;">
// <tr>
// <td>
// ssdfsd
// <p/>
// sdfsdf
// </td>
// <td>
// <table border="1px" style="color: red; border: 2px solid blue;" >
// <tr>
// <td></td>
// <td>Passed message</td>
// </tr>
// <tr>
// <td><img/></td>
// <td>Error message</td>
// </tr>
// <tr>
// <td></td>
// <td>Fatal message</td>
// </tr>
// <tr>
// <td></td>
// <td>Info message</td>
// </tr>
// <tr>
// <td></td>
// <td>Warn message</td>
// </tr>
// </table>
// </td>
// <tr>
// </table>
// </body>
// </html>
