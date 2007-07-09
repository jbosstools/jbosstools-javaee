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
import java.util.Iterator;
import java.util.LinkedList;
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
import org.w3c.dom.Text;

public class RichFacesToolBarTemplate extends VpeAbstractTemplate {
	public static final String TAG_NAME = "toolBar";
	
	public static final String ITEM_SEPARATOR_NONE = "none";
	public static final String ITEM_SEPARATOR_LINE = "line";
	public static final String ITEM_SEPARATOR_GRID = "grid";
	public static final String ITEM_SEPARATOR_DISC = "disc";
	public static final String ITEM_SEPARATOR_SQUARE = "square";

	public static final String ITEM_SEPARATOR_LINE_URL = "toolBar/separatorLine.gif";
	public static final String ITEM_SEPARATOR_GRID_URL = "toolBar/separatorGrid.gif";
	public static final String ITEM_SEPARATOR_DISC_URL = "toolBar/separatorDisc.gif";
	public static final String ITEM_SEPARATOR_SQUARE_URL = "toolBar/separatorSquare.gif";

	public static final String EXCEPTION_ATTR_STYLE_VALUE = "color: red; font-weight:bold;";

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

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		VpeCreationData creationData = null;
		Element visualNode = null;
		
		Element sourceElement = (Element) sourceNode;
		String itemSeparator = sourceElement.getAttribute(ITEMSEPARATOR_ATTR_NAME); 
		if (!isValidItemSeparatorName(itemSeparator)) {
			visualNode = createExceptionNode(visualDocument,
					"Unknown type of separator \"" + itemSeparator + "\"");
			
			creationData = new VpeCreationData(visualNode);
		} else {
			SourceToolBarItems sourceToolBarItems = new SourceToolBarItems(sourceNode, itemSeparator);
			String itemSeparatorImageUrl = getSeparatorImageUrlString(sourceToolBarItems.getItemSeparator());
	
			ComponentUtil.setCSSLink(pageContext, "toolBar/toolBar.css", "richFacesToolBar");
			
			visualNode = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
	
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					WIDTH_ATTR_NAME,
					HtmlComponentUtil.HTML_WIDTH_ATTR, null, "100%");
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					HEIGHT_ATTR_NAME,
					HtmlComponentUtil.HTML_HEIGHT_ATTR, null, null);
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					STYLECLASS_ATTR_NAME,
					HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-ext rich-toolbar", "dr-toolbar-ext rich-toolbar");
			
			String style = ComponentUtil.getHeaderBackgoundImgStyle() + ";";
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					STYLE_ATTR_NAME,
					HtmlComponentUtil.HTML_STYLE_ATTR, style, style);
	
			creationData = new VpeCreationData(visualNode);
			
			Element body = null, row = null, cell = null;
	
			body = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
			row = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
			row.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN, HtmlComponentUtil.HTML_ATTR_VALIGN_MIDDLE_VALUE);
	
			SourceToolBarItem toolBarItem;
			Iterator<SourceToolBarItem> iterator = sourceToolBarItems.getLeftItemsIterator();
			while (iterator.hasNext()) {
				toolBarItem = iterator.next();
				cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
				if (toolBarItem.isItem()) {
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px 0px 0px 0px;" : null,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px 0px 0px 0px;" : null);
					
					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(cell);
					creationData.addChildrenInfo(childrenInfo);
					childrenInfo.addSourceChild(toolBarItem.getToolBarItem());
				} else {
					if (itemSeparatorImageUrl != null) {
						cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
						cell.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, HtmlComponentUtil.HTML_ALIGN_CENTER_VALUE);
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
						ComponentUtil.setImg(separatorImage, itemSeparatorImageUrl);
						cell.appendChild(separatorImage);
						MozillaSupports.release(separatorImage);
					}
				}
				
				row.appendChild(cell);
				MozillaSupports.release(cell);
			}
	
			// Empty column
			cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
			cell.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
			row.appendChild(cell);
			MozillaSupports.release(cell);
	
			iterator = sourceToolBarItems.getRightItemsIterator();
			while (iterator.hasNext()) {
				toolBarItem = iterator.next();
				cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
				if (toolBarItem.isItem()) {
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px;" : null,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px;" : null);
					
					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(cell);
					creationData.addChildrenInfo(childrenInfo);
					childrenInfo.addSourceChild(toolBarItem.getToolBarItem());
				} else {
					if (itemSeparatorImageUrl != null) {
						cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
						cell.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, HtmlComponentUtil.HTML_ALIGN_CENTER_VALUE);
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
						ComponentUtil.setImg(separatorImage, itemSeparatorImageUrl);
						cell.appendChild(separatorImage);
						MozillaSupports.release(separatorImage);
					}
				}
				
				row.appendChild(cell);
				MozillaSupports.release(cell);
			}
			
			body.appendChild(row);
			MozillaSupports.release(row);
			visualNode.appendChild(body);
			MozillaSupports.release(body);
		}
		
		return creationData;
	}

	static Element createExceptionNode(Document visualDocument, String message) {
		Element visualNode;
		visualNode = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		visualNode.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EXCEPTION_ATTR_STYLE_VALUE);
		Text text = visualDocument.createTextNode(message);
		visualNode.appendChild(text);
		MozillaSupports.release(text);
		
		return visualNode;
	}

	
	
