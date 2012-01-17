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
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.XmlUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
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
 		Element sourceElement = (Element) sourceNode;
 		Node facetParent = null;
 		nsIDOMElement facetVisualTag = null;
 
 		NodeList children = sourceNode.getChildNodes();
 		boolean jsfComponentFound = false;
 		
 		/*
 		 * https://jira.jboss.org/jira/browse/JBIDE-3373
 		 * By rendering facet to existed visual node
 		 * we avoid unwarranted tag creation for facet element. 
 		 */
 		facetParent = sourceNode.getParentNode();
 		nsIDOMNode facetParentVisualTag = pageContext.getDomMapping().getVisualNode(facetParent);
		facetVisualTag = VisualDomUtil.findVisualTagWithFacetAttribute(
				facetParentVisualTag, sourceElement.getAttribute(JSF.ATTR_NAME));
 		/*
 		 * When no tag found use 'SPAN' tag by default.
 		 * So facet will be rendered in any case.
 		 */
 		if (null == facetVisualTag) {
 			facetVisualTag = visualDocument.createElement(HTML.TAG_SPAN);
		}
 
 		VpeCreationData creationData = new VpeCreationData(facetVisualTag);
 		/*
 		 * Only one JSF component may be present inside a facet tag, if more are
 		 * present only the first one is rendered and the other ones are
 		 * ignored.
 		 */
 		for (int i = children.getLength() - 1; i >= 0 ; i--) {
 			Node child = children.item(i);
 			String sourcePrefix = child.getPrefix();
 			List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(sourceNode,
 					pageContext);
 			TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(
 					sourcePrefix, taglibs);
 			if (null != sourceNodeTaglib) {
 				String sourceNodeUri = sourceNodeTaglib.getUri();
 				if (VisualDomUtil.JSF_CORE_URI.equalsIgnoreCase(sourceNodeUri)
 								|| VisualDomUtil.JSF_HTML_URI.equalsIgnoreCase(sourceNodeUri)
 								|| VisualDomUtil.RICH_FACES_URI.equalsIgnoreCase(sourceNodeUri) 
 								|| VisualDomUtil.A4J_URI.equalsIgnoreCase(sourceNodeUri)
 								|| VisualDomUtil.FACELETS_URI.equalsIgnoreCase(sourceNodeUri)) {
 					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(facetVisualTag);
 					childrenInfo.addSourceChild(child);
 					creationData.addChildrenInfo(childrenInfo);
 					jsfComponentFound = true;
 					break;
 				}
 			}
 		}
 		if (!jsfComponentFound) {
 			facetVisualTag.setAttribute(HTML.ATTR_STYLE, "display: none; "); //$NON-NLS-1$
 			creationData.addChildrenInfo(new VpeChildrenInfo(facetVisualTag));
 		}
		return creationData;
    }
}
