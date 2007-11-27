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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
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
	final static private String CSS_FILE_NAME = "dataList/dataList.css";


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement unorderedList = visualDocument.createElement("ul");

		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, "richFacesDataList");
		ComponentUtil.copyAttributes(sourceNode, unorderedList);

		ComponentUtil.correctAttribute(sourceElement, unorderedList,
				HtmlComponentUtil.HTML_STYLECLASS_ATTR,
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				"dr-list rich-datalist",
				"dr-list rich-datalist");
		ComponentUtil.correctAttribute(sourceElement, unorderedList,
				HtmlComponentUtil.HTML_STYLE_ATTR,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);

		VpeCreationData creatorInfo = new VpeCreationData(unorderedList);

		int rows = -1;
		try {
			rows = Integer.valueOf(sourceElement.getAttribute(HtmlComponentUtil.HTML_ROW_ATTR));
		} catch (Exception x) {
			rows = -1;
		}
		
		for (int i = 0; i < (rows == -1 ? 3 : rows); i++) {
			nsIDOMElement listItem = visualDocument.createElement("li");
			listItem.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-list-item rich-list-item");
			unorderedList.appendChild(listItem);
			
			VpeChildrenInfo info = new VpeChildrenInfo(listItem);
			creatorInfo.addChildrenInfo(info);
			encodeListItem(info, sourceElement);
		}
		
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
				} else if (child.getNodeType() == Node.TEXT_NODE) {
					String text = child.getNodeValue();
					text = (text == null ? null : text.trim());
					if (text != null && text.length() > 0) {
						info.addSourceChild(child);
					}
				}
			}
		}
	}
}
