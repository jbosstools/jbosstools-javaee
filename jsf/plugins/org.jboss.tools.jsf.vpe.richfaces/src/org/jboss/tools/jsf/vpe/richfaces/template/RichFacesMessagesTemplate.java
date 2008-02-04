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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesMessagesTemplate extends RichFacesMessageTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

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

	nsIDOMElement table = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

	if (styleValue != null && !styleValue.trim().equals(""))
	    table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, styleValue);
	if (styleClassValue != null && !styleClassValue.trim().equals(""))
	    table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
		    styleClassValue);

	creationData = new VpeCreationData(table);

	HashMap<String, Node> facets = getFacelets((Element) sourceNode);

	for (int i = 0; i < markers.length; i++) {
	    if (facets.containsKey(markers[i])) {
		table.appendChild(createVisualMessage(creationData,
			visualDocument, i, (Element) facets.get(markers[i])));
	    } else {
		table.appendChild(createVisualMessage(creationData,
			visualDocument, i, null));
	    }
	}

	return creationData;
    }

    /**
     * Method for creating rich:message template if rich:message has facets
     * 
     * @param visualDocument
     * @param sourceElement
     * @param facets
     * @return
     */
    private nsIDOMElement createVisualMessage(VpeCreationData creationData,
	    nsIDOMDocument visualDocument, int markerNum, Element facet) {

	String labelMessage = "";

	nsIDOMElement tr = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TR);

	nsIDOMElement td1 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);

	nsIDOMElement td2 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_TD);
	if (facet != null) {
	    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td1);
	    creationData.addChildrenInfo(childrenInfo);
	    childrenInfo.addSourceChild(facet);
	}
	// apply styles and classes
	switch (markerNum) {
	case 0: // passed
	    labelMessage = (passedLabelValue == null) ? "" : passedLabelValue;
	    if (labelClassValue != null && !labelClassValue.trim().equals(""))
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			labelClassValue);
	    if (markerClassValue != null && !markerClassValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			markerClassValue);
	    if (markerStyleValue != null && !markerStyleValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
			markerStyleValue);
	    break;
	case 1: // error
	    if (errorClassValue != null && !errorClassValue.trim().equals(""))
		tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			errorClassValue);
	    if (errorMarkerClassValue != null
		    && !errorMarkerClassValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			errorMarkerClassValue);
	    if (errorLabelClassValue != null
		    && !errorLabelClassValue.trim().equals(""))
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			errorLabelClassValue);

	    labelMessage = ERROR_MESSAGE;
	    break;
	case 2: // fatal
	    labelMessage = FATAL_MESSAGE;
	    if (fatalClassValue != null && !fatalClassValue.trim().equals(""))
		tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			fatalClassValue);
	    if (fatalMarkerClassValue != null
		    && !fatalMarkerClassValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			fatalMarkerClassValue);
	    if (fatalLabelClassValue != null
		    && !fatalLabelClassValue.trim().equals(""))
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			fatalLabelClassValue);
	    break;
	case 3: // info
	    labelMessage = INFO_MESSAGE;
	    if (infoClassValue != null && !infoClassValue.trim().equals(""))
		tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			infoClassValue);
	    if (infoMarkerClassValue != null
		    && !infoMarkerClassValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			infoMarkerClassValue);
	    if (infoLabelClassValue != null
		    && !infoLabelClassValue.trim().equals(""))
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			infoLabelClassValue);
	    break;
	case 4: // warn
	    labelMessage = WARNING_MESSAGE;
	    if (warnClassValue != null && !warnClassValue.trim().equals(""))
		tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			warnClassValue);
	    if (warnMarkerClassValue != null
		    && !warnMarkerClassValue.trim().equals(""))
		td1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			warnMarkerClassValue);
	    if (warnLabelClassValue != null
		    && !warnLabelClassValue.trim().equals(""))
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			warnLabelClassValue);
	    break;
	default:
	    break;
	}

	if (labelClassValue != null && !labelClassValue.trim().equals(""))
	    td2
		    .setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
			    labelClassValue);
	nsIDOMText text = visualDocument.createTextNode(labelMessage);
	tr.appendChild(td1);
	tr.appendChild(td2);
	td2.appendChild(text);
	return tr;
    }

}