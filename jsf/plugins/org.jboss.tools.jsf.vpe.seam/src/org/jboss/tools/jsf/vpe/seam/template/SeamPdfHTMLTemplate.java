/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

/**
 * @author yzhishko
 */

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.VpeElementProxyData;
import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class SeamPdfHTMLTemplate extends AbstractOutputJsfTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element element = (Element) sourceNode;

		// create container
		final nsIDOMElement container;
		container = VisualDomUtil.createBorderlessContainer(visualDocument);
		nsIDOMNode divNode = visualDocument.createElement(HTML.TAG_DIV);
		divNode.appendChild(container);
		VpeCreationData creationData = new VpeCreationData(divNode);

		processOutputAttribute(pageContext, visualDocument, element, container,
				creationData);

		return creationData;
	}

	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode,
			nsIDOMNode visualNode, Object data) {
		Node[] footers = SeamUtil.getChildsByName(pageContext, sourceNode,
				"p:footer"); //$NON-NLS-1$
		if (footers != null && footers.length != 0) {
			Node parentPdfDocumentNode = SeamUtil.getParentByName(pageContext,
					sourceNode, "p:document"); //$NON-NLS-1$
			if (parentPdfDocumentNode != null) {
				return parentPdfDocumentNode;
			}
		}
		return super
				.getNodeForUpdate(pageContext, sourceNode, visualNode, data);
	}

	@Override
	protected void processOutputAttribute(VpePageContext pageContext,
			nsIDOMDocument visualDocument, Element sourceElement,
			nsIDOMElement targetVisualElement, VpeCreationData creationData) {

		VpeElementProxyData elementData = new VpeElementProxyData();

		Attr outputAttr = getOutputAttributeNode(sourceElement);

		if (outputAttr != null) {

			String newValue = prepareAttrValue(pageContext, sourceElement, outputAttr);
			
			// create info
			VpeChildrenInfo targetVisualInfo = new VpeChildrenInfo(
					targetVisualElement);

			// get atribute's offset
			
			//mareshkau because it's node can be a proxy, see JBIDE-3144
			if(!(outputAttr instanceof IDOMAttr)) {
				outputAttr = (Attr) ((((Attr)outputAttr).getOwnerElement()).getAttributes().getNamedItem(outputAttr.getLocalName()));
			}
			
			int offset = ((IDOMAttr) outputAttr)
					.getValueRegionStartOffset();

			// reparse attribute's value
			NodeList list = NodeProxyUtil.reparseAttributeValue(
					elementData, newValue, offset + 1);

			// add children to info
			for (int i = 0; i < list.getLength(); i++) {

				Node child = list.item(i);

				// add info to creation data
				targetVisualInfo.addSourceChild(child);
			}

			elementData.addNodeData(new AttributeData(outputAttr,
					targetVisualElement, true));

			creationData.addChildrenInfo(targetVisualInfo);

		}

		creationData.setElementData(elementData);
	}

}
