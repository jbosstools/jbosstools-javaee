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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jboss.tools.vpe.editor.VpeSourceInnerDragInfo;
import org.jboss.tools.vpe.editor.VpeSourceInnerDropInfo;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeAnyData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpePanelLayoutCreator.PanelLayoutTable;
import org.jboss.tools.vpe.editor.template.textformating.TextFormatingData;

public class VpeOtrixTreeTemplate extends VpeAbstractTemplate {

	public static final String[][] MAP_ATTR_TO_MENU = {
		{"style","style",""}, 
		{"class","styleClass",""}
	};
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		
		TreeElements menuElements = new TreeElements(sourceNode, pageContext);
		Node[] menuItems = menuElements.getNodes();
		TreeTable tree = new TreeTable(visualDocument,sourceNode, MAP_ATTR_TO_MENU, menuItems.length);
		for(int i=0;i<menuItems.length;i++) {
			tree.addMenuItem(menuItems[i],new String[][]{}, i);
		}
		return tree.getVpeCreationData();
	}

	public void validate(VpePageContext pageContext, Node sourceNode,
			Document visualDocument, VpeCreationData data) {
	}

	public void setAttribute(VpePageContext pageContext, Element sourceElement,
			Document visualDocument, Node visualNode, Object data,
			String name, String value) {
	}

	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument,
			Node visualNode, Object data, String name) {
	}

	public boolean canInnerDrop(VpePageContext pageContext, Node container,
			Node sourceDragNode) {
		return false;
	}

	public void innerDrop(VpePageContext pageContext,
			VpeSourceInnerDragInfo dragInfo, VpeSourceInnerDropInfo dropInfo) {
	}

	public boolean isChildren() {
		return true;
	}

	public TextFormatingData getTextFormatingData() {
		return null;
	}

	public String[] getOutputAtributeNames() {
		return null;
	}

	public Node getOutputTextNode(VpePageContext pageContext,
			Element sourceElement, Object data) {
		return null;
	}

	public boolean isOutputAttributes() {
		return false;
	}

	public int getType() {
		return 0;
	}

	public VpeAnyData getAnyData() {
		return null;
	}

	public class TreeTable {
		Table table;
		Tr[] rows;
		VpeCreationData creatorInfo;
		
		public TreeTable(Document visualDocument, Node source,String[][] styleMap, int itemCount) {
			table = new Table(visualDocument, source);
			creatorInfo = new VpeCreationData(table.getDomElement());
			rows = new Tr[itemCount];
			for (int i = 0; i < rows.length; i++) {
				rows[i] = table.createRow();
			}
		}

		public void update(Node node, String[][] styleMap) {
			mapAttributes(table.getDomElement(),node,styleMap);			
		}
		
		public VpeCreationData getVpeCreationData() {
			return creatorInfo;
		}
		
		public void addMenuItem(Node sourceMenuItem, String[][] styleMap, int index) {
			Td td = rows[index].createCell();
			VpeChildrenInfo info = new VpeChildrenInfo(td.getDomElement()); 
			info.addSourceChild(sourceMenuItem);
			VpeOtrixMenuTemplate.mapAttributes(td.getDomElement(),sourceMenuItem,styleMap);
			creatorInfo.addChildrenInfo(info);
		}
	}

	public static int DEST=0, SOURCE=1, DEFAULT =2;	
	
	static String getAttributeValue(Node node, String attributeName, String defaultValue) {
		NamedNodeMap attrs = node.getAttributes();
		Node attr = attrs.getNamedItem(attributeName);
		if(attr!=null)  {
			return attr.getNodeValue();
		}
		return defaultValue;
	}
	
	static void mapAttributes(Element dest, Node source, String[][] map) {
		for (int i = 0;i<map.length;i++) {
			dest.setAttribute(map[i][DEST],getAttributeValue(source,map[i][SOURCE],map[i][DEFAULT]));
		}
	}
	
	public interface ElementWrapper {
		public Element getDomElement();
		public Document getOwnerDocument();	
		public void setAttributeValue(String name, String value);
	}
	
	public class DefaultNodeWrapper implements ElementWrapper {
		
		protected Element element;
		
		public DefaultNodeWrapper(Element element) {
			this.element = element;
		}

		public Element getDomElement() {
			return element;
		}
		
		public Document getOwnerDocument() {
			return ((Document)element.getOwnerDocument());
		}

		public void setAttributeValue(String name, String value) {
			getDomElement().setAttribute(name,value);
		}
		
	}
	
	public class Table extends DefaultNodeWrapper {
		private Element table;
		
		public Table(Document visualDocument,Node source) {
			super(visualDocument.createElement("div"));
			Node style = source.getAttributes().getNamedItem("style");
			if (style != null && style.getNodeValue().length() > 0) {
				getDomElement().setAttribute("style", style.getNodeValue());
			}
			Node styleClass = source.getAttributes().getNamedItem("styleClass");
			if (styleClass != null && styleClass.getNodeValue().length() > 0) {
				getDomElement().setAttribute("class", styleClass.getNodeValue());
			}
			table = visualDocument.createElement(PanelLayoutTable.TABLE);
			getDomElement().appendChild(table);
		}
		
		public Tr createRow() {
			Element tr = getOwnerDocument().createElement(PanelLayoutTable.TR);
			table.appendChild(tr);
			return new Tr(tr);
		}
	}
	
	public class Tr extends DefaultNodeWrapper {
		public Tr(Element rowNode) {
			super(rowNode);
		}
		
		public Td createCell() {
			Element tr = getOwnerDocument().createElement(PanelLayoutTable.TD);
			getDomElement().appendChild(tr);
			return new Td(tr);
		}
	}
	
	public class Td extends DefaultNodeWrapper {
		Element cell = null;
		
		public Td(Element cellNode) {
			super(cellNode);
			cell =  cellNode;
		}
	}

	public static class TreeElements {
		
		Node[] items = new Node[]{};
		VpePageContext pageContext = null;
		Node tree = null;
		public TreeElements(Node source,VpePageContext context) {
			pageContext = context;
			tree = source;
			init(tree);
		}
		
		public Node getTreeNode() {
			return tree;
		}
		
		public Node[] getNodes() {
			return items;
		}

		private void init(Node source) {
			NodeList list = source.getChildNodes();
			ArrayList<Node> items =  new ArrayList<Node>();
			int cnt = list != null ? list.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node node = list.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						boolean isItem = node.getNodeName().indexOf(":treeNode") > 0;
						if(isItem) {
							items.add(node);
						}
					}
				}
				this.items = (Node[])items.toArray(new Node[items.size()]);
			}
		}
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
}
