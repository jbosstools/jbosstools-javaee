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

import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
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
public class JsfCheckboxSelectItemTemplate extends VpeAbstractTemplate {

	private static final String ITEM_LABEL = "itemLabel";
	private static final String TYPE_CHECKBOX = "checkbox";
	private static final String CLASS = "class";

	// style of span
	private static final String SPAN_STYLE_VALUE = "-moz-user-modify: read-write;"; //$NON-NLS-1$

	/*h:SelectManyCheckbox attributes*/
	private static final String DISABLED = "disabled";
	private static final String ENABLED_CLASS = "enabledClass";
	private static final String DISABLED_CLASS = "disabledClass";

	private String disabled;
	private String enabledClass;
	private String disabledClass;

	/**
	 * 
	 */
	public JsfCheckboxSelectItemTemplate() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		readAttributes(sourceNode.getParentNode());

		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);
		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

		VpeCreationData creationData = new VpeCreationData(span);

		// add title attribute to span
		span.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		span.setAttribute(HTML.ATTR_STYLE, SPAN_STYLE_VALUE);

		input.setAttribute(HTML.ATTR_TYPE, TYPE_CHECKBOX);

		if (attrPresents(disabled) && "true".equalsIgnoreCase(disabled)) {
			label.setAttribute(CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			label.setAttribute(CLASS, enabledClass);
		}

		String itemLabel = getLabel(sourceNode);
		label.appendChild(visualDocument.createTextNode(itemLabel));

		span.appendChild(input);
		span.appendChild(label);

		return creationData;
	}

	/**
	 * Checks is attribute presents.
	 * 
	 * @param attr the attribute
	 * 
	 * @return true, if successful
	 */
	private boolean attrPresents(String attr) {
		return ((null != attr) && (!"".equals(attr)));
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
	 * get Label of element
	 * 
	 * @param sourceNode
	 * @return
	 */
	private String getLabel(Node sourceNode) {
		// get value of "itemLabeL" from jsf tag
		Node attrNode = sourceNode.getAttributes().getNamedItem(ITEM_LABEL);
		// if attribute exist return value
		if (attrNode != null) {
			return attrNode.getNodeValue();
		}
		return "";
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readAttributes(Node sourceNode) {
		if (null == sourceNode) {
			return;
		}
		Element source = (Element) sourceNode;
		disabled = source.getAttribute(DISABLED);
		enabledClass = source.getAttribute(ENABLED_CLASS);
		disabledClass = source.getAttribute(DISABLED_CLASS);
	}

	@Override
	public void setSourceAttributeSelection(VpePageContext pageContext,
			Element sourceElement, int offset, int length, Object data) {
		VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
		sourceBuilder.setSelection(sourceElement, 0, 0);
	}

}
