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
import org.jboss.tools.vpe.editor.util.MozillaSupports;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataOrderedListTemplate  extends VpeAbstractTemplate {

	static final String STYLECLASS_ATTR_NAME = "styleClass";
	static final String STYLE_ATTR_NAME = "style";
	static final String ROWS_ATTR_NAME = "rows";

	
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		Element sourceElement = (Element)sourceNode;
		Element orderedList = visualDocument.createElement("ol");
		
		ComponentUtil.setCSSLink(pageContext, "dataOrderedList/dataOrderedList.css", "richFacesDataOrderList");
		ComponentUtil.copyAttributes(sourceNode, orderedList);
		
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				STYLECLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, "dr-list rich-orderedlist", "dr-list rich-orderedlist");
		ComponentUtil.correctAttribute(sourceElement, orderedList,
				STYLE_ATTR_NAME,
				HtmlComponentUtil.HTML_STYLE_ATTR, null, null);

		VpeCreationData creatorInfo = new VpeCreationData(orderedList);

		int rows = -1;
		try {
			rows = Integer.valueOf(sourceElement.getAttribute(ROWS_ATTR_NAME));
		} catch (Exception x) {
			rows = -1;
		}

		for (int i = 0; i < (rows == -1 ? 3 : rows); i++) {
			Element listItem = visualDocument.createElement("li");
			listItem.setAttribute("class", "dr-list-item rich-list-item");
			orderedList.appendChild(listItem);
			
			VpeChildrenInfo info = new VpeChildrenInfo(listItem);
			creatorInfo.addChildrenInfo(info);
			encodeListItem(info, sourceElement);
			MozillaSupports.release(listItem);
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
