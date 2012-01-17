/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Template for rich:list.
 * 
 * @author Yahor Radtsevich (yradtsevich)
 */
public class RichFacesListTemplate extends VpeAbstractTemplate {

	private static final String TYPE_DEFINITIONS = "definitions"; //$NON-NLS-1$
	private static final String TYPE_ORDERED = "ordered"; //$NON-NLS-1$

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;

		String firstRowClass = getFirstRowClass(sourceElement);
		
		nsIDOMElement outerContainer;
		nsIDOMElement innerContainer;
		VpeChildrenInfo termInfo = null;
		String type = sourceElement.getAttribute(RichFaces.ATTR_TYPE);
		if (TYPE_ORDERED.equals(type)) {
			outerContainer = visualDocument.createElement(HTML.TAG_OL);
			innerContainer = visualDocument.createElement(HTML.TAG_LI);
		} else if (TYPE_DEFINITIONS.equals(type)) {
			outerContainer = visualDocument.createElement(HTML.TAG_DL);
			
			nsIDOMElement termContainer = visualDocument.createElement(HTML.TAG_DT);
			termContainer.setAttribute(HTML.ATTR_CLASS, firstRowClass);
			outerContainer.appendChild(termContainer);
			Element termElement = SourceDomUtil.getFacetByName(pageContext,
					sourceElement, RichFaces.NAME_FACET_TERM);
			termInfo = new VpeChildrenInfo(termContainer);
			termInfo.addSourceChild(termElement);

			innerContainer = visualDocument.createElement(HTML.TAG_DD);
		} else { // "unordered" by default
			outerContainer = visualDocument.createElement(HTML.TAG_UL);
			innerContainer = visualDocument.createElement(HTML.TAG_LI);
		}

		VisualDomUtil.copyAttribute(sourceElement, RichFaces.ATTR_STYLE,
				outerContainer, HTML.ATTR_STYLE);
		VisualDomUtil.copyAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS,
				outerContainer, HTML.ATTR_CLASS);
		innerContainer.setAttribute(HTML.ATTR_CLASS, firstRowClass);
		
		outerContainer.appendChild(innerContainer);
		
		VpeCreationData creationData = new VpeCreationData(outerContainer);
		if (termInfo != null) {
			creationData.addChildrenInfo(termInfo);
		}
		
		NodeList childNodes = sourceElement.getChildNodes();
		VpeChildrenInfo nonFacetChildrenInfo = new VpeChildrenInfo(innerContainer);
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (!SourceDomUtil.isFacetElement(pageContext, childNode)) {
				nonFacetChildrenInfo.addSourceChild(childNode);
			}
		}
		creationData.addChildrenInfo(nonFacetChildrenInfo);
		
		return creationData;
	}

	private String getFirstRowClass(Element sourceElement) {
		StringBuilder firstRowClass = new StringBuilder();

		if (sourceElement.hasAttribute(RichFaces.ATTR_ROW_CLASSES)) {
			String rowClasses = sourceElement.getAttribute(RichFaces.ATTR_ROW_CLASSES); 
			int commaIndex = rowClasses.indexOf(Constants.COMMA);
			if (commaIndex >= 0) {
				firstRowClass.append(rowClasses.substring(0, commaIndex));
			} else {
				firstRowClass.append(rowClasses);
			}
			firstRowClass.append(Constants.WHITE_SPACE);
		}
		if (sourceElement.hasAttribute(RichFaces.ATTR_ROW_CLASS)) {
			firstRowClass.append(sourceElement.getAttribute(RichFaces.ATTR_ROW_CLASS));
		}
		
		return firstRowClass.toString();
	}
}
