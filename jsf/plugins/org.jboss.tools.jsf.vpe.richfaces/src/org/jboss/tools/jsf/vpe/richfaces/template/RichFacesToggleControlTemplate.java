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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesToggleControlTemplate  extends VpeAbstractTemplate implements VpeToggableTemplate {

	private static Map toggleMap = new HashMap();
	private static Element storedSwitchSpan = null;

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		Element sourceElement = (Element)sourceNode;

		Element span = visualDocument.createElement("span");
		storedSwitchSpan = span;
		VpeCreationData creationData = new VpeCreationData(span);

		String forIds = sourceElement.getAttribute("for");
		String value = sourceElement.getAttribute("value");
		String switchToState = sourceElement.getAttribute("switchToState");
		
		ComponentUtil.correctAttribute(sourceElement, span,
				"styleClass",
				HtmlComponentUtil.HTML_CLASS_ATTR, "", "");

		ComponentUtil.correctAttribute(sourceElement, span,
				"style",
				HtmlComponentUtil.HTML_STYLE_ATTR, "color:blue;text-decoration:underline;", "color:blue;text-decoration:underline;");

		span.setAttribute("vpe-user-toggle-id", (switchToState == null ? "" : switchToState.trim()));

		List<Node> children = ComponentUtil.getChildren(sourceElement);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(span);
		//string shoudn't be null, if then it's crash application
		if(value==null){
			value="";
		}
		Node valueText = visualDocument.createTextNode(value);
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
	public void validate(VpePageContext pageContext, Node sourceNode, Document visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		if (storedSwitchSpan == null) return;
		
		String value = storedSwitchSpan.getAttribute("vpe-user-toggle-id");
		applyAttributeValueOnChildren("vpe-user-toggle-id", value, ComponentUtil.getChildren(storedSwitchSpan));
		applyAttributeValueOnChildren("vpe-user-toggle-lookup-parent", "true", ComponentUtil.getChildren(storedSwitchSpan));
	}
	
	private void applyAttributeValueOnChildren(String attrName, String attrValue, List<Node> children) {
		if (children == null || attrName == null || attrValue == null) return;
		
		for (Node child : children) {
			if (child instanceof Element) {
				Element childElement = (Element)child;
				childElement.setAttribute(attrName, attrValue);
				applyAttributeValueOnChildren(attrName, attrValue, ComponentUtil.getChildren(childElement));
			}
		}
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
		toggleMap.put(sourceNode, toggleId);
		
		Element sourceElement = (Element)(sourceNode instanceof Element ? sourceNode : sourceNode.getParentNode());
		
		String forIds = sourceElement.getAttribute("for");
		if (forIds == null) return;
		
		StringTokenizer st = new StringTokenizer(forIds.trim(), ",", false);
		while (st.hasMoreElements()) {
			String id = st.nextToken().trim();
			if (null == id) continue;
			
			id = id.trim();
			
			List<Element> sourceElements = findElementsById ((Element)sourceElement.getOwnerDocument().getDocumentElement(), id);
			
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

	private List<Element> findElementsById (Element root, String id) {
    	ArrayList<Element> list = new ArrayList<Element>();
		NodeList nodeList = root.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			
			if(child instanceof Element) {
				Element childElement = (Element)child;
				
				if (childElement.getNodeName().endsWith(":togglePanel") && 
						id.equals(childElement.getAttribute("id")) ) {
					list.add(childElement);
				}
				
				list.addAll(findElementsById(childElement, id));
			}
		}
    	return list;
	}
	
	
	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}

}