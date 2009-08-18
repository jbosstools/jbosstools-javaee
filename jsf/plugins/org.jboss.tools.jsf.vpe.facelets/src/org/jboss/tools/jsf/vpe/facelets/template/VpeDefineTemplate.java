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

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.jsf.vpe.facelets.template.messages.Messages;
import org.jboss.tools.jsf.vpe.facelets.template.util.Facelets;
import org.jboss.tools.jsf.vpe.facelets.util.FaceletsUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VpeDefineTemplate extends VpeAbstractTemplate {
	
	@Override
	protected void init(Element templateElement) {
		children = true;
		modify = true;
		initTemplateSections(templateElement, true, true, true, true, true);
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		if (VpeDefineContainerTemplate.isDefineContainer(sourceNode.getParentNode())) {
			//FIX for JBIDE-1213, we shouldn't wrap ui:define content( Max Areshkau)
			//nsIDOMElement visualNewElement = visualDocument.createElement(HTML.TAG_SPAN);
			return new VpeCreationData(null);
		}
		return createStub((Element)sourceNode, visualDocument);
	}
	
	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode, nsIDOMNode visualNode, Object data) {
		return sourceNode.getParentNode();
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
	
	private VpeCreationData createStub(Element sourceElement, nsIDOMDocument visualDocument) {
		nsIDOMElement container = visualDocument.createElement(HTML.TAG_DIV);
		container.setAttribute(HTML.ATTR_STYLE, "border: 1px solid gray"); //$NON-NLS-1$

		String name = sourceElement.getAttribute(Facelets.ATTR_NAME);
		final String message;
		if (name != null) {
			message = NLS.bind(Messages.UNKNOWN_NAME, name);
		} else {
			message = Messages.NAME_NOT_SPECIFIED;
		}

		container.appendChild(FaceletsUtil.createErrorMessageElement(
				visualDocument, sourceElement.getNodeName(), message));

		return new VpeCreationData(container);
	}
}
