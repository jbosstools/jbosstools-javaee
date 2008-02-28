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

import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	private static final String DIR = "dir";
	private static final String DISABLED = "disabled";
	private static final String ENABLED_CLASS = "enabledClass";
	private static final String DISABLED_CLASS = "disabledClass";

	
	/* "escape" attribute of f:selectItem */
	private static final String ESCAPE = "escape";
	
	private String escape;
	private String dir;
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
		
		readParentAttributes(sourceNode.getParentNode());
		readAttributes(sourceNode);
		
		Element element = (Element) sourceNode;
		
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		nsIDOMElement label = visualDocument.createElement(HTML.TAG_LABEL);
		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
		nsIDOMElement labelSpan = visualDocument.createElement(HTML.TAG_SPAN);

		VpeCreationData creationData = new VpeCreationData(span);

		// add title attribute to span
		span.setAttribute(HTML.ATTR_TITLE, getTitle(sourceNode));
		span.setAttribute(HTML.ATTR_STYLE, SPAN_STYLE_VALUE);

		input.setAttribute(HTML.ATTR_TYPE, TYPE_CHECKBOX);

		if (attrPresents(dir)) {
			input.setAttribute(HTML.ATTR_DIR, dir);
		}
		
		if (attrPresents(disabled) && "true".equalsIgnoreCase(disabled)) {
			label.setAttribute(CLASS, disabledClass);
		} else if (attrPresents(enabledClass)) {
			label.setAttribute(CLASS, enabledClass);
		}

		label.appendChild(labelSpan);
		span.appendChild(input);
		span.appendChild(label);
		
		Attr attr = null;
		if (element.hasAttribute(ITEM_LABEL)) {
			attr = element.getAttributeNode(ITEM_LABEL);
		}

		if (null != attr) {
			if (null == escape || "true".equalsIgnoreCase(escape)) {
				// show text as is
				String itemLabel = attr.getNodeValue();
				labelSpan.appendChild(visualDocument.createTextNode(itemLabel));
			} else {
				// show formatted text
				VpeChildrenInfo labelSpanInfo = new VpeChildrenInfo(labelSpan);
				// re-parse attribute's value
				NodeList list = NodeProxyUtil.reparseAttributeValue(attr);
				// add children to info
				for (int i = 0; i < list.getLength(); i++) {
					Node child = list.item(i);
					// add info to creation data
					labelSpanInfo.addSourceChild(child);
				}
				creationData.addChildrenInfo(labelSpanInfo);
			}
		}

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
	 * Read attributes from the h:SelectManyCheckbox element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readParentAttributes(Node sourceNode) {
		if (null == sourceNode) {
			return;
		}
		Element source = (Element) sourceNode;
		dir = source.getAttribute(DIR);
		disabled = source.getAttribute(DISABLED);
		enabledClass = source.getAttribute(ENABLED_CLASS);
		disabledClass = source.getAttribute(DISABLED_CLASS);
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
		escape = source.getAttribute(ESCAPE);
	}

	@Override
	public void setSourceAttributeSelection(VpePageContext pageContext,
			Element sourceElement, int offset, int length, Object data) {
		VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
		sourceBuilder.setSelection(sourceElement, 0, 0);
	}

}
