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
package org.jboss.tools.jsf.vpe.jsf.template.selectitem;

import org.eclipse.wst.xml.core.internal.document.ElementImpl;
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
 * @author sdzmitrovich
 * 
 * template for radio select item
 * 
 */
abstract public class AbstractRadioSelectItemTemplate extends AbstractSelectItemTemplate {

	/* "itemDisabled" attribute of f:selectItem */
	public static final String ITEM_DISABLED = "itemDisabled"; //$NON-NLS-1$

	// type of input tag
	private static final String ATTR_TYPE_VALUE = "radio"; //$NON-NLS-1$

	// common part of the name of element
	private static final String ATTR_NAME_VALUE = "radio_name_"; //$NON-NLS-1$

	// style of span
	private static final String SPAN_STYLE_VALUE = "-moz-user-modify: read-write;"; //$NON-NLS-1$

	private String dir;

	protected AbstractRadioSelectItemTemplate(SelectItemType selectItemType) {
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

		Element element = (Element) sourceNode;

		// create container element
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		final boolean disabledItem = selectItemType.isDisabledItem(element);

		// add title attribute to div
		div.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		nsIDOMElement radio = visualDocument.createElement(HTML.TAG_INPUT);
		if (disabledItem)
			radio.setAttribute(ITEM_DISABLED, Constants.TRUE);
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);
		if (disabledItem)
			label.setAttribute(ITEM_DISABLED, Constants.TRUE);
		div.appendChild(radio);
		div.appendChild(label);

		if (null != element) {
			dir = element.getAttribute(JSF.ATTR_DIR);
		}

		VpeCreationData creationData = new VpeCreationData(div);

		// set attributes
		div.setAttribute(HTML.ATTR_STYLE, SPAN_STYLE_VALUE);
		radio.setAttribute(HTML.ATTR_TYPE, ATTR_TYPE_VALUE);
		radio.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		radio.setAttribute(HTML.ATTR_NAME, ATTR_NAME_VALUE
				+ getNameSuffix(sourceNode));

		if (attrPresents(dir)) {
			radio.setAttribute(HTML.ATTR_DIR, dir);
		}

		processOutputAttribute(pageContext, visualDocument, element, label,
				creationData);

		return creationData;
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
		
//		if ((parent == null) && (sourceNode instanceof Element)
//                && (((Element) sourceNode).getAttribute(VpeVisualDomBuilder.PARENT) != null)) {
//            parent = (Node) ((Element) sourceNode).getAttributeNode(VpeVisualDomBuilder.PARENT).getUserData(VpeVisualDomBuilder.PARENT);
//        }
		if ((parent != null) && (parent.getNodeType() == Node.ELEMENT_NODE)) {

			ElementImpl element = (ElementImpl) parent;

			// get start position of parent
			name_suffix = String.valueOf(element.getStartOffset());
		}

		return name_suffix;

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
		return ((null != attr) && (attr.length()!=0));
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
