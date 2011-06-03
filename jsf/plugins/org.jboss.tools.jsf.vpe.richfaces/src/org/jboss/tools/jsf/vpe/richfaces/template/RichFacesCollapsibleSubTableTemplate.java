/*******************************************************************************
 * Copyright (c) 2007-2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesCollapsibleSubTableTemplate extends RichFacesSubTableTemplate {
	
	public VpeCreationData create(final VpePageContext pageContext,
			final Node sourceNode, final nsIDOMDocument visualDocument) {
		final Element sourceElement = (Element)sourceNode;
		final nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		String style = "display: table-row-group;"; //$NON-NLS-1$
		if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
			style = "display: none;"; //$NON-NLS-1$
		}
		tbody.setAttribute(HTML.ATTR_STYLE, style);
		VpeCreationData creationData = new VpeCreationData(tbody);
		creationData = encodeSubTable(pageContext, creationData, sourceElement,
				visualDocument, tbody);
		return creationData;
	}
	
	public void toggle(Node sourceNode, String toggleId) {
		/*
		 * Collapsed state will be changed.
		 * Then the whole template should be rebuilt to apply changes.
		 */
		if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
			sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "false", null); //$NON-NLS-1$
		} else {
			sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "true", null); //$NON-NLS-1$
		}
	}
}
