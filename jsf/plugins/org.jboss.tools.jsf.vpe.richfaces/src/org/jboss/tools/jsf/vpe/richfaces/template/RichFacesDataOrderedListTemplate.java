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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jboss.tools.vpe.editor.util.HTML;

public class RichFacesDataOrderedListTemplate  extends VpeAbstractTemplate {

	private static final String ORDERED_LIST_CLASSES = "dr-list rich-orderedlist"; //$NON-NLS-1$
	private static final String LIST_ITEM_CLASSES = "dr-list-item rich-list-item"; //$NON-NLS-1$
	private static final String CSS_FILE_NAME = "dataOrderedList/dataOrderedList.css"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement orderedList = visualDocument.createElement(HTML.TAG_OL);
		
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, "richFacesDataOrderList"); //$NON-NLS-1$
		VisualDomUtil.copyAttributes(sourceNode, orderedList);
		
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				RichFaces.ATTR_STYLE_CLASS,
				HTML.ATTR_CLASS, 
				ORDERED_LIST_CLASSES, 
				ORDERED_LIST_CLASSES);
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				RichFaces.ATTR_STYLE,
				HTML.ATTR_STYLE , null, null);

		VpeCreationData creatorInfo = new VpeCreationData(orderedList);
		creatorInfo.addChildrenInfo(new VpeChildrenInfo(null));

		int rows = 1;
		try {
			rows = Integer.parseInt(sourceElement.getAttribute(RichFaces.ATTR_ROWS));
		} catch (NumberFormatException x) {
			// this is OK, rows still equals 1
		}
		
		String rowClassesString = sourceElement.getAttribute(RichFaces.ATTR_ROW_CLASSES);
		String[] rowClasses = null;
		if (ComponentUtil.isNotBlank(rowClassesString)) {
		    rowClasses = rowClassesString.split(Constants.COMMA);
		}
		
		/*
		 * If rows==0 display at least one row.
		 */
		int i = 0;
		do {
			nsIDOMElement listItem = visualDocument.createElement(HTML.TAG_LI);
			String rowClass = LIST_ITEM_CLASSES;
			if ((null != rowClasses) && (rowClasses.length > 0)) {
			    rowClass += Constants.WHITE_SPACE + rowClasses[i % rowClasses.length];
			}
			
			listItem.setAttribute(HTML.ATTR_CLASS, rowClass);
			orderedList.appendChild(listItem);
			
			VpeChildrenInfo info = new VpeChildrenInfo(listItem);
			encodeListItem(info, sourceElement);
			creatorInfo.addChildrenInfo(info);
			i++;
		} while (i < rows);
		
		return creatorInfo;
	}

	
	void encodeListItem(VpeChildrenInfo info, Element sourceElement) {
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
