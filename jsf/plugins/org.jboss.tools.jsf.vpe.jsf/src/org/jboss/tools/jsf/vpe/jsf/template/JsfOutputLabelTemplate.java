/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Sergey Dzmitrovich
 * 
 * template for <h:outputLabel .../> jsf tag
 * 
 */
public class JsfOutputLabelTemplate extends AbstractOutputJsfTemplate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element element = (Element) sourceNode;

		// create label element
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);

		// copy attributes
		copyOutputJsfAttributes(label, element);
		copyAttribute(label, element, JSF.ATTR_FOR, HTML.ATTR_FOR);

		// creation data
		VpeCreationData creationData = new VpeCreationData(label);

		processOutputAttribute(pageContext, visualDocument, element, label,
				creationData);

		return creationData;

	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {

		return true;
	}

}
