/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util;

import org.eclipse.jst.jsp.core.internal.domdocument.DOMModelForJSP;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.ElementProxy;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.NodeListImpl;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.NodeProxy;
import org.jboss.tools.jsf.vpe.jsf.template.util.model.TextProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeProxyUtil {

	static public NodeList reparseAttributeValue(String attrString, int offset) {
		IStructuredDocument newStructuredDocument = StructuredDocumentFactory
				.getNewStructuredDocumentInstance(new JSPSourceParser());

		newStructuredDocument.set(attrString);

		IDOMModel modelForJSP = new DOMModelForJSP();
		modelForJSP.setStructuredDocument(newStructuredDocument);

		IDOMDocument document = modelForJSP.getDocument();

		NodeList list = document.getChildNodes();

		NodeList adaptersList = getNodeAdapterList(list, offset);

		return adaptersList;

	}

	static public NodeList reparseAttributeValue(Attr attr) {

		return reparseAttributeValue(attr.getValue(), ((IDOMAttr) attr)
				.getValueRegionStartOffset());

	}

	static public NodeProxy getNodeAdapter(Node node, int basicOffset) {
		if (node == null)
			return null;

		if (node instanceof IDOMText)
			return new TextProxy((IDOMText) node, basicOffset);
		else if (node instanceof IDOMElement)
			return new ElementProxy((IDOMElement) node, basicOffset);
		else
			return new NodeProxy(node, basicOffset);
	}

	static public NodeList getNodeAdapterList(NodeList nodeList, int basicOffset) {

		NodeListImpl newNodeList = new NodeListImpl();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			// if node is only html tag
			if (node.getPrefix() == null)
				newNodeList.appendNode(getNodeAdapter(node, basicOffset));

		}

		return newNodeList;

	}

}
