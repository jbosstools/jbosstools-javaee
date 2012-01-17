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
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class JsfSubView.
 */
public class JsfSubView extends VpeAbstractTemplate {

	private static String WRAPPER_DIV_STYLE = "width: 100%; display: table;"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		div.setAttribute(VpeStyleUtil.ATTRIBUTE_STYLE, WRAPPER_DIV_STYLE);
		
		VpeCreationData creationData = new VpeCreationData(div);
		VpeChildrenInfo divInfo = new VpeChildrenInfo(div);
		creationData.addChildrenInfo(divInfo);
		
		for (Node child : ComponentUtil.getChildren(sourceElement)) {
			divInfo.addSourceChild(child);
		}

		return creationData;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
