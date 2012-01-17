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
package org.jboss.tools.jsf.vpe.jsf.template.selectitem;

import org.jboss.tools.jsf.vpe.jsf.template.JSF;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author dmaliarevich
 * 
 */
abstract public class AbstractCheckboxSelectItemTemplate extends AbstractSelectItemTemplate {

	private static final String TYPE_CHECKBOX = "checkbox"; //$NON-NLS-1$

	// style of span
	private static final String SPAN_STYLE_VALUE = "-moz-user-modify: read-write;"; //$NON-NLS-1$

	private String dir;
	private String disabled;
	private String enabledClass;
	private String disabledClass;

	protected AbstractCheckboxSelectItemTemplate(SelectItemType selectItemType) {
		super(selectItemType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		readParentAttributes(sourceNode.getParentNode());

		Element element = (Element) sourceNode;

		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);
		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

		VpeCreationData creationData = new VpeCreationData(span);

		// add title attribute to span
		span.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		span.setAttribute(HTML.ATTR_STYLE, SPAN_STYLE_VALUE);

		input.setAttribute(HTML.ATTR_TYPE, TYPE_CHECKBOX);

		if (attrPresents(dir)) {
			input.setAttribute(HTML.ATTR_DIR, dir);
		}

		if (attrPresents(disabled) && Constants.TRUE.equalsIgnoreCase(disabled)) { 
			label.setAttribute(HTML.ATTR_CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			label.setAttribute(HTML.ATTR_CLASS, enabledClass);
		}

		span.appendChild(input);
		span.appendChild(label);

		processOutputAttribute(pageContext, visualDocument, element, label,
				creationData);

		return creationData;
	}

	/**
	 * Checks is attribute presents.
	 * 
	 * @param attr
	 *            the attribute
	 * 
	 * @return true, if successful
	 */
	private boolean attrPresents(String attr) {
		return ((null != attr) && (attr.length() != 0));
	}

	/**
	 * generate title of element
	 * 
	 * @param sourceNode
	 * @return
	 */
	private String getTitle(Node sourceNode) {

		String tagString = " <" + sourceNode.getNodeName(); //$NON-NLS-1$
		NamedNodeMap attrs = sourceNode.getAttributes();
		if (attrs != null) {
			tagString += attrs.getLength() > 0 ? " " : ""; //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				tagString += attr.getNodeName() + "=\"" + attr.getNodeValue() //$NON-NLS-1$
						+ "\"" + (i < (attrs.getLength() - 1) ? " " : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		tagString += (sourceNode.hasChildNodes() ? "" : "/") + "> "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return tagString;
	}

	/**
	 * Read attributes from the h:SelectManyCheckbox element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readParentAttributes(Node sourceNode) {
		if (null == sourceNode) {
			return;
		}
		Element source = (Element) sourceNode;
		dir = source.getAttribute(JSF.ATTR_DIR);
		disabled = source.getAttribute(JSF.ATTR_DISABLED);
		enabledClass = source.getAttribute(JSF.ATTR_ENABLED_CLASS);
		disabledClass = source.getAttribute(JSF.ATTR_DISABLED_CLASS);
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
