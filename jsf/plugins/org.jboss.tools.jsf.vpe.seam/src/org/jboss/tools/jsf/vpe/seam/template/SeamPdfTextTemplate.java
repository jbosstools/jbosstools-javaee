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

import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfTextTemplate extends AbstractOutputJsfTemplate {


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element element = (Element) sourceNode;

		// create container
		final nsIDOMElement container;
		container = VisualDomUtil.createBorderlessContainer(visualDocument);

		VpeCreationData creationData = new VpeCreationData(container);

		processOutputAttribute(pageContext, visualDocument, element, container,
				creationData);

		return creationData;
	}

	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode,
			nsIDOMNode visualNode, Object data) {
		Node[] footers = SeamUtil.getChildsByName(pageContext, sourceNode,
				"p:footer"); //$NON-NLS-1$
		if (footers != null && footers.length != 0) {
			Node parentPdfDocumentNode = SeamUtil.getParentByName(pageContext,
					sourceNode, "p:document"); //$NON-NLS-1$
			if (parentPdfDocumentNode != null) {
				return parentPdfDocumentNode;
			}
		}
		return super
				.getNodeForUpdate(pageContext, sourceNode, visualNode, data);
	}
	
}
