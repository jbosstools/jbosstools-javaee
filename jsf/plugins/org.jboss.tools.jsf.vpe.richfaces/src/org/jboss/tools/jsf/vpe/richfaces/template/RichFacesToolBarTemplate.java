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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.MozillaSupports;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

public class RichFacesToolBarTemplate extends VpeAbstractTemplate {

	public static final String ITEM_SEPARATOR_NONE = "none";
	public static final String ITEM_SEPARATOR_LINE = "line";
	public static final String ITEM_SEPARATOR_GRID = "grid";
	public static final String ITEM_SEPARATOR_DISC = "disc";
	public static final String ITEM_SEPARATOR_SQUARE = "square";

	public static final String ITEM_SEPARATOR_LINE_URL = "toolBar/separatorLine.gif";
	public static final String ITEM_SEPARATOR_GRID_URL = "toolBar/separatorGrid.gif";
	public static final String ITEM_SEPARATOR_DISC_URL = "toolBar/separatorDisc.gif";
	public static final String ITEM_SEPARATOR_SQUARE_URL = "toolBar/separatorSquare.gif";

	
	static final String CONTENTCLASS_ATTR_NAME = "contentClass";
	static final String CONTENTSTYLE_ATTR_NAME = "contentStyle";
	static final String STYLECLASS_ATTR_NAME = "styleClass";
	static final String STYLE_ATTR_NAME = "style";
	static final String ITEMSEPARATOR_ATTR_NAME = "itemSeparator";
	static final String SEPARATORCLASS_ATTR_NAME = "separatorClass";
	static final String WIDTH_ATTR_NAME = "width";
	static final String HEIGHT_ATTR_NAME = "height";

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		SourceToolBarElements sourceElements = new SourceToolBarElements(sourceNode);
		VisualToolBarElements visualElements = new VisualToolBarElements();

		Element sourceElement = (Element)sourceNode;
		
		ComponentUtil.setCSSLink(pageContext, "toolBar/toolBar.css", "richFacesToolBar");

		Element visualTable = visualDocument.createElement("table");
		
