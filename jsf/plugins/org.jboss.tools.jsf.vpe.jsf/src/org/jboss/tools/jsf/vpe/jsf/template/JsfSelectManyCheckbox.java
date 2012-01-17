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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmaliarevich
 * 
 */
public class JsfSelectManyCheckbox extends VpeAbstractTemplate {

	private static final String PAGE_DIRECTION = "pageDirection"; //$NON-NLS-1$
	private static final String LINE_DIRECTION = "lineDirection"; //$NON-NLS-1$

	/* h:SelectManyCheckbox attributes */
	private static final String BORDER = "border"; //$NON-NLS-1$

	private String style;
	private String styleClass;
	private String border;
	private String layout;

	/**
	 * list of visible children
	 */
	private static List<String> CHILDREN_LIST = new ArrayList<String>();

	static {
		CHILDREN_LIST.add(JSF.TAG_SELECT_ITEM);
		CHILDREN_LIST.add(JSF.TAG_SELECT_ITEMS);
	}

	/**
	 * 
	 */
	public JsfSelectManyCheckbox() {
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

		readAttributes(sourceNode);

		Element sourceElement = (Element) sourceNode;
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		div.appendChild(table);
		nsIDOMElement tr = null;
		nsIDOMElement td = null;

		if (attrPresents(style)) {
			table.setAttribute(HTML.ATTR_STYLE, style);
		}
		if (attrPresents(styleClass)) {
			table.setAttribute(HTML.ATTR_CLASS, styleClass);
		}
		if (attrPresents(border)) {
			table.setAttribute(BORDER, border);
		}

		VpeCreationData creationData = new VpeCreationData(div);

		NodeList children = sourceNode.getChildNodes();

		if (attrPresents(layout) && PAGE_DIRECTION.equalsIgnoreCase(layout)) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				// if children is one of visible items
				if (CHILDREN_LIST.contains(child.getLocalName())) {
					tr = visualDocument.createElement(HTML.TAG_TR);
					td = visualDocument.createElement(HTML.TAG_TD);
					tr.appendChild(td);
					table.appendChild(tr);
					VpeChildrenInfo info = new VpeChildrenInfo(td);
					info.addSourceChild(child);
					creationData.addChildrenInfo(info);
				}
			}
		} else {
			tr = visualDocument.createElement(HTML.TAG_TR);
			table.appendChild(tr);
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				// if children is one of visible items
				if (CHILDREN_LIST.contains(child.getLocalName())) {
					td = visualDocument.createElement(HTML.TAG_TD);
					tr.appendChild(td);
					VpeChildrenInfo info = new VpeChildrenInfo(td);
					info.addSourceChild(child);
					creationData.addChildrenInfo(info);
				}
			}
		}

		return creationData;
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readAttributes(Node sourceNode) {
		Element source = (Element) sourceNode;
		style = source.getAttribute(JSF.ATTR_STYLE);
		styleClass = source.getAttribute(JSF.ATTR_STYLE_CLASS);
		border = source.getAttribute(BORDER);
		layout = source.getAttribute(JSF.ATTR_LAYOUT);
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

}
