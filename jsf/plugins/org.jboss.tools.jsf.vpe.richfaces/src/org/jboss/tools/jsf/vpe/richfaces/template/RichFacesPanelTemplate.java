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

import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Default template for <code>rich:panel</code> component.
 * <p>
 * It is used to render <code>rich:panel</code> for RichFaces untill version 3.3.
 */
public class RichFacesPanelTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
	   
		ComponentUtil.setCSSLink(pageContext, "panel/panel.css", "richFacesPanel"); //$NON-NLS-1$ //$NON-NLS-2$
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement div = visualDocument.createElement("div"); //$NON-NLS-1$
		VpeCreationData creationData = new VpeCreationData(div);		
		
		String styleClass = "dr-pnl rich-panel"; //$NON-NLS-1$
		String styleClassAttrName = "styleClass"; //$NON-NLS-1$
		if (sourceElement.hasAttribute(styleClassAttrName)) {
			styleClass += " " + sourceElement.getAttribute(styleClassAttrName); //$NON-NLS-1$
		}
		div.setAttribute("class", styleClass); //$NON-NLS-1$
		
		String styleAttrName = "style"; //$NON-NLS-1$
		if(sourceElement.hasAttribute(styleAttrName)) {
			String style = sourceElement.getAttribute(styleAttrName);
			div.setAttribute("style", style); //$NON-NLS-1$
		}

		/*
		 * Encode the Header Facet
		 * Find elements from the f:facet 
		 */
		Map<String, List<Node>> headerFacetChildren = null;
		Map<String, List<Node>> footerFacetChildren = null;
		Element headerFacet = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_HEADER);
		Element footerFacet = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_FOOTER);
		if (headerFacet != null) {
			headerFacetChildren = getHeaderFacetChildren(headerFacet, pageContext);
			nsIDOMElement headerDiv = visualDocument.createElement(HTML.TAG_DIV);
			/*
			 * By adding attribute VPE-FACET to this visual node 
			 * we force JsfFacet to be rendered inside it.
			 */
			headerDiv.setAttribute(VpeVisualDomBuilder.VPE_FACET, RichFaces.NAME_FACET_HEADER);
			div.appendChild(headerDiv);
			String headerClass = "dr-pnl-h rich-panel-header"; //$NON-NLS-1$
			if (sourceElement.hasAttribute(RichFaces.ATTR_HEADER_CLASS)) {
				headerClass += " " + sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS); //$NON-NLS-1$
			}
			headerDiv.setAttribute(HTML.ATTR_CLASS, headerClass);
			headerDiv.setAttribute(HTML.ATTR_STYLE, 
					ComponentUtil.getHeaderBackgoundImgStyle());
			/*
			 * https://issues.jboss.org/browse/JBIDE-6072
			 * Render the header: differs for RF3.3 and RF4
			 */
			renderHeaderFacet(headerFacet, headerDiv, creationData, pageContext, visualDocument);
		}
		if (footerFacet != null) {
			footerFacetChildren = getHeaderFacetChildren(footerFacet, pageContext);
		}
		/*
		 * Encode rich:panel content
		 */
		nsIDOMElement bodyDiv = visualDocument.createElement(HTML.TAG_DIV);
		div.appendChild(bodyDiv);
		String bodyClass = "dr-pnl-b rich-panel-body"; //$NON-NLS-1$
		if (sourceElement.hasAttribute(RichFaces.ATTR_BODY_CLASS)) {
			bodyClass += " " + sourceElement.getAttribute(RichFaces.ATTR_BODY_CLASS); //$NON-NLS-1$
		}
		bodyDiv.setAttribute(HTML.ATTR_CLASS, bodyClass);
		/*
		 * If there are some odd HTML elements from facet
		 * add them to the panel body first.
		 */
		boolean headerHtmlElementsPresents = ((headerFacetChildren != null) && (headerFacetChildren
				.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0));
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(bodyDiv);
		if (headerHtmlElementsPresents) {
				for (Node node : headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
					bodyInfo.addSourceChild(node);
				}
		}
		boolean footerHtmlElementsPresents = ((footerFacetChildren != null) && (footerFacetChildren
				.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0));
		if (footerHtmlElementsPresents) {
				for (Node node : footerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
					bodyInfo.addSourceChild(node);
				}
		}
		/*
		 * Add the rest panel's content
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);
		
		return creationData;
	}

	protected void renderHeaderFacet(Element headerFacet, nsIDOMElement headerDiv, 
			VpeCreationData creationData, VpePageContext pageContext, nsIDOMDocument visualDocument) {
		VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
		headerInfo.addSourceChild(headerFacet);
		creationData.addChildrenInfo(headerInfo);
	}
	
	protected Map<String, List<Node>> getHeaderFacetChildren(Element headerFacet, VpePageContext pageContext) {
		return VisualDomUtil.findFacetElements(headerFacet, pageContext);
	}
	
	protected Map<String, List<Node>> getFooterFacetChildren(Element footerFacet, VpePageContext pageContext) {
		return VisualDomUtil.findFacetElements(footerFacet, pageContext);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
			return true;
	}
}
