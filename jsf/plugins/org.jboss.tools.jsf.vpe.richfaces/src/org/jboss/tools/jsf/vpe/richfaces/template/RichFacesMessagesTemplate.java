package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class RichFacesMessagesTemplate extends RichFacesMessageTemplate {

	private static String LAYOUT_ATTRIBUTE_NAME = "layout";
	private static String LAYOUT_ATTRIBUTE_VALUE_TABLE = "table";

	private String layoutValue;

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

		layoutValue = ((Element) sourceNode)
				.getAttribute(LAYOUT_ATTRIBUTE_NAME);

		if (layoutValue != null
				&& LAYOUT_ATTRIBUTE_VALUE_TABLE.equalsIgnoreCase(layoutValue
						.trim()))
			createTableLayout(visualDocument, sourceNode);
		else
			createListLayout(visualDocument, sourceNode);

		return creationData;
	}

	/**
	 * Create <rich:messages> with layout="table"
	 */
	public void createTableLayout(Document visualDocument, Node sourceNode) {

		createRichMessage(visualDocument, sourceNode);

	}

	/**
	 * Create <rich:messages> with layout="list"
	 */
	public void createListLayout(Document visualDocument, Node sourceNode) {

		Element table = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		if (styleValue != null && !styleValue.trim().equals(""))
			table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, styleValue);
		if (styleClassValue != null && !styleClassValue.trim().equals(""))
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					styleClassValue);

		creationData = new VpeCreationData(table);

		Element tr = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		Element td = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// create first td for PASSED
		Element td1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		Element span1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set markerClass
		if (markerClassValue != null && !markerClassValue.trim().equals(""))
			span1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					markerClassValue);

		// set markerStyle
		if (markerStyleValue != null && !markerStyleValue.trim().equals(""))
			span1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					markerStyleValue);

		Element span2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		if (labelClassValue != null && !labelClassValue.trim().equals(""))
			span2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					labelClassValue);

		Text passedText = visualDocument
				.createTextNode(passedLabelValue == null ? ""
						: passedLabelValue);
		// -----------------------------------------------------------

		// Create second td for ERROR
		Element td2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		// set errorClass
		if (errorClassValue != null && !errorClassValue.trim().equals(""))
			td2
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							errorClassValue);

		Element span3 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set errorMarkerClass
		if (errorMarkerClassValue != null
				&& !errorMarkerClassValue.trim().equals(""))
			span3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					errorMarkerClassValue);

		Element span4 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set errorLabelClass
		if (errorLabelClassValue != null
				&& !errorLabelClassValue.trim().equals(""))
			span4.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					errorLabelClassValue);

		Text errorText = visualDocument.createTextNode(ERROR_MESSAGE);
		// -------------------------------------------------------------

		// Create third td for FATAL
		Element td3 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set fatalClass
		if (fatalClassValue != null && !fatalClassValue.trim().equals(""))
			td3
					.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							fatalClassValue);

		Element span5 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set fatalMarkerClass
		if (fatalMarkerClassValue != null
				&& !fatalMarkerClassValue.trim().equals(""))
			span5.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					fatalMarkerClassValue);

		Element span6 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set fatalLabelClass
		if (fatalLabelClassValue != null
				&& !fatalLabelClassValue.trim().equals(""))
			span6.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					fatalLabelClassValue);

		Text fatalText = visualDocument.createTextNode(FATAL_MESSAGE);
		// ---------------------------------------------------------------------------

		// Create four td for INFO

		Element td4 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set infoClass
		if (infoClassValue != null && !infoClassValue.trim().equals(""))
			td4.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, infoClassValue);

		Element span7 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set infoMarkerClass
		if (infoMarkerClassValue != null
				&& !infoMarkerClassValue.trim().equals(""))
			span7.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					infoMarkerClassValue);

		Element span8 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set infoLabelClass
		if (infoLabelClassValue != null
				&& !infoLabelClassValue.trim().equals(""))
			span8.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					infoLabelClassValue);

		Text infoText = visualDocument.createTextNode(INFO_MESSAGE);
		// --------------------------------------------------------------------

		// Create fifth for WARN

		Element td5 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);

		// set warnClass
		if (warnClassValue != null && !warnClassValue.trim().equals(""))
			td5.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, warnClassValue);

		Element span9 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set warnMarkerClass
		if (warnMarkerClassValue != null
				&& !warnMarkerClassValue.trim().equals(""))
			span9.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					warnMarkerClassValue);

		Element span10 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

		// set warnLabelClass
		if (warnLabelClassValue != null
				&& !warnLabelClassValue.trim().equals(""))
			span10.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					warnLabelClassValue);

		Text warnText = visualDocument.createTextNode(WARNING_MESSAGE);
		// ---------------------------------------------------------------------

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

				if (elemFacet.getChildNodes().getLength() != 0)
					if (markers[0].equalsIgnoreCase(markerName)) {
						createVisualFacet(span1, elemFacet);
					} else if (markers[1].equalsIgnoreCase(markerName)) {
						createVisualFacet(span3, elemFacet);
					} else if (markers[2].equalsIgnoreCase(markerName)) {
						createVisualFacet(span5, elemFacet);
					} else if (markers[3].equalsIgnoreCase(markerName)) {
						createVisualFacet(span7, elemFacet);
					} else if (markers[4].equalsIgnoreCase(markerName)) {
						createVisualFacet(span9, elemFacet);
					}
			}
		}

		addNotFacetComponent(td, sourceNode);

		table.appendChild(tr);
		tr.appendChild(td);

		tr.appendChild(td1);
		td1.appendChild(span1);
		td1.appendChild(span2);
		span2.appendChild(passedText);

		tr.appendChild(td2);
		td2.appendChild(span3);
		td2.appendChild(span4);
		span4.appendChild(errorText);

		tr.appendChild(td3);
		td3.appendChild(span5);
		td3.appendChild(span6);
		span6.appendChild(fatalText);

		tr.appendChild(td4);
		td4.appendChild(span7);
		td4.appendChild(span8);
		span8.appendChild(infoText);

		tr.appendChild(td5);
		td5.appendChild(span9);
		td5.appendChild(span10);
		span10.appendChild(warnText);
	}
}