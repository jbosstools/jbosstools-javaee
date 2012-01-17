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
import org.w3c.dom.Node;

/**
 * @author dmaliarevich
 * 
 */
public abstract class AbstractOptionSelectItemTemplate extends AbstractSelectItemTemplate /* VpeAbstractTemplate */{

	/* "escape" attribute of f:selectItem */
	private String escape = null;
	private String disabled = null;
	private String enabledClass = null;
	private String disabledClass = null;

	protected AbstractOptionSelectItemTemplate(SelectItemType selectItemType) {
		super(selectItemType);
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

		readParentAttributes(sourceNode.getParentNode());
		readAttributes(sourceNode);
		Element element = (Element) sourceNode;

		nsIDOMElement option = visualDocument.createElement(HTML.TAG_OPTION);

		if (selectItemType.isDisabledItem(element)) {
			option.setAttribute(HTML.ATTR_DISABLED, Constants.TRUE);
		}
		VpeCreationData creationData = new VpeCreationData(option);

		if (attrPresents(disabled) && Constants.TRUE.equalsIgnoreCase(disabled)) {
			option.setAttribute(HTML.ATTR_CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			option.setAttribute(HTML.ATTR_CLASS, enabledClass);
		}

		processOutputAttribute(pageContext, visualDocument, element, option,
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
		return ((null != attr) && (!Constants.EMPTY.equals(attr)));
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
		disabled = source.getAttribute(HTML.ATTR_DISABLED);
		enabledClass = source.getAttribute(JSF.ATTR_ENABLED_CLASS);
		disabledClass = source.getAttribute(JSF.ATTR_DISABLED_CLASS);
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readAttributes(Node sourceNode) {
		if (null == sourceNode) {
			return;
		}
		Element source = (Element) sourceNode;
		escape = source.getAttribute(JSF.ATTR_ESCAPE);
	}
}
