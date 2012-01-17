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
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author mareshkau
 *
 */
public class JsfForm extends VpeAbstractTemplate {

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement content_form= null;
		if(!isExistingInParent(sourceNode)) {
			content_form = visualDocument.createElement(HTML.TAG_FORM);
			// copy jsf attributes to html, util function hasn't been founded, mareshkat
			Element form = (Element) sourceNode;
			if(form.hasAttribute(JSF.ATTR_STYLE)) {
				content_form.setAttribute(HTML.ATTR_STYLE, form.getAttribute(JSF.ATTR_STYLE));
			}
			if(form.hasAttribute(JSF.ATTR_STYLE_CLASS)) {
				content_form.setAttribute(HTML.ATTR_CLASS, form.getAttribute(JSF.ATTR_STYLE_CLASS));
			}
		} else {
			content_form = visualDocument.createElement(HTML.TAG_SPAN);
		}	
		VpeCreationData creationData = new VpeCreationData(content_form);
		return creationData;
	}
	//mareshkau, fir for JBIDE-3011
	private static boolean isExistingInParent(Node sourceNode) {
		String nodeName=sourceNode.getNodeName();
		Node parentNode = sourceNode.getParentNode();
		while((parentNode!=null)&&(parentNode instanceof Node)){
			if(nodeName.equals(parentNode.getNodeName())) {
				return true;
			}
			parentNode = parentNode.getParentNode();
		}
		return false;
	}
}
