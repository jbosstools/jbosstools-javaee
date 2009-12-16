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
package org.jboss.tools.jsf.vpe.seam.template;

import java.io.StringReader;

import org.jboss.seam.text.xpl.SeamTextLexer;
import org.jboss.seam.text.xpl.SeamTextParser;
import org.jboss.tools.jsf.vpe.seam.SeamTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMParser;
import org.mozilla.xpcom.Mozilla;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * @author Vitali (vyemialyanchyk@exadel.com)
 * 
 * s:formattedText template
 */
public class SeamFormattedTextTemplate extends VpeAbstractTemplate {

	private static final String CID_DOMPARSER = "@mozilla.org/xmlextras/domparser;1"; //$NON-NLS-1$

	/**
	 * component manager
	 */
	private nsIComponentManager componentManager;
	
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
	    /*
	     * Fixes https://jira.jboss.org/jira/browse/JBIDE-4104
	     * When there is no value attribute string reader
	     * will be created with empty value.
	     */
	    String valueT = ""; //$NON-NLS-1$
	    if (sourceElement.hasAttribute(HTML.ATTR_VALUE)) {
		valueT = sourceElement.getAttribute(HTML.ATTR_VALUE);
	    }
	    
	    StringReader r = new StringReader(valueT);
	    SeamTextLexer lexer = new SeamTextLexer(r);
	    SeamTextParser parser = new SeamTextParser(lexer);
	    try {
		parser.startRule();
	    } catch (RecognitionException e) {
		SeamTemplatesActivator.getPluginLog().logError(e);
	    } catch (TokenStreamException e) {
		SeamTemplatesActivator.getPluginLog().logError(e);
	    }

	    nsIDOMParser parserDom = (nsIDOMParser) getComponentManager()
	    .createInstanceByContractID(CID_DOMPARSER, null,
		    nsIDOMParser.NS_IDOMPARSER_IID);

	    String strDoc = "<HTML><BODY>" + parser.toString() + "</BODY></HTML>"; //$NON-NLS-1$ //$NON-NLS-2$
	    nsIDOMDocument domDoc = parserDom.parseFromString(strDoc,
	    "application/xhtml+xml"); //$NON-NLS-1$
	    nsIDOMNode patronItem = null, nodeTmp = null;
	    nsIDOMNodeList list = null;
	    if (null != domDoc.getDocumentElement()) {
		list = domDoc.getDocumentElement().getChildNodes();
		long i = 0;
		for (; i < list.getLength(); i++) {
		    nodeTmp = list.item(i);
		    if (HTML.TAG_BODY.equalsIgnoreCase(nodeTmp.getNodeName())) {
			patronItem = nodeTmp.cloneNode(true);
			break;
		    }
		}
	    }
	    if (null != patronItem) {
		list = patronItem.getChildNodes();
		// mainItem = visualDocument.createElement("DIV");
		patronItem = VisualDomUtil.createBorderlessContainer(visualDocument);
		createCopyChildren(visualDocument, patronItem, list);
	    }
	    VpeCreationData creationData = new VpeCreationData(patronItem);
	    return creationData;
	}

	public void createCopyChildren(nsIDOMDocument visualDocument,
			nsIDOMNode nodeParent, nsIDOMNodeList listCopyChildren) {
		long i = 0;
		nsIDOMNode nodeTmp = null, nodeTmp2 = null;
		for (; i < listCopyChildren.getLength(); i++) {
			nodeTmp = listCopyChildren.item(i);
			// remark: cloneNode true/false - is not suitable function here
			//nodeTmp2 = nodeTmp.cloneNode(false);
			if (nodeTmp.getNodeName().startsWith("#text")) { //$NON-NLS-1$
				nodeTmp2 = visualDocument.createTextNode(nodeTmp.getNodeValue());
			}
			else {
				if (!nodeTmp.getNodeName().startsWith("#")) { //$NON-NLS-1$
					nodeTmp2 = visualDocument.createElement(nodeTmp.getNodeName());
				}
			}
			if (null == nodeTmp2) {
				continue;
			}
			nodeParent.appendChild(nodeTmp2);
			createCopyChildren(visualDocument, nodeTmp2, nodeTmp.getChildNodes());
			nodeTmp2 = null;
		}
	}
	
	public nsIComponentManager getComponentManager() {
		if (null == componentManager) {
			componentManager = Mozilla.getInstance().getComponentManager();
		}
		return componentManager;
	}
}
// html code
