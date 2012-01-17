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
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Sergey Dzmitrovich
 * 
 * template for <h:outputText .../> jsf tag
 * 
 */
public class JsfOutputTextTemplate extends AbstractOutputJsfTemplate {
	/**
	 * If at least one of these attributes is present,
	 * {@code sourceNode} should be rendered in {@code SPAN}
	 * visual node.
	 * 
	 * @see #isSpanNeeding(Element)
	 */
	private static final String[] SPAN_MARKERS = {
			JSF.ATTR_STYLE, JSF.ATTR_STYLE_CLASS, JSF.ATTR_ID,
			JSF.ATTR_DIR, JSF.ATTR_TITLE, JSF.ATTR_LANG};

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element element = (Element) sourceNode;

		// create container
		final nsIDOMElement container;
		if (isSpanNeeding(element)) {
			container = visualDocument.createElement(HTML.TAG_SPAN);
		} else {
			container = VisualDomUtil.createBorderlessContainer(visualDocument);
		}

		// creation data
		VpeCreationData creationData = new VpeCreationData(container);

		// copy attributes
		copyOutputJsfAttributes(container, element);

		processOutputAttribute(pageContext, visualDocument, element, container,
				creationData);

		return creationData;

	}

	/**
	 * Returns {@code true} if given {@code element} should be
	 * rendered in {@code SPAN} tag, otherwise returns {@code false}.
	 * 
	 * @see #SPAN_MARKERS
	 */
	private boolean isSpanNeeding(Element element) {
		for (String spanMarker : SPAN_MARKERS) {
			if (element.hasAttribute(spanMarker)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
