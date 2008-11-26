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
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataOrderedListTemplate  extends VpeAbstractTemplate {
	/** CSS_FILE_NAME */
	final static private String CSS_FILE_NAME = "dataOrderedList/dataOrderedList.css";

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement orderedList = visualDocument.createElement("ol");
		
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, "richFacesDataOrderList");
		VisualDomUtil.copyAttributes(sourceNode, orderedList);
		
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				HtmlComponentUtil.HTML_STYLECLASS_ATTR,
				HtmlComponentUtil.HTML_CLASS_ATTR, 
				"dr-list rich-orderedlist", 
				"dr-list rich-orderedlist");
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				HtmlComponentUtil.HTML_STYLE_ATTR,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);

		VpeCreationData creatorInfo = new VpeCreationData(orderedList);

		int rows = 3;
		try {
			rows = Integer.parseInt(sourceElement.getAttribute(HtmlComponentUtil.HTML_ROW_ATTR));
		} catch (NumberFormatException x) {
			rows = 3;
		}

		for (int i = 0; i < rows; i++) {
			nsIDOMElement listItem = visualDocument.createElement("li");
			listItem.setAttribute("class", "dr-list-item rich-list-item");
			orderedList.appendChild(listItem);
			
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
