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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates rich:dataDefinitionList template.
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesDataDefinitionListTemplate extends VpeAbstractTemplate {

	private static final String FACET_DEFINITION = "facet";

	private static final String FACET_URI = "http://java.sun.com/jsf/core";

	private static final String FACET_NAME_ATTR = "name";

	private static final String FACET_NAME_ATTR_VALUE = "term";

	private static final String STYLE_RESOURCES_PATH = "/dataDefinitionList/dataDefinitionList.css";

	private static final String HEADER_CLASS_ATTR_NAME = "headerClass";

	private static final String ROWCLASSES_ATTR_NAME = "rowClasses";

	private static final String COLUMNCLASSES_ATTR_NAME = "columnClasses";

	private static final String STYLECLASSES_ATTR_NAME = "styleClass";

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have
	 * child nodes.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		Element listElement = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DL);
		ComponentUtil.setCSSLink(pageContext, STYLE_RESOURCES_PATH,
				"dataDefinitionList");
		VpeCreationData creationData = new VpeCreationData(listElement);
		Element el = null;
		Node tempNode = null;
		NodeList list = sourceNode.getChildNodes();
		// sets attributes for list
		correctAttribute((Element) sourceNode, listElement,
				HtmlComponentUtil.HTML_STYLE_ATTR,
				HtmlComponentUtil.HTML_STYLE_ATTR, null);
		correctAttribute((Element) sourceNode, listElement,
				HtmlComponentUtil.HTML_CLASS_ATTR,
				HtmlComponentUtil.HTML_CLASS_ATTR, "listClass");

		for (int i = 0; i < list.getLength(); i++) {
			tempNode = list.item(i);
			if (!(tempNode instanceof Element)) {
				continue;
			}
			el = (Element) tempNode;
			if (el.getLocalName().equals(FACET_DEFINITION)
					&& pageContext.getSourceTaglibUri(el).equals(FACET_URI)
					&& el.getAttribute(FACET_NAME_ATTR) != null
					&& el.getAttribute(FACET_NAME_ATTR).equals(
							FACET_NAME_ATTR_VALUE)) {
				parseListFacetDefinitionElement(pageContext, sourceNode,
						visualDocument, creationData, listElement, el);
			} else {
				parseListElement(pageContext, sourceNode, visualDocument,
						creationData, listElement, el);
			}
		}
		return creationData;
	}

	/**
	 * Parses elements for list
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param creationData
	 * @param parentList
	 * @param childElement
	 */
	private void parseListElement(VpePageContext pageContext, Node sourceNode,
			Document visualDocument, VpeCreationData creationData,
			Element parentList, Element childElement) {
		Element dd = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DD);
		correctAttribute((Element) sourceNode, dd, ROWCLASSES_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, null);
		if (dd.getAttribute(HtmlComponentUtil.HTML_CLASS_ATTR) == null
				|| dd.getAttribute(HtmlComponentUtil.HTML_CLASS_ATTR).length() == 0) {
			correctAttribute((Element) sourceNode, dd, COLUMNCLASSES_ATTR_NAME,
					HtmlComponentUtil.HTML_CLASS_ATTR, "columnClass");
		}
		parentList.appendChild(dd);
		VpeChildrenInfo vpeChildrenInfo = new VpeChildrenInfo(dd);
		vpeChildrenInfo.addSourceChild(childElement);
		creationData.addChildrenInfo(vpeChildrenInfo);
	}

	/**
	 * Parse listDataDefinition facet to HTML DT element
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param creationData
	 * @param parentList
	 * @facet facetElement
	 */
	private void parseListFacetDefinitionElement(VpePageContext pageContext,
			Node sourceNode, Document visualDocument,
			VpeCreationData creationData, Element parentList,
			Element facetElement) {
		Element dt = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DT);
		correctAttribute((Element) sourceNode, dt, HEADER_CLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, "headerClass");
		parentList.appendChild(dt);
		VpeChildrenInfo child = new VpeChildrenInfo(dt);
		child.addSourceChild(facetElement);
		creationData.addChildrenInfo(child);
	}

	/**
	 * Move attributes from sourceNode to html
	 * 
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualNode
	 * @param attrName
	 * @param htmlAttrName
	 * @param defValue
	 */
	private void correctAttribute(Element sourceNode, Element visualNode,
			String attrName, String htmlAttrName, String defValue) {
		String attrValue = ((Element) sourceNode).getAttribute(attrName);
		if (attrValue != null) {
			visualNode.setAttribute(htmlAttrName, attrValue);
		} else if (defValue != null) {
			visualNode.setAttribute(htmlAttrName, defValue);
		} else
			visualNode.removeAttribute(attrName);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data, String name,
			String value) {
		processAttributeChanges(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
	}

	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		processAttributeChanges(pageContext, sourceElement, visualDocument,
				visualNode, data, name);
	}

	/**
	 * Correct list style accordinly parameters
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceElement
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param visualNode
	 * @param data
	 * @param name
	 */

	private void processAttributeChanges(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		Element el = (Element) visualNode;
		if (HtmlComponentUtil.HTML_STYLE_ATTR.equals(name)) {
			correctAttribute(sourceElement, el, name, name, null);
		} else if (STYLECLASSES_ATTR_NAME.equals(name)) {
			correctAttribute(sourceElement, el, name,
					HtmlComponentUtil.HTML_CLASS_ATTR, "listClass");
		} else if (HEADER_CLASS_ATTR_NAME.equals(name)) {
			NodeList nodeList = el.getChildNodes();
			Node temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DT))) {
					correctAttribute(sourceElement, (Element) temp,
							HEADER_CLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "headerClass");
				}
			}
		} else if (ROWCLASSES_ATTR_NAME.equals(name)) {
			NodeList nodeList = el.getChildNodes();
			Node temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DD))) {
					correctAttribute(sourceElement, (Element) temp,
							ROWCLASSES_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "columnClass");
				}
			}
		} else if (COLUMNCLASSES_ATTR_NAME.equals(name)) {
			NodeList nodeList = el.getChildNodes();
			Node temp = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				temp = nodeList.item(i);
				if ((temp instanceof Element)
						&& (temp.getNodeName()
								.equalsIgnoreCase(HtmlComponentUtil.HTML_TAG_DD))) {
					correctAttribute(sourceElement, (Element) temp,
							COLUMNCLASSES_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "columnClass");
				}
			}
		}
	}
}
