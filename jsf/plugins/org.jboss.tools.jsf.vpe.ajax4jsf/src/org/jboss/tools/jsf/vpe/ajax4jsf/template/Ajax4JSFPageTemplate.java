/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The template intended for processing {@code <a4j:page>} elements. 
 * 
 * @author dmaliarevich
 * @author yradtsevich
 */
public class Ajax4JSFPageTemplate extends VpeAbstractTemplate {

	private static final String FACET_TEMPLATE_NAME = "f:facet"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String HEAD_FACET_NAME = "head"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;

		nsIDOMElement bodyContainer = visualDocument.createElement(HTML.TAG_DIV);
		VpeCreationData creationData = new VpeCreationData(bodyContainer);

		// Mozilla renders HEAD nested in HEAD pretty well, so we are nesting these elements
		nsIDOMElement headContainer = visualDocument.createElement(HTML.TAG_HEAD);
		pageContext.getVisualBuilder().getHeadNode().appendChild(headContainer);
		
		VpeChildrenInfo headChildrenInfo = new VpeChildrenInfo(headContainer);
		creationData.addChildrenInfo(headChildrenInfo);

		VpeChildrenInfo bodyChildrenInfo = new VpeChildrenInfo(bodyContainer);
		creationData.addChildrenInfo(bodyChildrenInfo);
		
		NodeList childNodes = sourceElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);

			if (isHeadFacet(pageContext, child)) {
				Element headFacet = (Element) child;				

				NodeList headFacetChildren = headFacet.getChildNodes();
				for (int j = 0; j < headFacetChildren.getLength(); j++) {
					headChildrenInfo.addSourceChild(headFacetChildren.item(j));
				}
			} else {
				bodyChildrenInfo.addSourceChild(child);
			}
		}

		return creationData;
	}

	/**
	 * Returns {@code true} if and only if the {@code node} is a head facet.
	 */
	private static boolean isHeadFacet(VpePageContext pageContext, Node node) {
		String templateName = VpeTemplateManager.getInstance().getTemplateName(pageContext, node);
		if (FACET_TEMPLATE_NAME.equals(templateName)) {
			Element element = (Element) node;
			String name = element.getAttribute(ATTR_NAME);
			if (HEAD_FACET_NAME.equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
