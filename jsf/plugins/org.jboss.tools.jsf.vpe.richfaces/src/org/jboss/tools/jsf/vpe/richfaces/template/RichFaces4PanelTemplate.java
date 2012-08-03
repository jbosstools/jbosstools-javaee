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
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * It is used to render <code>rich:panel</code> from RichFaces 4 library.
 * 
 * @author dmaliarevich
 */
public class RichFaces4PanelTemplate extends RichFacesPanelTemplate {

	@Override
	protected void renderHeaderFacet(Element headerFacet,
			nsIDOMElement headerDiv, VpeCreationData creationData,
			VpePageContext pageContext, nsIDOMDocument visualDocument) {
		
		Map<String, List<Node>> children = VisualDomUtil.findFacetElements(headerFacet, pageContext);
		VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
		if (((children != null) && (children.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0))) {
				for (Node node : children.get(VisualDomUtil.FACET_HTML_TAGS)) {
					headerInfo.addSourceChild(node);
				}
		}
		creationData.addChildrenInfo(headerInfo);
	}
	
	@Override
	protected Map<String, List<Node>> getHeaderFacetChildren(
			Element headerFacet, VpePageContext pageContext) {
		return null;
	}
	@Override
	protected Map<String, List<Node>> getFooterFacetChildren(
			Element footerFacet, VpePageContext pageContext) {
		return null;
	}
	
}
