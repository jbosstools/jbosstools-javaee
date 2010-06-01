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

import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeClassUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Creates rich:dataDefinitionList template.
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesDataDefinitionListTemplate extends VpeAbstractTemplate {

	/**
	 * CSS settings
	 */
	private static final String DEFAULT_DD_CLASS = "columnClass"; //$NON-NLS-1$
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
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement listElement = visualDocument.createElement(HTML.TAG_DL);
		ComponentUtil.setCSSLink(
				pageContext, 
				STYLE_RESOURCES_PATH,
				"dataDefinitionList"); //$NON-NLS-1$
		
		VpeCreationData creationData = new VpeCreationData(listElement);		
		creationData.addChildrenInfo(new VpeChildrenInfo(null));
		// sets attributes for list
		ComponentUtil.correctAttribute((Element)sourceNode, listElement,
				RichFaces.ATTR_STYLE, HTML.ATTR_STYLE, null, null);
		
		ComponentUtil.correctAttribute((Element)sourceNode, listElement, 
				RichFaces.ATTR_STYLE_CLASS, HTML.ATTR_CLASS, null, "listClass"); //$NON-NLS-1$
		Element termFacet = SourceDomUtil.getFacetByName(sourceElement,
				RichFaces.NAME_FACET_TERM);
		Map<String, List<Node>> termFacetChildren = VisualDomUtil
				.findFacetElements(termFacet, pageContext);
		Node termNode= ComponentUtil.getFacetBody(termFacetChildren);
		
		/*
		 * Encode body of the tag.
		 * Add text nodes to children list also.
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement, true);

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
		
		VpeChildrenInfo childInfo = null;
		for (int row = 0; row < rows; row++) {		
			if (termNode != null) {
				insertDtElement(sourceNode, visualDocument,
						creationData, listElement, termNode);
			}
			
			if ((termFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0) 
					|| !children.isEmpty()) {
				String ddClass = DEFAULT_DD_CLASS; 
				if (rowClassesSize > 0) {
					ddClass+= " " + rowClasses.get(row % rowClassesSize); //$NON-NLS-1$
				}
				nsIDOMElement dd = visualDocument.createElement(HTML.TAG_DD);
				dd.setAttribute(HTML.ATTR_CLASS, ddClass);
				listElement.appendChild(dd);
				
				childInfo = new VpeChildrenInfo(dd);
				for (Node child : termFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
				    childInfo.addSourceChild(child);
				}
				for (Node child : children) {
				    childInfo.addSourceChild(child);
				}
				creationData.addChildrenInfo(childInfo);
			}
		}
		return creationData;
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
			Node facetElement) {
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
//		nsIDOMElement el = queryInterface(visualNode, nsIDOMElement.class);
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
//					nsIDOMElement tempVisualElement = queryInterface(temp, nsIDOMElement.class); 
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
//					nsIDOMElement tempVisualElement = queryInterface(temp, nsIDOMElement.class); 
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
//					nsIDOMElement tempVisualElement = queryInterface(temp, nsIDOMElement.class);
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
