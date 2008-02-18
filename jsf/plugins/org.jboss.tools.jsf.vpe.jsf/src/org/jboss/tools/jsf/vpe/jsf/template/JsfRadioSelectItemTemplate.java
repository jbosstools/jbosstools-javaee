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

import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author sdzmitrovich
 * 
 * template for radio select item
 * 
 */
public class JsfRadioSelectItemTemplate extends VpeAbstractTemplate {

	// type of input tag
	private static final String ATTR_TYPE_VALUE = "radio"; //$NON-NLS-1$

	// common part of the name of element
	private static final String ATTR_NAME_VALUE = "radio_name_"; //$NON-NLS-1$

	// name of attribute which need represent
	private static final String ITEM_LABEL_ATTR = "itemLabel"; //$NON-NLS-1$

	// style of span
	private static final String SPAN_STYLE_VALUE = "-moz-user-modify: read-write;"; //$NON-NLS-1$

	/**
	 * 
	 */
	public JsfRadioSelectItemTemplate() {

		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
		// add title attribute to span
		span.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		span.setAttribute(HTML.ATTR_STYLE, SPAN_STYLE_VALUE);

		// create radio element
		nsIDOMElement radio = visualDocument.createElement(HTML.TAG_INPUT);
		radio.setAttribute(HTML.ATTR_TYPE, ATTR_TYPE_VALUE);

		// set title
		radio.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));

		// set name
		radio.setAttribute(HTML.ATTR_NAME, ATTR_NAME_VALUE
				+ getNameSuffix(sourceNode));

		// add radio to span
		span.appendChild(radio);

		// get label for element
		String label = getLabel(sourceNode);

		// label exist
		if (null != label) {
			// add label to span
			nsIDOMText text = visualDocument.createTextNode(label);
			span.appendChild(text);
		}

		return new VpeCreationData(span);
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
		Node attrNode = sourceNode.getAttributes()
				.getNamedItem(ITEM_LABEL_ATTR);

		// if attribute exist return value
		if (attrNode != null)
			return attrNode.getNodeValue();

		return null;
	}

	/**
	 * generate unique suffix of name for radio as unique suffix use start
	 * position of parent tag ( "h:selectOneRadio" or "x:selectOneRadio")
	 * 
	 * @param sourceNode
	 * @return
	 */
	private String getNameSuffix(Node sourceNode) {

		String name_suffix = ""; //$NON-NLS-1$

		// get parent element
		Node parent = sourceNode.getParentNode();

		if (parent.getNodeType() == Node.ELEMENT_NODE) {
			
			ElementImpl element = (ElementImpl) parent;

			// get start position of parent
			name_suffix = String.valueOf(element.getStartOffset());
		}

		return name_suffix;

	}

}
