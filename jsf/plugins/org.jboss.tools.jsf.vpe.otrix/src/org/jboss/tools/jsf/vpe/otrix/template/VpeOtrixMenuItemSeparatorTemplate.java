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
package org.jboss.tools.jsf.vpe.otrix.template;

import java.util.Map;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.vpe.editor.VpeSourceInnerDragInfo;
import org.jboss.tools.vpe.editor.VpeSourceInnerDropInfo;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.template.textformating.TextFormatingData;

public class VpeOtrixMenuItemSeparatorTemplate extends VpeAbstractTemplate {

	public static final String[][] MAP_ATTR_TO_MENU = {
		{"style","style",""}, 
		{"class","styleClass",""}
	};
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		VpeDomMapping mapping = pageContext.getDomMapping();
		Node menuNode = sourceNode.getParentNode();
		Node visualTable = mapping.getVisualNode(menuNode);
		if (VpeOtrixMenuTemplate.isHorizontalMenu(menuNode)) {
			Element td = visualDocument.createElement("td");
			Element p = visualDocument.createElement("p");
			p.setAttribute("class", "owmhseparator");
			p.appendChild(visualDocument.createElement("br"));
			td.appendChild(p);
			return new VpeCreationData(td);
		} else {
			Element tr = visualDocument.createElement("tr");
			Element td = visualDocument.createElement("td");
			td.setAttribute("class", "owmxpc owmc");
			tr.appendChild(td);
			td = visualDocument.createElement("td");
			td.setAttribute("class", "owmxpc owmi");
			tr.appendChild(td);
			td = visualDocument.createElement("td");
			td.setAttribute("colspan", "2");
			Element div = visualDocument.createElement("div");
			div.setAttribute("class", "owmseparator");
			Element img = visualDocument.createElement("img");
			img.setAttribute("src", "/WebMenu/resources/otrix/ospacer.gif");
			img.setAttribute("alt", "");
			img.setAttribute("border", "0");
			div.appendChild(img);
			td.appendChild(div);
			tr.appendChild(td);
			visualTable.appendChild(tr);
			return new VpeCreationData(tr);
		}
	}

	public void validate(VpePageContext pageContext, Node sourceNode,
			Document visualDocument, VpeCreationData data) {
		// TODO Auto-generated method stub
	}

	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data,
			String name, String value) {
		// TODO Auto-generated method stub

	}

	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument,
			Node visualNode, Object data, String name) {
		// TODO Auto-generated method stub

	}

	public boolean canInnerDrop(VpePageContext pageContext, Node container,
			Node sourceDragNode) {
		// TODO Auto-generated method stub
		return false;
	}

	public void innerDrop(VpePageContext pageContext,
			VpeSourceInnerDragInfo dragInfo, VpeSourceInnerDropInfo dropInfo) {
		// TODO Auto-generated method stub

	}

	public boolean isChildren() {
		// TODO Auto-generated method stub
		return true;
	}

	public TextFormatingData getTextFormatingData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getOutputAtributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getOutputTextNode(VpePageContext pageContext,
			Element sourceElement, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOutputAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public VpeAnyData getAnyData() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
}
