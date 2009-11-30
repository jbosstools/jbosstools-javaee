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

/**
 * @author yzhishko
 */

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfParagraphTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement visualElement;
	private Element sourceElement;
	private nsIDOMDocument visualDocument;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		this.visualDocument = visualDocument;
		sourceElement = (Element) sourceNode;
		nsIDOMNode visualNode = visualDocument.createElement(HTML.TAG_DIV);
		visualElement = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		processFirstChild();
		SeamUtil.setAlignment(sourceElement, visualElement);
		return new VpeCreationData(visualElement);
	}

	private void processFirstChild() {
		Node firstChild = sourceElement.getFirstChild();
		if (firstChild != null) {
			if (firstChild.getNodeType() == Node.TEXT_NODE) {
				String nodeValue = firstChild.getNodeValue();
				nodeValue = nodeValue.replace(" ", "").replace("\n", "")  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
						.replace("\t", "").replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				if (!nodeValue.equals("")) { //$NON-NLS-1$
					nsIDOMNode brNode = visualDocument
							.createElement(HTML.TAG_BR);
					visualElement.appendChild(brNode);
				}
			}
		}
	}

}
