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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesToggleControlTemplate  extends VpeAbstractTemplate implements VpeToggableTemplate {

	private static Map toggleMap = new HashMap();
	private static nsIDOMElement storedSwitchSpan = null;
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement span = visualDocument.createElement("span"); //$NON-NLS-1$
		storedSwitchSpan = span;
		
		VpeCreationData creationData = new VpeCreationData(span);		
		
		ComponentUtil.correctAttribute(sourceElement, span,
				"styleClass", //$NON-NLS-1$
				HtmlComponentUtil.HTML_CLASS_ATTR, "", ""); //$NON-NLS-1$ //$NON-NLS-2$

		ComponentUtil.correctAttribute(sourceElement, span,
				"style", //$NON-NLS-1$
				HtmlComponentUtil.HTML_STYLE_ATTR, "color:blue;text-decoration:underline;", "color:blue;text-decoration:underline;"); //$NON-NLS-1$ //$NON-NLS-2$

		String switchToStateAttrName = "switchToState"; //$NON-NLS-1$ 
		String switchToState = sourceElement.hasAttribute(switchToStateAttrName) ? 
				sourceElement.getAttribute(switchToStateAttrName).trim() : Constants.EMPTY;
		span.setAttribute("vpe-user-toggle-id", switchToState); //$NON-NLS-1$

		List<Node> children = ComponentUtil.getChildren(sourceElement);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(span);
		
		String valueAttrName = "value"; //$NON-NLS-1$
		String value = sourceElement.hasAttribute(valueAttrName) ? sourceElement.getAttribute(valueAttrName) : Constants.EMPTY;
		nsIDOMNode valueText = visualDocument.createTextNode(value);
		span.appendChild(valueText);
		
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);

		return creationData;
	}

	/**
	 * Is invoked after construction of all child nodes of the current visual node.
	 * @param pageContext Contains the information on edited page.
	 * @param sourceNode The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @param data Object <code>VpeCreationData</code>, built by a method <code>create</code>
	 */
	public void validate(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument, VpeCreationData data) {
		
		super.validate(pageContext, sourceNode, visualDocument, data);
		if (storedSwitchSpan == null) return;
		
		String vpeToogleAttrName = "vpe-user-toggle-id"; //$NON-NLS-1$
		if (storedSwitchSpan.hasAttribute(vpeToogleAttrName)) {
			String value = storedSwitchSpan.getAttribute(vpeToogleAttrName);
			applyAttributeValueOnChildren("vpe-user-toggle-id", value, ComponentUtil.getChildren(storedSwitchSpan)); //$NON-NLS-1$
		}
		applyAttributeValueOnChildren("vpe-user-toggle-lookup-parent", "true", ComponentUtil.getChildren(storedSwitchSpan)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void applyAttributeValueOnChildren(String attrName, String attrValue, List<nsIDOMNode> children) {
		if (children == null || attrName == null || attrValue == null) return;
		
		for (nsIDOMNode child : children) {
			if (child instanceof nsIDOMElement) {
				nsIDOMElement childElement = queryInterface(child, nsIDOMElement.class);
				childElement.setAttribute(attrName, attrValue);
				applyAttributeValueOnChildren(attrName, attrValue, ComponentUtil.getChildren(childElement));
			}
		}
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
		toggleMap.put(sourceNode, toggleId);
		
		Element sourceElement = (Element)(sourceNode instanceof Element ? sourceNode : sourceNode.getParentNode());
		
		String forAttrName = "for"; //$NON-NLS-1$
		if (!sourceElement.hasAttribute(forAttrName)) return;

		String forIds = sourceElement.getAttribute(forAttrName);
		StringTokenizer st = new StringTokenizer(forIds.trim(), ",", false); //$NON-NLS-1$
		while (st.hasMoreElements()) {
			String id = st.nextToken().trim();
			if (null == id) continue;
			
			id = id.trim();
			
			List<Element> sourceElements = RichFaces.findElementsById(
					(Element) sourceElement.getOwnerDocument()
							.getDocumentElement(), id, ":togglePanel"); //$NON-NLS-1$
			
			for (Element el : sourceElements) {
				if (builder != null) {
					VpeElementMapping elementMapping = (VpeElementMapping)builder.getDomMapping().getNodeMapping(el);
					if (elementMapping != null) {
						VpeTemplate template = elementMapping.getTemplate(); 
						if (template instanceof RichFacesTogglePanelTemplate) {
							((RichFacesTogglePanelTemplate)template).toggle(el, toggleId);
							builder.updateNode(el);
						}
					}
				}
			}
		}
	}

	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}
}