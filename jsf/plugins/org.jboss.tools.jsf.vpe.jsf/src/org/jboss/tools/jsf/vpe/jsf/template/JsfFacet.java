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
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author sdzmitrovich
 * 
 */
public class JsfFacet extends VpeAbstractTemplate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);

		VpeCreationData creationData = new VpeCreationData(div);

		NodeList children = sourceNode.getChildNodes();

		/*
		 * <f:facet .../> can contain only one element. so we find first visible
		 * node and add to "div"
		 */

		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);

			// we add to "div" non-empty text node or tag
			if (((child.getNodeType() == Node.TEXT_NODE) && !(child
					.getNodeValue().trim().length() == 0))
					|| child.getNodeType() == (Node.ELEMENT_NODE)) {

				VpeChildrenInfo childrenInfo = new VpeChildrenInfo(div);

				childrenInfo.addSourceChild(children.item(i));

				creationData.addChildrenInfo(childrenInfo);

				break;
			}
		}

		return creationData;
	}
}