//	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
//		SourceToolBarElements sourceElements = new SourceToolBarElements(sourceNode);
//		VisualToolBarElements visualElements = new VisualToolBarElements();
//
//		Element sourceElement = (Element)sourceNode;
//		
//		ComponentUtil.setCSSLink(pageContext, "toolBar/toolBar.css", "richFacesToolBar");
//
//		Element visualTable = visualDocument.createElement("table");
//		
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				WIDTH_ATTR_NAME,
//				HtmlComponentUtil.HTML_WIDTH_ATTR, null, "100%");
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				HEIGHT_ATTR_NAME,
//				HtmlComponentUtil.HTML_HEIGHT_ATTR, null, null);
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				STYLECLASS_ATTR_NAME,
//				HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-ext rich-toolbar", "dr-toolbar-ext rich-toolbar");
//
//		String style = ComponentUtil.getHeaderBackgoundImgStyle() + ";";
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				STYLE_ATTR_NAME,
//				HtmlComponentUtil.HTML_STYLE_ATTR, style, style);
//
//
//		VpeCreationData creatorInfo = new VpeCreationData(visualTable);
//
//		String separatorClass = sourceElement.getAttribute("separatorClass");
//		
//		Element section = null, row = null, cell = null;
//
//		if (true || sourceElements.hasBodySection()) {
//			section = visualDocument.createElement("tbody");
//			row = visualDocument.createElement("tr");
//			row.setAttribute("valign", "middle");
//			
//			// Columns at left
//			for (int i = 0; i < sourceElements.getColumnAtLeftCount(); i++) {
//				SourceToolBarColumnElements column = sourceElements.getColumnAtLeft(i);
//
//				if (column.hasBody()) {
//					Node columnBody = column.getColumn();
//					cell = visualDocument.createElement("td");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTCLASS_ATTR_NAME,
//							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTSTYLE_ATTR_NAME,
//							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
//						
//					row.appendChild(cell);
//	
//					VpeChildrenInfo info = new VpeChildrenInfo(cell);
//					creatorInfo.addChildrenInfo(info);
//					
//					info.addSourceChild(column.getColumn());
//					MozillaSupports.release(cell);
//				} else if (column.isSeparator()){
//					String itemSeparator = column.getSeparatorType();
//					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);
//					
//					if (separatorImageUrl != null) {
//						// Insert separator here
//						cell = visualDocument.createElement("td");
//						cell.setAttribute("align", "center");
//						ComponentUtil.correctAttribute(sourceElement, cell,
//								SEPARATORCLASS_ATTR_NAME,
//								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
//						Element separatorImage = visualDocument.createElement("img");
//						ComponentUtil.setImg(separatorImage, separatorImageUrl);
//						cell.appendChild(separatorImage);
//						row.appendChild(cell);
//						MozillaSupports.release(separatorImage);
//						MozillaSupports.release(cell);
//					}
//				}
//			}
//			
//			// Empty column
//			cell = visualDocument.createElement("td");
//			cell.setAttribute("width", "100%");
//			row.appendChild(cell);
//			MozillaSupports.release(cell);
//
//			// Columns at right
//			for (int i = 0; i < sourceElements.getColumnAtRightCount(); i++) {
//				SourceToolBarColumnElements column = sourceElements.getColumnAtRight(i);
//				if (column.hasBody()) {
//					Node columnBody = column.getColumn();
//					cell = visualDocument.createElement("td");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTCLASS_ATTR_NAME,
//							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTSTYLE_ATTR_NAME,
//							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
//					row.appendChild(cell);
//	
//					VpeChildrenInfo info = new VpeChildrenInfo(cell);
//					creatorInfo.addChildrenInfo(info);
//					
//					info.addSourceChild(column.getColumn());
//					MozillaSupports.release(cell);
//				} else if (column.isSeparator()){
//					String itemSeparator = column.getSeparatorType();
//					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);
//
//					if (separatorImageUrl != null) {
//						// Insert separator here
//						cell = visualDocument.createElement("td");
//						cell.setAttribute("align", "center");
//						ComponentUtil.correctAttribute(sourceElement, cell,
//								SEPARATORCLASS_ATTR_NAME,
//								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
//						Element separatorImage = visualDocument.createElement("img");
//						ComponentUtil.setImg(separatorImage, separatorImageUrl);
//						cell.appendChild(separatorImage);
//						row.appendChild(cell);
//						MozillaSupports.release(separatorImage);
//						MozillaSupports.release(cell);
//					}
//				}
//			}
//
//			
//			section.appendChild(row);
//			MozillaSupports.release(row);
//			visualTable.appendChild(section);
//			MozillaSupports.release(section);
//			visualElements.setBodyRow(row);
//			visualElements.setBody(section);
//		}
//
//		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
//		
//		Object[] elements = new Object[2];
//		elements[0] = visualElements;
//		elements[1] = sourceElements;
//		visualNodeMap.put(this, elements);
//
//		return creatorInfo;
//	}

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
	
	static boolean isValidItemSeparatorName(String itemSeparator) {
		return itemSeparator == null
				|| (itemSeparator != null && itemSeparator.length() == 0)
				|| ITEM_SEPARATOR_DISC.equals(itemSeparator)
				|| ITEM_SEPARATOR_LINE.equals(itemSeparator)
				|| ITEM_SEPARATOR_GRID.equals(itemSeparator)
				|| ITEM_SEPARATOR_SQUARE.equals(itemSeparator)
				|| ITEM_SEPARATOR_NONE.equals(itemSeparator);
	}
	
	private class SourceToolBarItem {
		private Node toolBarItem;
		private String itemSeparator;
		private boolean isToolBarItemLocationRight;
		private boolean isToolBarGroupItem;
		
		public SourceToolBarItem(Node toolBarItem) {
			this.toolBarItem = toolBarItem;
			this.itemSeparator = null;
			this.isToolBarItemLocationRight = false;
			 
			if (toolBarItem.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) toolBarItem;
				this.isToolBarGroupItem = element.getNodeName().endsWith(":" + RichFacesToolBarGroupTemplate.TAG_NAME);
				if (isToolBarGroupItem()) {
					isToolBarItemLocationRight = RichFacesToolBarGroupTemplate
							.ATTR_LOCATION_RIGHT_VALUE
							.equals(element.getAttribute(RichFacesToolBarGroupTemplate.ATTR_LOCATION_NAME));
				}
			}
		}

		public SourceToolBarItem(String itemSeparator) {
			this.toolBarItem = null;
			this.itemSeparator = itemSeparator;
		}
		
		public Node getToolBarItem() {
			return toolBarItem;
		}

		public String getItemSeparator() {
			return itemSeparator;
		}

		public boolean isItem() {
			return toolBarItem != null;
		}
		
		public boolean isToolBarItemLocationRight() {
			return isToolBarItemLocationRight;
		}

		public boolean isToolBarGroupItem() {
			return isToolBarGroupItem;
		}
	}
	
	private class SourceToolBarItems {
		private List<SourceToolBarItem> leftToolBarItems;
		private List<SourceToolBarItem> rightToolBarItems;
		private String itemSeparator;
		private boolean itemSeparatorExists; 

		public SourceToolBarItems(Node sourceNode, String itemSeparator) {
			this.leftToolBarItems = new LinkedList<SourceToolBarItem>();
			this.rightToolBarItems = new LinkedList<SourceToolBarItem>();
			this.itemSeparator = itemSeparator;
			this.itemSeparatorExists = !(itemSeparator == null
					|| itemSeparator.length() == 0
					|| ITEMSEPARATOR_ATTR_NAME.equals(itemSeparator));
			
			init(sourceNode);
		}
		
		private void init(Node sourceNode) {
			NodeList childrenList = sourceNode.getChildNodes();
			int childrenCount = childrenList.getLength();
			for (int i=0; i < childrenCount; i++) {
				Node child = childrenList.item(i);
				if (isVisibleNode(child)) {
					SourceToolBarItem toolBarItem = new SourceToolBarItem(child);
					if (toolBarItem.isToolBarItemLocationRight()) {
						if (isItemSeparatorExists()) {
							rightToolBarItems.add(new SourceToolBarItem(itemSeparator));
						}
						rightToolBarItems.add(toolBarItem);
					} else {
						leftToolBarItems.add(toolBarItem);
						if (isItemSeparatorExists()) {
							leftToolBarItems.add(new SourceToolBarItem(itemSeparator));
						}
					}
				}
			}
			
			if (isItemSeparatorExists() && !isLeftItemsExists()) {
				rightToolBarItems.remove(0);
			}
			
			if (isItemSeparatorExists() && !isRightItemsExists()) {
				leftToolBarItems.remove(leftToolBarItems.size()-1);
			}
		}
		
		public boolean isLeftItemsExists() {
			return !leftToolBarItems.isEmpty();
		}

		public boolean isRightItemsExists() {
			return !rightToolBarItems.isEmpty();
		}
		
		public Iterator<SourceToolBarItem> getLeftItemsIterator() {
			return leftToolBarItems.iterator();
		}

		public Iterator<SourceToolBarItem> getRightItemsIterator() {
			return rightToolBarItems.iterator();
		}
		
		private boolean isVisibleNode(Node node) {
			return node.getNodeType() == Node.ELEMENT_NODE
					|| (node.getNodeType() == Node.TEXT_NODE
							&& node.getNodeValue() != null
							&& node.getNodeValue().trim().length() > 0);
		}

		public boolean isItemSeparatorExists() {
			return itemSeparatorExists;
		}

		public String getItemSeparator() {
			return itemSeparator;
		}
	}
}