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
package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Ajax4JSFPageTemplate extends VpeAbstractTemplate {

    private static final String HEAD_FACET_NAME = "head"; //$NON-NLS-1$

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	Element sourceElement = (Element) sourceNode;
	nsIDOMElement divHtml = visualDocument.createElement(HTML.TAG_DIV);
	nsIDOMElement divHead = visualDocument.createElement(HTML.TAG_DIV);
	nsIDOMElement divBody = visualDocument.createElement(HTML.TAG_DIV);
	VpeCreationData creationData = new VpeCreationData(divHtml);

	/*
	 * Encoding label value
	 */
	Element headFacet = ComponentUtil.getFacet(sourceElement,
		HEAD_FACET_NAME);
	if (null != headFacet) {
	    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(divHead);
	    childrenInfo.addSourceChild(headFacet);
	    divHtml.appendChild(divHead);
	    creationData.addChildrenInfo(childrenInfo);
	}

	VpeChildrenInfo divBodyInfo = new VpeChildrenInfo(divBody);
	creationData.addChildrenInfo(divBodyInfo);
	divHtml.appendChild(divBody);
	for (Node child : ComponentUtil.getChildren(sourceElement, true)) {
	    divBodyInfo.addSourceChild(child);
	}

	return creationData;
    }

    public boolean isRecreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

}
