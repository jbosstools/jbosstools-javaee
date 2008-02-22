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
import org.w3c.dom.Node;

/**
 * @author dmaliarevich
 *
 */
public class JsfOptionSelectItemTemplate extends VpeAbstractTemplate {

	private static final String CLASS = "class";
	private static final String ITEM_LABEL = "itemLabel";
	
	private static final String DISABLED = "disabled";
	private static final String ENABLED_CLASS = "enabledClass";
	private static final String DISABLED_CLASS = "disabledClass";
	
	private String disabled;
	private String enabledClass;
	private String disabledClass;
	
	/**
	 * 
	 */
	public JsfOptionSelectItemTemplate() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		readAttributes(sourceNode.getParentNode());

		nsIDOMElement option = visualDocument.createElement(HTML.TAG_OPTION);
		VpeCreationData creationData = new VpeCreationData(option);
		
		if (attrPresents(disabled) && "true".equalsIgnoreCase(disabled)) {
			option.setAttribute(CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			option.setAttribute(CLASS, enabledClass);
		}
		
		String itemLabel = getLabel(sourceNode);
		option.appendChild(visualDocument.createTextNode(itemLabel));

		return creationData;
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
