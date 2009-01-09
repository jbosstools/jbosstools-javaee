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

import java.util.List;

import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.XmlUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author sdzmitrovich
 * 
 */
public class JsfFacet extends VpeAbstractTemplate {

    private static String JSF_CORE_URI = "http://java.sun.com/jsf/core"; //$NON-NLS-1$
    private static String JSF_HTML_URI = "http://java.sun.com/jsf/html"; //$NON-NLS-1$
    private static String RICH_FACES_URI = "http://richfaces.org/rich"; //$NON-NLS-1$
    private static String A4J_URI = "http://richfaces.org/a4j"; //$NON-NLS-1$
    private static String FACELETS_URI = "http://java.sun.com/jsf/facelets"; //$NON-NLS-1$

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
	    String sourcePrefix = child.getPrefix();
	    List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(sourceNode,
		    pageContext);
	    TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(
		    sourcePrefix, taglibs);
	    if (null != sourceNodeTaglib) {
		String sourceNodeUri = sourceNodeTaglib.getUri();
		if ((child.getNodeType() == Node.ELEMENT_NODE)
			&& (JSF_CORE_URI.equalsIgnoreCase(sourceNodeUri)
				|| JSF_HTML_URI.equalsIgnoreCase(sourceNodeUri)
				|| RICH_FACES_URI.equalsIgnoreCase(sourceNodeUri) 
				|| A4J_URI.equalsIgnoreCase(sourceNodeUri)
				|| FACELETS_URI.equalsIgnoreCase(sourceNodeUri))) {
		    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(div);
		    childrenInfo.addSourceChild(child);
		    creationData.addChildrenInfo(childrenInfo);
		    jsfComponentFound = true;
		    break;
		}
	    }
	}
	
	if (!jsfComponentFound) {
	    div.setAttribute(HTML.ATTR_STYLE, "display: none; "); //$NON-NLS-1$
	    creationData.addChildrenInfo(new VpeChildrenInfo(div));
	}

	return creationData;
    }
}
