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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;

public class VpeDefineTemplate extends VpeAbstractTemplate {
	
	protected void init(Element templateElement) {
		children = true;
		modify = true;
		initTemplateSections(templateElement, true, true, true, true, true);
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		if (VpeDefineContainerTemplate.isDefineContainer(sourceNode.getParentNode())) {
			Element visualNewElement = visualDocument.createElement("div");
			return new VpeCreationData(visualNewElement);
		}
		return createStub((Element)sourceNode, visualDocument);
	}
	
	public Node getNodeForUptate(VpePageContext pageContext, Node sourceNode, Node visualNode, Object data) {
		return sourceNode.getParentNode();
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
	
	private VpeCreationData createStub(Element sourceElement, Document visualDocument) {
		Element container = visualDocument.createElement("div");
		container.setAttribute("style", "border: 1px solid gray");

		Element title = visualDocument.createElement("div");
		Element tag = visualDocument.createElement("span");
		tag.setAttribute("class", "__any__tag__caption");
		tag.appendChild(visualDocument.createTextNode(sourceElement.getNodeName()));
		title.appendChild(tag);
		String name = sourceElement.getAttribute("name");
		if (name != null && name.length() > 0) {
			title.appendChild(visualDocument.createTextNode(name));
		}
		container.appendChild(title);
		
		return new VpeCreationData(container);
	}
}
