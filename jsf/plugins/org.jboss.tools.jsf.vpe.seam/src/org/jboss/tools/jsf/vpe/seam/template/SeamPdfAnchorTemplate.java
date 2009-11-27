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

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfAnchorTemplate extends SeamPdfAbstractTemplate {

	private static final String ATTR_REF = "reference";

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement visualElement = visualDocument.createElement(HTML.TAG_A);
		if (sourceElement.hasAttribute(HTML.ATTR_NAME)) {
			visualElement.setAttribute(HTML.ATTR_NAME, sourceElement
					.getAttribute(HTML.ATTR_NAME));
			visualElement.setAttribute(HTML.ATTR_STYLE,
					"color:black; text-decoration:none");
		}
		if (sourceElement.hasAttribute(ATTR_REF)) {
			visualElement.setAttribute(HTML.ATTR_HREF, sourceElement
					.getAttribute(ATTR_REF));
			visualElement.removeAttribute(HTML.ATTR_STYLE);
		}
		return new VpeCreationData(visualElement);
	}

}
