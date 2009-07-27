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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeClassUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
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

	/**
	 * 
	 */
	private static final String DEFAULT_DD_CLASS = "columnClass"; //$NON-NLS-1$
	private static final String FACET_URI = "http://java.sun.com/jsf/core"; //$NON-NLS-1$
	private static final String FACET_NAME_ATTR = "name"; //$NON-NLS-1$
	private static final String FACET_NAME_ATTR_VALUE = "term"; //$NON-NLS-1$
	private static final String STYLE_RESOURCES_PATH = "/dataDefinitionList/dataDefinitionList.css"; //$NON-NLS-1$

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
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		nsIDOMElement listElement = visualDocument.createElement(HTML.TAG_DL);
		ComponentUtil.setCSSLink(
				pageContext, 
				STYLE_RESOURCES_PATH,
				"dataDefinitionList"); //$NON-NLS-1$
		
		VpeCreationData creationData = new VpeCreationData(listElement);		
		creationData.addChildrenInfo(new VpeChildrenInfo(null));
		
		Element child = null;
		NodeList list = sourceNode.getChildNodes();
		
		// sets attributes for list
		ComponentUtil.correctAttribute((Element)sourceNode, listElement,
				RichFaces.ATTR_STYLE, HTML.ATTR_STYLE, null, null);
		
		ComponentUtil.correctAttribute((Element)sourceNode, listElement, 
				RichFaces.ATTR_STYLE_CLASS, HTML.ATTR_CLASS, null, "listClass"); //$NON-NLS-1$

		Element facetElement = null;
		List<Element> dataDefinitionElements = new ArrayList<Element>();		
		for (int i = 0; i < list.getLength(); i++) {
			Node nodeChild = list.item(i);
			if (!(nodeChild instanceof Element)) {
				continue;
			}
			child = (Element) nodeChild;
			
			if (!child.getLocalName().equals(RichFaces.TAG_FACET)) {
				dataDefinitionElements.add(child);				
			} else if (facetElement == null 
					&& (FACET_URI.equals(pageContext.getSourceTaglibUri(child)))
					&& child.getAttribute(FACET_NAME_ATTR) != null
					&& child.getAttribute(FACET_NAME_ATTR).equals(
							FACET_NAME_ATTR_VALUE)) {
				facetElement = child;
			}
		}

		final List<String> rowClasses;
		try {
			final VpeExpression exprRowClasses = RichFaces.getExprRowClasses();		
			rowClasses = VpeClassUtil.getClasses(exprRowClasses, sourceNode,
					pageContext);
		} catch (VpeExpressionException e) {
			throw new RuntimeException(e);
		}
		
		final int rowClassesSize = rowClasses.size();		
		int rows = 1;
		try {
			rows = Integer.parseInt(((Element)sourceNode).getAttribute(RichFaces.ATTR_ROWS));
		} catch (NumberFormatException x) {
			// this is OK, rows still equals 1
		}
		
		for (int row = 0; row < rows; row++) {		
			if (facetElement != null) {
				insertDtElement(sourceNode, visualDocument,
						creationData, listElement, facetElement);
				
			}
			
			if (!dataDefinitionElements.isEmpty()) {
				String ddClass = DEFAULT_DD_CLASS; 
				if (rowClassesSize > 0) {
					ddClass+= " " + rowClasses.get(row % rowClassesSize); //$NON-NLS-1$
				}
				insertDdElement(sourceNode, visualDocument,
						creationData, listElement, dataDefinitionElements,
						ddClass);
			}
		}
		return creationData;
	}

	/**
	 * Insert elements in list
	 * 
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param creationData
	 * @param parentList
	 * @param childElement
	 * @param styleClass class of this DD element
	 */
	private void insertDdElement(Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData creationData, 
			nsIDOMElement parentList, List<Element> childElements, String styleClass) {
		nsIDOMElement dd = visualDocument.createElement(HTML.TAG_DD);
		
		dd.setAttribute(HTML.ATTR_CLASS, styleClass);
		
		parentList.appendChild(dd);

		VpeChildrenInfo vpeChildrenInfo = new VpeChildrenInfo(dd);
		for (Element childElement : childElements) {
			vpeChildrenInfo.addSourceChild(childElement);
		}
		creationData.addChildrenInfo(vpeChildrenInfo);
	}

	/**
	 * Insert listDataDefinition facet to HTML DT element
	 * 
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param creationData
	 * @param parentList
	 * @facet facetElement
	 */
	private void insertDtElement(Node sourceNode, nsIDOMDocument visualDocument,
			VpeCreationData creationData, nsIDOMElement parentList,
			Element facetElement) {
		nsIDOMElement dt = visualDocument.createElement(HTML.TAG_DT);
		ComponentUtil.correctAttribute(
				(Element) sourceNode, 
				dt, 
				RichFaces.ATTR_HEADER_CLASS,
				HTML.ATTR_CLASS,
				null,
				"headerClass"); //$NON-NLS-1$
		parentList.appendChild(dt);
		VpeChildrenInfo child = new VpeChildrenInfo(dt);
		child.addSourceChild(facetElement);
		creationData.addChildrenInfo(child);
	}
	
	/**
	 * @see VpeAbstractTemplate#isRecreateAtAttrChange
	 */
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

