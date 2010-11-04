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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for rich:subTableToggleControl.
 * 
 * @author Yahor Radtsevich (yradtsevich)
 *
 */
public class RichFacesSubTableToggleControlTemplate extends VpeAbstractTemplate {

	private static final String DOWN_ICON_PATH = "/subTableToggleControl/down_icon.gif"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		
		nsIDOMElement container = VisualDomUtil.createBorderlessContainer(visualDocument);
		VpeCreationData creationData = new VpeCreationData(container);
		
		if (sourceElement.hasAttribute(RichFaces.ATTR_COLLAPSE_LABEL)) {
			Attr collapseLabelAttr = sourceElement.getAttributeNode(RichFaces.ATTR_COLLAPSE_LABEL);
			nsIDOMElement link = visualDocument.createElement(HTML.TAG_A);
					
			nsIDOMText collapseLabelText
					= visualDocument.createTextNode(collapseLabelAttr.getNodeValue());
			link.appendChild(collapseLabelText);
			
			VpeElementData elementData = new VpeElementData();
			creationData.setElementData(elementData);
			elementData.addNodeData(new AttributeData(collapseLabelAttr, collapseLabelText, true));
			
			container.appendChild(link);
		} else {
			nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
			if (sourceElement.hasAttribute(RichFaces.ATTR_COLLAPSE_ICON)) {
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(
						sourceElement.getAttribute(RichFaces.ATTR_COLLAPSE_ICON), pageContext, true);
				img.setAttribute(HTML.ATTR_SRC, imgFullPath);
			} else {
				ComponentUtil.setImg(img, DOWN_ICON_PATH);
			}
			
			container.appendChild(img);
		}
			
		return creationData;
	}
}
