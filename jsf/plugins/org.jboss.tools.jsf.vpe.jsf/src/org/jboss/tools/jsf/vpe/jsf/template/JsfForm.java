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
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmaliarevich
 *
 */
public class JsfForm extends VpeAbstractTemplate {

	private static String TABLE_WIDTH_STYLE = "width: 100%;"; //$NON-NLS-1$
	
	private static final String DIR_VALUE_RTL = "RTL"; //$NON-NLS-1$
	private static final String DIR_VALUE_LTR = "LTR"; //$NON-NLS-1$
	
	/* Attributes of h:form */
	private static final String DIR = "dir"; //$NON-NLS-1$
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$
	
	private String dir;
	private String style;
	private String styleClass;
	
	/**
	 * Instantiates a new jsf form.
	 */
	public JsfForm() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		readAttributes(sourceElement);
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

		nsIDOMElement content_div = visualDocument.createElement(HTML.TAG_DIV);
		
		if (attrPresents(style)) {
			content_div.setAttribute(STYLE, style);
		}
		if (attrPresents(styleClass)) {
			content_div.setAttribute(CLASS, styleClass);
		}
		if (attrPresents(dir)
				&& (dir.equalsIgnoreCase(DIR_VALUE_RTL) 
						|| dir.equalsIgnoreCase(DIR_VALUE_LTR))) {
			content_div.setAttribute(DIR, dir);
		} 
		
		table.setAttribute(VpeStyleUtil.ATTRIBUTE_STYLE, TABLE_WIDTH_STYLE);

		td.appendChild(content_div);
		tr.appendChild(td);
		table.appendChild(tr);
		
		VpeCreationData creationData = new VpeCreationData(table);
		VpeChildrenInfo divInfo = new VpeChildrenInfo(content_div);
		creationData.addChildrenInfo(divInfo);
		
		for (Node child : getChildren(sourceElement)) {
			divInfo.addSourceChild(child);
		}

		return creationData;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @param sourceElement the source element
	 * 
	 * @return the children
	 */
	public static List<Node> getChildren(Element sourceElement) {
		ArrayList<Node> children = new ArrayList<Node>();
		NodeList nodeList = sourceElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
				children.add(child);
		}
		return children;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode
	 *            the source node
	 */
	private void readAttributes(Element sourceElement) {
		style = sourceElement.getAttribute(STYLE);
		styleClass = sourceElement.getAttribute(STYLE_CLASS);
		dir = sourceElement.getAttribute(DIR);
	}
	
    /**
     * Checks is attribute presents.
     * 
     * @param attr the attribute
     * 
     * @return true, if successful
     */
    private boolean attrPresents(String attr) {
		return ((null != attr) && (!"".equals(attr))); //$NON-NLS-1$
	}
}
