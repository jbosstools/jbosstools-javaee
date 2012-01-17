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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeClassUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class RichFacesDataListTemplate extends VpeAbstractTemplate {
	/** CSS_FILE_NAME */
	final static private String CSS_FILE_NAME = "dataList/dataList.css";//$NON-NLS-1$
	final static private int NUMBER_OF_ROWS_TO_DISPLAY  = 1;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement unorderedList = visualDocument.createElement(HTML.TAG_UL);

		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, "richFacesDataList");//$NON-NLS-1$
		VisualDomUtil.copyAttributes(sourceNode, unorderedList);

		ComponentUtil.correctAttribute(sourceElement, unorderedList,
				RichFaces.ATTR_STYLE_CLASS,
				HTML.ATTR_CLASS, 
				"dr-list rich-datalist", //$NON-NLS-1$
				"dr-list rich-datalist");//$NON-NLS-1$
		ComponentUtil.correctAttribute(sourceElement, unorderedList,
				RichFaces.ATTR_STYLE,
				HTML.ATTR_STYLE, null, null);

		VpeCreationData creatorInfo = new VpeCreationData(unorderedList);
		creatorInfo.addChildrenInfo(new VpeChildrenInfo(null));

		
		final List<String> rowClasses;
		try {
			final VpeExpression exprRowClasses = RichFaces.getExprRowClasses();		
			rowClasses = VpeClassUtil.getClasses(exprRowClasses, sourceNode,
					pageContext);
		} catch (VpeExpressionException e) {
			throw new RuntimeException(e);
		}
		final int rowClassesSize = rowClasses.size();
		
		int rows = NUMBER_OF_ROWS_TO_DISPLAY;
		
		for (int i = 0; i < rows; i++) {
			nsIDOMElement listItem = visualDocument.createElement(HTML.TAG_LI);

			String rowClass = "dr-list-item rich-list-item"; //$NON-NLS-1$
			if (rowClassesSize > 0) {
				rowClass+= " " + rowClasses.get(i % rowClassesSize); //$NON-NLS-1$
			}
			
			listItem.setAttribute(HTML.ATTR_CLASS, rowClass);
			unorderedList.appendChild(listItem);
			
			VpeChildrenInfo info = new VpeChildrenInfo(listItem);
			encodeListItem(info, sourceElement);
			creatorInfo.addChildrenInfo(info);
		}
		
		return creatorInfo;
	}

	
	private void encodeListItem(VpeChildrenInfo info, Element sourceElement) {
		NodeList children = sourceElement.getChildNodes();

		int cnt = children != null ? children.getLength() : 0;
		if (cnt > 0) {
			for (int i = 0; i < cnt; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element)child;
					info.addSourceChild(childElement);
				} 
			}
		}
	}
	
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}
