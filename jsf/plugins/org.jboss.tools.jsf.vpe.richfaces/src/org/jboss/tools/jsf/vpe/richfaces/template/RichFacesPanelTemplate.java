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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
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

public class RichFacesPanelTemplate extends VpeAbstractTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

	   
		Element sourceElement = (Element)sourceNode;

		nsIDOMElement div = visualDocument.createElement("div"); //$NON-NLS-1$

		
		VpeCreationData creationData = new VpeCreationData(div);
		

		ComponentUtil.setCSSLink(pageContext, "panel/panel.css", "richFacesPanel"); //$NON-NLS-1$ //$NON-NLS-2$
		String styleClass = sourceElement.getAttribute("styleClass"); //$NON-NLS-1$
		div.setAttribute("class", "dr-pnl rich-panel " + (styleClass==null?"":styleClass)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String style = sourceElement.getAttribute("style"); //$NON-NLS-1$
		if(style!=null && style.length()>0) {
			div.setAttribute("style", style); //$NON-NLS-1$
		}

		/*
		 * Encode the Header Facet
		 * Find elements from the f:facet 
		 */
		Map<String, List<Node>> headerFacetChildren = null;
		Element headerFacet = SourceDomUtil.getFacetByName(sourceElement, RichFaces.NAME_FACET_HEADER);
		if (headerFacet != null) {
			headerFacetChildren = VisualDomUtil.findFacetElements(headerFacet, pageContext);
			nsIDOMElement headerDiv = visualDocument.createElement(HTML.TAG_DIV);
			/*
			 * By adding attribute VPE-FACET to this visual node 
			 * we force JsfFacet to be rendered inside it.
			 */
			headerDiv.setAttribute(VpeVisualDomBuilder.VPE_FACET, RichFaces.NAME_FACET_HEADER);
			div.appendChild(headerDiv);
			String headerClass = sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS);
			headerDiv.setAttribute(HTML.ATTR_CLASS,
							"dr-pnl-h rich-panel-header " + (headerClass == null ? "" : headerClass)); //$NON-NLS-1$ //$NON-NLS-2$
			headerDiv.setAttribute(HTML.ATTR_STYLE, 
					ComponentUtil.getHeaderBackgoundImgStyle());

			VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
			headerInfo.addSourceChild(headerFacet);
			creationData.addChildrenInfo(headerInfo);
		}

		/*
		 * Encode rich:panel content
		 */
		nsIDOMElement bodyDiv = visualDocument.createElement(HTML.TAG_DIV);
		div.appendChild(bodyDiv);
		String bodyClass = sourceElement.getAttribute(RichFaces.ATTR_BODY_CLASS);
		bodyDiv.setAttribute(HTML.ATTR_CLASS,
						"dr-pnl-b rich-panel-body " + (bodyClass == null ? "" : bodyClass)); //$NON-NLS-1$ //$NON-NLS-2$
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
