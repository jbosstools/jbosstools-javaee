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
package org.jboss.tools.jsf.vpe.jsf.template;

import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.jsf.vpe.jsf.VpeElementProxyData;
import org.jboss.tools.jst.web.ui.internal.editor.util.NodesManagingUtil;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.util.VpeNodesManagingUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeProxyUtil {

	static public NodeList reparseAttributeValue(VpeElementProxyData data,
			String attrString, int offset) {
		IStructuredDocument newStructuredDocument = StructuredDocumentFactory
				.getNewStructuredDocumentInstance(new JSPSourceParser());

		newStructuredDocument.set(attrString);

		IDOMModel modelForJSP = (IDOMModel) StructuredModelManager
				.getModelManager().createUnManagedStructuredModelFor(
						ContentTypeIdForJSP.ContentTypeID_JSP);

		modelForJSP.setStructuredDocument(newStructuredDocument);

		// data.setModel(modelForJSP);
		// data.setOffset(offset);

		IDOMDocument document = modelForJSP.getDocument();

		NodeList list = document.getChildNodes();

		NodeList adaptersList = (NodeList) JsfTemplateInvocationHandler
				.createNodeListProxy(list, offset);

		data.setNodelist(adaptersList);

		return (NodeList) adaptersList;

	}

	static public NodeList reparseAttributeValue(VpeElementProxyData data,
			Attr attr) {

		return reparseAttributeValue(data, attr.getValue(), ((IDOMAttr) attr)
				.getValueRegionStartOffset() + 1);

	}

	/**
	 * 
	 * @param nodeList
	 * @param focusPosition
	 * @param anchorPosition
	 * @return
	 */
	static public VpeNodeMapping findNodeByPosition(VpeDomMapping domMapping,
			NodeList nodeList, Point selectionRange) {
		int beginPosition = Math.min(selectionRange.x, selectionRange.x + selectionRange.y);
		int endPosition = Math.max(selectionRange.x, selectionRange.x + selectionRange.y);

		for (int i = 0; i < nodeList.getLength(); i++) {

			Node child = nodeList.item(i);

			VpeNodeMapping result = null;
			if (child.hasChildNodes()) {
				result = findNodeByPosition(domMapping, child.getChildNodes(),
						selectionRange);
			}

			if (result != null)
				return result;

			if ((beginPosition >= (NodesManagingUtil.getStartOffsetNode(child)))
					&& (endPosition <= (NodesManagingUtil
							.getEndOffsetNode(child)))) {

				return VpeNodesManagingUtil.getNodeMapping(domMapping, child);
			}
		}

		return null;

	}

}
