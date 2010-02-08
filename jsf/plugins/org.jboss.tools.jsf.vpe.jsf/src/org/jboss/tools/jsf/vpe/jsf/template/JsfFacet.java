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

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author sdzmitrovich
 * 
 */
public class JsfFacet extends VpeAbstractTemplate {

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
	nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
	VpeCreationData creationData = new VpeCreationData(div);
	NodeList children = sourceNode.getChildNodes();
	boolean jsfComponentFound = false;
	/*
	 * Only one JSF component may be present inside a facet tag, if more are
	 * present only the first one is rendered and the other ones are
	 * ignored.
	 */
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
//	    String sourcePrefix = child.getPrefix();
//	    if (XmlUtil.hasTaglib(sourceNode, pageContext, sourcePrefix)) {
//		String sourceNodeUri = XmlUtil.getTaglibUri(sourceNode, pageContext, sourcePrefix);
		if( ((child.getNodeType()==Node.TEXT_NODE) && 
				(child.getNodeValue()!=null)
				&& (child.getNodeValue().trim().length()>0)
				) ||(child.getNodeType() == Node.ELEMENT_NODE)) {
		    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(div);
		    childrenInfo.addSourceChild(child);
		    creationData.addChildrenInfo(childrenInfo);
		    jsfComponentFound = true;
		    break;
		}
		//commented by Maksim Areshkau as fix for https://jira.jboss.org/jira/browse/JBIDE-5744
//	    if ((child.getNodeType() == Node.ELEMENT_NODE)){
////			&& (VisualDomUtil.JSF_CORE_URI.equalsIgnoreCase(sourceNodeUri)
////				|| VisualDomUtil.JSF_HTML_URI.equalsIgnoreCase(sourceNodeUri)
////				|| VisualDomUtil.RICH_FACES_URI.equalsIgnoreCase(sourceNodeUri) 
////				|| VisualDomUtil.A4J_URI.equalsIgnoreCase(sourceNodeUri)
////				|| VisualDomUtil.FACELETS_URI.equalsIgnoreCase(sourceNodeUri))) {
//		    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(div);
//		    childrenInfo.addSourceChild(child);
//		    creationData.addChildrenInfo(childrenInfo);
//		    jsfComponentFound = true;
//		    break;
////		}
//	    }
	}
	
	if (!jsfComponentFound) {
	    div.setAttribute(HTML.ATTR_STYLE, "display: none; "); //$NON-NLS-1$
	    creationData.addChildrenInfo(new VpeChildrenInfo(div));
	}

	return creationData;
    }
}
