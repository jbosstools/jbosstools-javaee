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
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
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
    private static final String TABLE = "table"; //$NON-NLS-1$
    private static final String CSS_RICH_MESSAGES = "rich-messages"; //$NON-NLS-1$
    private static final String CSS_RICH_MESSAGES_MARKER = "rich-messages-marker"; //$NON-NLS-1$
    private static final String CSS_RICH_MESSAGES_LABEL = "rich-messages-label"; //$NON-NLS-1$

    @Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

		VpeCreationData creationData = null;
		final Element sourceElement = (Element) sourceNode;
		final Attributes attrs = new Attributes(sourceElement);
		String styleClass = CSS_RICH_MESSAGES;
		final String layout = ((Element) sourceNode).getAttribute(LAYOUT);
	
		final nsIDOMElement container;
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
	
		if (ComponentUtil.isNotBlank(attrs.getStyleValue())) {
		    container.setAttribute(HTML.ATTR_STYLE, attrs.getStyleValue());
		}
		if (ComponentUtil.isNotBlank(attrs.getStyleClassValue())) {
		    styleClass += Constants.WHITE_SPACE + attrs.getStyleClassValue();
		}
		container.setAttribute(HTML.ATTR_CLASS, styleClass);
	
		creationData = new VpeCreationData(container);
	
		final HashMap<String, Node> facets = getFacelets((Element) sourceNode);
	
		for (int i = 0; i < markers.length; i++) {
		    if (facets.containsKey(markers[i])) {
				container.appendChild(createVisualMessage(creationData,
						visualDocument,	layout,	i,
						(Element) facets.get(markers[i]), attrs));
		    } else {
				container.appendChild(createVisualMessage(creationData,
						visualDocument, layout, i, null, attrs));
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
    		Element facet, Attributes attrs) {

		String containerClass = Constants.EMPTY;
		String markerClass = CSS_RICH_MESSAGES_MARKER;
		String labelClass = CSS_RICH_MESSAGES_LABEL;
	
		final String labelMessage;
		final nsIDOMElement topContainer;
		final nsIDOMElement container;
		final nsIDOMElement marker = visualDocument
				.createElement(HTML.TAG_SPAN);
		final nsIDOMElement label = visualDocument
				.createElement(HTML.TAG_SPAN);
	
		if (TABLE.equalsIgnoreCase(layout)) {
		    final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		    container = visualDocument.createElement(HTML.TAG_TD);
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
		    container.appendChild(marker);
		    container.appendChild(label);
		    topContainer = container;
		}
	
		if (facet != null) {
		    final VpeChildrenInfo childrenInfo = new VpeChildrenInfo(marker);
			marker.setAttribute(VpeVisualDomBuilder.VPE_FACET, facet
					.getAttribute(RichFaces.ATTR_NAME));
		    creationData.addChildrenInfo(childrenInfo);
		    childrenInfo.addSourceChild(facet);
		}
		/*
		 * apply styles and classes
		 */
		switch (markerNum) {
		case 0: // passed
		    labelMessage = (attrs.getPassedLabelValue() == null)
				    		? Constants.EMPTY
				    		: attrs.getPassedLabelValue();
		    if (ComponentUtil.isNotBlank(attrs.getMarkerClassValue())) {
		    	markerClass += Constants.WHITE_SPACE 
		    			+ attrs.getMarkerClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getLabelClassValue())) {
		    	labelClass += Constants.WHITE_SPACE 
		    			+ attrs.getLabelClassValue();
		    }
		    break;
		case 1: // error
		    labelMessage = ERROR_MESSAGE;
		    if (ComponentUtil.isNotBlank(attrs.getErrorClassValue())) {
		    	containerClass += Constants.WHITE_SPACE
		    			+ attrs.getErrorClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getErrorMarkerClassValue())) {
		    	markerClass += Constants.WHITE_SPACE
		    			+ attrs.getErrorMarkerClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getErrorLabelClassValue())) {
		    	labelClass += Constants.WHITE_SPACE
		    			+ attrs.getErrorLabelClassValue();
		    }
		    break;
		case 2: // fatal
		    labelMessage = FATAL_MESSAGE;
		    if (ComponentUtil.isNotBlank(attrs.getFatalClassValue())) {
		    	containerClass += Constants.WHITE_SPACE
		    			+ attrs.getFatalClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getFatalMarkerClassValue())) {
		    	markerClass += Constants.WHITE_SPACE
		    			+ attrs.getFatalMarkerClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getFatalLabelClassValue())) {
		    	labelClass += Constants.WHITE_SPACE
		    			+ attrs.getFatalLabelClassValue();
		    }
		    break;
		case 3: // info
		    labelMessage = INFO_MESSAGE;
		    if (ComponentUtil.isNotBlank(attrs.getInfoClassValue())) {
		    	containerClass += Constants.WHITE_SPACE
		    			+ attrs.getInfoClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getInfoMarkerClassValue())) {
		    	markerClass += Constants.WHITE_SPACE
		    			+ attrs.getInfoMarkerClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getInfoLabelClassValue())) {
		    	labelClass += Constants.WHITE_SPACE
		    			+ attrs.getInfoLabelClassValue();
		    }
		    break;
		case 4: // warn
		    labelMessage = WARNING_MESSAGE;
		    if (ComponentUtil.isNotBlank(attrs.getWarnClassValue())) {
		    	containerClass += Constants.WHITE_SPACE
		    			+ attrs.getWarnClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getWarnMarkerClassValue())) {
		    	markerClass += Constants.WHITE_SPACE
		    			+ attrs.getWarnMarkerClassValue();
		    }
		    if (ComponentUtil.isNotBlank(attrs.getWarnLabelClassValue())) {
		    	labelClass += Constants.WHITE_SPACE
		    			+ attrs.getWarnLabelClassValue();
		    }
		    break;
		default:
			labelMessage = Constants.EMPTY;
		    break;
		}
	
		if (ComponentUtil.isNotBlank(containerClass)) {
		    container.setAttribute(HTML.ATTR_CLASS, containerClass);
		}
		marker.setAttribute(HTML.ATTR_CLASS, markerClass);
		label.setAttribute(HTML.ATTR_CLASS, labelClass);
	
		final nsIDOMText text = visualDocument.createTextNode(labelMessage);
		label.appendChild(text);
		return topContainer;
    }
}
