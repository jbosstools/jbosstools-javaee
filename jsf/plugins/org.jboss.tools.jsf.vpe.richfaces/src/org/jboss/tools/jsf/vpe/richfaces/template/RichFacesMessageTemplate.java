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

import java.util.HashMap;

import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
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

    private static String VALIDATION_MESSAGE = "Validation message"; //$NON-NLS-1$

    protected static String PASSED_LABEL_ATTRIBUTE_NAME = "passedLabel"; //$NON-NLS-1$
    protected static String LABEL_CLASS_ATTRIBUTE_NAME = "labelClass"; //$NON-NLS-1$
    protected static String MARKER_CLASS_ATTRIBUTE_NAME = "markerClass"; //$NON-NLS-1$
    protected static String MARKER_STYLE_ATTRIBUTE_NAME = "markerStyle"; //$NON-NLS-1$

    protected static String ERROR_MARKER_CLASS_ATTRIBUTE_NAME = "errorMarkerClass"; //$NON-NLS-1$
    protected static String ERROR_LABEL_CLASS_ATTRIBUTE_NAME = "errorLabelClass"; //$NON-NLS-1$
    protected static String ERROR_CLASS_ATTRIBUTE_NAME = "errorClass"; //$NON-NLS-1$

    protected static String FATAL_MARKER_CLASS_ATTRIBUTE_NAME = "fatalMarkerClass"; //$NON-NLS-1$
    protected static String FATAL_LABEL_CLASS_ATTRIBUTE_NAME = "fatalLabelClass"; //$NON-NLS-1$
    protected static String FATAL_CLASS_ATTRIBUTE_NAME = "fatalClass"; //$NON-NLS-1$

    protected static String INFO_MARKER_CLASS_ATTRIBUTE_NAME = "infoMarkerClass"; //$NON-NLS-1$
    protected static String INFO_LABEL_CLASS_ATTRIBUTE_NAME = "infoLabelClass"; //$NON-NLS-1$
    protected static String INFO_CLASS_ATTRIBUTE_NAME = "infoClass"; //$NON-NLS-1$

    protected static String WARN_MARKER_CLASS_ATTRIBUTE_NAME = "warnMarkerClass"; //$NON-NLS-1$
    protected static String WARN_LABEL_CLASS_ATTRIBUTE_NAME = "warnLabelClass"; //$NON-NLS-1$
    protected static String WARN_CLASS_ATTRIBUTE_NAME = "warnClass"; //$NON-NLS-1$

    protected static String ERROR_MESSAGE = "Error message"; //$NON-NLS-1$
    protected static String FATAL_MESSAGE = "Fatal message"; //$NON-NLS-1$
    protected static String INFO_MESSAGE = "Info message"; //$NON-NLS-1$
    protected static String WARNING_MESSAGE = "Warning message"; //$NON-NLS-1$

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

    protected static String[] markers = { "passedMarker", "errorMarker", //$NON-NLS-1$ //$NON-NLS-2$
	    "fatalMarker", "infoMarker", "warnMarker" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    protected static String FACET_TAG_NAME = "facet"; //$NON-NLS-1$

    protected static String NAME_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

    private final static String MESSAGE_STYLE = "padding-left: 1px;padding-right: 1px;padding-top: 1px;padding-bottom: 1px"; //$NON-NLS-1$

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element sourceElement = (Element) sourceNode;

	VpeCreationData creationData;

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

	HashMap<String, Node> facets = getFacelets(sourceElement);

	if (facets.size() != 0) {
	    creationData = createVisualFacets(visualDocument, sourceElement,
		    facets);
	} else {
	    nsIDOMElement span = visualDocument
		    .createElement(HtmlComponentUtil.HTML_TAG_SPAN);

	    if (styleValue != null && !styleValue.trim().equals("")) //$NON-NLS-1$
		span
			.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				styleValue);
	    if (styleClassValue != null && !styleClassValue.trim().equals("")) //$NON-NLS-1$
		span.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			styleClassValue);
	    if (labelClassValue != null && !labelClassValue.trim().equals("")) //$NON-NLS-1$
		span.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			labelClassValue);

	    creationData = new VpeCreationData(span);

	    nsIDOMText passedText = visualDocument
		    .createTextNode(VALIDATION_MESSAGE);
	    span.appendChild(passedText);
	}

	return creationData;
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
     * Method for creating rich:message template if rich:message has facets
     * 
     * @param visualDocument
     * @param sourceElement
     * @param facets
     * @return
     */
    private VpeCreationData createVisualFacets(nsIDOMDocument visualDocument,
	    Element sourceElement, HashMap<String, Node> facets) {

	nsIDOMElement tableHeader = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	tableHeader.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
		MESSAGE_STYLE);

	VpeCreationData creationData = new VpeCreationData(tableHeader);

	nsIDOMElement tbody = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
	tbody.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN, "top"); //$NON-NLS-1$
	tableHeader.appendChild(tbody);

	nsIDOMElement tr = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TR);

	tbody.appendChild(tr);

	if (styleValue != null && !styleValue.trim().equals("")) //$NON-NLS-1$
	    tableHeader.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
		    styleValue);
	if (styleClassValue != null && !styleClassValue.trim().equals("")) //$NON-NLS-1$
	    tableHeader.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		    styleClassValue);

	for (int i = 0; i < markers.length; i++) {

	    if (facets.containsKey(markers[i])) {

		nsIDOMElement td = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_TD);

		switch (i) {
		case 0: // passed

		    if (markerClassValue != null
			    && !markerClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				markerClassValue);
		    if (markerStyleValue != null
			    && !markerStyleValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				markerStyleValue);
		    break;
		case 1: // error
		    if (errorClassValue != null
			    && !errorClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				errorClassValue);
		    if (errorMarkerClassValue != null
			    && !errorMarkerClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				errorMarkerClassValue);

		    break;
		case 2: // fatal
		    if (fatalClassValue != null
			    && !fatalClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				fatalClassValue);

		    if (fatalMarkerClassValue != null
			    && !fatalMarkerClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				fatalMarkerClassValue);

		    break;
		case 3: // info
		    if (infoClassValue != null
			    && !infoClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				infoClassValue);
		    if (infoMarkerClassValue != null
			    && !infoMarkerClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				infoMarkerClassValue);
		    break;
		case 4: // warn
		    if (warnClassValue != null
			    && !warnClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				warnClassValue);
		    if (warnMarkerClassValue != null
			    && !warnMarkerClassValue.trim().equals("")) //$NON-NLS-1$
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
				warnMarkerClassValue);

		    break;
		default:
		    break;
		}

		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
		creationData.addChildrenInfo(childrenInfo);

		if (!(facets.get(markers[i]) instanceof Element))
		    continue;
		else {
		    childrenInfo.addSourceChild(facets.get(markers[i]));
		}
		tr.appendChild(td);
	    }
	}

	nsIDOMElement td1 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);

	if (labelClassValue != null && !labelClassValue.trim().equals("")) //$NON-NLS-1$
	    td1
		    .setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			    labelClassValue);

	nsIDOMText passedText = visualDocument
		.createTextNode(VALIDATION_MESSAGE);
	tr.appendChild(td1);
	td1.appendChild(passedText);

	return creationData;
    }

    @Override
    public void setSourceAttributeSelection(VpePageContext pageContext,
	    Element sourceElement, int offset, int length, Object data) {
	VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	sourceBuilder.setSelection(sourceElement, 0, 0);
    }

    /**
     * Method for getting message facets
     * 
     * @param sourceElement
     * @return List of facets
     */
    protected HashMap<String, Node> getFacelets(Element sourceElement) {

	NodeList nodeList = sourceElement.getChildNodes();
	HashMap<String, Node> facets = new HashMap<String, Node>();

	for (int i = 0; i < nodeList.getLength(); i++) {

	    if (!(nodeList.item(i) instanceof Element))
		continue;

	    String facetName = nodeList.item(i).getPrefix() + ":" //$NON-NLS-1$
		    + FACET_TAG_NAME;

	    if (nodeList.item(i).getNodeName().equalsIgnoreCase(facetName)
		    && searchInMarker(((Element) nodeList.item(i))
			    .getAttribute(NAME_ATTRIBUTE_NAME))) {
		facets.put(((Element) nodeList.item(i))
			.getAttribute(NAME_ATTRIBUTE_NAME), nodeList.item(i));
	    }
	}

	return facets;
    }
}
