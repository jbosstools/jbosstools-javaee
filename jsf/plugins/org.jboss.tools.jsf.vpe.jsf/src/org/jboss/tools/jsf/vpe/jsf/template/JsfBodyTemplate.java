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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.html.template.HtmlBodyTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A wrapper of VpeAbstractTemplate. Does the same, but ignores all attributes except id.
 * @author yradtsevich
 * @see HtmlBodyTemplate
 */
public class JsfBodyTemplate extends VpeAbstractTemplate {
	public static final String ID_ID = "id"; //$NON-NLS-1$
	
	private final HtmlBodyTemplate htmlBodyTemplate = new HtmlBodyTemplate();
	
	/**
	 * @see org.jboss.tools.vpe.html.template.HtmlBodyTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		sourceNode = sourceNode.cloneNode(true);
		NamedNodeMap attributes = sourceNode.getAttributes();
		int length  = attributes.getLength();
		for (int i = 0; i < length; i++) {
			Node attribute = attributes.item(i);

			if(!attribute.getNodeName().equalsIgnoreCase(ID_ID)) {
				attributes.removeNamedItem(attribute.getNodeName());
			}
		}
		
		return htmlBodyTemplate.create(pageContext, sourceNode, visualDocument);
	}
}
