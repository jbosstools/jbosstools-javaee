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
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 * template for selectOneMenu select item
 * 
 */
public class JsfSelectOneMenuTemplate extends VpeAbstractTemplate {

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
		ATTR_LIST_COPY.put(JSF.ATTR_DISABLED, HTML.ATTR_DISABLED);
		ATTR_LIST_COPY.put(JSF.ATTR_DIR, HTML.ATTR_DIR);
	}

	/**
	 * 
	 */
	public JsfSelectOneMenuTemplate() {

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

		// create select element
		nsIDOMElement select = visualDocument.createElement(HTML.TAG_SELECT);
		select.setAttribute(HTML.ATTR_SIZE, "1"); //$NON-NLS-1$
		Element element = (Element) sourceNode;

		// import attributes from source
		Set<String> jsfAttributes = ATTR_LIST_COPY.keySet();

		for (String attributeName : jsfAttributes) {			
			// add attribute to "select"
			if (element.hasAttribute(attributeName)) {
				// get attribute
				String attr = element.getAttribute(attributeName);
				if (attributeName.equalsIgnoreCase(HTML.ATTR_DISABLED)) {
					if (attr.equalsIgnoreCase(Constants.TRUE)) {
						select.setAttribute(ATTR_LIST_COPY.get(attributeName),
								"disabled"); //$NON-NLS-1$
						
					}
					continue;
				}
				select.setAttribute(ATTR_LIST_COPY.get(attributeName), attr);
			}

		}

		return new VpeCreationData(select);
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

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		if (data.getNode() != null) {
			applyChildAttributes((Element) sourceNode, data.getNode());
		}
	}

	private void applyChildAttributes(Element sourceElement, nsIDOMNode node) {
		boolean disabled = false;
		try {
			nsIDOMNodeList list = node.getChildNodes();
			nsIDOMElement element = queryInterface(node, nsIDOMElement.class);
			disabled = ComponentUtil.string2boolean(ComponentUtil.getAttribute(
					sourceElement, HTML.ATTR_DISABLED));
			if (node.getNodeName().equalsIgnoreCase(HTML.TAG_OPTION)) {
				element.setAttribute(HTML.ATTR_CLASS, disabled ? ComponentUtil
						.getAttribute(sourceElement, JSF.ATTR_DISABLED_CLASS)
						: ComponentUtil.getAttribute(sourceElement,
								JSF.ATTR_ENABLED_CLASS));
			}
			for (int i = 0; i < list.getLength(); i++) {
				applyChildAttributes(sourceElement, list.item(i));
			}
		} catch (XPCOMException e) {
			// Ignore
			return;
		}
	}
}