//	@Override
//	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name,
//			String value) {
//		processAttributeChanges(pageContext, sourceElement, visualDocument, visualNode, data, name);
//	}
//
//	@Override
//	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
//		processAttributeChanges(pageContext, sourceElement, visualDocument, visualNode, data, name);
//	}
//
//	/**
//	 * Correct list style accordinly parameters
//	 * 
//	 * @param pageContext
//	 *            Contains the information on edited page.
//	 * @param sourceElement
//	 *            The current node of the source tree.
//	 * @param visualDocument
//	 *            The document of the visual tree.
//	 * @param visualNode
//	 * @param data
//	 * @param name
//	 */
//	private void processAttributeChanges(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
//		nsIDOMElement el = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
//		if (HTML.ATTR_STYLE.equals(name)) {
//			ComponentUtil.correctAttribute(sourceElement, el, name, name, null, null);
//		} else if (RichFaces.ATTR_STYLE_CLASS.equals(name)) {
//			ComponentUtil.correctAttribute(sourceElement, el, name,
//					HTML.ATTR_CLASS, null, "listClass");
//		} else if (RichFaces.ATTR_HEADER_CLASS.equals(name)) {
//			nsIDOMNodeList nodeList = el.getChildNodes();
//			nsIDOMNode temp = null;
//			for (int i = 0; i < nodeList.getLength(); i++) {
//				temp = nodeList.item(i);
//				if ((temp != null)
//						&& (temp.getNodeName()
//								.equalsIgnoreCase(HTML.TAG_DT))) {
//					nsIDOMElement tempVisualElement = (nsIDOMElement)temp.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
//					ComponentUtil.correctAttribute(sourceElement, 
//							tempVisualElement,
//							RichFaces.ATTR_HEADER_CLASS,
//							HTML.ATTR_CLASS,
//							null,
//							"headerClass");
//				}
//			}
//		} else if (RichFaces.ATTR_ROW_CLASSES.equals(name)) {
//			nsIDOMNodeList nodeList = el.getChildNodes();
//			nsIDOMNode temp = null;
//			for (int i = 0; i < nodeList.getLength(); i++) {
//				temp = nodeList.item(i);
//				if ((temp != null )
//						&& (temp.getNodeName()
//								.equalsIgnoreCase(HTML.TAG_DD))) {
//					nsIDOMElement tempVisualElement = (nsIDOMElement)temp.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID); 
//					ComponentUtil.correctAttribute(sourceElement, 
//							tempVisualElement,
//							RichFaces.ATTR_ROW_CLASSES,
//							HTML.ATTR_CLASS,
//							null,
//							"columnClass");
//				}
//			}
//		} else if (RichFaces.ATTR_COLUMN_CLASSES.equals(name)) {
//			nsIDOMNodeList nodeList = el.getChildNodes();
//			nsIDOMNode temp = null;
//			for (int i = 0; i < nodeList.getLength(); i++) {
//				temp = nodeList.item(i);
//				if ((temp != null)
//						&& (temp.getNodeName()
//								.equalsIgnoreCase(HTML.TAG_DD))) {
//					nsIDOMElement tempVisualElement = (nsIDOMElement)temp.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
//					ComponentUtil.correctAttribute(
//							sourceElement, 
//							tempVisualElement,							
//							RichFaces.ATTR_COLUMN_CLASSES,
//							HTML.ATTR_CLASS, 
//							null,
//							"columnClass");
//				}
//			}
//		}
//	}
}
