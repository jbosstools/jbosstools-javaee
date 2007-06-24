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

import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;

public class VpeCompositionTemplate extends VpeDefineContainerTemplate {
	
	protected VpeCreationData createStub(String fileName, Node sourceElement, Document visualDocument) {
		Element container = visualDocument.createElement("div");
		container.setAttribute("style", "border: 1px dashed #2A7F00");
		VpeVisualDomBuilder.markIncludeElement(container);

		Element title = visualDocument.createElement("div");
		Element tag = visualDocument.createElement("span");
		tag.setAttribute("class", "__any__tag__caption");
		tag.appendChild(visualDocument.createTextNode(sourceElement.getNodeName()));
		title.appendChild(tag);
		if (fileName != null) {
			title.appendChild(visualDocument.createTextNode(fileName));
		}
		container.appendChild(title);
		
		return new VpeCreationData(container);
	}
	
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
}
