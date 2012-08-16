/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfMessages extends VpeAbstractTemplate {
	
	private static final String MESSAGES = "Error Messages"; //$NON-NLS-1$

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement ul = visualDocument.createElement(HTML.TAG_UL);
		nsIDOMElement li = visualDocument.createElement(HTML.TAG_LI);
		nsIDOMText text = visualDocument.createTextNode(MESSAGES);
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE)) {
			ul.setAttribute(JSF.ATTR_STYLE, sourceElement.getAttribute(JSF.ATTR_STYLE));
		}
		if (sourceElement.hasAttribute(JSF.ATTR_STYLE_CLASS)) {
			ul.setAttribute(HTML.ATTR_CLASS, sourceElement.getAttribute(JSF.ATTR_STYLE_CLASS));
		}
		li.appendChild(text);
		ul.appendChild(li);
		/*
		 * https://issues.jboss.org/browse/JBIDE-3225
		 * Components should render usual text inside
		 */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, ul, HTML.TAG_DIV, visualDocument);
		
		return creationData;
	}

}
