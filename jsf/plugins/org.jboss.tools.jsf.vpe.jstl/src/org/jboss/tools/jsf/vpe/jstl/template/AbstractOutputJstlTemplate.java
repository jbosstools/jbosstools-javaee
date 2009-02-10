/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.jstl.template;

import org.jboss.tools.jsf.vpe.jsf.template.util.model.VpeElementProxyData;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.NodeData;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractOutputJstlTemplate extends VpeAbstractTemplate {

	/**
	 * 
	 * @param element
	 * @return
	 */
	public Attr getOutputAttributeNode(Element element) {
		if (element.hasAttribute(HTML.ATTR_VALUE)) {
			return element.getAttributeNode(HTML.ATTR_VALUE);
		}

		return null;
	}

	/**
	 * 
	 * @param pageContext
	 * @param visualDocument
	 * @param sourceElement
	 * @param targetVisualElement
	 * @param creationData
	 */
	protected void processOutputAttribute(VpePageContext pageContext,
			nsIDOMDocument visualDocument, Element sourceElement,
			nsIDOMElement targetVisualElement, VpeCreationData creationData) {

		VpeElementProxyData elementData = new VpeElementProxyData();

		Attr outputAttr = getOutputAttributeNode(sourceElement);

		if (outputAttr != null) {

			// prepare value
			String newValue = prepareAttrValue(pageContext, sourceElement, outputAttr);
			// if escape then contents of value (or other attribute) is only
			// text
//			if (!sourceElement.hasAttribute(JSF.ATTR_ESCAPE)
//					|| "true".equalsIgnoreCase(sourceElement //$NON-NLS-1$
//							.getAttribute(JSF.ATTR_ESCAPE))) {
//
//				String value = outputAttr.getValue();
//
//				nsIDOMText text;
//				// if bundleValue differ from value then will be represent
//				// bundleValue, but text will be not edit
//				boolean isEditable = value.equals(newValue);
//
//				text = visualDocument.createTextNode(newValue);
//				// add attribute for ability of editing
//
//				elementData.addNodeData(new AttributeData(outputAttr, text,
//						isEditable));
//
//				targetVisualElement.appendChild(text);
//
//			}
		}

		creationData.setElementData(elementData);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#setPseudoContent
	 * (org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node,
	 * org.mozilla.interfaces.nsIDOMNode, org.mozilla.interfaces.nsIDOMDocument)
	 */
	@Override
	public void setPseudoContent(VpePageContext pageContext,
			Node sourceContainer, nsIDOMNode visualContainer,
			nsIDOMDocument visualDocument) {
		// Empty
	}

	@Override
	public NodeData getNodeData(nsIDOMNode node, VpeElementData elementData,
			VpeDomMapping domMapping) {
		// TODO Auto-generated method stub
		NodeData nodeData = super.getNodeData(node, elementData, domMapping);
		if (nodeData == null) {

			VpeNodeMapping nodeMapping = domMapping.getNodeMapping(node);

			if (nodeMapping != null) {
				if (nodeMapping instanceof VpeElementMapping) {
					nodeData = super.getNodeData(node,
							((VpeElementMapping) nodeMapping).getElementData(),
							domMapping);
				} 
//				else if (nodeMapping.getType() == VpeNodeMapping.TEXT_MAPPING) {
//					nodeData = new NodeData(nodeMapping.getSourceNode(), node,
//							true);
//				}
			}
		}
		return nodeData;
	}

	@Override
	public nsIDOMNode getVisualNodeByBySourcePosition(
			VpeElementMapping elementMapping, int focusPosition,
			int anchorPosition, VpeDomMapping domMapping) {
		nsIDOMNode node = null;

		if ((elementMapping.getElementData() instanceof VpeElementProxyData)
				&& (((VpeElementProxyData) elementMapping.getElementData())
						.getNodelist() != null)) {
			VpeElementProxyData elementProxyData = (VpeElementProxyData) elementMapping.getElementData();

//			VpeNodeMapping nodeMapping = NodeProxyUtil.findNodeByPosition(
//					domMapping, elementProxyData.getNodelist(), focusPosition,
//					anchorPosition);

//			if (nodeMapping != null) {
//
//				if (nodeMapping instanceof VpeElementMapping) {
//					node = super.getVisualNodeByBySourcePosition(
//							(VpeElementMapping) nodeMapping, focusPosition,
//							anchorPosition, domMapping);
//				} else {
//					node = nodeMapping.getVisualNode();
//				}
//			}
		}

		if (node == null) {
			node = super.getVisualNodeByBySourcePosition(elementMapping,
					focusPosition, anchorPosition, domMapping);
		}
		return node;
	}

	protected String prepareAttrValue(VpePageContext pageContext, Element parent, Attr attr) {
		return attr.getNodeValue();
	}
}