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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 *         template for selectOneListbox select item
 * 
 */
public class JsfSelectManyListbox extends VpeAbstractTemplate {

	private static final String ATTR_MULTIPLE_VALUE = "multiple"; //$NON-NLS-1$
	/**
	 * list of visible children
	 */
	private static List<String> CHILDREN_LIST = new ArrayList<String>();

	static {
		CHILDREN_LIST.add(JSF.TAG_SELECT_ITEM);
		CHILDREN_LIST.add(JSF.TAG_SELECT_ITEMS);
	}

	/**
	 * list of copied attributes
	 */
	private static Map<String, String> ATTR_LIST_COPY = new HashMap<String, String>();

	static {
		ATTR_LIST_COPY.put(JSF.ATTR_STYLE, HTML.ATTR_STYLE);
		ATTR_LIST_COPY.put(JSF.ATTR_STYLE_CLASS, HTML.ATTR_CLASS);
	}


	/**
	 * 
	 */
	public JsfSelectManyListbox() {

		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools
	 * .vpe.editor.context.VpePageContext, org.w3c.dom.Node,
	 * org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		// create select element
		nsIDOMElement select = visualDocument.createElement(HTML.TAG_SELECT);

		Element element = (Element) sourceNode;

		// import attributes from source
		Set<String> jsfAttributes = ATTR_LIST_COPY.keySet();

		for (String attributeName : jsfAttributes) {
			if (element.hasAttribute(attributeName)) {
				// get attribute
				String attr = element.getAttribute(attributeName);
				// add attribute to "select"
				select.setAttribute(ATTR_LIST_COPY.get(attributeName), attr);
			}
		}

		// set "multiple" attribute
		select.setAttribute(HTML.ATTR_MULTIPLE, ATTR_MULTIPLE_VALUE);

		// set "dir" attribute
		if (element.hasAttribute(JSF.ATTR_DIR)) {
			String dir = element.getAttribute(JSF.ATTR_DIR);
			select.setAttribute(HTML.ATTR_DIR, dir);
		}

		// set "size" attribute
		if (element.hasAttribute(JSF.ATTR_SIZE)) {
			String size = element.getAttribute(JSF.ATTR_SIZE);
			// if source has "size" attribute import it
			select.setAttribute(HTML.ATTR_SIZE, size);
		} else {
			// count size
			select.setAttribute(HTML.ATTR_SIZE, String
					.valueOf(countSize(element)));
		}

		return new VpeCreationData(select);
	}

	/**
	 * Count size for "select" (size = number of "selectItem" and "selectItems"
	 * children )
	 * 
	 * 
	 * @param sourceNode
	 * @return size of select (1 or more)
	 */
	private int countSize(Node sourceNode) {

		NodeList children = sourceNode.getChildNodes();
		int size = 0;
		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);
			// if children is one of visible items
			if (CHILDREN_LIST.contains(child.getLocalName()))
				size++;
		}
		// if 'size' == 0 return 1 else 'size'
		return size == 0 ? 1 : size;

	}

	/**
	 * 
	 */
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMNode visualNode, Object data, String name) {

		// get DOMElement(root element is select)
		nsIDOMElement select = queryInterface(visualNode, nsIDOMElement.class);

		// remove attribute
		select.removeAttribute(name);
	}

}
