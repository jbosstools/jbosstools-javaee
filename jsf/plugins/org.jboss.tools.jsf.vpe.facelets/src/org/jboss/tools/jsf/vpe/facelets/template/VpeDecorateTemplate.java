/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.facelets.template;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.jsf.vpe.facelets.Facelets;
import org.jboss.tools.jst.jsp.util.NodesManagingUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VpeDecorateTemplate extends VpeDefineContainerTemplate{
	
	public VpeCreationData create(VpePageContext pageContext,
		Node sourceNode, nsIDOMDocument visualDocument) {
	    String fileName = ((Element)sourceNode).getAttribute(Facelets.ATTR_TEMPLATE);
	    return createTemplate(fileName, pageContext, sourceNode, visualDocument);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#getSourceRegionForOpenOn(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode)
	 */
	@Override
	public IRegion getSourceRegionForOpenOn(VpePageContext pageContext,
			Node sourceNode, nsIDOMNode domNode) {
			Element sourceElement = (Element) sourceNode;
			Node paramAttr = sourceElement.getAttributeNode(Facelets.ATTR_TEMPLATE);
			return new Region(NodesManagingUtil.getStartOffsetNode(paramAttr),0);			
	}
}
