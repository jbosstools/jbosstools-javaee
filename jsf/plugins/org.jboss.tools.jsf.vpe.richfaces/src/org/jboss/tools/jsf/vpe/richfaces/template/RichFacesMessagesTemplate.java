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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesMessagesTemplate extends RichFacesMessageTemplate {

    private static final String LAYOUT = "layout"; //$NON-NLS-1$
    private static final String LIST = "list"; //$NON-NLS-1$
    private static final String TABLE = "table"; //$NON-NLS-1$
    private static final String ITERATOR = "iterator"; //$NON-NLS-1$
    
    private static final String CSS_RICH_MESSAGES = "rich-messages"; //$NON-NLS-1$
    private static final String CSS_RICH_MESSAGES_MARKER = "rich-messages-marker"; //$NON-NLS-1$
    private static final String CSS_RICH_MESSAGES_LABEL = "rich-messages-label"; //$NON-NLS-1$
    
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
		.getAttribute(HTML.ATTR_STYLE);
	styleClassValue = ((Element) sourceNode)
		.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	
	String styleClass = CSS_RICH_MESSAGES;
	String layout = ((Element) sourceNode).getAttribute(LAYOUT);
	nsIDOMElement container = null;
	if (TABLE.equalsIgnoreCase(layout)) {
	    container = visualDocument.createElement(HTML.TAG_TABLE);
	    container.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
	    container.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
	} else {
	    /*
	     * If layout is either list or iterator or not specified
	     * use list layout by default.
	     */
	    container = visualDocument.createElement(HTML.TAG_DL);
	}
	
	if (ComponentUtil.isNotBlank(styleValue)) {
	    container.setAttribute(HTML.ATTR_STYLE, styleValue);
	}
	if (ComponentUtil.isNotBlank(styleClassValue)) {
	    styleClass += Constants.WHITE_SPACE + styleClassValue;
	}
	container.setAttribute(HTML.ATTR_CLASS, styleClass);
	
	creationData = new VpeCreationData(container);

	HashMap<String, Node> facets = getFacelets((Element) sourceNode);

	for (int i = 0; i < markers.length; i++) {
	    if (facets.containsKey(markers[i])) {
		container.appendChild(createVisualMessage(creationData,
			visualDocument, layout, i, (Element) facets.get(markers[i])));
	    } else {
		container.appendChild(createVisualMessage(creationData,
			visualDocument, layout, i, null));
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
	    nsIDOMDocument visualDocument, String layout, int markerNum,
	    Element facet) {

	String containerClass = Constants.EMPTY;
	String markerClass = CSS_RICH_MESSAGES_MARKER;
	String labelClass = CSS_RICH_MESSAGES_LABEL;
	
	String labelMessage = Constants.EMPTY;
	nsIDOMElement topContainer = null;
	nsIDOMElement container = null;
	nsIDOMElement marker = null;
	nsIDOMElement label = null;

	if (TABLE.equalsIgnoreCase(layout)) {
	    nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	    container = visualDocument.createElement(HTML.TAG_TD);
	    marker = visualDocument.createElement(HTML.TAG_SPAN);
	    label = visualDocument.createElement(HTML.TAG_SPAN);
	    tr.appendChild(container);
	    container.appendChild(marker);
	    container.appendChild(label);
	    topContainer = tr;
	} else {
	    /*
	     * If layout is either list or iterator or not specified use list
	     * layout by default.
	     */
	    container = visualDocument.createElement(HTML.TAG_DT);
	    marker = visualDocument.createElement(HTML.TAG_SPAN);
	    label = visualDocument.createElement(HTML.TAG_SPAN);
	    container.appendChild(marker);
	    container.appendChild(label);
	    topContainer = container;
	}

	if (facet != null) {
	    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(marker);
	    creationData.addChildrenInfo(childrenInfo);
	    childrenInfo.addSourceChild(facet);
	}
	/*
	 * apply styles and classes
	 */
	switch (markerNum) {
	case 0: // passed
	    labelMessage = (passedLabelValue == null) ? Constants.EMPTY : passedLabelValue;
	    if (ComponentUtil.isNotBlank(markerClassValue)) {
		markerClass += Constants.WHITE_SPACE + markerClassValue;
	    }
	    if (ComponentUtil.isNotBlank(labelClassValue)) {
		labelClass += Constants.WHITE_SPACE + labelClassValue;
	    }
	    break;
	case 1: // error
	    labelMessage = ERROR_MESSAGE;
	    if (ComponentUtil.isNotBlank(errorClassValue)) {
		containerClass += Constants.WHITE_SPACE + errorClassValue;
	    }
	    if (ComponentUtil.isNotBlank(errorMarkerClassValue)) {
		markerClass += Constants.WHITE_SPACE + errorMarkerClassValue;
	    }
	    if (ComponentUtil.isNotBlank(errorLabelClassValue)) {
		labelClass += Constants.WHITE_SPACE + errorLabelClassValue;
	    }
	    break;
	case 2: // fatal
	    labelMessage = FATAL_MESSAGE;
	    if (ComponentUtil.isNotBlank(fatalClassValue)) {
		containerClass += Constants.WHITE_SPACE + fatalClassValue;
	    }
	    if (ComponentUtil.isNotBlank(fatalMarkerClassValue)) {
		markerClass += Constants.WHITE_SPACE + fatalMarkerClassValue;
	    }
	    if (ComponentUtil.isNotBlank(fatalLabelClassValue)) {
		labelClass += Constants.WHITE_SPACE + fatalLabelClassValue;
	    }
	    break;
	case 3: // info
	    labelMessage = INFO_MESSAGE;
	    if (ComponentUtil.isNotBlank(infoClassValue)) {
		containerClass += Constants.WHITE_SPACE + infoClassValue;
	    }
	    if (ComponentUtil.isNotBlank(infoMarkerClassValue)) {
		markerClass += Constants.WHITE_SPACE + infoMarkerClassValue;
	    }
	    if (ComponentUtil.isNotBlank(infoLabelClassValue)) {
		labelClass += Constants.WHITE_SPACE + infoLabelClassValue;
	    }
	    break;
	case 4: // warn
	    labelMessage = WARNING_MESSAGE;
	    if (ComponentUtil.isNotBlank(warnClassValue)) {
		containerClass += Constants.WHITE_SPACE + warnClassValue;
	    }
	    if (ComponentUtil.isNotBlank(warnMarkerClassValue)) {
		markerClass += Constants.WHITE_SPACE + warnMarkerClassValue;
	    }
	    if (ComponentUtil.isNotBlank(warnLabelClassValue)) {
		labelClass += Constants.WHITE_SPACE + warnLabelClassValue;
	    }
	    break;
	default:
	    break;
	}

	if (ComponentUtil.isNotBlank(containerClass)) {
	    container.setAttribute(HTML.ATTR_CLASS, containerClass);
	}
	marker.setAttribute(HTML.ATTR_CLASS, markerClass);
	label.setAttribute(HTML.ATTR_CLASS, labelClass);

	nsIDOMText text = visualDocument.createTextNode(labelMessage);
	label.appendChild(text);
	return topContainer;
    }

}