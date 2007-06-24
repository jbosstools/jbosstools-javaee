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

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Comment;
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
import org.jboss.tools.vpe.editor.template.VpeCreatorInfo;
import org.jboss.tools.vpe.editor.template.VpePanelLayoutCreator.PanelLayoutTable;
import org.jboss.tools.vpe.editor.template.textformating.TextFormatingData;
import org.jboss.tools.jsf.vpe.otrix.OtrixPlugin;

public class VpeOtrixMenuTemplate extends VpeAbstractTemplate {

	public static final String[][] MAP_ATTR_TO_MENU = {
		{"style","style",""}, 
		{"class","styleClass",""}
	};
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		
		setCSSLink(pageContext, "resources/menu/otrixshared.css");
		setCSSLink(pageContext, "resources/menu/otrixmenu.css");

		Element visualTable = visualDocument.createElement("table");
		visualTable.setAttribute("class", getVisualClass(sourceNode));
		visualTable.setAttribute("style", getVisualStyle(sourceNode));
		visualTable.setAttribute("cellspacing", "0"); 
		VpeCreationData creationData = new VpeCreationData(visualTable);
		return creationData;
	}

	private void setCSSLink(VpePageContext pageContext, String cssHref) {
		String pluginPath = OtrixPlugin.getInstallPath();
		IPath pluginFile = new Path(pluginPath);
		File cssFile = pluginFile.append(cssHref).toFile();
		if (cssFile.exists()) {
			String cssPath = "file:///" + cssFile.getPath();
			Node newNode = pageContext.getVisualBuilder().replaceLinkNodeToHead(cssPath, "otrix_menu");
		}
	}

	private String getVisualStyle(Node sourceNode) {
		String style = "border-collapse: separate; border-spacing: 0px; empty-cells: show;";
		Node menuBarStyleAttr = sourceNode.getAttributes().getNamedItem("menuBarStyle");
		if (menuBarStyleAttr != null) {
			String menuBarStyleValue = menuBarStyleAttr.getNodeValue();
			style += menuBarStyleValue;
		}
		return style;
	}

	private String getVisualClass(Node sourceNode) {
		String menuBarStyleClass = getAttrValue(sourceNode, "menuBarStyleClass");
		if (menuBarStyleClass != null) return menuBarStyleClass;

		return "otrixMenuBar_WinXp";
	}

	private String getAttrValue(Node sourceNode, String attrName) {
		if (sourceNode != null && attrName != null) {
			NamedNodeMap attrs = sourceNode.getAttributes();
			if (attrs != null) {
				Node attrNode = attrs.getNamedItem(attrName);
				if (attrNode != null) {
					String attrValue = attrNode.getNodeValue();
					if (attrValue.trim().length() > 0) {
						return attrValue;
					}
				}
			}
		}
		return null;
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

	public class MenuTable {
		
		public static final String TABLE = "table";
		public static final String TR = "tr";
		public static final String TD = "td";
		public static final String CLASS_ATTR = "class";		
		public static final String STYLE_ATTR = "style";
		public static final String ORIENTATION_ATTR = "orientation";		
		public static final String ORIENTATION_ATTR_VERTICAL = "Vertical";		
		public static final int ORIENTATION_HORIZONTAL = 0;		
		public static final int ORIENTATION_VERTICAL = 1;		
		
		Table table;
		Tr[] rows;
		VpeCreationData creatorInfo;
		int orientation = 0;
		
		public MenuTable(Document visualDocument, Node source,String[][] styleMap, int itemCount) {
			orientation = getOrientation(source);
			table = new Table(visualDocument, source);
			creatorInfo = new VpeCreationData(table.getDomElement());
			rows = new Tr[orientation == ORIENTATION_HORIZONTAL ? 1 : itemCount];
			for (int i = 0; i < rows.length; i++) {
				rows[i] = table.createRow();
			}
		}
		
		private int getOrientation(Node sourceNode) {
			NamedNodeMap attrs = sourceNode.getAttributes();
			Node orientationAttr = attrs.getNamedItem(ORIENTATION_ATTR);
			if (orientationAttr != null && orientationAttr.getNodeValue().equals(ORIENTATION_ATTR_VERTICAL)) {
				return ORIENTATION_VERTICAL;
			} else {
				return ORIENTATION_HORIZONTAL;
			}
		}

		public void update(Node node, String[][] styleMap) {
			mapAttributes(table.getDomElement(),node,styleMap);			
		}
		
		public VpeCreationData getVpeCreationData() {
			return creatorInfo;
		}
		
		public void addMenuItem(Node sourceMenuItem, String[][] styleMap, int index) {
			Td td = null;
			if (orientation == ORIENTATION_HORIZONTAL) {
				td = rows[0].createCell();
				if (sourceMenuItem.getNodeName().indexOf(":menuItemSeparator") < 0) {
					td.setAttributeValue("style", "padding-right:10px;padding-left:10px;");
				}
			} else {
				td = rows[index].createCell();
				td.setAttributeValue("style", "padding-right:5px;padding-left:20px;");
			}
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
		
		public Table(Document visualDocument,Node source) {
			super(visualDocument.createElement(PanelLayoutTable.TABLE));
			mapStyles(getDomElement(), source);
		}
		
		public Tr createRow() {
			Element tr = getOwnerDocument().createElement(PanelLayoutTable.TR);
			getDomElement().appendChild(tr);
			return new Tr(tr);
		}
		
		private void mapStyles(Element dest, Node source) {
			NamedNodeMap attrs = source.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				if (name.equals("menuBarStyle")) {
					dest.setAttribute("style", value + "border-collapse: separate; border-spacing: 0px; empty-cells: show;");
				} else if (name.equals("menuBarStyleClass") && value.length() > 0) {
					dest.setAttribute("class", value);
				}
			}
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
		
		public Td createCell(int colspanNumber) {
			Td td = createCell();
			td.setAttributeValue("colspan",""+colspanNumber);
			return td;
		}
		
	}
	
	public class Td extends DefaultNodeWrapper {
		Element cell = null;
		
		public Td(Element cellNode) {
			super(cellNode);
			cell =  cellNode;
		}
	}

	public static class BarMenuElements {
		
		Node[] menuItems = new Node[]{};
		VpePageContext pageContext = null;
		Node menu = null;
		public BarMenuElements(Node source,VpePageContext context) {
			pageContext = context;
			menu = source;
			init(menu);
		}
		
		public Node getMenuNode() {
			return menu;
		}
		
		public Node[] getNodes() {
			return menuItems;
		}

		private void init(Node source) {
			NodeList list = source.getChildNodes();
			ArrayList menuItems =  new ArrayList();
			int cnt = list != null ? list.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node node = list.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						boolean isMenuItemSeparator = node.getNodeName().indexOf(":menuItemSeparator") > 0;
						boolean isMenuItem = !isMenuItemSeparator && node.getNodeName().indexOf(":menuItem") > 0;
						if(isMenuItem) {
							menuItems.add(node);
						} else if (isMenuItemSeparator) {
							menuItems.add(node);
						}
					}
				}
				this.menuItems = (Node[])menuItems.toArray(new Node[menuItems.size()]);
			}
		}
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	protected static boolean isHorizontalMenu(Node menuNode) {
		NamedNodeMap attrs = menuNode.getAttributes();
		Node orientationAttr = attrs.getNamedItem("orientation");
		if (orientationAttr != null && orientationAttr.getNodeValue().equals("Vertical")) {
			return false;
		}
		return true;
	}
}
