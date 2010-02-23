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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.facelets.template.util.Facelets;
import org.jboss.tools.vpe.editor.VpeIncludeInfo;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class VpeInsertTemplate extends VpeAbstractTemplate {
	
	private static final String DEFINE_ELEMENT_NAME_PARAM="defineElementName"; //$NON-NLS-1$
	private String defineElementName=Facelets.TAG_DEFINE; 
	
	@Override
	protected void init(Element templateElement) {
		children = true;
		modify = false;
		this.defineElementName = templateElement.getAttribute(DEFINE_ELEMENT_NAME_PARAM)!=null?templateElement.getAttribute(DEFINE_ELEMENT_NAME_PARAM):Facelets.TAG_DEFINE;
		initTemplateSections(templateElement, false, true, false, false, false);
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		VpeVisualDomBuilder visualBuilder = pageContext.getVisualBuilder();
		VpeIncludeInfo includeInfo = visualBuilder.getCurrentIncludeInfo();
		if (includeInfo != null && includeInfo.getElement() != null) {
			String name = ((Element)sourceNode).getAttribute(Facelets.ATTR_NAME);
			if (name != null) {
				name = name.trim();
				if (name.length() <= 0) name = null;
			}
			if (name != null) {
				Element defineElement = findDefineElement(includeInfo.getElement(), name);
				if (defineElement != null) {
					VpeCreationData creationData = createInsert(defineElement, visualDocument);
					creationData.setData(pageContext.getVisualBuilder().popIncludeStack());
					return creationData;
				}
			} else {
				//if no name specified for insert Template, that expected that the all template body will be inserted
				List<Node> elements = findUndefinedElement(includeInfo.getElement());
				
				VpeCreationData creationData= new VpeCreationData(null);
				for (Node node : elements) {
					VpeChildrenInfo childInfo =  new VpeChildrenInfo(null);
					childInfo.addSourceChild(node);
					creationData.addChildrenInfo(childInfo);
				}
				creationData.setData(pageContext.getVisualBuilder().popIncludeStack());
				return creationData;
			}
		}
		VpeCreationData creationData = createStub((Element)sourceNode, visualDocument);
		creationData.setData(null);
		return creationData;
	}
	public void validate(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument, VpeCreationData creationData) {
		VpeIncludeInfo includeInfo = (VpeIncludeInfo)creationData.getData();
		if (includeInfo != null) {
			pageContext.getVisualBuilder().pushIncludeStack((VpeIncludeInfo)includeInfo);
		}
	}
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	private Element findDefineElement(Element defineContainer, String defineName) {
		Element defineElement = null;
		NodeList children = defineContainer.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				if (this.defineElementName.equals(childElement.getLocalName())
						&& defineName.equals(childElement
								.getAttribute(Facelets.ATTR_NAME))) {
					defineElement = childElement;

				} else {
					Element tempDefineElement = findDefineElement(childElement,
							defineName);
					if (tempDefineElement != null)
						defineElement = tempDefineElement;
				}
			}
		}
		return defineElement;
	}
	
	private List<Node> findUndefinedElement(Element defineContainer) {
		List<Node> result= new ArrayList<Node>();
		NodeList children = defineContainer.getChildNodes();
		int len = children.getLength();
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			if ((child.getNodeType() == Node.ELEMENT_NODE||child.getNodeType() == Node.TEXT_NODE)) {
				
				if(child.getNodeType() == Node.ELEMENT_NODE&&!this.defineElementName.equals(child.getLocalName())
						//&&((Element)child).getAttribute("name")==null
						) { 
					result.add(child);
					
				} else if(child.getNodeType() == Node.TEXT_NODE&&((Text)child).getNodeValue()!=null&&
						((Text)child).getNodeValue().trim().length()>0) {
					result.add(child);
				}
			}
		}
		return result;
	}
	
	private VpeCreationData createInsert(Node defineElement, nsIDOMDocument visualDocument) {
		VpeCreationData creationData = new VpeCreationData(null);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(null);
		childrenInfo.addSourceChild(defineElement);
		creationData.addChildrenInfo(childrenInfo);
		return creationData;
	}
	
	private VpeCreationData createStub(Element element, nsIDOMDocument visualDocument) {
		nsIDOMElement visualNewElement = visualDocument.createElement(HTML.TAG_DIV);
		return new VpeCreationData(visualNewElement);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#setPseudoContent(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode, org.mozilla.interfaces.nsIDOMDocument)
	 */
	@Override
	public void setPseudoContent(VpePageContext pageContext,
			Node sourceContainer, nsIDOMNode visualContainer,
			nsIDOMDocument visualDocument) {
		// TODO Fix  for JBIDE-1213
	}
}
