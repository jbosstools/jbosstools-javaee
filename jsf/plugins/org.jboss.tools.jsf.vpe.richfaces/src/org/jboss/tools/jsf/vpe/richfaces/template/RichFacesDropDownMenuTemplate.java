/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDropDownMenuTemplate extends VpeAbstractTemplate {
	
	private static final String STYLECLASS_ATTR_NAME = "styleClass";
	private static final String STYLE_ATTR_NAME = "style";
	private static final String ITEMCLASS_ATTR_NAME = "itemClass";
	private static final String ITEMSTYLE_ATTR_NAME = "itemStyle";
	private static final String LABEL_FACET_NAME = "label";
	

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
	    VpeCreationData creatorInfo = null;

	    Element sourceElement = (Element)sourceNode;
		nsIDOMElement visualMenu = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
 		
		ComponentUtil.setCSSLink(pageContext, "dropDownMenu/dropDownMenu.css", "richFacesDropDownMenu");

		correctAttribute(sourceElement, visualMenu,
				STYLECLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect",
				"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect");
		correctAttribute(sourceElement, visualMenu,
				STYLE_ATTR_NAME,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);

		nsIDOMElement visualMenuLabel = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		visualMenuLabel.setAttribute("class", "dr-label-text-decor rich-label-text-decor");
		correctAttribute(sourceElement, visualMenuLabel,
				ITEMCLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				"dr-label-text-decor rich-label-text-decor",
				"dr-label-text-decor rich-label-text-decor");
		correctAttribute(sourceElement, visualMenuLabel,
				ITEMSTYLE_ATTR_NAME,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);


		visualMenu.appendChild(visualMenuLabel);
		
		creatorInfo = new VpeCreationData(visualMenu);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(visualMenuLabel);
		Element facetElement = getLabelFacet(sourceElement);

		if (facetElement != null) {
			childrenInfo.addSourceChild(facetElement);
		} else {
			Attr ddmLabelFromAttribute = sourceElement.getAttributeNode("value");
	    	String valueForLabel = ddmLabelFromAttribute != null && ddmLabelFromAttribute.getValue() != null
		    			? ddmLabelFromAttribute.getValue()
		    			: "";
		    nsIDOMNode textLabel = visualDocument.createTextNode(valueForLabel);
			visualMenuLabel.appendChild(textLabel);
		}

		creatorInfo.addChildrenInfo(childrenInfo);
		visualMenu.appendChild(visualMenuLabel);
		
		return creatorInfo;
	}
	

	
	private Element getLabelFacet(Element sourceElement) {
		if (sourceElement == null) {
			return null;
		}
		
		NodeList children = sourceElement.getChildNodes();
		if (children != null) {
			int size = children.getLength();
			if (size > 0) {
				for (int i=0; i<size; i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE
							&& child.getNodeName().endsWith(":facet")) {
						Element facetElement = (Element)child;
						if (LABEL_FACET_NAME.equals(facetElement.getAttribute("name"))) {
							return facetElement;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private String getElementTextContent(Element element) {
		String content = null;
		NodeList children = element.getChildNodes();
		for (int i = 0; children != null && i < children.getLength(); i++) {
			String text = null;
			if (children.item(i).getNodeType() == Node.TEXT_NODE) {
				text = children.item(i).getNodeValue();
			} else if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				text = getElementTextContent((Element)children.item(i));
			}
			if (text != null && text.trim().length() > 0) {
				if (content == null) {
					content = text;
				} else {
					content += " " + text;
				}
			}
		}
		
		return content;
	}
	
	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		processAttributeChanges(pageContext, sourceElement, visualDocument, visualNode, data, name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		processAttributeChanges(pageContext, sourceElement, visualDocument, visualNode, data, name);
	}

	/**
	 * Correct list style accordinly parameters
	 * 
	 * @param pageContext
	 * @param sourceElement
	 * @param visualDocument
	 * @param visualNode
	 * @param data
	 * @param name
	 */

	private void processAttributeChanges(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode,
			Object data, String name) {
		nsIDOMElement el = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		if (STYLECLASS_ATTR_NAME.equals(name)) {
			if (el.getNodeName()
							.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV)) {
				correctAttribute(sourceElement, el,
						STYLECLASS_ATTR_NAME,
						HtmlComponentUtil.HTML_CLASS_ATTR,
						"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect",
						"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect");
			}
		} else if (STYLE_ATTR_NAME.equals(name)) {
			if (el.getNodeName()
							.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV)) {
				correctAttribute(sourceElement, el,
						STYLE_ATTR_NAME,
						HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
			}
		} else if (ITEMCLASS_ATTR_NAME.equals(name)) {
			nsIDOMNodeList nodeList = el.getChildNodes();
			nsIDOMNode temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV))) {
					correctAttribute(sourceElement, (nsIDOMElement) temp.queryInterface(nsIDOMNode.NS_IDOMNODE_IID),
							ITEMCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, 
							"dr-label-text-decor rich-label-text-decor",
							"dr-label-text-decor rich-label-text-decor");
				}
			}
		} else if (ITEMSTYLE_ATTR_NAME.equals(name)) {
		    	nsIDOMNodeList nodeList = el.getChildNodes();
		    	nsIDOMNode temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV))) {
					correctAttribute(sourceElement, (nsIDOMElement) temp.queryInterface(nsIDOMNode.NS_IDOMNODE_IID),
							ITEMSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
				}
			}
		}
	}

	/**
	 * Move attributes from sourceNode to html
	 * 
	 * @param sourceNode
	 * @param visualElement
	 * @param attrName
	 * @param htmlAttrName
	 * @param prefValue
	 * @param defValue
	 */
	private void correctAttribute(Element sourceNode, nsIDOMElement visualElement,
			String attrName, String htmlAttrName, String prefValue, String defValue) {
		String attrValue = ((Element) sourceNode).getAttribute(attrName);
		if (prefValue != null && prefValue.trim().length() > 0 && attrValue != null) {
			attrValue = prefValue.trim() + " " + attrValue;
		}
		if (attrValue != null) {
			visualElement.setAttribute(htmlAttrName, attrValue);
		} else if (defValue != null) {
			visualElement.setAttribute(htmlAttrName, defValue);
		} else
			visualElement.removeAttribute(attrName);
	}
	
	 /**
	     * Is invoked after construction of all child nodes of the current visual
	     * node.
	     * 
	     * @param pageContext
	     *                Contains the information on edited page.
	     * @param sourceNode
	     *                The current node of the source tree.
	     * @param visualDocument
	     *                The document of the visual tree.
	     * @param data
	     *                Object <code>VpeCreationData</code>, built by a method
	     *                <code>create</code>
	     */

	    @Override
	    public void validate(VpePageContext pageContext, Node sourceNode,
		    nsIDOMDocument visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		correctLabelStyles(data.getNode());
	    }
	    
	    /**
	     * Correct font for label
	     * 
	     * @param node
	     */
	    private void correctLabelStyles(nsIDOMNode node) {

		try {
		    nsIDOMElement element = (nsIDOMElement) node
			    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		    if (node.getNodeName().equalsIgnoreCase(
			    HtmlComponentUtil.HTML_TAG_SPAN)) {
			String styleClass = element.getAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
			styleClass = (styleClass==null?"":styleClass) + " dr-label-text-decor dr-menu-label";
			element.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		    }
		    nsIDOMNodeList list = node.getChildNodes();
		    for (int i = 0; i < list.getLength(); i++) {
			correctLabelStyles(list.item(i));
		    }
		} catch (XPCOMException e) {
		    return;
		}
	    }
}