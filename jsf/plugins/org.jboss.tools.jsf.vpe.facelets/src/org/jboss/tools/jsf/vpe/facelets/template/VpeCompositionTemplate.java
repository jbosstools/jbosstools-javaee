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
package org.jboss.tools.jsf.vpe.facelets.template;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.jsf.vpe.facelets.template.messages.Messages;
import org.jboss.tools.jsf.vpe.facelets.template.util.Facelets;
import org.jboss.tools.jsf.vpe.facelets.util.FaceletsUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.NodesManagingUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VpeCompositionTemplate extends VpeDefineContainerTemplate {
	
	public static final String MESSAGE_STYLE
			= "color:red;font-style:italic;";	//$NON-NLS-1$
	public static final String ANY_TAG_CAPTION_CLASS
			= "__any__tag__caption";			//$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument){

		Attr attr = ((Element)sourceNode).getAttributeNode(Facelets.ATTR_TEMPLATE);
		return super.createTemplate(attr == null ? null : attr.getNodeValue(),
				pageContext, sourceNode, visualDocument);
	}
	
	protected VpeCreationData createStub(String fileName, Node sourceElement, nsIDOMDocument visualDocument) {
		nsIDOMElement container = visualDocument.createElement(HTML.TAG_DIV);
		VpeVisualDomBuilder.markIncludeElement(container);

		if (fileName != null) {
			container.setAttribute(HTML.ATTR_STYLE, "border: 1px dashed #2A7F00"); //$NON-NLS-1$
			String message = NLS.bind(Messages.TEMPLATE_NOT_FOUND, fileName);
			container.appendChild(FaceletsUtil.createErrorMessageElement(
					visualDocument, sourceElement.getNodeName(), message));
		}


		return new VpeCreationData(container);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#getSourceRegionForOpenOn(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode)
	 */
	@Override
	public IRegion getSourceRegionForOpenOn(VpePageContext pageContext,
			Node sourceNode, nsIDOMNode domNode) {
			Element sourceElement = (Element) sourceNode;
			Node paramAttr = sourceElement.getAttributeNode(Facelets.ATTR_TEMPLATE);
			return new Region(NodesManagingUtil.getStartOffsetNode(paramAttr),0);			
	}

}
