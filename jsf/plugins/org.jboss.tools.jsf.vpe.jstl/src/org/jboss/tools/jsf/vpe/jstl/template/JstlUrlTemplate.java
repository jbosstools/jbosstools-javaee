/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jstl.template;

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

/**
 * Class for c:url template
 * 
 * @author dmaliarevich
 *
 */
public class JstlUrlTemplate extends VpeAbstractTemplate {

    public JstlUrlTemplate() {
	super();
    }

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
		
    	Element sourceElement = (Element) sourceNode;		
		nsIDOMElement span = VisualDomUtil.createBorderlessContainer(visualDocument);
		
		if (sourceElement.hasAttribute(HTML.ATTR_VALUE)) {
			String value = sourceElement.getAttribute(HTML.ATTR_VALUE);
			nsIDOMText urlText = visualDocument.createTextNode(value);
			span.appendChild(urlText);
		}
		
		VpeCreationData creationData = new VpeCreationData(span);		
		return creationData;
    }

}
