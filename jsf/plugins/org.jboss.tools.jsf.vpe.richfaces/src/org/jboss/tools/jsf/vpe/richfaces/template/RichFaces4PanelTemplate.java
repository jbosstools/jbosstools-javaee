/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;
import java.util.Map;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * It is used to render <code>rich:panel</code> from RichFaces 4 library.
 * 
 * @author dmaliarevich
 */
public class RichFaces4PanelTemplate extends RichFacesPanelTemplate {

	@Override
	protected void renderHeaderFacet(Element headerFacet, nsIDOMElement headerDiv, 
			VpeCreationData creationData, VpePageContext pageContext, nsIDOMDocument visualDocument) {
		
		VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
		NodeList allFacetElements = headerFacet.getChildNodes();
		for (int i = 0; i < allFacetElements.getLength(); i++) {
			headerInfo.addSourceChild(allFacetElements.item(i));
		}		
		creationData.addChildrenInfo(headerInfo);
	}
	
	@Override
	protected void addHeaderFacetElementsToPanelBody(
			Map<String, List<Node>> headerFacetChildren, 
			VpeChildrenInfo bodyInfo, VpePageContext pageContext) {
		// Do nothing
	}
	
	@Override
	protected void addElementsFromOtherFacetsToPanelBody(Element sourceElement,
			VpeChildrenInfo bodyInfo, VpePageContext pageContext) {
		// Do nothing
	}
	
}
