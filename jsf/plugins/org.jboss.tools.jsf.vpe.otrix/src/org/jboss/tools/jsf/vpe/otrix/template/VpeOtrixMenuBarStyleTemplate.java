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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;

public class VpeOtrixMenuBarStyleTemplate extends VpeAbstractTemplate {

	static private HashMap attributes = new HashMap();
	static{
		attributes.put("backgroundColor", new AttributesData("background-color"));
		attributes.put("borderColor",     new AttributesData("border-color"));
		attributes.put("borderStyle",     new AttributesData("border-style"));
		attributes.put("borderWidth",     new AttributesData("border-width"));
		attributes.put("color",           new AttributesData("color"));
		attributes.put("fontName",        new AttributesData("font-family"));
		attributes.put("fontSize",        new AttributesData("font-size"));
		attributes.put("height",          new AttributesData("height"));
		attributes.put("left",            new AttributesData("left"));
		attributes.put("top",             new AttributesData("top"));
		attributes.put("width",           new AttributesData("width"));

		attributes.put("fontBold",
				new AttributesData("", "true", "font-weight", "bold"));
		attributes.put("fontItalic",
				new AttributesData("", "true", "font-style", "italic"));
		attributes.put("fontOverline",
				new AttributesData("", "true", "text-decoration", "overline"));
		attributes.put("fontStrikeout",
				new AttributesData("", "true", "text-decoration", "line-through"));
		attributes.put("fontUnderline",
				new AttributesData("", "true", "text-decoration", "underline"));
		attributes.put("textWrap",
				new AttributesData("", "true", "white-space", "nowrap"));
	}
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {

		VpeDomMapping mapping = pageContext.getDomMapping();
		Node parent = sourceNode.getParentNode();
		Node visualTable = mapping.getVisualNode(parent);
		Node styleAttr = visualTable.getAttributes().getNamedItem("style");
		String styleValue = styleAttr.getNodeValue();
		styleValue = getStyle(sourceNode, styleValue);
		if (styleValue.trim().length() > 0) {
			styleAttr.setNodeValue(styleValue);
		}
		
		return new VpeCreationData(null);
	}

	private String getStyle(Node sourceNode, String styleValue) {
		String style = styleValue;
		NamedNodeMap attrs = sourceNode.getAttributes();
		int len = attrs != null ? attrs.getLength() : 0;
		for (int i = 0; i < len; i++) {
			Node attr = attrs.item(i);
			String name = attr.getNodeName();
			String value = attr.getNodeValue();
			AttributesData.AttributesDataItem dataItem = getAttributesDataItem(name, value);
			if (dataItem != null) {
				style += (style.trim().length() > 0 && !style.trim().endsWith(";")) ? ";" : "";
				if (dataItem.value != null) {
					style += dataItem.name + ":" + dataItem.value + ";";
				} else {
					style += dataItem.name + ":" + value + ";";
				}
			}
		}
		return style;
	}

	private static AttributesData.AttributesDataItem getAttributesDataItem(String name, String value) {
		AttributesData.AttributesDataItem item = null;
		Object dataObj = attributes.get(name);
		if (dataObj != null) {
			AttributesData data = (AttributesData)dataObj;
			item = data.getItem(value);
		}
		return item;
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

	private static class AttributesData {
		private List items = new ArrayList();

		private AttributesData(String name) {
			addItem(name);
		}
		private AttributesData(String name, String value) {
			addItem(name, value);
		}
		private AttributesData(String srcValue, String name, String value) {
			addItem(srcValue, name, value);
		}
		private AttributesData(String srcValue1, String srcValue2, String name, String value) {
			addItem(srcValue1, name, value);
			addItem(srcValue2, name, value);
		}

		private void addItem(String name) {
			items.add(new AttributesDataItem(name));
		}
		private void addItem(String name, String value) {
			items.add(new AttributesDataItem(name, value));
		}
		private void addItem(String srcValue, String name, String value) {
			items.add(new AttributesDataItem(srcValue, name, value));
		}
		
		private AttributesDataItem getItem(String srcValue) {
			for (int i = 0; i < items.size(); i++) {
				AttributesDataItem item = (AttributesDataItem)items.get(i);
				if (item.srcValue != null) {
					if (item.srcValue.equals(srcValue)) {
						return item;
					}
				} else {
					return item;
				}
			}
			return null;
		}

		private class AttributesDataItem {
			private String srcValue;
			private String name;
			private String value;

			private AttributesDataItem(String name) {
				this.name = name;
			}
			private AttributesDataItem(String name, String value) {
				this.name = name;
				this.value = value;
			}
			private AttributesDataItem(String srcValue, String name, String value) {
				this.srcValue = srcValue;
				this.name = name;
				this.value = value;
			}
		}
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
}
