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

import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for jsf tag commandLink
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public class JsfCommandLinkTemplate extends AbstractOutputJsfTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element element = (Element) sourceNode;

	boolean disabled = ComponentUtil.string2boolean(ComponentUtil
		.getAttribute(element, HTML.ATTR_DISABLED));

	nsIDOMElement parentElement;
	if (disabled)
	    parentElement = visualDocument.createElement(HTML.TAG_SPAN);
	else
	    parentElement = visualDocument.createElement(HTML.TAG_A);

	VpeCreationData creationData = new VpeCreationData(parentElement);

	// copy attributes
	copyOutputJsfAttributes(parentElement, element);

	processOutputAttribute(pageContext, visualDocument, element,
		parentElement, creationData);

	return creationData;

    }

    @Override
    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

}
