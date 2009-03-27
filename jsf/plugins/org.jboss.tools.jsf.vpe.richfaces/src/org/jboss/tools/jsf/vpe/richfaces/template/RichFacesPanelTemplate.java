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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
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

		// Encode Header
		Node header = ComponentUtil.getFacet(sourceElement, "header", true); //$NON-NLS-1$
		if(header!=null) {
		    	nsIDOMElement headerDiv = visualDocument.createElement("div"); //$NON-NLS-1$
			div.appendChild(headerDiv);
			String headerClass = sourceElement.getAttribute("headerClass"); //$NON-NLS-1$
			headerDiv.setAttribute("class", "dr-pnl-h rich-panel-header " + (headerClass==null?"":headerClass)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			headerDiv.setAttribute("style", ComponentUtil.getHeaderBackgoundImgStyle()); //$NON-NLS-1$

			VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
			headerInfo.addSourceChild(header);
			creationData.addChildrenInfo(headerInfo);
		}

		// Encode Body
		nsIDOMElement bodyDiv = visualDocument.createElement("div"); //$NON-NLS-1$
		div.appendChild(bodyDiv);
		String bodyClass = sourceElement.getAttribute("bodyClass"); //$NON-NLS-1$
		bodyDiv.setAttribute("class", "dr-pnl-b rich-panel-body " + (bodyClass==null?"":bodyClass)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(bodyDiv);
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
