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
package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.jboss.tools.jsf.vpe.jsf.template.ComponentUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for s:link tag
 * 
 * @author dmaliarevich
 * 
 */
public class SeamLinkTemplate extends AbstractOutputJsfTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element element = (Element) sourceNode;
	nsIDOMElement parentElement;

	boolean disabled = ComponentUtil.string2boolean(ComponentUtil
		.getAttribute(element, HTML.ATTR_DISABLED));
	String value = ComponentUtil.getAttribute(element, HTML.ATTR_VALUE);
	if (disabled){
	    parentElement = VisualDomUtil.createBorderlessContainer(visualDocument);
	} else {
	    parentElement = visualDocument.createElement(HTML.TAG_A);
	}

	/*
	 * copy attributes
	 */
	copyOutputJsfAttributes(parentElement, element);

	VpeCreationData creationData;
	creationData = new VpeCreationData(parentElement);
	
	VpeChildrenInfo linkInfo = new VpeChildrenInfo(parentElement);
	creationData.addChildrenInfo(linkInfo);
	
	for (Node child : ComponentUtil.getChildren(element)) {
	    linkInfo.addSourceChild(child);
	}
	if (ComponentUtil.isNotBlank(value)) {
	    parentElement.appendChild(visualDocument.createTextNode(value));
	}
	return creationData;

    }
    
    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

}
