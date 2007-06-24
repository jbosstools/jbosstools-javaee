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
import org.jboss.tools.vpe.editor.util.MozillaSupports;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDropDownMenuTemplate extends VpeAbstractTemplate {
	
	private static final String STYLECLASS_ATTR_NAME = "styleClass";
	private static final String STYLE_ATTR_NAME = "style";
	private static final String ITEMCLASS_ATTR_NAME = "itemClass";
	private static final String ITEMSTYLE_ATTR_NAME = "itemStyle";
	
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		Element sourceElement = (Element)sourceNode;
		Element visualMenu = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
 		
		ComponentUtil.setCSSLink(pageContext, "dropDownMenu/dropDownMenu.css", "richFacesDropDownMenu");

		correctAttribute(sourceElement, visualMenu,
				STYLECLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect",
				"dr-menu-label dr-menu-label-unselect rich-ddmenu-label rich-ddmenu-label-unselect");
		correctAttribute(sourceElement, visualMenu,
				STYLE_ATTR_NAME,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);

		Attr ddmLabelFromAttribute = sourceElement.getAttributeNode("value");

		Element visualMenuLabel = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
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
		
		String ddmLabelFromFacet = getLabelFacet(sourceElement);
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		
		VpeCreationData creatorInfo = new VpeCreationData(visualMenu);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(visualMenuLabel);
		Node textLabel = null;
		if (ddmLabelFromFacet != null) {
			textLabel = visualDocument.createTextNode(ddmLabelFromFacet);
		} else {
			textLabel = visualDocument.createTextNode(ddmLabelFromAttribute.getValue());
		}
		if (textLabel != null) {
			visualMenuLabel.appendChild(textLabel);
			creatorInfo.addChildrenInfo(childrenInfo);
		}
		visualMenu.appendChild(visualMenuLabel);
		MozillaSupports.release(visualMenuLabel);
		
		return creatorInfo;
	}

	private static final String LABEL_FACET_NAME = "label";
	
	private String getLabelFacet(Element sourceElement) {
		String labelFacet = null;
		NodeList children = sourceElement.getChildNodes();

		int cnt = children != null ? children.getLength() : 0;
		if (cnt > 0) {
			for (int i = 0; i < cnt; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE &&
						child.getNodeName().endsWith(":facet")) {
					Element facetElement = (Element)child;
					String facetName = facetElement.getAttribute("name");
					if (LABEL_FACET_NAME.equals(facetName)) {
						NodeList facetChildren = facetElement.getChildNodes();
						int facetCnt = facetChildren != null ? facetChildren.getLength() : 0;
						if (facetCnt > 0) {
							for (int j = 0; j < facetCnt; j++) {
								Node facetChild = facetChildren.item(i);
								if (facetChild.getNodeType() == Node.ELEMENT_NODE &&
										facetChild.getNodeName().endsWith(":verbatim")) {
									labelFacet = getElementTextContent((Element)facetChild);
									break;
								}
							}
						}
					}
				}
			}
		}
		return labelFacet;
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
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name) {
		processAttributeChanges(pageContext, sourceElement, visualDocument, visualNode, data, name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
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
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		Element el = (Element) visualNode;
		
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
			NodeList nodeList = el.getChildNodes();
			Node temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV))) {
					correctAttribute(sourceElement, (Element) temp,
							ITEMCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, 
							"dr-label-text-decor rich-label-text-decor",
							"dr-label-text-decor rich-label-text-decor");
				}
			}
		} else if (ITEMSTYLE_ATTR_NAME.equals(name)) {
			NodeList nodeList = el.getChildNodes();
			Node temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DIV))) {
					correctAttribute(sourceElement, (Element) temp,
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
	 * @param visualNode
	 * @param attrName
	 * @param htmlAttrName
	 * @param prefValue
	 * @param defValue
	 */
	private void correctAttribute(Element sourceNode, Element visualNode,
			String attrName, String htmlAttrName, String prefValue, String defValue) {
		String attrValue = ((Element) sourceNode).getAttribute(attrName);
		if (prefValue != null && prefValue.trim().length() > 0 && attrValue != null) {
			attrValue = prefValue.trim() + " " + attrValue;
		}
		if (attrValue != null) {
			visualNode.setAttribute(htmlAttrName, attrValue);
		} else if (defValue != null) {
			visualNode.setAttribute(htmlAttrName, defValue);
		} else
			visualNode.removeAttribute(attrName);
	}

}
