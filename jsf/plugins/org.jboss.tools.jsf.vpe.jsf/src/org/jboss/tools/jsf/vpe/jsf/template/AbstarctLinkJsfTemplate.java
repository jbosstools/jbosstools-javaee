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
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Abstract template for h:link and h:commandLink 
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public abstract class AbstarctLinkJsfTemplate extends AbstractOutputJsfTemplate {

    private static final String H_FORM = "h:form"; //$NON-NLS-1$
    private static final String A4J_FORM = "a4j:form"; //$NON-NLS-1$
    private static final String OUTSIDE_FORM_TEXT = ": This link is disabled as it is not nested within a JSF form."; //$NON-NLS-1$
    
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element element = (Element) sourceNode;
	nsIDOMElement parentElement;

	boolean disabled = ComponentUtil.string2boolean(ComponentUtil
		.getAttribute(element, HTML.ATTR_DISABLED));
	String value = ComponentUtil.getAttribute(element, HTML.ATTR_VALUE);
	boolean hasParentForm = hasParentForm(pageContext, element);
	
	if (!hasParentForm) {
	    parentElement = VisualDomUtil.createBorderlessContainer(visualDocument);
	} else if (disabled) {
	    parentElement = VisualDomUtil.createBorderlessContainer(visualDocument);
	} else {
	    parentElement = visualDocument.createElement(HTML.TAG_A);
	}

	// copy attributes
	copyOutputJsfAttributes(parentElement, element);

	VpeCreationData creationData;
	if (!hasParentForm) {
	    nsIDOMElement topSpan = VisualDomUtil.createBorderlessContainer(visualDocument);
	     nsIDOMElement noteSpan = VisualDomUtil.createBorderlessContainer(visualDocument);
	     noteSpan.appendChild(visualDocument.createTextNode(OUTSIDE_FORM_TEXT));
	     topSpan.appendChild(parentElement);
	    topSpan.appendChild(noteSpan);
	    creationData = new VpeCreationData(topSpan);
	} else {
	    creationData = new VpeCreationData(parentElement);
	}
	
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

    private boolean hasParentForm(VpePageContext pageContext, Element sourceElement) {
	Node parent = sourceElement.getParentNode();
	while (parent != null && parent instanceof Element && parent.getNodeName() != null) {
		String parentTemplateName = VpeTemplateManager.getInstance().getTemplateName(pageContext, parent);
	    if (H_FORM.equals(parentTemplateName)
		    || A4J_FORM.equals(parentTemplateName)) {
		return true;
	    }
	    parent = parent.getParentNode();
	}
	return false;
    }
    
    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

}
