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

import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.VpeElementProxyData;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmaliarevich
 * 
 */
public class JsfOptionSelectItemTemplate extends AbstractOutputJsfTemplate /*VpeAbstractTemplate*/ {

	public static final String ITEM_DISABLED = "itemDisabled";

	private static final String CLASS = "class";
	private static final String ITEM_LABEL = "itemLabel";

	private static final String DISABLED = "disabled";
	private static final String ENABLED_CLASS = "enabledClass";
	private static final String DISABLED_CLASS = "disabledClass";

	/* "escape" attribute of f:selectItem */
	private static final String ESCAPE = "escape";

	private String escape;
	private String disabled;
	private String enabledClass;
	private String disabledClass;

	/**
	 * 
	 */
	public JsfOptionSelectItemTemplate() {
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
		readAttributes(sourceNode);
		Element element = (Element) sourceNode;
		boolean disabledItem = ComponentUtil.string2boolean(ComponentUtil
				.getAttribute(element, ITEM_DISABLED));
		nsIDOMElement option = visualDocument.createElement(HTML.TAG_OPTION);
		// nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
		// option.appendChild(span);
		if (disabledItem)
			option.setAttribute(DISABLED, "true");
		VpeCreationData creationData = new VpeCreationData(option);

		if (attrPresents(disabled) && "true".equalsIgnoreCase(disabled)) {
			option.setAttribute(CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			option.setAttribute(CLASS, enabledClass);
		}
		
		processOutputAttribute(pageContext, visualDocument, element, option, creationData);

//		Attr attr = null;
//		if (element.hasAttribute(ITEM_LABEL)) {
//			attr = element.getAttributeNode(ITEM_LABEL);
//		}
//		
//		VpeElementProxyData elementData = new VpeElementProxyData();
//		
//		if (null != attr) {
//			if (null == escape || "true".equalsIgnoreCase(escape)) {
//				// show text as is
//				String itemLabel = attr.getNodeValue();
//				String bundleValue = ComponentUtil.getBundleValue(pageContext,
//						attr);
//				nsIDOMText text;
//				// if bundleValue differ from value then will be represent
//				// bundleValue, but text will be not edit
//				if (!itemLabel.equals(bundleValue)) {
//					text = visualDocument.createTextNode(bundleValue);
//
//				} else {
//					text = visualDocument.createTextNode(itemLabel);
//					
//					elementData.addNodeData(new AttributeData(attr,
//							option, true));
//					creationData.setElementData(elementData);
//				}
//				
//				
//				option.appendChild(text);
//			} else {
//				// show formatted text
//				VpeChildrenInfo spanInfo = new VpeChildrenInfo(option);
//				// re-parse attribute's value
//				NodeList list = NodeProxyUtil.reparseAttributeValue(elementData,attr);
//				// add children to info
//				for (int i = 0; i < list.getLength(); i++) {
//					Node child = list.item(i);
//					// add info to creation data
//					spanInfo.addSourceChild(child);
//				}
//				creationData.addChildrenInfo(spanInfo);
//			}
//		}
//
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
		return ((null != attr) && (!"".equals(attr)));
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
		disabled = source.getAttribute(DISABLED);
		enabledClass = source.getAttribute(ENABLED_CLASS);
		disabledClass = source.getAttribute(DISABLED_CLASS);
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
		escape = source.getAttribute(ESCAPE);
	}

	/*
	 * @Override public void setSourceAttributeSelection(VpePageContext
	 * pageContext, Element sourceElement, int offset, int length, Object data) {
	 * VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	 * sourceBuilder.setSelection(sourceElement, 0, 0); }
	 */
	
	@Override
	protected Attr getOutputAttributeNode(Element element) {

		if (element.hasAttribute(JSF.ATTR_ITEM_LABEL))
			return element.getAttributeNode(JSF.ATTR_ITEM_LABEL);
		return null;
	}

}
