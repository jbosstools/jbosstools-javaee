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

import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VpeDecorateTemplate extends VpeDefineContainerTemplate {
	
	protected VpeCreationData createStub(String fileName, Node sourceElement, nsIDOMDocument visualDocument) {
		nsIDOMElement container = visualDocument.createElement(HTML.TAG_DIV);
		container.setAttribute("style", "border: 1px dashed #2A7F00");
		VpeVisualDomBuilder.markIncludeElement(container);

		nsIDOMElement title = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement tag = visualDocument.createElement(HTML.TAG_SPAN);
		tag.setAttribute("class", "__any__tag__caption");
		tag.appendChild(visualDocument.createTextNode(sourceElement.getNodeName()));
		title.appendChild(tag);
		if (fileName != null) {
			title.appendChild(visualDocument.createTextNode(fileName));
		}
		container.appendChild(title);
		
		VpeCreationData creationData = new VpeCreationData(container);

		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(container);
		NodeList sourceChildren = sourceElement.getChildNodes();
		int len = sourceChildren.getLength();
		for (int i = 0; i < len; i++) {
			Node sourceChild = sourceChildren.item(i);
			if (sourceChild.getNodeType() == Node.ELEMENT_NODE && "define".equals(sourceChild.getLocalName())) {
				childrenInfo.addSourceChild(sourceChild);
			}
		}
		creationData.addChildrenInfo(childrenInfo);
		return creationData;
	}
}
