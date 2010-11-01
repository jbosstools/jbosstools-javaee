/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for the <rich:autocomplete> component.
 * 
 * @author dvinnichek
 */
public class RichFacesAutocompleteTemplate extends
		AbstractEditableRichFacesTemplate {

	private boolean disabled;
	private boolean showButton;

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		readAttributes(pageContext, sourceNode);

		nsIDOMElement wrapper = visualDocument.createElement(HTML.TAG_SPAN);
		nsIDOMElement element = showButton ? visualDocument
				.createElement(HTML.TAG_SELECT) : visualDocument
				.createElement(HTML.TAG_INPUT);
		if (disabled) {
			element.setAttribute(RichFaces.ATTR_DISABLED,
					RichFaces.ATTR_DISABLED);
		}
		wrapper.appendChild(element);
		VpeCreationData creationData = new VpeCreationData(wrapper);

		return creationData;
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readAttributes(VpePageContext pageContext, Node sourceNode) {

		Element sourceElement = (Element) sourceNode;

		// disabled
		disabled = Constants.TRUE.equalsIgnoreCase(sourceElement
				.getAttribute(RichFaces.ATTR_DISABLED));
		// showButton
		showButton = Constants.TRUE.equalsIgnoreCase(sourceElement
				.getAttribute(RichFaces.ATTR_SHOW_BUTTON));
	}
}