		ComponentUtil.correctAttribute(sourceElement, visualTable,
				WIDTH_ATTR_NAME,
				HtmlComponentUtil.HTML_WIDTH_ATTR, null, "100%");
		ComponentUtil.correctAttribute(sourceElement, visualTable,
				HEIGHT_ATTR_NAME,
				HtmlComponentUtil.HTML_HEIGHT_ATTR, null, null);
		ComponentUtil.correctAttribute(sourceElement, visualTable,
				STYLECLASS_ATTR_NAME,
				HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-ext rich-toolbar", "dr-toolbar-ext rich-toolbar");

		String style = ComponentUtil.getHeaderBackgoundImgStyle() + ";";
		ComponentUtil.correctAttribute(sourceElement, visualTable,
				STYLE_ATTR_NAME,
				HtmlComponentUtil.HTML_STYLE_ATTR, style, style);


		VpeCreationData creatorInfo = new VpeCreationData(visualTable);

		String separatorClass = sourceElement.getAttribute("separatorClass");
		
		Element section = null, row = null, cell = null;

		if (true || sourceElements.hasBodySection()) {
			section = visualDocument.createElement("tbody");
			row = visualDocument.createElement("tr");
			row.setAttribute("valign", "middle");
			
			// Columns at left
			for (int i = 0; i < sourceElements.getColumnAtLeftCount(); i++) {
				SourceToolBarColumnElements column = sourceElements.getColumnAtLeft(i);

				if (column.hasBody()) {
					Node columnBody = column.getColumn();
					cell = visualDocument.createElement("td");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
						
					row.appendChild(cell);
	
					VpeChildrenInfo info = new VpeChildrenInfo(cell);
					creatorInfo.addChildrenInfo(info);
					
					info.addSourceChild(column.getColumn());
					MozillaSupports.release(cell);
				} else if (column.isSeparator()){
					String itemSeparator = column.getSeparatorType();
					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);
					
					if (separatorImageUrl != null) {
						// Insert separator here
						cell = visualDocument.createElement("td");
						cell.setAttribute("align", "center");
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement("img");
						ComponentUtil.setImg(separatorImage, separatorImageUrl);
						cell.appendChild(separatorImage);
						row.appendChild(cell);
						MozillaSupports.release(separatorImage);
						MozillaSupports.release(cell);
					}
				}
			}
			
			// Empty column
			cell = visualDocument.createElement("td");
			cell.setAttribute("width", "100%");
			row.appendChild(cell);
			MozillaSupports.release(cell);

			// Columns at right
			for (int i = 0; i < sourceElements.getColumnAtRightCount(); i++) {
				SourceToolBarColumnElements column = sourceElements.getColumnAtRight(i);
				if (column.hasBody()) {
					Node columnBody = column.getColumn();
					cell = visualDocument.createElement("td");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
					row.appendChild(cell);
	
					VpeChildrenInfo info = new VpeChildrenInfo(cell);
					creatorInfo.addChildrenInfo(info);
					
					info.addSourceChild(column.getColumn());
					MozillaSupports.release(cell);
				} else if (column.isSeparator()){
					String itemSeparator = column.getSeparatorType();
					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);

					if (separatorImageUrl != null) {
						// Insert separator here
						cell = visualDocument.createElement("td");
						cell.setAttribute("align", "center");
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement("img");
						ComponentUtil.setImg(separatorImage, separatorImageUrl);
						cell.appendChild(separatorImage);
						row.appendChild(cell);
						MozillaSupports.release(separatorImage);
						MozillaSupports.release(cell);
					}
				}
			}

			
			section.appendChild(row);
			MozillaSupports.release(row);
			visualTable.appendChild(section);
			MozillaSupports.release(section);
			visualElements.setBodyRow(row);
			visualElements.setBody(section);
		}

		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		
		Object[] elements = new Object[2];
		elements[0] = visualElements;
		elements[1] = sourceElements;
		visualNodeMap.put(this, elements);

		return creatorInfo;
	}

	public static String getSeparatorImageUrlString (String itemSeparator) {
		String separatorImageUrl = null;
		if (ITEM_SEPARATOR_DISC.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_DISC_URL;
		} else if (ITEM_SEPARATOR_GRID.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_GRID_URL;
		} else if (ITEM_SEPARATOR_LINE.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_LINE_URL;
		} else if (ITEM_SEPARATOR_SQUARE.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_SQUARE_URL;
		}
		return separatorImageUrl;
	}
	private VisualToolBarElements getVisualToolBarElements(Map visualNodeMap) {
		if (visualNodeMap != null) {
			Object o = visualNodeMap.get(this);
			try {
				if (o != null && o instanceof Object[] && ((Object[])o)[0] instanceof VisualToolBarElements) {
					return (VisualToolBarElements)((Object[])o)[0];
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	private void setRowDisplayStyle(Element row, boolean visible) {
		if (row != null) {
			row.setAttribute("style", "display:" + (visible ? "" : "none"));
		}
	}

	private SourceToolBarElements getSourceToolBarElements(Map visualNodeMap) {
		if (visualNodeMap != null) {
			Object o = visualNodeMap.get(this);
			try {
				if (o != null && o instanceof Object[] && ((Object[])o)[1] instanceof VisualToolBarElements) {
					return (SourceToolBarElements)o;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public static final String TOOLBARGROUP_LOCATE_ATTRIBUTE = "location";
	public static final String TOOLBARGROUP_LOCATE_AT_RIGHT_ATTRIBUTE_VALUE = "right";
	public static final String TOOLBARGROUP_LOCATE_AT_LEFT_ATTRIBUTE_VALUE = "left";
	
	public static class SourceToolBarElements {
		private List columnsAtLeft;
		private List columnsAtRight;
		Map attributes;
		
		public SourceToolBarElements(Node sourceNode) {
			init(sourceNode);
			initAttributes(sourceNode);
		}
		
		void initAttributes(Node sourceNode) {
			NamedNodeMap attrs = sourceNode.getAttributes();
			attributes = new HashMap<String, String>();
			for (int i = 0; attrs != null && i < attrs.getLength(); i++) {
				Node attribute = attrs.item(i);
				attributes.put(attribute.getNodeName(), attribute.getNodeValue());
			}
		}
		
		void init (Node sourceNode) {
			
			String separator = ((Element)sourceNode).getAttribute("itemSeparator");

			NodeList children = sourceNode.getChildNodes();

			int cnt = children != null ? children.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element)child;
						
						if (childElement.getNodeName().endsWith(":toolBarGroup")) {
							boolean locateAtRight = TOOLBARGROUP_LOCATE_AT_RIGHT_ATTRIBUTE_VALUE.equals(childElement.getAttribute(TOOLBARGROUP_LOCATE_ATTRIBUTE));
							String itemSeparator = childElement.getAttribute(ITEMSEPARATOR_ATTR_NAME);
							if (itemSeparator == null || itemSeparator.trim().length() == 0) {
								itemSeparator = ITEM_SEPARATOR_NONE;
							}
							if (locateAtRight) {
								if (columnsAtRight == null) columnsAtRight = new ArrayList();
								if (i != 0 && !(ITEM_SEPARATOR_NONE.equals(separator))) columnsAtRight.add(new SourceToolBarColumnElements(separator));
							}
							initToolBarGroup(childElement, locateAtRight);
							if (!locateAtRight) {
								if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
								if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(separator))) columnsAtLeft.add(new SourceToolBarColumnElements(separator));
							}

						} else {
							if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
							columnsAtLeft.add(new SourceToolBarColumnElements(child));
							if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(separator))) columnsAtLeft.add(new SourceToolBarColumnElements(separator));
						}
					} else if (child.getNodeType() == Node.TEXT_NODE) {
						String text = child.getNodeValue();
						text = (text == null ? null : text.trim());
						if (text != null && text.length() > 0) {
							if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
							columnsAtLeft.add(new SourceToolBarColumnElements(child));
							if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(separator))) columnsAtLeft.add(new SourceToolBarColumnElements(separator));
						}
					}
				}
			}
		}

		private boolean isLastChild(NodeList children, int index) {
			int cnt = children != null ? children.getLength() : 0;
			if (cnt > index + 1) {
				for (int i = index + 1; i < cnt; i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE) return false;
					else if (children.item(i).getNodeType() == Node.TEXT_NODE) {
						String text = children.item(i).getNodeValue();
						text = (text == null ? null : text.trim());
						if (text != null && text.length() > 0) {
							return false;
						}
					}
				}
			}
			return true;
		}
		
		void initToolBarGroup(Element sourceElement, boolean locateAtRight) {
			if (sourceElement == null) return;
			String itemSeparator = sourceElement.getAttribute(ITEMSEPARATOR_ATTR_NAME);
			if (itemSeparator == null || itemSeparator.trim().length() == 0) {
				itemSeparator = ITEM_SEPARATOR_NONE;
			}
			NodeList children = sourceElement.getChildNodes();

			int cnt = children != null ? children.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element)child;
						
						if (childElement.getNodeName().endsWith(":toolBarGroup")) {
							if (locateAtRight) {
								if (columnsAtRight == null) columnsAtRight = new ArrayList();
								if (i != 0 && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtRight.add(new SourceToolBarColumnElements(itemSeparator));
							}
							initToolBarGroup(childElement, locateAtRight);
							if (!locateAtRight) {
								if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
								if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtLeft.add(new SourceToolBarColumnElements(itemSeparator));
							}
						} else {
							if (locateAtRight) {
								if (columnsAtRight == null) columnsAtRight = new ArrayList();
								if (i != 0 && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtLeft.add(new SourceToolBarColumnElements(itemSeparator));
								columnsAtRight.add(new SourceToolBarColumnElements(child));
							} else {
								if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
								columnsAtLeft.add(new SourceToolBarColumnElements(child));
								if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtLeft.add(new SourceToolBarColumnElements(itemSeparator));
							}
						}
					} else if (child.getNodeType() == Node.TEXT_NODE) {
						String text = child.getNodeValue();
						text = (text == null ? null : text.trim());
						if (text != null && text.length() > 0) {
							if (locateAtRight) {
								if (columnsAtRight == null) columnsAtRight = new ArrayList();
								if (i != 0 && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtLeft.add(new SourceToolBarColumnElements(itemSeparator));
								columnsAtRight.add(new SourceToolBarColumnElements(child));
							} else {
								if (columnsAtLeft == null) columnsAtLeft = new ArrayList();
								columnsAtLeft.add(new SourceToolBarColumnElements(child));
								if (!isLastChild(children, i) && !(ITEM_SEPARATOR_NONE.equals(itemSeparator))) columnsAtLeft.add(new SourceToolBarColumnElements(itemSeparator));
							}
						}
					}
				}
			}
		}
		
		public SourceToolBarColumnElements getColumnAtLeft(int index) {
			if (columnsAtLeft != null && index < getColumnAtLeftCount()) return (SourceToolBarColumnElements)columnsAtLeft.get(index);
			return null;
		}

		public SourceToolBarColumnElements getColumnAtRight(int index) {
			if (columnsAtRight != null && index < getColumnAtRightCount()) return (SourceToolBarColumnElements)columnsAtRight.get(index);
			return null;
		}

		public int getColumnAtLeftCount() {
			if (columnsAtLeft != null) return columnsAtLeft.size();
			return 0;
		}

		public int getColumnAtRightCount() {
			if (columnsAtRight != null) return columnsAtRight.size();
			return 0;
		}

		public boolean hasColspan() {
			return (getColumnAtLeftCount() + getColumnAtRightCount()) >= 2;
		}

		public boolean hasBodySection() {
			for (int i = 0; i < getColumnAtLeftCount(); i++) {
				SourceToolBarColumnElements column = getColumnAtLeft(i);
				if (column.hasBody()) return true;
			}
			for (int i = 0; i < getColumnAtRightCount(); i++) {
				SourceToolBarColumnElements column = getColumnAtRight(i);
				if (column.hasBody()) return true;
			}
			return false;
		}

		Map getAttributes() {
			return attributes;
		}
		
		String getAttributeValue(String name) {
			return (String)attributes.get(name);
		}
	}
	
	
	
	public static class SourceToolBarColumnElements {
		private Node column;
		private String separatorType;
		

		public SourceToolBarColumnElements(Node columnNode) {
			init(columnNode);
			separatorType = null;
		}

		public SourceToolBarColumnElements(String separatorType) {
			column = null;
			this.separatorType = separatorType;
		}

		private void init(Node columnNode) {
			column = columnNode;
		}

		public boolean hasBody() {
			return column != null;
		}

		public void setColumn(Node column) {
			this.column = column;
		}

		public Node getColumn() {
			return column;
		}
		
		public boolean isSeparator() {
			return (column == null);
		}
		
		public String getSeparatorType() {
			return separatorType;
		}
	}

	public static class VisualToolBarElements {
		private Element body;
		private Element bodyRow;
		private List columns;

		public VisualToolBarElements() {
		}
		public VisualToolBarElements(Element body) {
			this.body = body;
		}
		
		private VisualToolBarColumnElements getColumn(int index) {
			if (columns != null && index < getColumnCount()) return (VisualToolBarColumnElements)columns.get(index);
			return null;
		}

		private int getColumnCount() {
			if (columns != null) return columns.size();
			return 0;
		}

		private List getColumns() {
			if (columns == null) columns = new ArrayList();
			return columns;
		}

		public Element getBody() {
			return body;
		}

		public void setBody(Element body) {
			this.body = body;
		}

		public Element getBodyRow() {
			return bodyRow;
		}

		public void setBodyRow(Element bodyRow) {
			this.bodyRow = bodyRow;
		}
	}

	public static class VisualToolBarColumnElements {
		private Element bodyCell;

		private boolean isEmpty() {
			return bodyCell == null;
		}

		public Element getBodyCell() {
			return bodyCell;
		}

		public void setBodyCell(Element bodyCell) {
			this.bodyCell = bodyCell;
		}
	}
}