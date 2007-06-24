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
import org.w3c.dom.Text;

import org.jboss.tools.vpe.editor.VpeSourceInnerDragInfo;
import org.jboss.tools.vpe.editor.VpeSourceInnerDropInfo;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.template.textformating.TextFormatingData;

public class VpeOtrixMenuItemTemplate extends VpeAbstractTemplate {

	public static final String[][] MAP_ATTR_TO_MENU = {
		{"style","style",""}, 
		{"class","styleClass",""}
	};
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		VpeDomMapping mapping = pageContext.getDomMapping();
		Node menuNode = sourceNode.getParentNode();
		Node visualTable = mapping.getVisualNode(menuNode);
		boolean isHorizontal = VpeOtrixMenuTemplate.isHorizontalMenu(menuNode);
		if (isHorizontal) {
			Element td = visualDocument.createElement("td");
			td.setAttribute("class", getClass(isHorizontal, sourceNode));
			td.setAttribute("style", getStyle(sourceNode));
			Element a = visualDocument.createElement("a");
			a.setAttribute("href", "#");
			a.setAttribute("class", "owmhl");
			Element img = visualDocument.createElement("img");
			img.setAttribute("src", "/WebMenu/resources/otrix/ospacer.gif");
			img.setAttribute("alt", "");
			img.setAttribute("border", "0"); 
			img.setAttribute("class", "owmhi");
			a.appendChild(img);
			Text text = visualDocument.createTextNode(sourceNode.getAttributes().getNamedItem("value").getNodeValue());
			a.appendChild(text);
			td.appendChild(a);
			return new VpeCreationData(td);
		} else {
			Element tr = visualDocument.createElement("tr");
			String nbsp = new String(new char[] {160});
			makeCell(tr, "otrixMenuItem_WinXp owmxpc owmc", nbsp, visualDocument);
			makeCell(tr, "otrixMenuItem_WinXp owmxpc owmi", nbsp, visualDocument);
			Element td = makeCell(tr, getClass(isHorizontal, sourceNode), sourceNode.getAttributes().getNamedItem("value").getNodeValue(), visualDocument);
			td.setAttribute("style", getStyle(sourceNode));
			makeCell(tr, "otrixMenuItem_WinXp owmm", nbsp, visualDocument);
			visualTable.appendChild(tr);
			return new VpeCreationData(tr);
		}
	}
	
	private String getStyle(Node sourceNode) {
		String style = "";
		Node styleAttr = sourceNode.getAttributes().getNamedItem("style");
		if (styleAttr != null) {
			String styleAttrValue = styleAttr.getNodeValue();
			if (styleAttrValue.trim().length() > 0) {
				style = styleAttrValue;
			}
		}
		return style;
	}

	private String getClass(boolean isHorizontal, Node sourceNode) {
		String classStr = "otrixMenuBarItem_WinXp";
		if (isHorizontal) {
			classStr = "otrixMenuBarItem_WinXp";
		} else {
			classStr = "otrixMenuItem_WinXp owml";
		}
		Node classAttr = sourceNode.getAttributes().getNamedItem("styleClass");
		if (classAttr != null) {
			String classAttrValue = classAttr.getNodeValue();
			if (classAttrValue.trim().length() > 0) {
				classStr = classAttrValue;
			}
		}
		return classStr;
	}
	
	private Element makeCell(Element tr, String tdClass, String tdText, Document visualDocument) {
		Element td = visualDocument.createElement("td");
		td.setAttribute("class", tdClass);
		Text text = visualDocument.createTextNode(tdText);
		td.appendChild(text);
		tr.appendChild(td);
		return td;
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
