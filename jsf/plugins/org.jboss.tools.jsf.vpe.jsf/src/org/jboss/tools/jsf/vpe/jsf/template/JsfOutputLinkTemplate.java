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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *   Template for jsf tag outputLink (ver 1.*)
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 *
 */
public class JsfOutputLinkTemplate extends VpeAbstractTemplate {

   
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	Element sourceElement = (Element) sourceNode;

	boolean disabled = ComponentUtil.string2boolean(ComponentUtil
		.getAttribute(sourceElement, HTML.ATTR_DISABLED));
	String dir = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_DIR);
	String style = ComponentUtil.getAttribute(sourceElement,
		HTML.ATTR_STYLE);
	String styleClass = ComponentUtil.getAttribute(sourceElement,
		HTML.ATTR_CLASS);

	nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
	VpeCreationData creationData = new VpeCreationData(span);
	if (disabled) {

	    if (styleClass != null && styleClass.length() > 0)
		span.setAttribute(HTML.ATTR_CLASS, styleClass);
	    if (dir != null && dir.length() > 0)
		span.setAttribute(HTML.ATTR_DIR, dir);
	    if (style != null && style.length() > 0)
		span.setAttribute(HTML.ATTR_STYLE, style);

	    return creationData;
	}

	nsIDOMElement a = visualDocument.createElement(HTML.TAG_A);
	span.appendChild(a);
	NodeList children = sourceNode.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (!(child instanceof Element)) {
		continue;
	    }

	    VpeChildrenInfo info;
	    if (child.getPrefix() == null) {
		info = new VpeChildrenInfo(span);

	    } else {
		info = new VpeChildrenInfo(a);
	    }
	    info.addSourceChild(child);
	    creationData.addChildrenInfo(info);
	}
	return creationData;
    }
    
}


