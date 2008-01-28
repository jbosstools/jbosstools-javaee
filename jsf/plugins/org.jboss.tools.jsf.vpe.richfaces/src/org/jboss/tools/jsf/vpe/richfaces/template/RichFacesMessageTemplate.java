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
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesMessageTemplate extends VpeAbstractTemplate {

    private static String VALIDATION_MESSAGE = "Validation message";

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

    private nsIDOMElement td1; // passed marker
    private nsIDOMElement td2; // passed label
    private final static String MESSAGE_STYLE = "padding-left: 1px;padding-right: 1px;padding-top: 1px;padding-bottom: 1px";
    protected VpeCreationData creationData;

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

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

    protected void createRichMessage(nsIDOMDocument visualDocument,
	    Node sourceNode) {

	NodeList nodeList = sourceNode.getChildNodes();

	for (int i = 0; i < nodeList.getLength(); i++) {

	    if (!(nodeList.item(i) instanceof Element))
		continue;

	    Element elemFacet = (Element) nodeList.item(i);
	    if (elemFacet.getNodeName().equalsIgnoreCase(FACET_TAG_NAME)
		    && searchInMarker(elemFacet
			    .getAttribute(NAME_ATTRIBUTE_NAME))) {

		// if f:facet not empty
		if (elemFacet.getChildNodes().getLength() != 0) {
		    nsIDOMElement tableHeader = visualDocument
			    .createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		    tableHeader.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
			    MESSAGE_STYLE);

		    creationData = new VpeCreationData(tableHeader);

		    nsIDOMElement tbody = visualDocument
			    .createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		    tbody.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN,
			    "top");
		    tableHeader.appendChild(tbody);

		    nsIDOMElement tr = visualDocument
			    .createElement(HtmlComponentUtil.HTML_TAG_TR);

		    if (styleValue != null && !styleValue.trim().equals(""))
			tableHeader.setAttribute(
				HtmlComponentUtil.HTML_STYLE_ATTR, styleValue);
		    if (styleClassValue != null
			    && !styleClassValue.trim().equals(""))
			tableHeader.setAttribute(
				HtmlComponentUtil.HTML_CLASS_ATTR,
				styleClassValue);

		    td1 = visualDocument
			    .createElement(HtmlComponentUtil.HTML_TAG_TD);

		    td2 = visualDocument
			    .createElement(HtmlComponentUtil.HTML_TAG_TD);

		    // set labelClass
		    if (labelClassValue != null
			    && !labelClassValue.trim().equals(""))
			td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				labelClassValue);

		    nsIDOMText passedText = visualDocument
			    .createTextNode(VALIDATION_MESSAGE);
		    createVisualFacet(td1, elemFacet);
		    tbody.appendChild(tr);
		    tr.appendChild(td1);
		    tr.appendChild(td2);
		    td2.appendChild(passedText);
		    return;
		}
	    }
	}

	nsIDOMElement span = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

	if (styleValue != null && !styleValue.trim().equals(""))
	    span.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, styleValue);
	if (styleClassValue != null && !styleClassValue.trim().equals(""))
	    span.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		    styleClassValue);

	creationData = new VpeCreationData(span);

	nsIDOMText passedText = visualDocument
		.createTextNode(VALIDATION_MESSAGE);
	span.appendChild(passedText);

    }

    /**
     * Checks, whether it is necessary to re-create an element at change of
     * attribute
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceElement
     *                The current element of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @param visualNode
     *                The current node of the visual tree.
     * @param data
     *                The arbitrary data, built by a method <code>create</code>
     * @param name
     *                Atrribute name
     * @param value
     *                Attribute value
     * @return <code>true</code> if it is required to re-create an element at
     *         a modification of attribute, <code>false</code> otherwise.
     */
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

    /**
     * 
     * @param td01
     */
    protected void addNotFacetComponent(nsIDOMElement td01, Node sourceNode) {

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
     *                Marker name
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
    protected void createVisualFacet(nsIDOMElement td, Element elemFacet) {
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
