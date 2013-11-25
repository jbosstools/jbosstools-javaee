/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.jsf.vpe.jsf.template.ComponentUtil;
import org.jboss.tools.jst.web.ui.internal.editor.util.NodesManagingUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamDecorateTemplate extends VpeDefineContainerTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	
	Element sourceElement = (Element) sourceNode;
	
	/*
	 * Reading attributes
	 */
	String fileName = sourceElement.getAttribute(SeamUtil.ATTR_TEMPLATE);
	String styleClass = sourceElement.getAttribute(SeamUtil.ATTR_STYLE_CLASS);
	String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
	
	/*
	 * Creating template
	 */
	VpeCreationData creationData = createTemplate(fileName, pageContext, sourceNode, visualDocument);
	
	/*
	 * Setting style and class attributes
	 */
	if (ComponentUtil.isNotBlank(style)) {
	    ((nsIDOMElement)creationData.getNode()).setAttribute(HTML.ATTR_STYLE, style);
	}
	if (ComponentUtil.isNotBlank(styleClass)) {
	    ((nsIDOMElement)creationData.getNode()).setAttribute(HTML.ATTR_CLASS, styleClass);
	}
	
	return creationData;
    }
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#getSourceRegionForOpenOn(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode)
	 */
	@Override
	public IRegion getSourceRegionForOpenOn(VpePageContext pageContext,
			Node sourceNode, nsIDOMNode domNode) {
			Element sourceElement = (Element) sourceNode;
			Node paramAttr = sourceElement.getAttributeNode(SeamUtil.ATTR_TEMPLATE);
			return new Region(NodesManagingUtil.getStartOffsetNode(paramAttr),0);			
	}
}
